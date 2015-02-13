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



public class LogSubscriberDataModel implements LogMessageDataModel {

	private static final Logger logger = Logger.getLogger(LogSubscriberDataModel.class.getName());
	
	private List<LogObject> allMessages = new Vector<LogObject>();
	
	private IceLogMessageSubscriberController messageController = null;
	
	private int logViewMaxMessages = 1000;
		
	public LogSubscriberDataModel(String logMessageTopicName, int logViewMaxMessages, String logAdaptorName) {
		this.logViewMaxMessages  = logViewMaxMessages;
		messageController = new IceLogMessageSubscriberController(logMessageTopicName, logAdaptorName);
	}
	
	@Override
	public LogObject[] getMessages() {
		return allMessages.toArray(new LogObject[]{});
	}
	
	synchronized public void addMessage(LogObject logObject) {
		if (allMessages.size()>logViewMaxMessages) {
			removeMessages();
		}
		
		allMessages.add(logObject);
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
	
	public void start() throws Exception {
		messageController.subscribe(this);
	}

	public void stop() throws Exception {
		messageController.stop();		
	}	
}
