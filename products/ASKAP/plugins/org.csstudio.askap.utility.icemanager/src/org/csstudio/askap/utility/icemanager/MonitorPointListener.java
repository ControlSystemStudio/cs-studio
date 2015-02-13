package org.csstudio.askap.utility.icemanager;

import askap.interfaces.monitoring.MonitorPoint;

public interface MonitorPointListener {
	public void onUpdate(MonitorPoint point);
	public void disconnected(String pointName);
}
