package de.paladinsinn.tp.dcis.users.domain.model;

import java.io.Serial;
import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@ToString
@EqualsAndHashCode(of = {"id"})
public class UserLogEntryImpl implements UserLogEntry {
    @Serial
    private static final long serialVersionUID = 1L;
    
    private UUID id;
    private OffsetDateTime created;
    private OffsetDateTime modified;
    private OffsetDateTime deleted;
    
    private User user;

    private String system;
    private String text;
}
