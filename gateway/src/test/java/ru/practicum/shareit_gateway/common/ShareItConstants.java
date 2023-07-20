package ru.practicum.shareit_gateway.common;

import java.net.URI;
import java.time.LocalDateTime;

public class ShareItConstants {
    public static final String NOT_ASSIGNED = "N/A";
    public static final URI DEFAULT_URI = URI.create(NOT_ASSIGNED);
    public static final LocalDateTime DEFAULT_LOCAL_DATE_TIME =
            LocalDateTime.of(2023, 7, 7, 7, 7, 7);

}