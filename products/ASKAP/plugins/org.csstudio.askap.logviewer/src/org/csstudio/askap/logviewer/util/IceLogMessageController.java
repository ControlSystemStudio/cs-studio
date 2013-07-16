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

package org.csstudio.askap.logviewer.util;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.csstudio.askap.logviewer.util.LogObject.LogQueryObject;
import org.csstudio.askap.utility.icemanager.IceManager;

import Ice.Current;
import Ice.ObjectPrx;
import askap.interfaces.logging.ILogEvent;
import askap.interfaces.logging.ILogQueryPrx;
import askap.interfaces.logging.ILoggerPrx;
import askap.interfaces.logging.IQueryObject;
import askap.interfaces.logging.LogLevel;
import askap.interfaces.logging._ILoggerDisp;

public class IceLogMessageController {

	public static final Logger log = Logger.getLogger(IceLogMessageController.class.getName());
	
	private LogDataModel dataModel = null;

	private ILogQueryPrx logQueryProxy = null;
	
	private ILoggerPrx logMessagePublisher = null;
	
	private String logMessageTopicName = "";
	private String logQueryObjectName = "";
	private int maxMessagePerLogQuery = 1000;
	
	ObjectPrx subscriber = null;
	
	public IceLogMessageController(String logMessageTopicName, String logQueryObjectName, int maxMessagePerLogQuery) {
		this.logQueryObjectName = logQueryObjectName;
		this.logMessageTopicName = logMessageTopicName;
		this.maxMessagePerLogQuery = maxMessagePerLogQuery;
	}
	
	public List<LogObject> getLogMessage(LogQueryObject queryObj) throws Exception {

		if (logQueryProxy==null)
			logQueryProxy = IceManager.getLogQueryProxy(logQueryObjectName);
		
		IQueryObject iceQuery = new IQueryObject();
		
		iceQuery.hostname = (queryObj.hostName==null) ? "": queryObj.hostName;
			
		iceQuery.tag = (queryObj.tag==null) ? "" : queryObj.tag;
		iceQuery.origin = (queryObj.origin==null) ? "" : queryObj.origin ;

		if (queryObj.maxTime==null || queryObj.minTime==null) {
			iceQuery.datemax = System.currentTimeMillis()/1000;
			iceQuery.datemin = 0.0;			
		} else {
			iceQuery.datemax = queryObj.maxTime.getTime()/1000;
			iceQuery.datemin = queryObj.minTime.getTime()/1000;
		}

		
		iceQuery.limit = maxMessagePerLogQuery;
		
		iceQuery.offset = queryObj.startIndex;
		
		if (queryObj.logLevel!=null) {
			iceQuery.levels = new LogLevel[queryObj.logLevel.length];
			for (int i=0; i<queryObj.logLevel.length; i++)
				iceQuery.levels[i] = LogLevel.valueOf(queryObj.logLevel[i]);
		}
		
		ILogEvent messages[] = logQueryProxy.query(iceQuery);
	
		List<LogObject> logs = new ArrayList<LogObject>();
		for (int i=0; i<messages.length; i++) {
			logs.add(logEventToLogObject(messages[i]));
		}
		return logs;
	}
	
	public void subscribe(Object receiver, String adaptorName) throws Exception{
		dataModel = (LogDataModel) receiver;
		_ILoggerDisp callbackObj = new _ILoggerDisp() {
			public void send(ILogEvent event, Current current) {
				LogObject logObj = logEventToLogObject(event);
				dataModel.addMessage(logObj);
			}
		};
		subscriber = IceManager.setupSubscriber(logMessageTopicName, adaptorName, callbackObj);
	}
	
	protected LogObject logEventToLogObject(ILogEvent event) {
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
	
	protected ILogEvent logObjectToLogEvent(LogObject logObject) {
		ILogEvent logEvent = new ILogEvent();
		logEvent.created = logObject.getTimeStamp().getTime()/1000;
		logEvent.message = logObject.getLogMessage();
		logEvent.level = LogLevel.valueOf(logObject.getLogLevel());
		logEvent.origin = logObject.getOrigin();
		logEvent.tag = logObject.getTag();
		logEvent.hostname = logObject.getHostName();
		
		return logEvent;
	}

	/**
	 * 
	 */
	public void stop() throws Exception {
		IceManager.unsubscribe(logMessageTopicName, subscriber);
	}

	/* (non-Javadoc)
	 * @see askap.ui.operatordisplay.controller.LogMessageController#publishLogMessage(askap.ui.operatordisplay.util.LogObject)
	 */
	public void publishLogMessage(LogObject logObject) throws Exception {
		if (logMessagePublisher==null)
			logMessagePublisher = IceManager.getLogMessagePublisher(logMessageTopicName);
		
		logMessagePublisher.send(logObjectToLogEvent(logObject));
	}
}
