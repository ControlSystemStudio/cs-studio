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

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.csstudio.askap.utility.icemanager.LogObject;
import org.csstudio.askap.utility.icemanager.LogObject.LogQueryObject;

public class LogQueryDataModel implements LogMessageDataModel {

	private static final Logger logger = Logger.getLogger(LogQueryDataModel.class.getName());
	
	private List<LogObject> allMessages = new Vector<LogObject>();
	
	private IceLogMessageQueryController messageController = null;
	
	private int logViewMaxMessages = 1000;
	
	private LogQueryObject lastQuery = null;
		
	public LogQueryDataModel(String logQueryObjectName, int maxMessagePerLogQuery, int logViewMaxMessages) {
		this.logViewMaxMessages = logViewMaxMessages;
		messageController = new IceLogMessageQueryController(logQueryObjectName, maxMessagePerLogQuery);
	}
	
	@Override
	public LogObject[] getMessages() {
		return allMessages.toArray(new LogObject[]{});
	}	
	
	synchronized public void addMessages(List<LogObject> logObjects) {
		if (allMessages.size()>logViewMaxMessages) {
			removeMessages();
		}
		
		for (Iterator<LogObject> iter=logObjects.iterator(); iter.hasNext();) {
			LogObject logObject = iter.next();
			allMessages.add(logObject);
		}		
	}

	private void removeMessages() {
		// we'll remove 1/10 of messages each time
		for (int i=0; i<logViewMaxMessages/10; i++) {
			allMessages.remove(0);
		}
	}

	@Override
	public int getSize() {
		return allMessages.size();
	}

	public void clear() {
		allMessages.clear();
	}
	
	/**
	 * @param query
	 * @return
	 * @throws Exception 
	 */
	public void getLogMessage(LogQueryObject query) throws Exception {
		this.lastQuery  = query;
		
		java.util.List<LogObject> messages = messageController.getLogMessage(query);		
		this.addMessages(messages);				
	}
	
	public LogQueryObject getLastQuery() {
		return lastQuery;
	}
}
