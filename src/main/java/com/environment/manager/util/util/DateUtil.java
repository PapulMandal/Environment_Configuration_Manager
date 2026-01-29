package com.environment.manager.util.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for date and time operations.
 */
public final class DateUtil {

    private DateUtil() {
        // Utility class - prevent instantiation
    }

    // Formatters
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern(Constants.TIME_FORMAT);
    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT);
    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern(Constants.TIMESTAMP_FORMAT);
    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Gets current date as string in default format.
     */
    public static String getCurrentDate() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    /**
     * Gets current time as string in default format.
     */
    public static String getCurrentTime() {
        return LocalTime.now().format(TIME_FORMATTER);
    }

    /**
     * Gets current date-time as string in default format.
     */
    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DATETIME_FORMATTER);
    }

    /**
     * Gets current timestamp as string.
     */
    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(TIMESTAMP_FORMATTER);
    }

    /**
     * Gets current date-time in ISO format.
     */
    public static String getCurrentISO() {
        return LocalDateTime.now().format(ISO_FORMATTER);
    }

    /**
     * Gets current epoch milliseconds.
     */
    public static long getCurrentEpochMillis() {
        return System.currentTimeMillis();
    }

    /**
     * Gets current epoch seconds.
     */
    public static long getCurrentEpochSeconds() {
        return Instant.now().getEpochSecond();
    }

    /**
     * Formats a LocalDateTime to string.
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DATETIME_FORMATTER);
    }

    /**
     * Formats a LocalDate to string.
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DATE_FORMATTER);
    }

    /**
     * Formats a LocalTime to string.
     */
    public static String formatTime(LocalTime time) {
        if (time == null) {
            return null;
        }
        return time.format(TIME_FORMATTER);
    }

    /**
     * Parses a string to LocalDateTime.
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }

        try {
            // Try default format first
            return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
        } catch (DateTimeParseException e1) {
            try {
                // Try ISO format
                return LocalDateTime.parse(dateTimeStr, ISO_FORMATTER);
            } catch (DateTimeParseException e2) {
                throw new IllegalArgumentException("Invalid date-time format: " + dateTimeStr, e2);
            }
        }
    }

    /**
     * Parses a string to LocalDate.
     */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + dateStr, e);
        }
    }

    /**
     * Converts Date to LocalDateTime.
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Converts LocalDateTime to Date.
     */
    public static Date toDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Gets the duration between two date-times in human-readable format.
     */
    public static String getDurationReadable(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return "N/A";
        }

        Duration duration = Duration.between(start, end);
        long seconds = duration.getSeconds();

        if (seconds < 0) {
            return "Invalid duration (end before start)";
        }

        if (seconds < 60) {
            return seconds + " seconds";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            long remainingSeconds = seconds % 60;
            return minutes + " minutes " + remainingSeconds + " seconds";
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            return hours + " hours " + minutes + " minutes";
        } else {
            long days = seconds / 86400;
            long hours = (seconds % 86400) / 3600;
            return days + " days " + hours + " hours";
        }
    }

    /**
     * Gets the duration in milliseconds.
     */
    public static long getDurationMillis(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return Duration.between(start, end).toMillis();
    }

    /**
     * Gets the duration in seconds.
     */
    public static long getDurationSeconds(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return Duration.between(start, end).getSeconds();
    }

    /**
     * Checks if a date-time is in the past.
     */
    public static boolean isPast(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        return dateTime.isBefore(LocalDateTime.now());
    }

    /**
     * Checks if a date-time is in the future.
     */
    public static boolean isFuture(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        return dateTime.isAfter(LocalDateTime.now());
    }

    /**
     * Adds days to a date-time.
     */
    public static LocalDateTime addDays(LocalDateTime dateTime, int days) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.plusDays(days);
    }

    /**
     * Subtracts days from a date-time.
     */
    public static LocalDateTime subtractDays(LocalDateTime dateTime, int days) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.minusDays(days);
    }

    /**
     * Adds hours to a date-time.
     */
    public static LocalDateTime addHours(LocalDateTime dateTime, int hours) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.plusHours(hours);
    }

    /**
     * Subtracts hours from a date-time.
     */
    public static LocalDateTime subtractHours(LocalDateTime dateTime, int hours) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.minusHours(hours);
    }

    /**
     * Gets the start of the day for a date-time.
     */
    public static LocalDateTime startOfDay(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.toLocalDate().atStartOfDay();
    }

    /**
     * Gets the end of the day for a date-time.
     */
    public static LocalDateTime endOfDay(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.toLocalDate().atTime(23, 59, 59, 999999999);
    }

    /**
     * Gets the age in days from a date-time.
     */
    public static long getAgeInDays(LocalDateTime dateTime) {
        if (dateTime == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(dateTime, LocalDateTime.now());
    }

    /**
     * Gets the age in hours from a date-time.
     */
    public static long getAgeInHours(LocalDateTime dateTime) {
        if (dateTime == null) {
            return 0;
        }
        return ChronoUnit.HOURS.between(dateTime, LocalDateTime.now());
    }

    /**
     * Gets the age in minutes from a date-time.
     */
    public static long getAgeInMinutes(LocalDateTime dateTime) {
        if (dateTime == null) {
            return 0;
        }
        return ChronoUnit.MINUTES.between(dateTime, LocalDateTime.now());
    }

    /**
     * Formats a duration in milliseconds to human-readable format.
     */
    public static String formatDuration(long millis) {
        if (millis < 0) {
            return "Invalid duration";
        }

        if (millis < 1000) {
            return millis + " ms";
        }

        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long days = TimeUnit.MILLISECONDS.toDays(millis);

        if (days > 0) {
            return String.format("%d days %02d:%02d:%02d",
                    days, hours % 24, minutes % 60, seconds % 60);
        } else if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
        } else if (minutes > 0) {
            return String.format("%02d:%02d", minutes, seconds % 60);
        } else {
            return String.format("%d.%03d seconds", seconds, millis % 1000);
        }
    }

    /**
     * Gets the time zone offset.
     */
    public static String getTimeZoneOffset() {
        ZoneId zone = ZoneId.systemDefault();
        ZoneOffset offset = LocalDateTime.now().atZone(zone).getOffset();
        return offset.getId();
    }

    /**
     * Gets the current time in a specific time zone.
     */
    public static LocalDateTime getCurrentDateTimeInZone(String zoneId) {
        try {
            ZoneId zone = ZoneId.of(zoneId);
            return LocalDateTime.now(zone);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }

    /**
     * Converts date-time between time zones.
     */
    public static LocalDateTime convertTimeZone(LocalDateTime dateTime,
                                                String fromZone, String toZone) {
        if (dateTime == null) {
            return null;
        }

        try {
            ZoneId fromZoneId = ZoneId.of(fromZone);
            ZoneId toZoneId = ZoneId.of(toZone);

            ZonedDateTime fromZoned = dateTime.atZone(fromZoneId);
            ZonedDateTime toZoned = fromZoned.withZoneSameInstant(toZoneId);

            return toZoned.toLocalDateTime();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid time zone conversion", e);
        }
    }

    /**
     * Checks if two date-times are on the same day.
     */
    public static boolean isSameDay(LocalDateTime date1, LocalDateTime date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.toLocalDate().equals(date2.toLocalDate());
    }

    /**
     * Gets the day of week for a date-time.
     */
    public static String getDayOfWeek(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.getDayOfWeek().toString();
    }

    /**
     * Gets a formatted string for relative time (e.g., "2 hours ago").
     */
    public static String getRelativeTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "Never";
        }

        Duration duration = Duration.between(dateTime, LocalDateTime.now());
        long seconds = duration.getSeconds();

        if (seconds < 0) {
            return "In the future";
        } else if (seconds < 60) {
            return seconds + " seconds ago";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else {
            long days = seconds / 86400;
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        }
    }
}