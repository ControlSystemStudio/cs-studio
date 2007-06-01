package org.csstudio.util.time;

import java.util.Calendar;

/** Pieces of a relative time specification.
 *  <p>
 *  No, this is not special relativity.
 *  This is simply about relative date and time offsets
 *  like "6 hours before".
 *  
 *  TODO include milliseconds
 *  @author Kay Kasemir
 */
public class RelativeTime
{
    /** Constant to define 'now', i.e. the current wallclock date and time. */
    public static final String NOW = "now"; //$NON-NLS-1$
    
    /** String identifier for seconds */
    public static final String SECOND_TOKEN = "seconds"; //$NON-NLS-1$

    /** String identifier for minutes */
    public static final String MINUTE_TOKEN = "minutes"; //$NON-NLS-1$

    /** String identifier for hours */
    public static final String HOUR_TOKEN = "hours"; //$NON-NLS-1$

    /** String identifier for days */
    public static final String DAY_TOKEN = "days"; //$NON-NLS-1$

    /** String identifier for months */
    public static final String MONTH_TOKEN = "Months"; //$NON-NLS-1$

    /** String identifier for years */
    public static final String YEAR_TOKEN = "years"; //$NON-NLS-1$

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
    @SuppressWarnings("nls")
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
        rel_time = new int[6];
    }

    /** Construct relative time information from the given data.
     *  @param ymdhms Array with years, months, days, hours, minutes, seconds
     */
    public RelativeTime(int ymdhms[])
    {
        rel_time = ymdhms;
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
    }

    @Override
    public Object clone()
    {
        RelativeTime copy = new RelativeTime();
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
        for (int piece=0; piece<rel_time.length; ++piece)
            addToStringBuffer(result, piece);
        return result.toString();
    }
    
    /** Add piece==YEAR etc. to buffer; value and token. */
    private void addToStringBuffer(StringBuffer buf, int piece)
    {
        if (rel_time[piece] == 0)
            return;
        if (buf.length() > 0)
            buf.append(' ');
        buf.append(rel_time[piece]);
        buf.append(' ');
        // Use the full (long) token, but lowercase
        buf.append(tokens[piece].toLowerCase());
    }
}
