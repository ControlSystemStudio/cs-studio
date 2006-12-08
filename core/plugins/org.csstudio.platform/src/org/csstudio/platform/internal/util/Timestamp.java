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
package org.csstudio.platform.internal.util;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.csstudio.platform.util.ITimestamp;

/**
 * A control system time stamp.
 * <p>
 * Users are most likely interested in seeing the time stamp formatted as some
 * permutation od day/month/year, hour:minute:seconds, for the local time zone.
 * In a control system context, milliseconds or nanoseconds might be important
 * as well.
 * <p>
 * There are regional preferences as to using day/month/year vs. month/day/year,
 * or '13:00' vs. '01:00pm'. In a control system context, more often than not
 * people of different origin need to work together, so one has to agree on a
 * common time format. On the technical side, a format that sorts the same way
 * based on time or ASCII text has certain advantages, as is the case for the
 * format<br>
 * <code>YYYY-MM-DD HH:MM:SS.000000000</code>, for example<br>
 * <code>2003-02-01 13:47:42.200000000</code> for a time on the first day of
 * February 2003.
 * <p>
 * Most operating systems respectively programming environments are capable of
 * dealing with time stamps based on seconds since 1970, the so called 'epoch'.
 * Sometimes millisecond resolution is offered. The time is stored in the
 * UT/UTC/GMT/Greenwhich time zone, which is all the same except for some subtle
 * leap second differences.
 * <p>
 * EPICS used seconds and nanoseconds based on 1990; higher resolution, with an
 * atypical choice of epoch. <br>
 * This class uses a compromise: Seconds and nanoseconds since 1970, which is
 * most compatible with existing programming environments but offers better
 * resolution where needed.
 * <p>
 * <b>TODO:</b><br>
 * Determine if this is 1970 UT, UTC, GMT, whatever.
 * <p>
 * <b>TODO:</b><br>
 * Determine how much more is needed in here for time stamp calculations.
 * java.util.Calendar mentions and handles many key points. For example, many
 * applications need to deal with calculating end=start + duration.<br>
 * What is "January 31, 1999 + 1 month"? Is it "March 3"? Or "February 28"? <br>
 * A Java application can of course use the Calendar class. Should the Timestamp
 * class include such features so that control system apps written in other
 * languages share the same behavior?
 * 
 * @see java.util.Calendar
 * @author Kay Kasemir
 */
public final class Timestamp implements ITimestamp {
	/** Seconds since epoch. */
	private long _seconds;

	/** Nanoseconds within the seconds. */
	private long _nanoseconds;

	/**
	 * A date format.
	 */
	private static SimpleDateFormat _dateFormatter = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");

	/** Constructor for 'zero' time. */
	public Timestamp() {
		this(0);
	}

	/**
	 * Constructor with seconds since epoch.
	 * 
	 * @param seconds
	 *            seconds
	 */
	public Timestamp(final long seconds) {
		this(seconds, 0);
	}

	/**
	 * Constructor fractional seconds since epoch.
	 * 
	 * @param seconds
	 *            seconds
	 */
	public Timestamp(final double seconds) {
		long secs = (long) seconds;
		long nano = (long) ((seconds - secs) * 1e9);
		setSecondsAndNanoseconds(secs, nano);
	}

	/**
	 * Constructor with seconds and nanoseconds since epoch.
	 * 
	 * @param seconds
	 *            seconds
	 * @param nanoseconds
	 *            nanoseconds
	 */
	public Timestamp(final long seconds, final long nanoseconds) {
		setSecondsAndNanoseconds(seconds, nanoseconds);
	}

	/**
	 * Parse time stamp from string "yyyy/MM/dd HH:mm:ss".
	 * 
	 * @param text
	 *            a text containing a date
	 * 
	 * @throws Exception
	 *             an exception is thrown, when the specified String does follow
	 *             a certain time format (yyyy/MM/dd HH:mm)
	 * 
	 * @return the timestamp
	 */
	public static ITimestamp fromString(final String text) throws Exception {
		Date d;
		try {
			if (text.length() == 10) { // "yyyy/MM/dd" -> append "00:00:00"
				d = _dateFormatter.parse(text + " 00:00:00");
			} else if (text.length() == 16) { // "yyyy/MM/dd HH:mm" -> append
				// ":00"
				d = _dateFormatter.parse(text + ":00");
			} else {
				d = _dateFormatter.parse(text.substring(0, 19));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Date has to match '2006-01-18',"
					+ " and time has to match '15:42'", e);
		}
		long millis = d.getTime();
		long secs = millis / 1000;
		long nano = (millis - secs * 1000) * 1000000;
		return new Timestamp(secs, nano);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setSeconds(final long seconds) {
		setSecondsAndNanoseconds(seconds, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setSecondsAndNanoseconds(final long seconds,
			final long nanoseconds) {
		final long nanoPerSecond = 1000000000;

		long newSeconds = seconds;
		long newNanoSeconds = nanoseconds;

		if (nanoseconds > nanoPerSecond) {
			long s = nanoseconds / nanoPerSecond;
			newSeconds += s;
			newNanoSeconds -= s * nanoPerSecond;
		}
		_seconds = newSeconds;
		_nanoseconds = newNanoSeconds;

	}

	/**
	 * {@inheritDoc}
	 */
	public long seconds() {
		return _seconds;
	}

	/**
	 * {@inheritDoc}
	 */
	public long nanoseconds() {
		return _nanoseconds;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isValid() {
		return _seconds > 0 || _nanoseconds > 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public long[] toPieces() {
		final long millisPerSec = 1000L;
		final long nanosPerMilli = 1000000L;
		long[] pieces = new long[7];
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(_seconds * millisPerSec + _nanoseconds
				/ nanosPerMilli);
		pieces[ITimestamp.YEAR] = c.get(Calendar.YEAR);
		pieces[ITimestamp.MONTH] = c.get(Calendar.MONTH) + 1;
		pieces[ITimestamp.DAY] = c.get(Calendar.DAY_OF_MONTH);
		pieces[ITimestamp.HOUR] = c.get(Calendar.HOUR_OF_DAY);
		pieces[ITimestamp.MINUTE] = c.get(Calendar.MINUTE);
		pieces[ITimestamp.SECOND] = c.get(Calendar.SECOND);
		pieces[ITimestamp.NANO] = _nanoseconds;
		return pieces;
	}

	/**
	 * Create timestamp from pieces in the local time zone.
	 * <p>
	 * Behavior is undefined for years before 1970.
	 * 
	 * @param year
	 *            e.g. 2005
	 * @param month
	 *            1...12
	 * @param day
	 *            1...31
	 * @param hours
	 *            0...23
	 * @param minutes
	 *            0...59
	 * @param seconds
	 *            0...59
	 * @param nanoseconds
	 *            0...999999999
	 * @see #toPieces()
	 * 
	 * @return the timestamp, which was created from the specified pieces
	 */
	public static Timestamp fromPieces(final int year, final int month,
			final int day, final int hours, final int minutes,
			final int seconds, final long nanoseconds) {
		/*
		 * Deprecated version, seems to work OK: Date date = new Date
		 * (year-1900, month-1, day, hours, minutes, seconds); long millis =
		 * date.getTime();
		 */
		/*
		 * 'Calendar' version. Thanks to Tom Pelaia for finding the need to
		 * clear(). Calendar cal = Calendar.getInstance(); cal.clear();
		 * cal.set(year, month-1, day, hours, minutes, seconds);
		 */
		Calendar cal = new GregorianCalendar(year, month - 1, day, hours,
				minutes, seconds);
		long millis = cal.getTimeInMillis();
		long secs = millis / 1000L;
		return new Timestamp(secs, nanoseconds);
	}

	/**
	 * Create timestamp from pieces. Order of array elements is the same as
	 * returned by toPieces().
	 * 
	 * @see #fromPieces(int, int, int, int, int, int, long)
	 * @see #toPieces()
	 * @param pieces the pieces
	 * @return a timestamp
	 */
	public static Timestamp fromPieces(final long[] pieces) {
		return Timestamp.fromPieces((int) pieces[0], (int) pieces[1],
				(int) pieces[2], (int) pieces[3], (int) pieces[4],
				(int) pieces[5], pieces[6]);
	}

	/**
	 * {@inheritDoc}
	 */
	public double toDouble() {
		return _seconds + nanoseconds() / 1e9;
	}

	/**
	 * Format time as "YYYY/MM/DD HH:MM:SS.000000000".
	 * 
	 * @return The formatted time.
	 */
	@Override
	public String toString() {
		return format(ITimestamp.FMT_DATE_HH_MM_SS_NANO);
	}

	/**
	 * {@inheritDoc}
	 */
	public String format(final int how) {
		StringBuffer buf = new StringBuffer();
		FieldPosition pos = new FieldPosition(0);
		NumberFormat fmt = NumberFormat.getIntegerInstance();
		fmt.setGroupingUsed(false);

		long[] pieces = toPieces();

		// YYYY
		fmt.setMinimumIntegerDigits(4);
		fmt.format(pieces[0], buf, pos);
		buf.append("/");
		// MM
		fmt.setMinimumIntegerDigits(2);
		fmt.format(pieces[1], buf, pos);
		buf.append("/");
		// DD
		fmt.format(pieces[2], buf, pos);
		if (how >= FMT_DATE_HH_MM) {
			buf.append(" ");
			// HH
			fmt.format(pieces[3], buf, pos);
			buf.append(":");
			// MM
			fmt.format(pieces[4], buf, pos);
			if (how >= FMT_DATE_HH_MM_SS) {
				buf.append(":");
				// SS
				fmt.format(pieces[5], buf, pos);
				if (how >= FMT_DATE_HH_MM_SS_NANO) {
					buf.append(".");
					// 000000000
					fmt.setMinimumIntegerDigits(9);
					fmt.format(pieces[6], buf, pos);
				}
			}
		}
		return buf.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isGreaterThan(final ITimestamp other) {
		if (_seconds > other.seconds()) {
			return true;
		}
		if (_seconds < other.seconds()) {
			return false;
		}
		// Seconds tie, let nanoseconds decide.
		return _nanoseconds > other.nanoseconds();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isGreaterOrEqual(final ITimestamp other) {
		if (_seconds > other.seconds()) {
			return true;
		}
		if (_seconds < other.seconds()) {
			return false;
		}
		// Seconds tie, let nanoseconds decide.
		return _nanoseconds >= other.nanoseconds();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isLessThan(final ITimestamp other) {
		return !isGreaterOrEqual(other);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isLessOrEqual(final ITimestamp other) {
		return !isGreaterThan(other);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof ITimestamp)) {
			return false;
		}
		ITimestamp rhs = (ITimestamp) obj;
		return rhs.seconds() == _seconds && rhs.nanoseconds() == _nanoseconds;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return (int) (_seconds + _nanoseconds);
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(final ITimestamp rhs) {
		if (isGreaterThan(rhs)) {
			return 1;
		}
		if (equals(rhs)) {
			return 0;
		}
		return -1;
	}
}
