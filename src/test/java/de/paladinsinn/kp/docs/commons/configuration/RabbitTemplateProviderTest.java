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

package de.paladinsinn.kp.docs.commons.configuration;


import de.paladinsinn.tp.dcis.commons.messaging.RabbitTemplateProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author klenkes74
 * @since 24.12.24
 */
@SpringBootTest
@ActiveProfiles("rabbitmq-test")
@Slf4j
public class RabbitTemplateProviderTest {
  @Autowired
  private RabbitTemplateProvider sut;

  @Test
  public void shouldProvideAValidMessageConverter() {
    // Given
    // When
    var result = sut.messageConverter();
    // Then
    log.debug("Checking the message converter: {}", result);
    assertThat(result).isNotNull();
    assertThat(result.getDefaultCharset()).isEqualTo("UTF-8");
  }

  @Test
  public void shouldProvideAVaildConnectionFactory() {
    // Given
    // When
    var result = sut.connectionFactory();

    // Then
    log.debug("Checking the connection factory: {}", result);
    assertThat(result).isNotNull();
    assertThat(result.getHost()).isEqualTo("localhost");
    assertThat(result.getPort()).isEqualTo(5672);
  }

  @Test
  public void shouldProvideARabbitTemplate() {
    // Given
    // When
    var result = sut.rabbitTemplate(sut.messageConverter(), sut.connectionFactory());

    // Then
    log.debug("Checking the rabbit template: {}", result);
    assertThat(result).isNotNull();

    assertThat(result.getConnectionFactory()).isEqualTo(sut.connectionFactory());
    assertThat(result.getMessageConverter()).isEqualTo(sut.messageConverter());
  }
}
