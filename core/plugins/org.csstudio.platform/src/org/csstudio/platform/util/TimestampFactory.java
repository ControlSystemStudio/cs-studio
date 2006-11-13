package org.csstudio.platform.util;

import java.util.Date;

import org.csstudio.platform.internal.util.Timestamp;

/**
 * A factory for time stamps.
 * 
 * @author Sven Wende
 * 
 */
public final class TimestampFactory {

	/**
	 * Private constructor to prevent instantiation.
	 * 
	 */
	private TimestampFactory() {

	}

	/**
	 * Creates a timestamp with seconds since epoch.
	 * 
	 * @return a timestamp with seconds since epoch
	 */
	public static ITimestamp createTimestamp() {
		return new Timestamp();
	}

	/**
	 * Creates a time stamp based on the specified seconds and nano seconds.
	 * 
	 * @param seconds
	 *            the seconds
	 * @param nanoSeconds
	 *            the nano seconds
	 * @return a timestamp
	 */
	public static ITimestamp createTimestamp(final long seconds,
			final long nanoSeconds) {
		return new Timestamp(seconds, nanoSeconds);
	}

	/**
	 * Creates a time stamp for the current system time.
	 * 
	 * @return a time stamp for the current system time
	 */
	public static Timestamp now() {
		Date d = new Date();
		long milli = d.getTime();
		long secs = milli / 1000;
		milli -= secs * 1000;
		long nano = milli * 1000000;
		Timestamp t = new Timestamp();
		t.setSecondsAndNanoseconds(secs, nano);
		return t;
	}
}
