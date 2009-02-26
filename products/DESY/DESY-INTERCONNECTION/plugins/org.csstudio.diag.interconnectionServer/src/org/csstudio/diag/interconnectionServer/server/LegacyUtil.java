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

package org.csstudio.diag.interconnectionServer.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Provides utility methods used by the old ICS code. The utility methods have
 * been moved here to reduce code duplication.
 * 
 * @author Joerg Rathlev
 */
final class LegacyUtil {
	
	// prevent instantiation
	private LegacyUtil() {
	}

	/**
	 * Calculates the difference in milliseconds between the two given times.
	 * 
	 * @param fromTime
	 *            the first time.
	 * @param toTime
	 *            the second time.
	 * @return the number of milliseconds that have elapsed between
	 *         <code>fromTime</code> and <code>toTime</code>.
	 */
	static int gregorianTimeDifference(GregorianCalendar fromTime, GregorianCalendar toTime) {
		Date fromDate = fromTime.getTime();
		Date toDate = toTime.getTime();
		long fromLong = fromDate.getTime();
		long toLong = toDate.getTime();
		long timeDifference = toLong - fromLong;
		int intDiff = (int) timeDifference;
		return intDiff;
	}

	/**
	 * Calculates the difference between the given time and the current time.
	 * 
	 * @param time
	 *            the time.
	 * @return the number of milliseconds that have elapsed since
	 *         <code>time</code>.
	 */
	static int timeSince(GregorianCalendar time) {
		return gregorianTimeDifference(time, new GregorianCalendar());
	}

	/**
	 * Formats the given date into the YYYY-MM-DD HH:MM:SS format.
	 * 
	 * @param time
	 *            the date and time.
	 * @return the formatted date and time.
	 */
	public static String formatDate(Date time) {
		SimpleDateFormat df = new SimpleDateFormat(PreferenceProperties.JMS_DATE_FORMAT);
		return df.format(time);
	}

}
