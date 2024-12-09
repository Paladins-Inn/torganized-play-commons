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

package de.paladinsinn.kp.docs.commons.events;


import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import lombok.extern.slf4j.XSlf4j;


/**
 * @author klenkes74
 * @since 24.11.24
 */
@Getter
@XSlf4j
public class TestEventListener {
  private TestEvent testEvent;
  private final TestEvent expectedEvent;

  public TestEventListener(TestEvent expectedEvent) {
    this.expectedEvent = expectedEvent;
  }

  @Subscribe
  public void receive(final TestEvent event) {
    log.entry(event);

    if (expectedEvent.equals(event)) {
      this.testEvent = event;
      log.debug("Received expected event. expected={}, actual={}", expectedEvent, event);
    } else {
      log.debug("This is not the expected event. expected={}, actual={}", expectedEvent, event);
    }

    log.exit();
  }

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public boolean checkEventReceived() {
    return testEvent != null;
  }
}