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

import java.util.LinkedList;
import java.util.Optional;
import java.util.UUID;

import de.paladinsinn.tp.dcis.commons.events.LoggingEventBus;
import de.paladinsinn.tp.dcis.domain.users.events.arbitation.UserBannedEvent;
import de.paladinsinn.tp.dcis.domain.users.events.arbitation.UserDetainedEvent;
import de.paladinsinn.tp.dcis.domain.users.events.arbitation.UserReleasedEvent;
import de.paladinsinn.tp.dcis.domain.users.events.state.UserCreatedEvent;
import de.paladinsinn.tp.dcis.domain.users.events.state.UserRemovedEvent;
import de.paladinsinn.tp.dcis.domain.users.model.UserToImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.*;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import de.paladinsinn.tp.dcis.domain.users.model.User;
import de.paladinsinn.tp.dcis.domain.users.model.UserImpl;
import de.paladinsinn.tp.dcis.domain.users.persistence.UserJPA;
import de.paladinsinn.tp.dcis.domain.users.persistence.UserRepository;
import de.paladinsinn.tp.dcis.domain.users.persistence.UserToJpa;

@Service
@RequiredArgsConstructor
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@XSlf4j
public class UserService {
    private final UserRepository userRepository;
    private final LoggingEventBus bus;

    private final UserToImpl toUser;
    private final UserToJpa toUserJPA;

    @Value("${spring.application.name:DCIS")
    @Setter(AccessLevel.PACKAGE)
    private String applicationName;
    
    @Getter
    @Setter
    private Authentication authentication;
    
    @PostConstruct
    public void init() {
        log.entry(applicationName, bus);
        bus.register(this);
        log.exit();
    }
    
    @PreDestroy
    public void destroy() {
        log.entry(applicationName, bus);
        bus.unregister(this);
        log.exit();
    }

    public User createUser(final User player) {
        log.entry(player, authentication);

        User result = userRepository.save(toUserJPA.apply(player));
        
        bus.post(UserCreatedEvent.builder()
                .user(result)
                .system(applicationName)
                .build()
        );

        return log.exit(result);
    }

    public User createUser(final UUID uid, final String nameSpace, final String name) {
        log.entry(uid, nameSpace, name, authentication);
        
        User result = createUser(UserImpl.builder()
                .id(uid)
                .nameSpace(nameSpace)
                .name(name)
                .build()
        );
        
        return log.exit(result);
    }

    public Optional<User> retrieveUser(final UUID uid) {
        log.entry(uid, authentication);

        Optional<UserJPA> result = userRepository.findById(uid);

        log.debug("Loaded player from database. uid={}, player={}", uid, result.isPresent() ? result.get() : "***none***");

        return Optional.of(log.exit(result.orElse(null)));
    }
    
    public Optional<User> retrieveUser(final String nameSpace, final String name) {
        log.entry(nameSpace, name, authentication);
        
        Optional<UserJPA> result = userRepository.findByNameSpaceAndName(nameSpace, name);
        
        log.debug("Loaded player from database. nameSpace={}, name={}, player={}", nameSpace, name, result.isPresent() ? result.get() : "***none***");
        
        return Optional.of(log.exit(result.orElse(null)));
    }

    public Page<User> retrieveUsers(final String nameSpace, final Pageable pageable) {
        log.entry(nameSpace, pageable, authentication);

        Page<UserJPA> data = userRepository.findByNameSpace(nameSpace, pageable);
        Page<User> result = new PageImpl<>(new LinkedList<>(data.stream().map(toUser).toList()), pageable, data.getTotalElements());

        log.debug("Loaded users for namespace. nameSpace='{}', page={}/{}, size={}", nameSpace, 
                result.getPageable().getPageNumber(), result.getTotalPages(), result.getTotalElements());

        return log.exit(result);
    }
    
    public User updateUser(final UUID uid, final User user) {
        log.entry(uid, user, authentication);
        
        log.info("Updating user not implemented yet. uid={}, user={}, authentication={}", uid, user, authentication);
        
        Optional<User> data = retrieveUser(uid);
        
        data.ifPresent(value -> bus.post(UserRemovedEvent.builder().user(value).build()));
        
        return log.exit(data.orElse(user));
    }
    
    public void deleteUser(final UUID uid) {
        log.entry(uid, authentication);
        
        log.info("Deleting user not implemented yet. uid={}, authentication={}", uid, authentication);
        
        Optional<User> data = retrieveUser(uid);
        
        data.ifPresent(value -> bus.post(UserBannedEvent.builder().user(value).build()));
        
        log.exit();
    }
    
    public Optional<User> detainUser(final UUID uid, final long ttl) {
        log.entry(uid, authentication);
        
        Optional<UserJPA> data = userRepository.findById(uid);
        
        if (data.isEmpty()) {
            log.warn("User can't be detained. User not found. uid={}", uid);
            
            return log.exit(Optional.empty());
        }

        data.get().detain(ttl);
        
        UserJPA result = userRepository.save(data.get());
        bus.post(UserDetainedEvent.builder().user(result).build());

        return log.exit(Optional.of(result));
    }
    
    public Optional<User> releaseUser(final UUID uid) {
        log.entry(uid, authentication);
        
        Optional<UserJPA> data = userRepository.findById(uid);
        if (data.isEmpty()) {
            log.warn("User can't be released. User not found. uid={}", uid);
            return log.exit(Optional.empty());
        }
        
        data.get().release();
        
        UserJPA result = userRepository.save(data.get());
        bus.post(UserReleasedEvent.builder().user(result).build());
        
        return log.exit(Optional.of(result));
    }
}
