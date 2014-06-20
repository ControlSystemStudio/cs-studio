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

package org.csstudio.askap.sb.util;

import org.csstudio.askap.utility.icemanager.IceManager;
import org.csstudio.askap.utility.icemanager.LogObject;

import Ice.Current;
import Ice.ObjectPrx;
import askap.interfaces.logging.ILogEvent;
import askap.interfaces.logging._ILoggerDisp;

/**
 * @author wu049
 * @created Jun 29, 2010
 *
 */
public class IceExecutiveLogController {
	private ObjectPrx subscriber = null;
	private String topicName = null;
	private String adaptorName = null;
	private String tagName = null;
	

	
	public IceExecutiveLogController(String adaptorName, String topicName, String tagName) {
		this.topicName = topicName;
		this.adaptorName = adaptorName;
		this.tagName  = tagName;
	}
			
	public void subscribe(final DataChangeListener messageReceiver) throws Exception{
		_ILoggerDisp callbackObj = new _ILoggerDisp() {
			public void send(ILogEvent event, Current current) {
				if (event.tag!=null && event.tag.equals(tagName)) {				
					LogObject logObj = LogObject.logEventToLogObject(event);
					DataChangeEvent change = new DataChangeEvent();
					change.setChange(logObj);
					messageReceiver.dataChanged(change);
				}
			}
		};
		subscriber = IceManager.setupSubscriber(topicName, adaptorName, callbackObj);
	}
	
	public void stop() throws Exception {
		IceManager.unsubscribe(topicName, subscriber);
	}
}
