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

/** Time stamp formats
 *
 *  <p>Java 8 introduced {@link Instant} which handles time stamps
 *  with up to nanosecond detail, obsoleting custom classes
 *  for wrapping control system time stamps.
 *
 *  <p>The {@link DateTimeFormatter} is immutable and thread-safe,
 *  finally allowing re-use of common time stamp formatters.
 *
 *  <p>The formatters defined here are suggested for CS-Studio time stamps
 *  because they can show the full detail of control system time stamps in a portable way,
 *  independent from locale.
 *
 *  @author Kay Kasemir
 */
public class TimestampFormats
{
    final private static ZoneId zone = ZoneId.systemDefault();
    final private static String FULL_PATTERN = "yyyy-MM-dd HH:mm:ss.nnnnnnnnn";
    final private static String MILLI_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    final private static String SECONDS_PATTERN = "yyyy-MM-dd HH:mm:ss";
    final private static String DATE_PATTERN = "yyyy-MM-dd";
    final private static String TIME_PATTERN = "HH:mm:ss";

    /** Time stamp format for 'full' time stamp */
    final public static DateTimeFormatter FULL_FORMAT= DateTimeFormatter.ofPattern(FULL_PATTERN).withZone(zone);

    /** Time stamp format for time stamp down to milliseconds */
    final public static DateTimeFormatter MILLI_FORMAT= DateTimeFormatter.ofPattern(MILLI_PATTERN).withZone(zone);

    /** Time stamp format for time stamp up to seconds, but not nanoseconds */
    final public static DateTimeFormatter SECONDS_FORMAT = DateTimeFormatter.ofPattern(SECONDS_PATTERN).withZone(zone);

    /** Time stamp format for time stamp up to seconds, but no date */
    final public static DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern(TIME_PATTERN).withZone(zone);

    /** Time stamp format for date, no time */
    final public static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(DATE_PATTERN).withZone(zone);

    // Internal
    final private static DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MM-dd HH:mm").withZone(zone);

    /** Format date and time in a 'compact' way.
     *
     *  <p>If the time stamp falls within today, only hours .. seconds are displayed.
     *  For a time stamp on a different day from today, the date and time without seconds shown.
     *  For a time in different year, only the date is shown.
     *
     *  @param timestamp {@link Instant}
     *  @return Date and time of the data in preferred text format
     */
    public static String formatCompactDateTime(final Instant timestamp)
    {
       if (timestamp == null)
           return "?";

       final LocalDateTime now = LocalDateTime.now();
       final LocalDateTime local = LocalDateTime.ofInstant(timestamp, zone);

       if (now.getYear() == local.getYear())
       {
           // Same day, show time down to HH:mm:ss
           if (now.getDayOfYear() == local.getDayOfYear())
               return TIME_FORMAT.format(timestamp);
           else
               // Different day, same year, show month, day, down to minutes
               return MONTH_FORMAT.format(timestamp);
       }
       else
           // Different year, show yyyy-MM-dd";
           return DATE_FORMAT.format(timestamp);
   }
}
