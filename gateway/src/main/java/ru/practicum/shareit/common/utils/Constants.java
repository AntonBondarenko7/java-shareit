package ru.practicum.shareit.common.utils;

import lombok.experimental.UtilityClass;

import java.time.format.DateTimeFormatter;

@UtilityClass
public class Constants {

    public static final String HEADER_USER_ID = "X-Sharer-User-Id";
    public static final String PATTERN_FOR_DATETIME = "yyyy-MM-dd'T'HH:mm:ss.SS";
    public static final DateTimeFormatter FORMATTER_FOR_DATETIME = DateTimeFormatter.ofPattern(PATTERN_FOR_DATETIME);

    public static final String PATTERN_FOR_BOOKING = "yyyy-MM-dd'T'HH:mm:ss";
    public static final DateTimeFormatter FORMATTER_FOR_BOOKING = DateTimeFormatter.ofPattern(PATTERN_FOR_BOOKING);

}
