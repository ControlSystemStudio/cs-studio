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

import java.util.StringTokenizer;

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
	private String origins[] = null;
	

	
	public IceExecutiveLogController(String adaptorName, String topicName, String origin) {
		this.topicName = topicName;
		this.adaptorName = adaptorName;
		
		// a semi colon (;) separated list of origins
		// each origin is hierarchical. eg if you have askap.epics as an origin filter, you'll get logs
		// whose origins are askap.epics.xxx and askap.epics.xxx.yyy etc
		if (origin==null) {
			origins = new String[0];
		} else {
			StringTokenizer tokeniser = new StringTokenizer(origin, ";");
			origins = new String[tokeniser.countTokens()];
			for (int i=0; i<tokeniser.countTokens(); i++) {
				origins[i] = tokeniser.nextToken();
			}
		}
		
	}
			
	public void subscribe(final DataChangeListener messageReceiver) throws Exception{
		_ILoggerDisp callbackObj = new _ILoggerDisp() {
			public void send(ILogEvent event, Current current) {
				
				if (filterOrigin(event.origin)) {
					LogObject logObj = LogObject.logEventToLogObject(event);
					DataChangeEvent change = new DataChangeEvent();
					change.setChange(logObj);
					messageReceiver.dataChanged(change);
				}
			}
		};
		subscriber = IceManager.setupSubscriber(topicName, adaptorName, callbackObj);
	}
	
	private boolean filterOrigin(String origin) {
		
		if (origin==null || origin.trim().length()==0)
			return false;
		
		for (String originFilter : origins) {
			if (origin.equals(originFilter))
				return true;
			
			if (origin.startsWith(originFilter+"."))
				return true;
		}
		
		return false;
	}

	
	public void stop() throws Exception {
		IceManager.unsubscribe(topicName, adaptorName, subscriber);
	}
}
