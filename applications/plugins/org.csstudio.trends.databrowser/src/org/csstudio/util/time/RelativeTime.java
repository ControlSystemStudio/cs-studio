package org.csstudio.util.time;

import java.util.Calendar;

/** Pieces of a relative time specification.
 *  <p>
 *  No, this is not special relativity.
 *  This is simply about relative date and time offsets
 *  like "6 hours before".
 *  @author Kay Kasemir
 */
public class RelativeTime
{
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

    /** Construct new relative time information. */
    public RelativeTime()
    {
        rel_time = new int[6];
    }

    /** Set the YEAR etc. to a new value.
     *  @param piece One of the constants YEAR, ..., SECONDS.
     *  @param new_value The new value.
     */
    public void set(int piece, int new_value)
    {
        rel_time[piece] = new_value;
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
}
