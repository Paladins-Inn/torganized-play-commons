/*
 * Copyright (c) 2024 Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.paladinsinn.tp.dcis.commons.services;

import com.google.common.eventbus.Subscribe;
import de.paladinsinn.tp.dcis.commons.events.LoggingEventBus;
import de.paladinsinn.tp.dcis.users.domain.events.UserLoginEvent;
import de.paladinsinn.tp.dcis.users.domain.events.UserLogoutEvent;
import de.paladinsinn.tp.dcis.users.domain.model.User;
import io.micrometer.core.annotation.Timed;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 *  This service reports all user login event to the AMQP queue for user logins.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.1.0
 * @since 2024-11-05
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@XSlf4j
public class UserLogEntryClient {
  private static final String sinkName = "user-logs";
  
  @Value("${spring.application.name:unknown}")
  private String application;

  /** The messaging infrastructure. */
  private final StreamBridge streamBridge;
  
  /** The local event bus. */
  private final LoggingEventBus bus;

  /** The cache of logged in users. */
  private final HashMap<User, Instant> lastLogin = new HashMap<>();

  
  @PostConstruct
  public void init() {
    log.entry(streamBridge, bus);

    bus.register(this);
    log.exit();
  }

  
  @PreDestroy
  public void shutdown() {
    log.entry(streamBridge, bus);
    bus.unregister(this);
    
    synchronized (lastLogin) {
      log.trace("Clearing last login cache.");
      lastLogin.clear();
    }
    
    log.exit();
  }

  
  @SuppressWarnings("unused") // It is used by the event bus
  @Timed
  @Subscribe
  public void send(final UserLoginEvent event) {
    log.entry(streamBridge, event);

    if (!isAlreadyLoggedIn(event.getUser())) {
      // set application to event before sending it out.
      streamBridge.send(sinkName, event.toBuilder().system(application).build());
  
      log.info("User login has been reported to central logger. user={}, application={}", event.getUser(), application);
    } else {
      log.info("User has been logged in during the last hour. Ignoring this login event. user={}, application={}", event.getUser(), application);
    }

    log.exit();
  }
  
  @SuppressWarnings("unused") // It is used by the event bus
  @Timed
  @Subscribe
  public void send(final UserLogoutEvent event) {
    log.entry(streamBridge, event);
    
    if (isAlreadyLoggedIn(event.getUser())) {
      log.info("User is not logged in. Ignoring this logout event. user={}, application={}", event.getUser(), application);
    } else {
      streamBridge.send(sinkName, event.toBuilder().system(application).build());
      
      synchronized (lastLogin) {
        lastLogin.remove(event.getUser());
      }
      
      log.info("User logout has been reported to central logger. user={}, application={}", event.getUser(), application);
    }
    
    log.exit();
  }
  
  private boolean isAlreadyLoggedIn(User user) {
    log.entry(user);

    boolean result;
    
    synchronized (lastLogin) {
      result = lastLogin.containsKey(user)
          && lastLogin.get(user).isAfter(Instant.now().minusSeconds(3600));
      
      if (result) {
        // reset user seen timestamp to now.
        lastLogin.put(user, Instant.now());
      }
    }
    
    return log.exit(result);
  }

  @Timed
  @Scheduled(initialDelay = 20, fixedDelay = 20, timeUnit = TimeUnit.MINUTES)
  void purgeLoggedInUsers() {
    log.entry();
    
    int oldSize;
    
    synchronized(lastLogin) {
      oldSize = lastLogin.size();
      
      Map<User, Instant> result = lastLogin.entrySet().stream()
          .filter((e) -> e.getValue().isBefore(Instant.now().minusSeconds(3600)))
          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
      
      result.forEach((user, instant) -> {
        log.info("User locally logged out due to inactivity. user={}", user);
        lastLogin.remove(user);
      });
    }

    log.debug("Purging user login cache. old={}, new={}", oldSize, lastLogin.size());
    
    log.exit();
  }
}
