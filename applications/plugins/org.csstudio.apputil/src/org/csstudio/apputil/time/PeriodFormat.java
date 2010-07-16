/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.time;

/** Parser for scan or update periods
 *  @author Kay Kasemir
 */
public class PeriodFormat
{
    /** Identifier for 'day' units */
    private static final String DAYS = "days"; //$NON-NLS-1$

    /** Identifier for 'hour' units */
    private static final String HOURS = "hours"; //$NON-NLS-1$
    
    /** Identifier for 'minute' units */
    private static final String MINUTES = "minutes"; //$NON-NLS-1$
    
    /** Identifier for 'second' units */
    private static final String SECONDS = "seconds"; //$NON-NLS-1$

    /** Seconds per minute */
    private static final double SEC_PER_MIN = 60.0;

    /** Seconds per hour */
    private static final double SEC_PER_HOUR = 60.0*SEC_PER_MIN;

    /** Seconds per day */
    private static final double SEC_PER_DAY = 24.0*SEC_PER_HOUR;
    
    /** Parse seconds from text.
     *  <p>
     *  Text must contain a (double) number,
     *  optionally followed by units "days", "hours", "minutes", "seconds".
     *  Only "m", "min", ... is interpreted as "minutes" as well.
     *  In the absense of units, it defaults to seconds.
     *  
     *  @param text Text to parse
     *  @return seconds
     */
    public static double parseSeconds(String text)
    {
        text = text.trim().toLowerCase();
        int units = findUnits(text, DAYS);
        if (units > 0)
        {
            final double days = Double.parseDouble(text.substring(0, units));
            return days*SEC_PER_DAY; 
        }
        units = findUnits(text, HOURS);
        if (units > 0)
        {
            final double hours = Double.parseDouble(text.substring(0, units));
            return hours*SEC_PER_HOUR; 
        }
        units = findUnits(text, MINUTES);
        if (units > 0)
        {
            final double min = Double.parseDouble(text.substring(0, units));
            return min*SEC_PER_MIN; 
        }
        units = findUnits(text, SECONDS);
        if (units < 0)
            units = text.length();
        return Double.parseDouble(text.substring(0, units));
    }
    
    /** Format seconds as string, using hours or minutes if appropriate.
     *  @param seconds Seconds to format
     *  @return A string that <code>parseSeconds()</code> can handle.
     */
    @SuppressWarnings("nls")
    public static String formatSeconds(double seconds)
    {
        if (seconds >= SEC_PER_DAY)
        {
            final double days = seconds/SEC_PER_DAY;
            return String.format("%.2f days", days);
        }
        if (seconds >= SEC_PER_HOUR)
        {
            final double hours = seconds/SEC_PER_HOUR;
            return String.format("%.2f h", hours);
        }
        if (seconds >= SEC_PER_MIN)
        {
            final double minutes = seconds/SEC_PER_MIN;
            return String.format("%.2f min", minutes);
        }
        return String.format("%.2f sec", seconds);
    }

    /** Locate given "units", "unit", "uni", ... "u" in text.
     *  @param text Text to search
     *  @param units Units to locate
     *  @return Location of units in text, or -1
     */
    private static int findUnits(final String text, final String units)
    {
        final String NUM_OR_SPACE = " 0123456789."; //$NON-NLS-1$

        for (int len = units.length(); len > 0; --len)
        {
            final int pos = text.indexOf(units.substring(0, len));
            if (pos > 0)
            {
                // Just before that position, there should be a space
                // or the number. Don't find the "d" of "days" in "seconds".
                final char prev_char = text.charAt(pos-1);
                if (NUM_OR_SPACE.indexOf(prev_char) >= 0)
                    return pos;
            }
        }
        return -1;
    }
}
