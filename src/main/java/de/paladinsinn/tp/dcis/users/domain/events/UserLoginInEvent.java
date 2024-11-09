/*
 * Copyright (c) 2024 Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.paladinsinn.tp.dcis.users.domain.events;



import java.util.EventObject;
import java.util.UUID;

import de.paladinsinn.tp.dcis.users.domain.model.User;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;


/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2024-11-05
 */
@Jacksonized
@Builder(toBuilder = true)
@Getter
@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Slf4j
public class UserLoginInEvent extends EventObject {
  @Default
  @Include
  @lombok.ToString.Include
  private UUID id = UUID.randomUUID();

  @lombok.ToString.Include
  private User user;

  public UserLoginInEvent(final Object source, final UUID id, final User user) {
    super(source);

    this.id = id;
    this.user = user;
  }

  public UserLoginInEvent(final Object source, final User user) {
    super(source);

    this.user = user;
  }
}
