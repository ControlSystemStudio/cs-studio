/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.time;

import java.util.Calendar;

/** Extract relative date/time specifications for start and end times
 *  from strings.
 *  <p>
 *  The time strings can contain absolute date and time info according to
 *  the format <code>YYYY-MM-DD hh:mm:ss.sss</code> used by the
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
 *  <tr><td>Absolute</td><td>Relative</td><td>as given</td><td>rel. to start</td></tr>
 *  <tr><td>Relative</td><td>Relative</td><td>rel. to end</td><td>rel. to 'now'</td></tr>
 *  </table>
 *  <p>
 *  Finally, some absolute time specification might follow a relative
 *  date/time, for example:
 *  <p>
 *  <code>-7 days 08:00</code> addresses a time 7 days relative to the reference
 *  point, but at exactly 08:00.
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
    private final String start_specification;
    private final String end_specification;
    private final RelativeTimeParserResult rel_start;
    private final RelativeTimeParserResult rel_end;
    private Calendar start, end;

    /** Parse the given start and end time strings,
     *  and return the calendar date and time obtained from that.
     *  <p>
     *  Note that even if the start and end specifications were relative,
     *  for example "-6hours" and "now", the result would of course be
     *  the absolute values, determined "right now", for "6 hours ago"
     *  resp. "now".
     *  @throws Exception On parse error.
     */
    public StartEndTimeParser(final String start_specification,
                              final String end_specification)
        throws Exception
    {
        this.start_specification = start_specification.replace(',', ' ').trim();
        this.end_specification = end_specification.replace(',', ' ').trim();
        rel_start = RelativeTimeParser.parse(this.start_specification);
        rel_end = RelativeTimeParser.parse(this.end_specification);

        if (rel_start.isAbsolute())
            start = AbsoluteTimeParser.parse(this.start_specification);
        if (rel_end.isAbsolute())
            end = AbsoluteTimeParser.parse(this.end_specification);
        if (rel_start.isAbsolute() && rel_end.isAbsolute())
            return;
        else if (!rel_start.isAbsolute() && rel_end.isAbsolute())
        {
            start = adjust(end, this.start_specification, rel_start);
            return;
        }
        else if (rel_start.isAbsolute() && !rel_end.isAbsolute())
        {
            // Is it fixed date ... now?
            if (rel_end.getRelativeTime().isNow())
                end = Calendar.getInstance();
            else // Fixed date ... something relative to that date
                end = adjust(start, this.end_specification, rel_end);
            return;
        }
        // else !rel_start.isAbsolute() && !rel_end.isAbsolute()
        Calendar now = Calendar.getInstance();
        end = adjust(now, this.end_specification, rel_end);
        start = adjust(end, this.start_specification, rel_start);
    }

    /** @return Start time specification */
    public final String getStartSpecification()
    {
        return start_specification;
    }

    /** @return End time specification */
    public final String getEndSpecification()
    {
        return end_specification;
    }

    /** Re-evaluate the time specifications.
     *  <p>
     *  In case the start or end specification were relative,
     *  this updates the start resp. end time.
     *  @throws Exception
     *  @return <code>true</code> if the start/end times changed,
     *          <code>false</code> if they stayed the same.
     */
    public boolean eval() throws Exception
    {
        if (rel_start.isAbsolute() && rel_end.isAbsolute())
            return false;
        else if (!rel_start.isAbsolute() && rel_end.isAbsolute())
        {
            final long old_start = start.getTimeInMillis();
            start = adjust(end, start_specification, rel_start);
            return old_start != start.getTimeInMillis();
        }
        else if (rel_start.isAbsolute() && !rel_end.isAbsolute())
        {
            final long old_end = end.getTimeInMillis();
            end = adjust(start, end_specification, rel_end);
            return old_end != end.getTimeInMillis();
        }
        // else !rel_start.isAbsolute() && !rel_end.isAbsolute()
        Calendar now = Calendar.getInstance();
        final long old_start = start.getTimeInMillis();
        final long old_end = end.getTimeInMillis();
        end = adjust(now, end_specification, rel_end);
        start = adjust(end, start_specification, rel_start);
        return old_start != start.getTimeInMillis()
            || old_end   != end.getTimeInMillis();
    }

    /** Get the start time obtained from the given start and end strings.
     *  <p>
     *  In case relative times are involved, those were evalutaed at the
     *  time of the last eval().
     *  @see #eval()
     *  @return Calendar value for the start time.
     */
    public final Calendar getStart()
    {   return start; }

    /** Get the end time obtained from the given start and end strings.
     *  <p>
     *  In case relative times are involved, those were evalutaed at the
     *  time of the last eval().
     *  @see #eval()
     *  @return Calendar value for the end time.
     */
    public final Calendar getEnd()
    {   return end;  }

    /** @return <code>true</code> if the start time is absolute, i.e. there
     *          were no 'relative' pieces found.
     */
    public final boolean isAbsoluteStart()
    {   return rel_start.isAbsolute(); }

    /** @return <code>true</code> if the end time is absolute, i.e. there
     *          were no 'relative' pieces found.
     */
    public final boolean isAbsoluteEnd()
    {   return rel_end.isAbsolute(); }

    /** @see #isAbsoluteStart()
     *  @return RelativeTime component of the start time.
     */
    public final RelativeTime getRelativeStart()
    {   return rel_start.getRelativeTime();  }

    /** @see #isAbsoluteEnd()
     *  @return RelativeTime component of the end time.
     */
    public final RelativeTime getRelativeEnd()
    {   return rel_end.getRelativeTime();  }

    /** @return <code>true</code> if the end time is 'now',
     *          i.e. relative with zero offsets.
     */
    public final boolean isEndNow()
    {
        return !isAbsoluteEnd()  &&  getRelativeEnd().isNow();
    }

    /** Adjust the given date with the relative date/time pieces.
     *  @param date A date.
     *  @param relative_time Result of RelativeTimeParser.parse()
     *  @return The adjusted time (a new instance, not 'date' as passed in).
     * @throws Exception
     */
    private static Calendar adjust(final Calendar date, final String text,
                    RelativeTimeParserResult relative_time) throws Exception
    {
        // Get copy of date, and patch that one
        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(date.getTimeInMillis());

        relative_time.getRelativeTime().adjust(result);

        // In case there's more text after the end of the relative
        // date/time specification, for example because we got
        // "-2month 08:00", apply that absolute text to the result.
        if (relative_time.getOffsetOfNextChar() > 1  &&
            relative_time.getOffsetOfNextChar() < text.length())
            return AbsoluteTimeParser.parse(result,
                            text.substring(relative_time.getOffsetOfNextChar()));
        return result;
    }
}
