/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.data.values;

import java.io.Serializable;
import java.util.Calendar;

/** A control system time stamp.
 *  <p>
 *  Java already supports many time-stamp related operations via the
 *  <code>Calendar</code> class down to milliseconds, but control system time
 *  stamps often include micro or nanosecond detail.
 *  <p>
 *  This time stamp uses 'seconds' with the same epoch as Calendar
 *  (1 January 1970 0:00 UTC), and adds nanosecond detail.
 *  <p>
 *  In addition, it supports comparisons as well as string conversions to
*   the form <code>yyyy/mm/dd HH:MM:SS.MMM</code>, for example
 *  <code>2007/01/15 14:45:56.123</code>,
 *  which sorts well and is disambiguated from regional preferences like
 *  AM/PM formats, or day/month/year vs. month/day/year.
 *
 *  @see java.util.Calendar
 *  @see TimestampFactory
 *
 *  @author Sven Wende
 *  @author Kay Kasemir
 */
public interface ITimestamp extends Comparable<ITimestamp>, Serializable
{
    /** Get seconds since epoch, i.e. 1 January 1970 0:00 UTC.
     *  <p>
     *  Note that we always return seconds relative to this UTC epoch,
     *  even if the original control system data source might use a different
     *  epoch (example: EPICS uses 1990), because the 1970 epoch is most
     *  compatible with existing programming environments.
     *
     *  @return Seconds since 1970.
     */
    public long seconds();

    /** Nanoseconds within seconds.
     *  @return The nanoseconds, 0...999999999.
     *  @see #seconds()
     */
    public long nanoseconds();

    /** @return Returns <code>true</code> if seconds and
     *          nanoseconds are &gt; 0.
     */
    public boolean isValid();

    /** Convert to double.
     *  @return Return seconds and fractional nanoseconds.
     */
    public double toDouble();

    /** Convert to Calendar.
     *  @return Calendar version of this time stamp down to millisecond detail.
     */
    public Calendar toCalendar();

    /** Convert to SQL Timestamp.
     *  @return SQL Timestamp
     */
    public java.sql.Timestamp toSQLTimestamp();

    /** Format specifier.
     *  @see ITimestamp#format()
     */
    public enum Format
    {
        /** Format to "YYYY/MM/DD". */
        Date,

        /** Format to "YYYY/MM/DD HH:MM". */
        DateTime,

        /** Format to "YYYY/MM/DD HH:MM:SS". */
        DateTimeSeconds,

        /** Format to "YYYY/MM/DD HH:MM:SS.000000000". */
        Full;

        /** Obtain {@link Format} for given ordinal.
         *  @param ordinal Should be one of the Format.XX.ordinal() codes.
         *  @return Format for the given ordinal.
         */
        static public final Format fromOrdinal(int ordinal)
        {   // bad implementation
            switch (ordinal)
            {
            case 0: return Date;
            case 1: return DateTime;
            case 2: return DateTimeSeconds;
            }
            return Full;
        }
    }

    /** Format time according to the FMT_... flag.
     *  @param how
     *            One of the FMT_... flags.
     *  @return The formatted time.
     */
    public String format(final Format format);

    /** @return Returns <code>true</code> if this time stamp is greater than
     *          the <code>other</code> time stamp.
     *  @param other Other time stamp
     */
    public boolean isGreaterThan(final ITimestamp other);

    /** @return Returns <code>true</code> if this time stamp is greater than or
     *          equal to the <code>other</code> time stamp.
     *  @param other Other time stamp
     */
    public boolean isGreaterOrEqual(final ITimestamp other);

    /** @return Returns <code>true</code> if this time stamp is smaller than
     *          the <code>other</code> time stamp.
     *  @param other Other time stamp
     */
    public boolean isLessThan(final ITimestamp other);

    /** @return Returns <code>true</code> if this time stamp is smaller than or
     *          equal to the <code>other</code> time stamp.
     *  @param other Other time stamp
     */
    public boolean isLessOrEqual(final ITimestamp other);
}
