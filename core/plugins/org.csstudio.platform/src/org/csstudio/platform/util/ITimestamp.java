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
package org.csstudio.platform.util;


/**
 * Describes a time stamp.
 * 
 * NOTE: The interface was extraced from Kay Kasemir's Timestamp class, which
 * was originally located in plugin org.csstudio.data.
 * 
 * @author Sven Wende
 * 
 */
public interface ITimestamp extends Comparable<ITimestamp> {

	/** Format to "YYYY/MM/DD". */
	int FMT_DATE = 0;

	/** Format to "YYYY/MM/DD HH:MM". */
	int FMT_DATE_HH_MM = 1;

	/** Format to "YYYY/MM/DD HH:MM:SS". */
	int FMT_DATE_HH_MM_SS = 2;

	/** Format to "YYYY/MM/DD HH:MM:SS.000000000". */
	int FMT_DATE_HH_MM_SS_NANO = 3;

	/**
	 * Constant for a piece of the timestamp.
	 * 
	 * @see #toPieces()
	 */
	int YEAR = 0;

	/**
	 * Constant for a piece of the timestamp.
	 * 
	 * @see #toPieces()
	 */
	int MONTH = 1;

	/**
	 * Constant for a piece of the timestamp.
	 * 
	 * @see #toPieces()
	 */
	int DAY = 2;

	/**
	 * Constant for a piece of the timestamp.
	 * 
	 * @see #toPieces()
	 */
	int HOUR = 3;

	/**
	 * Constant for a piece of the timestamp.
	 * 
	 * @see #toPieces()
	 */
	int MINUTE = 4;

	/**
	 * Constant for a piece of the timestamp.
	 * 
	 * @see #toPieces()
	 */
	int SECOND = 5;

	/**
	 * Constant for a piece of the timestamp.
	 * 
	 * @see #toPieces()
	 */
	int NANO = 6;

	/**
	 * Set seconds (nanoseconds will be 0).
	 * 
	 * @param seconds
	 *            Seconds since 1970.
	 */
	void setSeconds(final long seconds);

	/**
	 * Set seconds and nanoseconds.
	 * 
	 * If nanoseconds exceed 1000000000, the seconds will be adjusted
	 * accordingly.
	 * 
	 * @param seconds
	 *            Seconds since 1970.
	 * @param nanoseconds
	 *            Nanoseconds within seconds.
	 * 
	 */
	void setSecondsAndNanoseconds(final long seconds, final long nanoseconds);

	/**
	 * Get seconds since 1970.
	 * 
	 * @return The seconds.
	 */
	long seconds();

	/**
	 * Nanoseconds within seconds.
	 * 
	 * @return The nanoseconds, 0...999999999.
	 * @see #seconds()
	 */
	long nanoseconds();

	/** @return Returns true if > 0. */
	boolean isValid();

	/**
	 * Extract year, month, day, hours, minutes, seconds, nanosecs.
	 * <p>
	 * Returns an array which, in this order, contains<br>
	 * the year (e.g. 2005),<br>
	 * month (1...12),<br>
	 * day (1...31),<br>
	 * hours (0...23),<br>
	 * minutes (0...59),<br>
	 * seconds (0...59),<br>
	 * nanoseconds (0...999999999).
	 * 
	 * @return Array with pieces of the time stamp.
	 * @see #fromPieces(int, int, int, int, int, int, long)
	 */
	long[] toPieces();

	/**
	 * @return Return seconds and fractional nanoseconds.
	 */
	double toDouble();

	/**
	 * Format time according to the FMT_... flag.
	 * 
	 * @param how
	 *            One of the FMT_... flags.
	 * @return The formatted time.
	 */
	String format(final int how);

	/**
	 * @return Returns <code>true</code> if this time stamp is greater than
	 *         the <code>other</code> time stamp.
	 * @param other
	 *            the other time stamp
	 */
	boolean isGreaterThan(final ITimestamp other);

	/**
	 * @return Returns <code>true</code> if this time stamp is greater than or
	 *         equal to the <code>other</code> time stamp.
	 * @param other
	 *            the other time stamp
	 */
	boolean isGreaterOrEqual(final ITimestamp other);

	/**
	 * @return Returns <code>true</code> if this time stamp is smaller than
	 *         the <code>other</code> time stamp.
	 * @param other
	 *            the other time stamp
	 */
	boolean isLessThan(final ITimestamp other);

	/**
	 * @return Returns <code>true</code> if this time stamp is smaller than or
	 *         equal to the <code>other</code> time stamp.
	 * @param other
	 *            the other time stamp
	 */
	boolean isLessOrEqual(final ITimestamp other);

}
