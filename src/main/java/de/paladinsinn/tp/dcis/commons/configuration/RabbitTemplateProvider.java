/*
 * Copyright (c) 2024. Kaiserpfalz EDV-Service, Roland T. Lichti
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

package de.paladinsinn.tp.dcis.commons.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
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
@XSlf4j
public class RabbitTemplateProvider {
    @Value("${spring.rabbitmq.host:localhost}")
    private final String host;
    @Value("${spring.rabbitmq.port:5672}")
    private final String port;
    @Value("${spring.rabbitmq.username}")
    private final String username;
    @Value("${spring.rabbitmq.password}")
    private final String password;

    @Bean
    public RabbitTemplate rabbitTemplate(final Jackson2JsonMessageConverter messageConverter, final ConnectionFactory connectionFactory) {
        log.entry(messageConverter, connectionFactory);

        final var result = new RabbitTemplate(connectionFactory);
        result.setMessageConverter(messageConverter);

        return log.exit(result);
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
        factory.setHost(host);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setPort(Integer.getInteger(port));

        return log.exit(new PooledChannelConnectionFactory(factory));
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return log.exit(new Jackson2JsonMessageConverter());
    }
}
