/*
 * Copyright (c) 2009 CSIRO Australia Telescope National Facility (ATNF) Commonwealth
 * Scientific and Industrial Research Organisation (CSIRO) PO Box 76, Epping NSW 1710,
 * Australia atnf-enquiries@csiro.au
 *
 * This file is part of the ASKAP software distribution.
 *
 * The ASKAP software distribution is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite
 * 330, Boston, MA 02111-1307 USA
 */

package org.csstudio.askap.utility.icemanager;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;

import askap.interfaces.logging.ILogEvent;

public class LogObject {
	
	private static final Logger logger = Logger.getLogger(LogObject.class.getName());
	
	private String origin="";
	
	private Date timeStamp;
	private String logLevel;
	
	private String hostName="";
	private String tag="";
	
	private String logMessage="";
	
	private static Map<String, Integer> LOG_LEVEL_COLOR_MAP = new HashMap<String, Integer>();
	static {
//		"TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL"
		LOG_LEVEL_COLOR_MAP.put("FATAL", SWT.COLOR_RED);
		LOG_LEVEL_COLOR_MAP.put("WARN", SWT.COLOR_YELLOW);
		LOG_LEVEL_COLOR_MAP.put("ERROR", SWT.COLOR_RED);
	}
	
	
	public static int getLogLevelColor(String logLevel) {
		Integer color =  LOG_LEVEL_COLOR_MAP.get(logLevel);
		if (color!=null) 
			return color;
		
		return SWT.COLOR_WHITE;
	}
	
	public static enum LogComparatorField {
		origin,		
		timeStamp,
		logLevel,		
		hostName,
		tag,		
		logMessage;		
	}
	
	public static class LogObjectComparator implements Comparator<LogObject> {

		private LogComparatorField compField = LogComparatorField.timeStamp;
		
		public LogObjectComparator(LogComparatorField compField) {
			if (compField==null)
				this.compField = LogComparatorField.timeStamp;
			else
				this.compField = compField;
		}
		
		public void setComparatorField(LogComparatorField compField) {
			this.compField = compField;
		}
		
		public LogComparatorField getComparatorField() {
			return compField;
		}
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(LogObject o1, LogObject o2) {
			// only return 0 if both objects are totally equal
			if (o1.origin.equals(o2.origin)
				&& o1.timeStamp.equals(o2.timeStamp)
				&& o1.logLevel.equals(o2.logLevel)
				&& o1.hostName.equals(o2.hostName)
				&& o1.tag.equals(o2.tag)
				&& o1.logMessage.equals(o2.logMessage)) {

//				logger.debug("two objects are the same " + o1.logMessage + " & " + o2.logMessage );
				return 0;
			}
			switch (compField) {

			case origin:
				if (o1.origin.compareTo(o2.origin) != 0)
					return o1.origin.compareTo(o2.origin);
				break;
			case logLevel:
				if (o1.logLevel.compareTo(o2.logLevel) != 0)
					return o1.logLevel.compareTo(o2.logLevel);
				break;
			case hostName:
				if (o1.hostName.compareTo(o2.hostName) != 0)
					return o1.hostName.compareTo(o2.hostName);
				break;
			case tag:
				if (o1.tag.compareTo(o2.tag) != 0)
					return o1.tag.compareTo(o2.tag);
				break;
			case logMessage:
				if (o1.logMessage.compareTo(o2.logMessage) != 0)
					return o1.logMessage.compareTo(o2.logMessage);			
			}
			// if they arrived at the same time, then does not really matter which one to display first
			// pick the first obj
			if (o1.timeStamp.compareTo(o2.timeStamp)==0)
				return o1.logMessage.compareTo(o2.logMessage);
			else
				return o1.timeStamp.compareTo(o2.timeStamp);
		}
		
	}
	
	
	public static class LogQueryObject {
		public String origin;
		
		public Date minTime;
		public Date maxTime;
		
		public String[] logLevel;
		
		public String hostName;
		public String tag;
		
		public int startIndex;
	}

	
	public LogObject() {
		
	}
	
	public LogObject(LogObject log) {		
		setOrigin(log.getOrigin());
		setTimeStamp(log.getTimeStamp());
		setLogLevel(log.getLogLevel());
		setHostName(log.getHostName());
		setTag(log.getTag());
		setLogMessage(log.getLogMessage());
	
	}

	/**
	 * @return the origin
	 */
	public String getOrigin() {
		return origin;
	}

	/**
	 * @param origin the origin to set
	 */
	public void setOrigin(String origin) {
		this.origin = (origin==null ? "" : origin);
	}

	/**
	 * @return the timeStamp
	 */
	public Date getTimeStamp() {
		return timeStamp;
	}

	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 * @return the logLevel
	 */
	public String getLogLevel() {
		return logLevel;
	}

	/**
	 * @param logLevel the logLevel to set
	 */
	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	/**
	 * @return the hostName
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * @param hostName the hostName to set
	 */
	public void setHostName(String hostName) {
		this.hostName = (hostName==null ? "" : hostName);
	}

	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		this.tag = (tag==null ? "" : tag);
	}

	/**
	 * @return the logMessage
	 */
	public String getLogMessage() {
		return logMessage;
	}

	/**
	 * @param logMessage the logMessage to set
	 */
	public void setLogMessage(String logMessage) {
		this.logMessage = (logMessage==null ? "" : logMessage);
	}

	
	public static LogObject logEventToLogObject(ILogEvent event) {
		LogObject logObj = new LogObject();

		Date date = new Date((long) (event.created * 1000));					
		logObj.setTimeStamp(date);
		
		logObj.setLogMessage(event.message);
		
		logObj.setLogLevel(event.level.name());
		logObj.setOrigin(event.origin);
		
		logObj.setHostName(event.hostname);
		logObj.setTag(event.tag);

		return logObj;
	}


}
