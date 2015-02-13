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

import org.csstudio.askap.utility.icemanager.IceManager;
import org.csstudio.askap.utility.icemanager.LogObject;
import org.csstudio.askap.utility.icemanager.LogObject.LogQueryObject;

import Ice.Current;
import Ice.ObjectPrx;
import askap.interfaces.logging.ILogEvent;
import askap.interfaces.logging.ILogQueryPrx;
import askap.interfaces.logging.ILoggerPrx;
import askap.interfaces.logging.IQueryObject;
import askap.interfaces.logging.LogLevel;
import askap.interfaces.logging._ILoggerDisp;

public class IceLogMessageQueryController {

	public static final Logger log = Logger.getLogger(IceLogMessageQueryController.class.getName());
	
	private ILogQueryPrx logQueryProxy = null;
	
	private String logQueryObjectName = "";
	private int maxMessagePerLogQuery = 1000;
	
	public IceLogMessageQueryController(String logQueryObjectName, int maxMessagePerLogQuery) {
		this.logQueryObjectName = logQueryObjectName;
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
			logs.add(LogObject.logEventToLogObject(messages[i]));
		}
		return logs;
	}
}
