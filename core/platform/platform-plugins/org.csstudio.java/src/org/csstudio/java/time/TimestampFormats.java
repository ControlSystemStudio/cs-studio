/*******************************************************************************
 * Copyright (c) 2016 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.java.time;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * Time stamp formats
 *
 * <p>
 * Java 8 introduced {@link Instant} which handles time stamps with up to nanosecond detail, obsoleting custom classes for wrapping control system
 * time stamps.
 *
 * <p>
 * The {@link DateTimeFormatter} is immutable and thread-safe, finally allowing re-use of common time stamp formatters.
 *
 * <p>
 * The formatters defined here are suggested for CS-Studio time stamps because they can show the full detail of control system time stamps in a
 * portable way, independent from locale.
 *
 * @author Kay Kasemir
 *
 * <p>
 * According to settings in plugin_customization.ini or preferences.ini, the time-stamp format pattern changes. Formatting pattern must use
 * {@link DateTimeFormatter} formating patterns.
 *
 * @author Borut Terpinc - borut.terpinc@cosylab.com
 *
 */
public class TimestampFormats {

    private static enum Format {
        FULL, MILLI, SECONDS, TIME, DATE, DATESHORT, DATETIME, DATETIMESHORT, MONTH
    }

    final private static ZoneId zone = ZoneId.systemDefault();
    final private static IEclipsePreferences preferenceNode = DefaultScope.INSTANCE.getNode("org.csstudio.java");

    /** Time stamp format for 'full' time stamp */
    final public static DateTimeFormatter FULL_FORMAT = createFormatter(Format.FULL).withZone(zone);

    /** Time stamp format for time stamp down to milliseconds */
    final public static DateTimeFormatter MILLI_FORMAT = createFormatter(Format.MILLI).withZone(zone);

    /** Time stamp format for time stamp up to seconds, but not nanoseconds */
    final public static DateTimeFormatter SECONDS_FORMAT = createFormatter(Format.SECONDS).withZone(zone);

    /** Time stamp format for time date and time, no seconds */
    final public static DateTimeFormatter DATETIME_FORMAT = createFormatter(Format.DATETIME).withZone(zone);

    /** Time stamp format for time date and time - short format */
    final public static DateTimeFormatter DATETIMESHORT_FORMAT = createFormatter(Format.DATETIMESHORT).withZone(zone);

    /** Time stamp format for time stamp up to seconds, but no date */
    final public static DateTimeFormatter TIME_FORMAT = createFormatter(Format.TIME).withZone(zone);

    /** Time stamp format for date, no time */
    final public static DateTimeFormatter DATE_FORMAT = createFormatter(Format.DATE).withZone(zone);

    /** Time stamp format for short date, no time */
    final public static DateTimeFormatter DATESHORT_FORMAT = createFormatter(Format.DATESHORT).withZone(zone);

    // Internal
    final private static DateTimeFormatter MONTH_FORMAT = createFormatter(Format.MONTH).withZone(zone);

    /**
     * Format date and time in a 'compact' way.
     *
     * <p>
     * If the time stamp falls within today, only hours .. seconds are displayed. For a time stamp on a different day from today, the date and time
     * without seconds shown. For a time in different year, only the date is shown.
     *
     * @param timestamp
     *            {@link Instant}
     * @return Date and time of the data in preferred text format
     */
    public static String formatCompactDateTime(final Instant timestamp) {

        if (timestamp == null)
            return "?";

        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime local = LocalDateTime.ofInstant(timestamp, zone);

        if (now.getYear() == local.getYear()) {
            // Same day, show time down to HH:mm:ss
            if (now.getDayOfYear() == local.getDayOfYear())
                return TIME_FORMAT.format(timestamp);
            else
                // Different day, same year, show month, day, down to minutes
                return MONTH_FORMAT.format(timestamp);
        } else
            // Different year, show yyyy-MM-dd";
            return DATE_FORMAT.format(timestamp);
    }

    /***
     * Create {@link DateTimeFormatter} for chosen format. If there is custom formatter specified, use the formatter from preferences.
     *
     * @param format
     *            {@link Format} that we want to return
     * @return instance of DateTimeFormatter in desired pattern
     */

    private static DateTimeFormatter createFormatter(Format format) {

        final String DATE_PATTERN = "yyyy-MM-dd";
        final String DATE_SHORT_PATTERN = "yy-MM-dd";
        final String TIME_PATTERN = "HH:mm:ss";
        final String FULL_PATTERN = "yyyy-MM-dd HH:mm:ss.nnnnnnnnn";
        final String MILLI_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
        final String SECONDS_PATTERN = "yyyy-MM-dd HH:mm:ss";
        final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";
        final String DATE_TIME_SHORT_PATTERN = "yy-MM-dd HH:mm";
        final String MONTH_PATTERN = "MM-dd HH:mm";

        switch (format) {
        case DATE:
            return getPatternFromPreference("custom_date_formatter_pattern", DATE_PATTERN);
        case DATESHORT:
            return getPatternFromPreference("#custom_short_date_formatter_pattern", DATE_SHORT_PATTERN);
        case MONTH:
            return getPatternFromPreference("custom_time_formatter_pattern", MONTH_PATTERN);
        case TIME:
            return getPatternFromPreference("custom_time_formatter_pattern", TIME_PATTERN);
        case DATETIME:
            return getPatternFromPreference("custom_datetime_formatter_pattern", DATE_TIME_PATTERN);
        case DATETIMESHORT:
            return getPatternFromPreference("custom_short_datetime_formatter_pattern", DATE_TIME_SHORT_PATTERN);
        case FULL:
            return getPatternFromPreference("custom_full_formatter_pattern", FULL_PATTERN);
        case MILLI:
            return getPatternFromPreference("custom_milli_formatter_pattern", MILLI_PATTERN);
        case SECONDS:
            return getPatternFromPreference("custom_seconds_formatter_pattern", SECONDS_PATTERN);
        }
        return null;
    }

    private static DateTimeFormatter getPatternFromPreference(String formatPatternSetting, String pattern) {
        if (preferenceNode == null) {
            return DateTimeFormatter.ofPattern(pattern);
        } else {
            return DateTimeFormatter.ofPattern(preferenceNode.get(formatPatternSetting, pattern));
        }
    }
}
