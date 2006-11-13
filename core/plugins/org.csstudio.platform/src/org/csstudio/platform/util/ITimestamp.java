package org.csstudio.platform.util;

import org.csstudio.platform.internal.util.Timestamp;

/**
 * Describes a time stamp.
 * 
 * NOTE: The interface was extraced from Kay Kasemier´s Timestamp class, which
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
	boolean isGreaterThan(final Timestamp other);

	/**
	 * @return Returns <code>true</code> if this time stamp is greater than or
	 *         equal to the <code>other</code> time stamp.
	 * @param other
	 *            the other time stamp
	 */
	boolean isGreaterOrEqual(final Timestamp other);

	/**
	 * @return Returns <code>true</code> if this time stamp is smaller than
	 *         the <code>other</code> time stamp.
	 * @param other
	 *            the other time stamp
	 */
	boolean isLessThan(final Timestamp other);

	/**
	 * @return Returns <code>true</code> if this time stamp is smaller than or
	 *         equal to the <code>other</code> time stamp.
	 * @param other
	 *            the other time stamp
	 */
	boolean isLessOrEqual(final Timestamp other);

}
