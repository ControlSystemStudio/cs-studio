package org.csstudio.askap.utility.icemanager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import askap.interfaces.monitoring.MonitorPoint;
import askap.interfaces.monitoring.MonitoringProviderPrx;

/**
 * Manages all the monitoring points for a MonitoringProvider
 * You need to create one for each MonitoringProvider interface
 * 
 * @author wu049
 *
 */
public class MonitoringPointManager {

	private static final Logger logger = Logger.getLogger(MonitoringPointManager.class.getName());

	private Map<String, MonitorPointUpdater> pointUpdaters = new HashMap<String, MonitorPointUpdater>();
	private MonitoringProviderPrx monitorProxy = null;
	
	private boolean keepPolling = true;
	private String myAdaptorName = "";
	
	
	public MonitoringPointManager(String adaptorName) {
		myAdaptorName = adaptorName;
		
		Thread pointPollingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (keepPolling) {
					String[] pointNames = (String[]) pointUpdaters.keySet().toArray(new String[]{});
					if (pointNames!=null && pointNames.length>0) {
						try {
							
							if (monitorProxy == null) 
								monitorProxy = IceManager.getMonitoringProvider(myAdaptorName);
								
							MonitorPoint[] pointValues = monitorProxy.get(pointNames);							
							update(pointValues);
							
						} catch (Exception e) {
							logger.log(Level.WARNING, "Adaptor " + myAdaptorName + " disconnected: " + e.getMessage());
							monitorProxy = null;
							IceManager.removeMonitoringPointManager(myAdaptorName);
							// notify all listeners
							disconnected();
						}
					}
					
					try {
						Thread.sleep(Preferences.getMonitorPointPollingPeriod());
					} catch (InterruptedException e) {
						logger.log(Level.INFO, "Interrupted MonitoringPointManager pointPollingThread", e);						
					}
				}
			}
		});
		
		pointPollingThread.start();
	}
	
	public MonitoringProviderPrx getProxy() {
		return monitorProxy;
	}

	public void addListener(String pointNames[], MonitorPointListener listener) {
		if (pointNames!=null && pointNames.length>0) {
			for (int i=0; i<pointNames.length; i++) {
				String pointName = pointNames[i];
				
				MonitorPointUpdater updater = pointUpdaters.get(pointName);
				
				if (updater == null) {
					updater = new MonitorPointUpdater();
					pointUpdaters.put(pointName, updater);
				}
				
				updater.addListener(listener);
			}
		}
	}
	
	public void remove(String pointNames[], MonitorPointListener listener) {
		if (pointNames!=null && pointNames.length>0) {
			for (int i=0; i<pointNames.length; i++) {
				String pointName = pointNames[i];
				
				MonitorPointUpdater updater = pointUpdaters.get(pointName);
				
				if (updater != null) {
					updater.removeListener(listener);
				}
			}
		}
	}
	
	public void update(MonitorPoint points[]) {
		if (points!=null && points.length>0) {
			for (int i=0; i<points.length; i++) {
				MonitorPoint point = points[i];
				String pointName = point.name;
				MonitorPointUpdater updater = pointUpdaters.get(pointName);
				if (updater != null)
					updater.updateListeners(point);
			}
		}
	}

	public void disconnected() {
		for (Iterator<String> iter = pointUpdaters.keySet().iterator(); iter.hasNext();) {
			String pointName = iter.next();
			MonitorPointUpdater updater = pointUpdaters.get(pointName);
			updater.disconnected(pointName);
		}
	}

	
	public void stop() {
		keepPolling = false;
	}
	
}
