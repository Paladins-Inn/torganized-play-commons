/*
 * Copyright (c) 2024. Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or  (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.kp.docs.commons.events;


import de.paladinsinn.tp.dcis.commons.messaging.EventSender;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * @author klenkes74
 * @since 30.11.24
 */
@ExtendWith(SpringExtension.class)
@ToString
@XSlf4j
public class EventSenderTest {

  @Autowired
  private EventSender<TestEvent> sut;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  private final EventSenderTestContextConfiguration config = new EventSenderTestContextConfiguration();

  @TestConfiguration
  static class EventSenderTestContextConfiguration {
    // FIXME 2025-01-06 rlichti MockBean is deprecated since spring-boot 3.4.0 and will be removed in 3.6.0
    @MockBean
    static RabbitTemplate rabbitTemplate;

    @Bean
    public RabbitTemplate rabbitTemplate() {
      return rabbitTemplate;
    }

    @Bean
    public EventSender<TestEvent> eventSender(RabbitTemplate rabbitTemplate) {
      return new EventSender<>(rabbitTemplate);
    }
  }

  @Test
  public void shouldSendTheEventWithAMessageAndQueue() {
    Queue target = Mockito.mock(Queue.class);
    TestEvent event = new TestEvent(UUID.randomUUID());
    log.entry(target, event);

    sut.send(target, event);

    verify(rabbitTemplate, times(1)).convertAndSend(target.getName(), event);

    log.exit();
  }
}
