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

/**
 *
 */
package org.epics.css.dal;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;

import java.util.Date;


/**
 * This is timestamp object with nanosecond resolution. It holds two long values. One is with millisoecnd
 * resolution and represents Java standart UTC format. Second long value is with nanosecond resolution
 * and its absolute value is lower than 1ms or 1000000ns.
 * @author ikriznar
 *
 */
public final class Timestamp implements Comparable
{
	private long milliseconds;
	private long nanoseconds;
	private final static SimpleDateFormat format = new SimpleDateFormat(
		    "yyyy-MM-dd'T'HH:mm:ss.SSS");

	private final static long currentSecondInNano()
	{
		long l = System.nanoTime();

		return l - ((l / 1000000000) * 1000000000);
	}

	/**
	 * Default constructor, uses system time for initialization.
	 *
	 */
	public Timestamp()
	{
		this((System.currentTimeMillis() / 1000) * 1000, currentSecondInNano());
	}

	/**
	 * Creates timestamp representing provided values. If nanoseconds exceed 1000000 or -1000000 then they are
	 * trucuted to
	 * @param milli
	 * @param nano
	 */
	public Timestamp(long milli, long nano)
	{
		// correction if there is more nanoseconds than it fits in 
		if (nano >= 1000000) {
			long t = nano / 1000000;
			milliseconds = milli + t;
			nanoseconds = nano - t * 1000000;
		} else if (nano <= -1000000) {
			long t = nano / 1000000;
			milliseconds = milli + t - 1;
			nanoseconds = nano - t * 1000000 + 1000000;
		} else if (nano < 0) {
			milliseconds = milli - 1;
			nanoseconds = nano + 1000000;
		} else {
			milliseconds = milli;
			nanoseconds = nano;
		}
	}

	/**
	 * Returns time in milliseconds since eppoch (standard Java UTC time, as returned by System.currentTimeMillis())
	 * @return Returns the milliseconds.
	 */
	public long getMilliseconds()
	{
		return milliseconds;
	}

	/**
	 * @return Returns the nanoseconds.
	 */
	public long getNanoseconds()
	{
		return nanoseconds;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(T)
	 */
	public int compareTo(Object o)
	{
		if (o instanceof Timestamp) {
			Timestamp t = (Timestamp)o;
			long d = milliseconds - t.milliseconds;

			if (d != 0) {
				return (int)d;
			}

			d = nanoseconds - t.nanoseconds;

			return (int)d;
		}

		return 0;
	}

	/**
	 * Returns time in nanoseconds since epoch. Not that this in only usefull for calculating
	 * time difference for up to 292 years (2<sup>63</sup> nanoseconds) since this is maximum time possible in
	 * nanoseconds due to long value range overflow.
	 * @return up to approx. 292 years big nano time
	 */
	public long getNanoTime()
	{
		return milliseconds * 1000000 + nanoseconds;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Timestamp) {
			Timestamp t = (Timestamp)obj;

			return t.milliseconds == milliseconds
			&& t.nanoseconds == nanoseconds;
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer(32);
		format.format(new Date(milliseconds), sb,
		    new FieldPosition(DateFormat.FULL));
		sb.append(nanoseconds);

		if (nanoseconds < 100000) {
			sb.append('0');

			if (nanoseconds < 10000) {
				sb.append('0');

				if (nanoseconds < 1000) {
					sb.append('0');

					if (nanoseconds < 100) {
						sb.append('0');

						if (nanoseconds < 10) {
							sb.append('0');
						}
					}
				}
			}
		}

		return sb.toString();
	}
}

/* __oOo__ */
