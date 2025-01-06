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

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.paladinsinn.tp.dcis.commons.messaging.EventSender;
import de.paladinsinn.tp.dcis.users.domain.events.UserLoginEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Service;


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
  private final EventSender<UserLoginEvent> service;
  private final EventBus bus;
  private final Queue userLogQueue;

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

    service.send(userLogQueue, event);

    log.exit();
  }
}
