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

import askap.interfaces.monitoring.MonitorPoint;
import askap.interfaces.monitoring.MonitoringProviderPrx;

/**
 * @author wu049
 * @created Jun 29, 2010
 *
 */
public class IceMonitoringController {
	private MonitoringProviderPrx monitoringProxy = null;
	private String monitoringAdaptorName = null;

	public IceMonitoringController(String monitoringAdaptorName) {
	}
		
	synchronized public MonitorPoint[] getStatus(String pointNames[]) throws Exception {
		if (monitoringProxy==null)
			monitoringProxy = IceManager.getMonitoringProvider(monitoringAdaptorName);
		
		
		MonitorPoint[] points = monitoringProxy.get(pointNames);
		
		return points;
	}
}
