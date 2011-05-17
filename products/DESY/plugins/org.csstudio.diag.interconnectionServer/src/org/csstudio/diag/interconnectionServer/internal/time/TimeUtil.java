/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.diag.interconnectionServer.internal.time;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.csstudio.diag.interconnectionServer.server.PreferenceProperties;

/**
 * Utility class for dealing with time.
 * 
 * @author Joerg Rathlev
 */
public final class TimeUtil {
	
	/**
	 * The date/time format.
	 */
	private static final SimpleDateFormat FORMAT =
			new SimpleDateFormat(PreferenceProperties.JMS_DATE_FORMAT);
	
	/**
	 * The singleton system time source. Note: this is initialized when this
	 * class is initialized because then no synchronization overhead is
	 * required in the accessor method {@link #systemClock()}.
	 */
	private static final TimeSource SYSTEM_TIME_SOURCE = new TimeSource() {
				public long now() {
					return System.currentTimeMillis();
				}
			};
	
	/**
	 * Formats the specified time.
	 * 
	 * @param milliseconds
	 *            the time in milliseconds since the epoch.
	 * @return the formatted time.
	 */
	public static String formatTime(long milliseconds) {
		/*
		 * XXX: This is sensitive to the current time zone :-(
		 */
//		return new Date(milliseconds).toString();
		return FORMAT.format(new Date(milliseconds));
	}
	
	/**
	 * Returns an {@link TimeSource} instance which provides the current system
	 * time.
	 * 
	 * @return a time source which provides the system time.
	 */
	public static TimeSource systemClock() {
		return SYSTEM_TIME_SOURCE;
	}

}
