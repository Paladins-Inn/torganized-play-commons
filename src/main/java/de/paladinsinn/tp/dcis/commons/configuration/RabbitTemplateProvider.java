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

import lombok.extern.slf4j.XSlf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.PooledChannelConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the messaging system to use json messages.
 *
 * @author klenkes74
 * @since 2024-11-09
 */
@Configuration
@XSlf4j
public class RabbitTemplateProvider {

    @Bean
    public RabbitTemplate rabbitTemplate(final Jackson2JsonMessageConverter messageConverter) {
        log.entry(messageConverter);

        final var result = new RabbitTemplate();
        result.setMessageConverter(messageConverter);

        return log.exit(result);
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        return log.exit(new PooledChannelConnectionFactory(new com.rabbitmq.client.ConnectionFactory()));
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return log.exit(new Jackson2JsonMessageConverter());
    }
}
