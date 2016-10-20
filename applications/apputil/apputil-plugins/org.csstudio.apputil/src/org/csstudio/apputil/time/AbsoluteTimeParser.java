/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/** Parse an absolute date/time string.
 *
 *  @see #parse(Calendar, String)
 *
 *  @author Sergei Chevtsov developed the original code for the
 *          Java Archive Viewer, from which this code heavily borrows.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AbsoluteTimeParser
{
    /** The accepted date formats for absolute times. */
    private static final DateFormat[] parsers = new SimpleDateFormat[]
    {   // Most complete version first
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"),
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
        new SimpleDateFormat("yyyy-MM-dd HH:mm"),
        new SimpleDateFormat("yyyy-MM-dd HH"),
        new SimpleDateFormat("yyyy-MM-dd")
    };

    /** Like parse(), using a base calendar of 'now', 'current time zone'.
     *  @see #parse(Calendar, String)
     *  @return Calendar initialized from parsed text.
     */
    public static Calendar parse(String text) throws Exception
    {
        Calendar cal = Calendar.getInstance();
        return parse(cal, text);
    }

    /** Adjust given calendar to the date and time parsed from the text.
     *  <p>
     *  The date/time text should follow the format
     *  <pre>
     *  YYYY/MM/DD hh:mm:ss.sss
     *  </pre>
     *  The milliseconds (sss), seconds(ss), minutes(mm), hours(hh)
     *  might be left off, and will then default to zero.
     *  <p>
     *  When omitting the year, the year from the passed-in calendar is used.
     *  When omitting the whole date (YYYY/MM/DD), the values from the passed-in
     *  calendar are used.
     *  It is not possible to provide <i>only</i> the month <i>without</i>
     *  the day or vice vesa.
     *  <p>
     *  An empty text leaves the provided calendar unchanged.
     *  <p>
     *  All other cases result in an exception.
     *
     *  @param cal Base calendar, defines the time zone as well as
     *             the year, in case the text doesn't include a year.
     *  @param text The text to parse.
     *  @return Adjusted Calendar.
     *  @exception On error.
     */
    public static Calendar parse(Calendar cal, String text) throws Exception
    {
        String cooked = text.trim().toLowerCase();
        // Empty string? Pass cal as is back, since we didn't change it?
        if (cooked.length() < 1)
            return cal;
        final Calendar result = Calendar.getInstance();

        // Provide missing year from given cal
        int datesep = cooked.indexOf('-');
        if (datesep < 0) // No date at all provided? Use the one from cal.
            cooked = String.format("%04d-%02d-%02d %s",
                                   cal.get(Calendar.YEAR),
                                   cal.get(Calendar.MONTH) + 1,
                                   cal.get(Calendar.DAY_OF_MONTH),
                                   cooked);
        else
        {   // Are there two date separators?
            datesep = cooked.indexOf('-', datesep + 1);
            // If not, assume that we have MM-DD, and add the YYYY.
            if (datesep < 0)
                cooked = String.format("%04d-%s",
                                       cal.get(Calendar.YEAR), cooked);
        }
        // In case the text includes the ITimestamp up to nanoseconds:
        // 2007/06/01 14:00:24.156959772
        // Sorry, not handled by Calendar.
        // Chop down to millisecs
        if (cooked.length() == 29  &&  cooked.charAt(19) == '.')
            cooked = cooked.substring(0, 23);
        // Try the parsers
        for (DateFormat parser : parsers)
        {
            try
            {   // DateFormat returns Date, but pretty much all of Date
                // is deprecated, which is why we use Calendar.
                long millis = parser.parse(cooked).getTime();
                result.setTimeInMillis(millis);
                return result;
            }
            catch (Exception e)
            {   // Ignore, try the next one
            }
        }
        // No parser parsed the string?
        throw new Exception("Cannot parse date and time from '" + text + "'");
    }

    /** Format given calendar value into something that this parser would handle.
     *  @return Date and time string.
     */
    public static String format(Calendar cal)
    {
        return parsers[0].format(cal.getTime());
    }
}
