package org.csstudio.askap.sb.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.Configuration;

import org.csstudio.askap.sb.Preferences;

public class DataCaptureDataModel {
	private static final Logger logger = Logger.getLogger(DataCaptureDataModel.class.getName());
	
	private boolean keepRunning = true;
	private DataChangeListener listener;

	private IceDataCaptureController controller = new IceDataCaptureController();
	
	private class DataCaptureStatusPolling implements Runnable {
		
		public synchronized void run() {
			logger.log(Level.INFO, "Start Data Capture Status polling thread");
			
			while (keepRunning) {
				DataChangeEvent event = new DataChangeEvent();
				try {
					long sbid = controller.getStatus();
					event.setChange(new Long(sbid));
				} catch (Exception e) {					
					logger.log(Level.WARNING, "Could not get status of DataCapture " + e.getMessage());
				}
				listener.dataChanged(event);
				
				try {
					this.wait(Preferences.getSBExecutionStatePollingPeriod());
				} catch (Exception e) {
					// nothing
					logger.log(Level.WARNING, "Error while waiting in polling thread", e);
				}
			}
			logger.log(Level.INFO, "Stop Data Capture Status polling thread");
		}
	}

				
	public DataCaptureDataModel() {
	}
	
	public void start(DataChangeListener l) {
		listener = l;
		
		Thread thread = new Thread(new DataCaptureStatusPolling());
		keepRunning = true;
		thread.start();
	}
	
	public void stop() {
		keepRunning = false;
	}

	public void stopDataCapture() throws Exception {
		controller.stop();
	}

}
