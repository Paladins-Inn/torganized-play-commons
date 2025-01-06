package de.paladinsinn.tp.dcis.users.domain.model;

import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.kaiserpfalzedv.commons.api.resources.HasId;
import de.kaiserpfalzedv.commons.api.resources.HasTimestamps;

@JsonDeserialize(as = UserLogEntryImpl.class)
public interface UserLogEntry extends HasId<UUID>, HasTimestamps, Serializable {
    User getUser();

    String getSystem();
    String getText();
}
