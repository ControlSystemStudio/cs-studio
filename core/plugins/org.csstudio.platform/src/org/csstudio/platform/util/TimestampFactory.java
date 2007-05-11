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

import java.util.Date;

import org.csstudio.platform.internal.util.Timestamp;

/**
 * A factory for time stamps.
 * 
 * @author Sven Wende
 * 
 * @deprecated Use org.csstudio.platform.data.TimestampFactory
 */
@Deprecated
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
	public static ITimestamp now() {
		Date d = new Date();
		long milli = d.getTime();
		long secs = milli / 1000;
		milli -= secs * 1000;
		long nano = milli * 1000000;
		return new Timestamp(secs, nano);
	}
}
