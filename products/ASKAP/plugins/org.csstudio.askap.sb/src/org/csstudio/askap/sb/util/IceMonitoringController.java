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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.csstudio.askap.utility.icemanager.IceManager;
import org.csstudio.askap.utility.icemanager.MonitorPointListener;

/**
 * @author wu049
 * @created Jun 29, 2010
 *
 */
public class IceMonitoringController {
	private String monitoringAdaptorName = null;
	
	private Map<MonitorPointListener, PointListenerManager> pointListenerMap 
					= new HashMap<MonitorPointListener, PointListenerManager>();

	private class PointListenerManager {
		List<String> pointNameList = new ArrayList<>();
		
		void addPointNames(String pointNames[]) {
			if (pointNames==null)
				return;
			
			for (int i=0; i<pointNames.length; i++) {
				String name = pointNames[i];
				
				if (!pointNameList.contains(name)) {
					pointNameList.add(name);
				}
			}
		}
		
		void removePointNames(String pointNames[]) {
			if (pointNames==null)
				return;
			
			for (int i=0; i<pointNames.length; i++) {
				String name = pointNames[i];
				pointNameList.remove(name);
			}
		}
		
		String[] getPointNames() {
			return pointNameList.toArray(new String[]{});
		}
	}
	
	
	public IceMonitoringController(String monitoringAdaptorName) {
		this.monitoringAdaptorName = monitoringAdaptorName;
	}
		
	public void addMonitorPointListener(String pointNames[], MonitorPointListener listener) throws Exception {
		PointListenerManager manager = pointListenerMap.get(listener);
		if (manager==null)
			manager = new PointListenerManager();
		
		manager.addPointNames(pointNames);
		pointListenerMap.put(listener, manager);
		
		IceManager.addPointListener(pointNames, listener, monitoringAdaptorName);
	}

	public void removeMonitorPointListener(String pointNames[], MonitorPointListener listener) throws Exception {
		PointListenerManager manager = pointListenerMap.get(listener);
		if (manager!=null)
			manager.removePointNames(pointNames);
		
		IceManager.removePointListener(pointNames, listener, monitoringAdaptorName);
	}
	
	public void removeAllListeners() {
		for (Iterator<MonitorPointListener> iter = pointListenerMap.keySet().iterator(); iter.hasNext();) {
			MonitorPointListener listener = iter.next();
			PointListenerManager manager = pointListenerMap.get(listener);
			String pointNames[] = manager.getPointNames();
			
			IceManager.removePointListener(pointNames, listener, monitoringAdaptorName);
		}
	}	
}
