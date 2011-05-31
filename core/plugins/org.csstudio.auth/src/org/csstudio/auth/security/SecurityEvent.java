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
package org.csstudio.auth.security;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * This is a superclass for all events that need a message with a timestamp.
 * @author Kai Meyer & Torsten Witte & Alexander Will & Sven Wende
 */
public abstract class SecurityEvent {
	
	/**
	 * 
	 */
	public static final int EVENT_1 = 1;

	/**
	 * The timestamp in milliseconds.
	 */
	private final long _timeStamp;
	
	/**
	 * The default-dateformat.
	 */
	private static final String DATE_FORMAT = "dd.MM.yyyy' - 'HH:mm:ss"; //$NON-NLS-1$
	
	/**
	 * Constructor.
	 * Generates a timestamp.
	 */
	public SecurityEvent() {
		_timeStamp = System.currentTimeMillis();
	}
	
	/**
	 * Delivers a description about this event. 
	 * @return A message
	 */
	public abstract String getMessage();
	
	/**
	 * Delivers the timestamp of this event.
	 * @return The timestamp of this event
	 */
	public final long getTimeStamp() {
		return _timeStamp;
	}
	
	/**
	 * Delivers a calendar with the time of this event.
	 * @return A calendar with the time of this event
	 */
	public final Calendar getTimeStampAsCalendar() {
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(_timeStamp);
		return calendar;
	}
	
	/**
	 * Delivers a representation as a String of the timestamp.
	 * @param dateFormat  The format for the representation
	 * @return A representation as a String of the timestamp
	 */
	public final String getTimeStampAsString(final String dateFormat) {
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		return format.format(getTimeStampAsCalendar().getTime());
	}
	
	/**
	 * Delivers a representation as a String of the timestamp.
	 * @return A representation as a String of the timestamp
	 */
	public final String getTimeStampAsString() {
		return getTimeStampAsString(DATE_FORMAT);
	}

}
