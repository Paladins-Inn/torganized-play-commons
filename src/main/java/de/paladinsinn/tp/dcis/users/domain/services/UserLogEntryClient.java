/*
 * Copyright (c) 2025. Kaiserpfalz EDV-Service, Roland T. Lichti
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

package de.paladinsinn.tp.dcis.users.domain.services;


import com.google.common.eventbus.Subscribe;
import de.paladinsinn.tp.dcis.commons.events.LoggingEventBus;
import de.paladinsinn.tp.dcis.users.domain.events.UserLoginEvent;
import de.paladinsinn.tp.dcis.users.domain.events.UserLogoutEvent;
import io.micrometer.core.annotation.Timed;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import lombok.*;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.stereotype.Service;


/**
 * @author klenkes74
 * @since 29.03.2025
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@XSlf4j
public class UserLogEntryClient {
    private final UserLogEntrySender sender;
    private final UserLoggedInStateRepository userState;
    private final LoggingEventBus bus;
    
    @PostConstruct
    public void init() {
        log.entry(sender, userState, bus);
        
        bus.register(this);
        
        log.exit();
    }
    
    
    @PreDestroy
    public void shutdown() {
        log.entry(sender, userState, bus);
        
        bus.unregister(this);
        
        log.exit();
    }
    
    
    @Timed
    @Subscribe
    public void on(final UserLoginEvent event) {
        log.entry(event);

        if (userState.isLoggedIn(event.getUser())) {
            log.debug("User is already logged in. user={}", event.getUser());
        } else {
            sender.send(event);
            log.debug("User is now logged in. user={}", event.getUser());
        }
        userState.login(event.getUser());
        
        log.exit();
    }
    
    @Timed
    @Subscribe
    public void on(final UserLogoutEvent event) {
        log.entry(event);
        
        if (userState.isLoggedIn(event.getUser())) {
            sender.send(event);
            userState.logout(event.getUser());
            log.debug("User is logged out. user={}", event.getUser());
        } else {
            log.debug("User is not logged in. user={}", event.getUser());
        }
        
        log.exit();
    }
}
