package ru.practicum.shareit.common;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ShareItConstants {
    public static final String NOT_ASSIGNED = "N/A";
    public static final URI DEFAULT_URI = URI.create(NOT_ASSIGNED);
    public static final LocalDate DEFAULT_DATE = LocalDate.of(2023, 1, 1);
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;

}