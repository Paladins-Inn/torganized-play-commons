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

package de.paladinsinn.tp.dcis.domain.users.model;

import java.time.*;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.XSlf4j;

@Jacksonized
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@ToString(of = {"id", "nameSpace", "name", "created", "modified"})
@EqualsAndHashCode(of = {"id"})
@XSlf4j
public class UserImpl implements User {
    private UUID id;
    @Builder.Default
    private OffsetDateTime created = OffsetDateTime.now(Clock.systemUTC());
    private OffsetDateTime modified;
    private OffsetDateTime deleted;
    
    private Duration detainmentDuration;
    private OffsetDateTime detainedTill;
    
    private boolean banned;

    @Builder.Default
    private String nameSpace = "./.";
    private String name;
    
    @Override
    public void detain(long days) {
        log.entry(days);
        
        detainmentDuration = Duration.ofDays(days);
        detainedTill = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(days).toOffsetDateTime();
        
        log.exit(detainedTill);
    }
    
    @Override
    public void release() {
        log.entry();
        
        detainmentDuration = null;
        detainedTill = null;
        
        log.exit();
    }
    
    @Override
    public void ban() {
        log.entry();
        
        this.banned = true;
        
        log.exit();
    }
    
    @Override
    public void unban() {
        log.entry();
        
        this.banned = false;
        
        log.exit();
    }
    
    @Override
    public void delete() {
        log.entry();
        
        this.deleted = OffsetDateTime.now(Clock.systemUTC());
        
        log.exit(deleted);
    }
    
    @Override
    public void undelete() {
        log.entry();
        
        this.deleted = null;
        
        log.exit();
    }
}
