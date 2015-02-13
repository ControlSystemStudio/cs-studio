package org.csstudio.askap.utility.icemanager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import askap.interfaces.monitoring.MonitorPoint;

public class MonitorPointUpdater {
	
	private List<MonitorPointListener> listeners = new ArrayList<MonitorPointListener>();
	
	public MonitorPointUpdater() {
		
	}
	
	public void addListener(MonitorPointListener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}
	
	public boolean removeListener(MonitorPointListener listener) {
		return listeners.remove(listener);
	}

	public void disconnected(final String pointName) {
		Thread disconnectThread = new Thread(new Runnable() {
			@Override
			public void run() {
				for (Iterator<MonitorPointListener> iter = listeners.iterator(); iter.hasNext();) {
					MonitorPointListener listener = iter.next();
					listener.disconnected(pointName);
				}
			}
		});
		
		disconnectThread.start();
		
	}
	
	public void updateListeners(final MonitorPoint point) {
		
		Thread updateThread = new Thread(new Runnable() {
			@Override
			public void run() {
				for (Iterator<MonitorPointListener> iter = listeners.iterator(); iter.hasNext();) {
					MonitorPointListener listener = iter.next();
					listener.onUpdate(point);
				}
			}
		});
		
		if (!listeners.isEmpty())
			updateThread.start();
	}

}
