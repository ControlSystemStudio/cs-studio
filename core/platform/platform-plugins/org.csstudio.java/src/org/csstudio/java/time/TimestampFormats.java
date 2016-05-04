/*******************************************************************************
 * Copyright (c) 2016 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.java.time;

import java.time.Instant;
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

    /** Time stamp format for 'full' time stamp */
    final public static DateTimeFormatter FULL_FORMAT= DateTimeFormatter.ofPattern(FULL_PATTERN).withZone(zone);

    /** Time stamp format for time stamp down to milliseconds */
    final public static DateTimeFormatter MILLI_FORMAT= DateTimeFormatter.ofPattern(MILLI_PATTERN).withZone(zone);

    /** Time stamp format for time stamp up to seconds, but not nanoseconds */
    final public static DateTimeFormatter SECONDS_FORMAT = DateTimeFormatter.ofPattern(SECONDS_PATTERN).withZone(zone);
}
