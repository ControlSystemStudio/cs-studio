package org.csstudio.askap.sb.util;

import org.csstudio.askap.sb.Preferences;
import org.csstudio.askap.utility.icemanager.IceManager;

import askap.interfaces.pksdatacapture.IPksDataCaptureServicePrx;

public class IceDataCaptureController {
	private IPksDataCaptureServicePrx dataCaptureService;
	
	public void stop() throws Exception{
		if (dataCaptureService==null)
			dataCaptureService = IceManager.getDataCaptureProxy(Preferences.getDataCaptureIceName());
		
		dataCaptureService.stop();
	}

	public long getStatus() throws Exception {
		if (dataCaptureService==null)
			dataCaptureService = IceManager.getDataCaptureProxy(Preferences.getDataCaptureIceName());
		
		return dataCaptureService.getState();
	}
}
