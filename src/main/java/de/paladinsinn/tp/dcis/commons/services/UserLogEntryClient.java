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
import de.paladinsinn.tp.dcis.commons.messaging.EventSender;
import de.paladinsinn.tp.dcis.users.domain.events.UserLoginEvent;
import de.paladinsinn.tp.dcis.users.domain.model.User;
import de.paladinsinn.tp.dcis.users.domain.model.UserLogEntry;
import de.paladinsinn.tp.dcis.users.domain.model.UserLogEntryImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;


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
  private final EventSender<UserLogEntry> service;
  private final LoggingEventBus bus;
  private final Queue userLogQueue;
  
  private final HashMap<User, Instant> lastLogin = new HashMap<>();
  
  @Value("${spring.application.name:unknown}")
  private String application;

  @PostConstruct
  public void init() {
    log.entry(service, bus, userLogQueue);

    bus.register(this);
    log.exit();
  }

  @PreDestroy
  public void shutdown() {
    log.entry(service, bus, userLogQueue);
    bus.unregister(this);
    log.exit();
  }

  @SuppressWarnings("unused") // It is used by the event bus
  @Subscribe
  public void send(final UserLoginEvent event) {
    log.entry(userLogQueue, event);

    if (!isAlreadyLoggedIn(event.getUser())) {
      service.send(userLogQueue, createUserLogEntry(event));
      log.info("User has been logged in. user={}", event.getUser());
    } else {
      log.info("User has been logged in during the last hour. Ignoring this login event. user={}", event.getUser());
    }

    log.exit();
  }
  
  private UserLogEntry createUserLogEntry(UserLoginEvent event) {
    return log.exit(
        UserLogEntryImpl.builder()
            .id(event.getId())
            .system(application)
            .text("User logged in. namespace=" + event.getUser().getNameSpace() + ", name=" + event.getUser().getName())
            .build()
    );
  }
  
  private boolean isAlreadyLoggedIn(User user) {
    log.entry(user);
    
    boolean result = !lastLogin.containsKey(user)
        || (lastLogin.containsKey(user) && lastLogin.get(user).isBefore(Instant.now().minusSeconds(3600)));
    lastLogin.put(user, Instant.now());
    
    return log.exit(result);
  }
}
