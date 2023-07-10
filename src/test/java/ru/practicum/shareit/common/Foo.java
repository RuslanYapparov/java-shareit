package ru.practicum.shareit.common;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.net.URI;
import java.time.LocalDateTime;

public class Foo {
    // Объекты всех слоев, поля которых имеют типы данных, применяемых в приложении
    public static final Domain DOMAIN = new Domain(0, 0.0F, ShareItConstants.NOT_ASSIGNED,
            ShareItConstants.DEFAULT_URI, ShareItConstants.DEFAULT_LOCAL_DATE_TIME);
    public static final Entity ENTITY = new Entity(0, ShareItConstants.NOT_ASSIGNED,
            ShareItConstants.DEFAULT_URI, ShareItConstants.DEFAULT_LOCAL_DATE_TIME);
    public static final RestCommand REST_COMMAND = new RestCommand(0, ShareItConstants.NOT_ASSIGNED,
                    ShareItConstants.DEFAULT_URI, ShareItConstants.DEFAULT_LOCAL_DATE_TIME);
    public static final RestView REST_VIEW = new RestView(0, ShareItConstants.NOT_ASSIGNED,
                    ShareItConstants.DEFAULT_URI, ShareItConstants.DEFAULT_LOCAL_DATE_TIME);

    @Value
    @Builder(toBuilder = true)
    @EqualsAndHashCode
    static class Domain {
        long fooId;
        float fooFloat;
        String fooString;
        URI fooUri;
        LocalDateTime fooDateTime;

    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    static class Entity extends UpdatableUserDependedEntity {
        long fooId;
        String fooString;
        URI fooUri;
        LocalDateTime fooDateTime;

        public long getId() {
            return fooId;
        }

    }

    @Value
    static class RestCommand {
        long fooId;
        String fooString;
        URI fooUri;
        LocalDateTime fooDateTime;

    }

    @Value
    @EqualsAndHashCode
    static class RestView {
        long fooId;
        String fooString;
        URI fooUri;
        LocalDateTime fooDateTime;

    }

}