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

import java.time.Duration;
import java.time.OffsetDateTime;
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
  Duration getDetainmentDuration();
  
  /**
   * @return The end of the user detainment.
   */
  OffsetDateTime getDetainedTill();
  
  /**
   * Detains the user for a number of days.
   *
   * @param days The number of days the user is detained within the system.
   */
  void detain(long days);
  
  /**
   * Release the user from detainment.
   */
  void release();
  
  /**
   * Ban the user from the system.
   */
  void ban();
  
  /**
   * Unban the user from the system.
   */
  void unban();
  
  /**
   * delete the user.
   */
  void delete();
  
  /**
   * undelete the user
   */
  void undelete();
  
  /**
   * @return true if the user is banned from the system.
   */
  boolean isBanned();
  
  /**
   * @return true if the user is detained.
   */
  default boolean isDetained() {
    return getDetainedTill() != null;
  }
  
  /**
   * @return true if the user is deleted.
   */
  default boolean isDeleted() {
    return getDeleted() != null;
  }
  
  /**
   * @return true if the user is inactive for any reason.
   */
  default boolean isInactive() {
    return (isDeleted() || isBanned() || isDetained());
  }
}