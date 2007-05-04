package org.csstudio.util.time;

import java.util.Calendar;

/** Extract relative date/time specifications for start and end times
 *  from strings.
 *  <p>
 *  The time strings can contain absolute date and time info according to
 *  the format <code>YYYY/MM/DD hh:mm:ss.sss</code> used by the
 *  {@link AbsoluteTimeParser}.
 *  When only certain pieces of the full date and time are provided,
 *  the omitted information is obtained from the current wallclock.
 *  <p>
 *  In addition, relative date and time specifications are allowed following
 *  the format of the {@link RelativeTimeParser}, i.e.
 *  <code>-3 years -3 Months 1 days 19Hours 45 minutes 0 seconds</code>.
 *  <p>
 *  When using relative date and time specifications, the reference date
 *  depends on the circumstance:
 *  <table>
 *  <tr><th>Start Specification </th><th>End Spec.</th><th>Start Time</th><th>End Time</th></tr>
 *  <tr><td>Absolute</td><td>Absolute</td><td>As given</td><td>as given</td></tr>
 *  <tr><td>Relative</td><td>Absolute</td><td>rel. to end</td><td>as given</td></tr>
 *  </table>
 *
 *  @see AbsoluteTimeParser
 *  @see RelativeTimeParser
 *  @see #parse(String, String)
 *  
 *  @author Sergei Chevtsov developed the original code for the
 *          Java Archive Viewer, from which this code heavily borrows.
 *  @author Kay Kasemir
 */
public class StartEndTimeParser
{
    public static Calendar[] parse(String start_text, String end_text)
        throws Exception
    {
        // Extract the relative date/time information?
        int rel_start[] = RelativeTimeParser.parse(start_text);
        int rel_end[]   = RelativeTimeParser.parse(end_text);
        // Which one is absolute?
        boolean abs_start = rel_start[0] < 0;
        boolean abs_end = rel_end[0] < 0;
        if (abs_start && abs_end)
        {
            Calendar start = AbsoluteTimeParser.parse(start_text);
            Calendar end = AbsoluteTimeParser.parse(end_text);
            return new Calendar[] { start, end };
        }
        else if (!abs_start && abs_end)
        {
            Calendar end = AbsoluteTimeParser.parse(end_text);
            Calendar start = adjust(end, rel_start);
            return new Calendar[] { start, end };
        }
        
        return null;
    }
    
    /** Adjust the given date with the relative date/time pieces.
     *  @param date A date.
     *  @param relative_time Result of RelativeTimeParser.parse()
     *  @return The adjusted time (a new instance, not 'date' as passed in).
     */
    private static Calendar adjust(Calendar date, int relative_time[])
    {
        // Get copy of date, and patch that one
        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(date.getTimeInMillis());
        result.add(Calendar.YEAR, relative_time[RelativeTimeParser.YEARS]);
        result.add(Calendar.MONTH, relative_time[RelativeTimeParser.MONTHS]);
        result.add(Calendar.DAY_OF_MONTH, relative_time[RelativeTimeParser.DAYS]);
        result.add(Calendar.HOUR_OF_DAY, relative_time[RelativeTimeParser.HOURS]);
        result.add(Calendar.MINUTE, relative_time[RelativeTimeParser.MINUTES]);
        result.add(Calendar.SECOND, relative_time[RelativeTimeParser.SECONDS]);
        
        return result;
    }
}
