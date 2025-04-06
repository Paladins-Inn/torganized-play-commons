/*
 * Copyright (c) 2024-2025. Kaiserpfalz EDV-Service, Roland T. Lichti
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

package de.paladinsinn.tp.dcis.domain.users.services;


import com.google.common.eventbus.EventBus;
import de.paladinsinn.tp.dcis.domain.users.events.UserLoginEvent;
import de.paladinsinn.tp.dcis.domain.users.events.UserLogoutEvent;
import de.paladinsinn.tp.dcis.domain.users.model.UserImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests the UserLogEntrySender.
 *
 * <p>The test ist done as integration test. It will send an {@link EventBus#post(Object)} for the user events and checks if it is handled by the spring cloud streaming service.</p>
 *
 * @author klenkes74
 * @since 2025-03-23
 */
@SpringBootTest
@XSlf4j
public class UserLogEntrySenderTest {
  
  @Value("${spring.application.name:Mary}")
  private String application;
  
  @Autowired
  private UserLogEntrySender sut;
  
  
  @Test
  public void shouldSendTheLoginEvent() {
    log.entry("shouldSendTheLoginEvent");

    UserLoginEvent loginEvent = UserLoginEvent.builder()
        .user(createDefaultUser())
        .system(application)
        .build();
    
    sut.send(loginEvent);
  }
  
  @Test
  public void shouldSendTheLogoutEvent() {
    log.entry("shouldSendTheLogoutEvent");
    
    UserLogoutEvent logoutEvent = UserLogoutEvent.builder()
        .user(createDefaultUser())
        .system(application)
        .build();

    sut.send(logoutEvent);
  }
  
  @Test
  public void shouldCompleteShutdownOfSender() {
    log.entry("shouldCompleteShutdownOfSender");
    
    sut.shutdown();
  }
  
  private static UserImpl createDefaultUser() {
    return UserImpl.builder()
        .name("Peter")
        .nameSpace("Paul")
        .build();
  }
  

  @Configuration
  static class TestConfiguration {
    @MockBean
    @Getter(onMethod = @__(@Bean))
    private StreamBridge streamBridge;
    
    @Getter(onMethod = @__(@Bean))
    private UserLogEntrySender sut;
    

    @PostConstruct
    public void init() {
      sut = new UserLogEntrySender(streamBridge);
      sut.init();
    }
    
    @PreDestroy
    public void shutdown() {
      sut.shutdown();
    }
  }
}
