/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.time;

import java.util.Calendar;

/** Pieces of a relative time specification.
 *  <p>
 *  No, this is not special relativity.
 *  This is simply about relative date and time offsets
 *  like "6 hours before".
 *  <p>
 *  The relative time can be applied to a given {@link Calendar} instance,
 *  and for example substract 6 from the calendar's hours.
 *  <p>
 *  The time gets somewhat normalized: 60 seconds turn into one minute etc.
 *  
 *  @author Kay Kasemir
 */
public class RelativeTime implements Cloneable
{
    /** Constant to define 'now', i.e. the current wallclock date and time. */
    public static final String NOW = "now"; //$NON-NLS-1$
    
    /** String identifier for years */
    public static final String YEAR_TOKEN = "years"; //$NON-NLS-1$

    /** String identifier for months */
    public static final String MONTH_TOKEN = "Months"; //$NON-NLS-1$

    /** String identifier for days */
    public static final String DAY_TOKEN = "days"; //$NON-NLS-1$

    /** String identifier for hours */
    public static final String HOUR_TOKEN = "hours"; //$NON-NLS-1$

    /** String identifier for minutes */
    public static final String MINUTE_TOKEN = "minutes"; //$NON-NLS-1$

    /** String identifier for seconds */
    public static final String SECOND_TOKEN = "seconds"; //$NON-NLS-1$

    /** The pieces of relative time. */
    private int rel_time[];
    
    /** Identifier of the relative years in get() or set(). */
    public static final int YEARS = 0;
    
    /** Identifier of the relative months in get() or set(). */
    public static final int MONTHS = 1;
    
    /** Identifier of the relative days in get() or set(). */
    public static final int DAYS = 2;
    
    /** Identifier of the relative hours in get() or set(). */
    public static final int HOURS = 3;
    
    /** Identifier of the relative minutes in get() or set(). */
    public static final int MINUTES = 4;
    
    /** Identifier of the relative seconds in get() or set(). */
    public static final int SECONDS = 5;

    /** Identifier of the relative seconds in get() or set(). */
    public static final int MILLISECONDS = 6;

    /** Tokens that mark a relative date/time piece.
     *  <p>
     *  The original implementation of the parser only allowed characters,
     *  like 'M' to indicate a month.
     *  This implementation allows both upper- and lowercase versions
     *  of the full "month" or shortened versions like "mon",
     *  but when only a single character is used for month resp. minutes,
     *  its case has to match Sergei's orignal specification,
     *  which explains the specific choice of upper and lower case in here.
     */
    static final String tokens[] = new String[]
    {
        YEAR_TOKEN,
        MONTH_TOKEN,
        DAY_TOKEN,
        HOUR_TOKEN,
        MINUTE_TOKEN,
        SECOND_TOKEN
    };

    /** Construct new relative time information. */
    public RelativeTime()
    {
        rel_time = new int[7];
    }

    /** Construct relative time information from the given data.
     *  @param ymdhms Array with years, months, days, hours, minutes, seconds,
     *                and maybe milliseconds
     */
    public RelativeTime(int ymdhms[])
    {
        rel_time = new int[7];
        int i=0;
        // Copy given pieces
        while (i < ymdhms.length)
        {
            rel_time[i] = ymdhms[i];
            ++i;
        }
        // Zero the rest
        while (i < rel_time.length)
        {
            rel_time[i] = 0;
            ++i;
        }
        normalize();
    }

    /** Construct relative time information from the given seconds.
     *  @param seconds Relative seconds, might amount to minutes, days, etc.
     */
    public RelativeTime(double seconds)
    {
        rel_time = new int[] { 0, 0, 0, 0, 0, (int)seconds, 0};
        double frac_seconds = seconds - rel_time[SECONDS];
        rel_time[MILLISECONDS] += frac_seconds * 1000; // 1000 millis per sec
        normalize();
    }
    
    /** Construct relative time information from the given data.
     *  <p>
     *  Some attempts are made to normalize fractional pieces.
     *  For example, x.5 days are turned into x days, 12 hours.
     *  
     *  @param ymdhms Array with years, months, days, hours, minutes, seconds
     */
    public RelativeTime(double ymdhms[])
    {
        rel_time = new int[7];
        // Copy given integer portions over
        int i=0;
        while (i < ymdhms.length)
        {
            rel_time[i] = (int)ymdhms[i];
            ++i;
        }
        // Zero the rest
        while (i < rel_time.length)
        {
            rel_time[i] = 0;
            ++i;
        }
        // Handle fractional parts
        double frac_years = ymdhms[YEARS] - rel_time[YEARS];
        double frac_month = ymdhms[MONTHS] - rel_time[MONTHS];
        double frac_days = ymdhms[DAYS] - rel_time[DAYS];
        double frac_hours = ymdhms[HOURS] - rel_time[HOURS];
        double frac_minutes = ymdhms[MINUTES] - rel_time[MINUTES];
        double frac_seconds = ymdhms[SECONDS] - rel_time[SECONDS];
        rel_time[MONTHS] += frac_years*12; // 12 month to a year
        rel_time[DAYS] += frac_month*31; // Assume 31 days to a month...
        rel_time[HOURS] += frac_days*24; // 24 hours in a day
        rel_time[MINUTES] += frac_hours*60; // 60 minutes in an hour
        rel_time[SECONDS] += frac_minutes*60; // 60 minutes in an hour
        rel_time[MILLISECONDS] += frac_seconds * 1000; // 1000 millis per sec
        normalize();
    }
    
    /** (Try to) normalize the relative time by turning 70 seconds
     *  into 1 minute, 10 seconds etc.
     */ 
    private void normalize()
    {
        rollover(MILLISECONDS, 1000);
        rollover(SECONDS, 60);
        rollover(MINUTES, 60);
        rollover(HOURS, 24);
        // How many DAYS in a MONTH? 30?
        // leave as is
        rollover(MONTHS, 12);
    }
    
    /** Roll one relative time field into the next bigger one
     *  when it exceeds the limit
     *  @param field Field index like MILLISECONDS
     *  @param limit The field's limit, like 1000 for millisecs
     */
    private void rollover(final int field, final int limit)
    {
        // Roll into the next bigger field
        final int into = field - 1;
        if (Math.abs(rel_time[field]) < limit)
            return;
        final int overrun = rel_time[field] / limit;
        rel_time[field] -= overrun * limit;
        rel_time[into] += overrun;
    }
    

    /** @return The string token that's recognized by the
     *          {@link RelativeTimeParser} and that's also used
     *          by toString() for a piece.
     */
    public String getToken(int piece)
    {
        return tokens[piece];
    }

    /** Get one of the pieces of relative time.
     *  <p>
     *  For example, if get(YEAR) == -1, that stands for "one year ago".
     *   
     *  @param piece One of the constants YEAR, ..., SECONDS.
     *  @return The piece.
     */
    public int get(int piece)
    {
        return rel_time[piece];
    }
    
    /** Adjust the given calendar with the relative years etc. of this 
     *  relative time.
     *  @param calendar The calendar that will be modified.
     */
    public void adjust(Calendar calendar)
    {
        calendar.add(Calendar.YEAR, get(YEARS));
        calendar.add(Calendar.MONTH, get(MONTHS));
        calendar.add(Calendar.DAY_OF_MONTH, get(DAYS));
        calendar.add(Calendar.HOUR_OF_DAY, get(HOURS));
        calendar.add(Calendar.MINUTE, get(MINUTES));
        calendar.add(Calendar.SECOND, get(SECONDS));
        calendar.add(Calendar.MILLISECOND, get(MILLISECONDS));
    }

    @Override
    public Object clone()
    {
    	RelativeTime copy;
        try
        {
	        copy = (RelativeTime) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
        	return null;
        }
        for (int i=0; i<rel_time.length; ++i)
            copy.rel_time[i] = rel_time[i]; 
        return copy;
    }
    
    /** @return <code>true</code> if all relative time pieces are zero,
     *          i.e. indicate "now".
     */
    public boolean isNow()
    {
        for (int i=0; i<rel_time.length; ++i)
            if (rel_time[i] != 0)
                return false;
        return true;
    }

    /** Format the relative time as a string suitable for
     *  {@link RelativeTimeParser}
     *  @return Formatted relative time.
     */
    @Override
    public String toString()
    {
        if (isNow())
            return NOW;
        StringBuffer result = new StringBuffer();
        for (int piece=0; piece<=SECONDS; ++piece)
            addToStringBuffer(result, piece);
        return result.toString();
    }
    
    /** Add piece==YEAR etc. to buffer; value and token. */
    private void addToStringBuffer(StringBuffer buf, int piece)
    {
        if (rel_time[piece] == 0   &&  piece != SECONDS)
            return;
        if (buf.length() > 0)
            buf.append(' ');
        if (piece == SECONDS)
        {   // Special handling: show seconds.milliseconds
            double secs = rel_time[SECONDS] + rel_time[MILLISECONDS] / 1000.0;
            buf.append(secs);
        }
        else
            buf.append(rel_time[piece]);
        buf.append(' ');
        // Use the full (long) token, but lowercase
        buf.append(tokens[piece].toLowerCase());
    }
}
