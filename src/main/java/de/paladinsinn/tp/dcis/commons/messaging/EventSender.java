/*
 * Copyright (c) 2024-2025. Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software Foundation,
 *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.paladinsinn.tp.dcis.commons.messaging;


import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;


/**
 * EventSender -- sending events via AMQP to RabbitMQ.
 * <code>
 * spring:
 *   rabbitmq:
 *     host: &lt;hostname&gt;
 *     port: 5672
 *     virtual-host: &lt;vhost&gt;
 *     username: &lt;username&gt;
 *     password: &lt:password&gt;
 *     template:
 *       retry:
 *         enabled: true
 *         initial-interval: 2s
 * </code>
 *
 * @author klenkes74
 * @since 30.11.24
 */
@Service
@RequiredArgsConstructor
@ToString
@XSlf4j
public class EventSender<T extends Serializable> {
  private final RabbitTemplate amqp;
  
  @Timed
  @Counted(value = "counted.event.sender", description = "Sending an amqp message.")
  public void send(Queue queue, final T event) {
    log.entry(queue, event);

    amqp.convertAndSend(queue.getName(), event);

    log.exit();
  }
}
