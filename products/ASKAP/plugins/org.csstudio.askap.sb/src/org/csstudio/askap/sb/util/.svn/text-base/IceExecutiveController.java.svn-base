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

import org.csstudio.askap.sb.Preferences;
import org.csstudio.askap.utility.icemanager.IceManager;

import Ice.LocalException;
import askap.interfaces.executive.AMI_IExecutiveService_abort;
import askap.interfaces.executive.AMI_IExecutiveService_stop;
import askap.interfaces.executive.IExecutiveServicePrx;

/**
 * @author wu049
 * @created Jun 29, 2010
 *
 */
public class IceExecutiveController {
	IExecutiveServicePrx executiveProxy = null;
	DataChangeListener listener = null;

	public IceExecutiveController() {
		
	}
		
	public void setDataChangedListener(DataChangeListener listener) {
		this.listener = listener;
	}
	                                 
	/* (non-Javadoc)
	 * @see askap.ui.operatordisplay.controller.ExecutiveController#abort()
	 */
	synchronized public void abort() throws Exception {
		if (executiveProxy==null)
			executiveProxy = IceManager.getExecutiveProxy(Preferences.getExecutiveIceName());
		
		executiveProxy.abort_async(new AMI_IExecutiveService_abort() {
			
			@Override
			public void ice_response() {
				listener.dataChanged(new DataChangeEvent());
			}
			
			@Override
			public void ice_exception(LocalException e) {
				DataChangeEvent event = new DataChangeEvent();
				event.setChange(new Exception(e));
				listener.dataChanged(event);
			}
		});
	}

	/* (non-Javadoc)
	 * @see askap.ui.operatordisplay.controller.ExecutiveController#start()
	 */
	public void start() throws Exception {
		if (executiveProxy==null)
			executiveProxy = IceManager.getExecutiveProxy(Preferences.getExecutiveIceName());
		
		executiveProxy.start();
	}

	/* (non-Javadoc)
	 * @see askap.ui.operatordisplay.controller.ExecutiveController#stop()
	 */
	public void stop() throws Exception {
		if (executiveProxy==null)
			executiveProxy = IceManager.getExecutiveProxy(Preferences.getExecutiveIceName());
		
		executiveProxy.stop_async(new AMI_IExecutiveService_stop() {			
			@Override
			public void ice_response() {
				listener.dataChanged(new DataChangeEvent());
			}
			
			@Override
			public void ice_exception(LocalException e) {
				DataChangeEvent event = new DataChangeEvent();
				event.setChange(new Exception(e));
				listener.dataChanged(event);
			}
		});
	}

}
