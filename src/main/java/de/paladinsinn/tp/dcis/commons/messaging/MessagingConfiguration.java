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

package de.paladinsinn.tp.dcis.commons.messaging;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.PooledChannelConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the messaging system to use json messages.
 *
 * @author klenkes74
 * @since 2024-11-09
 */
@Configuration
@RequiredArgsConstructor
@ToString
@XSlf4j
public class MessagingConfiguration {
    @Value("${spring.rabbitmq.host:localhost}")
    private final String host;
    @Value("${spring.rabbitmq.port:5672}")
    private final String port;
    @Value("${spring.rabbitmq.username}")
    private final String username;
    @Value("${spring.rabbitmq.password}")
    @ToString.Exclude // we don't want to print passwords in here ...
    private final String password;

    @Bean
    public RabbitTemplate rabbitTemplate(final Jackson2JsonMessageConverter messageConverter, final ConnectionFactory connectionFactory) {
        log.entry(messageConverter, connectionFactory);

        final var result = new RabbitTemplate(connectionFactory);
        result.setMessageConverter(messageConverter);

        return log.exit(result);
    }

    private ConnectionFactory factory = null;

    @Bean
    public ConnectionFactory connectionFactory() {
        if (factory == null) {
            factory = createConnectionFactory();
        }

        return log.exit(factory);
    }

    private ConnectionFactory createConnectionFactory() {
        com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
        factory.setHost(host);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setPort(Integer.parseInt(port, 10));

        return log.exit(new PooledChannelConnectionFactory(factory));
    }

    private Jackson2JsonMessageConverter messageConverter = null;
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        if (messageConverter == null) {
            messageConverter = createMessageConverter();
        }

        return log.exit(messageConverter);
    }

    private Jackson2JsonMessageConverter createMessageConverter() {
        return log.exit(new Jackson2JsonMessageConverter());
    }


    @Bean
    public Queue userLogQueue(@Value("${queues.dcis.users.log:dcis.users.log}") final String queueName) {
        log.entry(queueName);

        return log.exit(new Queue(queueName, true));
    }
}
