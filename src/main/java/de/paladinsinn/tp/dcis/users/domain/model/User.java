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

package de.paladinsinn.tp.dcis.users.domain.model;

import java.time.OffsetDateTime;
import java.time.Period;
import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.kaiserpfalzedv.commons.api.resources.HasId;
import de.kaiserpfalzedv.commons.api.resources.HasName;
import de.kaiserpfalzedv.commons.api.resources.HasNameSpace;
import de.kaiserpfalzedv.commons.api.resources.HasTimestamps;

/**
 * The user of the DCIS system.
 */
@JsonDeserialize(as = UserImpl.class)
public interface User extends HasId<UUID>, HasNameSpace, HasName, HasTimestamps {
    /**
     * @return The period the user has been detained for.
     */
    Period getDetainedPeriod();
    
    /**
     * @return The end of the user detainment.
     */
    OffsetDateTime getDetainedTill();
    
    boolean isBanned();
}