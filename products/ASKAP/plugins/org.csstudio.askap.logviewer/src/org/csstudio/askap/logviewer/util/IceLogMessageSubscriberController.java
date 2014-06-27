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

import java.util.logging.Logger;

import org.csstudio.askap.utility.icemanager.IceManager;
import org.csstudio.askap.utility.icemanager.LogObject;

import Ice.Current;
import Ice.ObjectPrx;
import askap.interfaces.logging.ILogEvent;
import askap.interfaces.logging._ILoggerDisp;

public class IceLogMessageSubscriberController {

	public static final Logger log = Logger.getLogger(IceLogMessageSubscriberController.class.getName());
	
	private LogSubscriberDataModel dataModel = null;

	private String logMessageTopicName = "";
	
	private String adaptorName = null;
	
	ObjectPrx subscriber = null;
	
	public IceLogMessageSubscriberController(String logMessageTopicName, String adaptorName) {
		this.logMessageTopicName = logMessageTopicName;
		this.adaptorName = adaptorName;
	}
	
	public void subscribe(Object receiver) throws Exception{
		dataModel = (LogSubscriberDataModel) receiver;
		_ILoggerDisp callbackObj = new _ILoggerDisp() {
			public void send(ILogEvent event, Current current) {
				LogObject logObj = LogObject.logEventToLogObject(event);
				dataModel.addMessage(logObj);
			}
		};
		subscriber = IceManager.setupSubscriber(logMessageTopicName, adaptorName, callbackObj);
	}
		
	/**
	 * 
	 */
	public void stop() throws Exception {
		IceManager.unsubscribe(logMessageTopicName, adaptorName, subscriber);
	}
}
