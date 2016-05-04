/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.data;

import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/** Helper for formatting {@link ScanSample}s
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanSampleFormatter
{
    final private static ZoneId zone = ZoneId.systemDefault();

    /** Suggested time format */
    final public static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    final private static DateTimeFormatter date_format = DateTimeFormatter.ofPattern(DATE_FORMAT).withZone(zone);

    final public static String TIME_FORMAT = "HH:mm:ss";
    final private static DateTimeFormatter time_format = DateTimeFormatter.ofPattern(TIME_FORMAT).withZone(zone);


    /** Extract double from sample
     *  @param sample ScanSample
     *  @return Double or NaN if there is no number to extract
     */
    public static double asDouble(final ScanSample sample)
    {
        if (sample instanceof NumberScanSample)
            return ((NumberScanSample) sample).getNumber(0).doubleValue();
        return Double.NaN;
    }

    /** Extract String from sample
     *  @param sample ScanSample
     *  @return String for scan sample
     */
    public static String asString(final ScanSample sample)
    {
        if (sample == null)
            return "";
        final Object[] values = sample.getValues();
        if (values == null)
            return "";
        if (values.length == 1)
            return values[0].toString();
        return Arrays.toString(values);
    }

    /** Format {@link Date} to the fullest detail: Date, time, seconds, ...
     *  @param timestamp {@link Date}
     *  @return Date in preferred text format
     */
    public static String format(final Date timestamp)
    {
        if (timestamp == null)
            return "?";
        return date_format.format(timestamp.toInstant());
    }

    /** Parse a time stamp
     *  @param timestamp Time stamp as returned by <code>format()</code>
     *  @return {@link Date}
     *  @throws ParseException on error
     *  @see {@link #format(Date)}
     */
    public static Date parseTimestamp(final String timestamp) throws ParseException
    {
        return Date.from(Instant.from(date_format.parse(timestamp)));
    }

    /** Format only the time (HH:MM:SS) of a {@link Date}
     *  @param timestamp {@link Date}
     *  @return Time of the data in preferred text format
     */
    public static String formatTime(final Date timestamp)
    {
        if (timestamp == null)
            return "?";
        return time_format.format(timestamp.toInstant());
    }

    /** Format date and time in a 'compact' way.
     *
     *  <p>If the time stamp falls within today, only hours .. seconds are displayed.
     *  For a time stamp on a different day from today, the date and time without seconds shown.
     *  @param timestamp {@link Date}
     *  @return Date and time of the data in preferred text format
     */
    public static String formatCompactDateTime(final Date timestamp)
    {
        if (timestamp == null)
            return "?";

        final Calendar cal = Calendar.getInstance();
        final int year = cal.get(Calendar.YEAR);
        final int day = cal.get(Calendar.DAY_OF_YEAR);

        cal.setTime(timestamp);
        if (year == cal.get(Calendar.YEAR)  &&
            day  == cal.get(Calendar.DAY_OF_YEAR))
        {   // Same day, show time down to HH:mm:ss
            return String.format("%02d:%02d:%02d",
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    cal.get(Calendar.SECOND));
        }
        else if (year == cal.get(Calendar.YEAR))
        {   // Different day, same year, show MM-dd HH:mm";
            return String.format("%02d-%02d %02d:%02d",
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE));
        }
        else
        {    // Different year, show yyyy-MM-dd";
            return String.format("%04d-%02d-%02d",
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH));
        }
    }
}
