package org.csstudio.askap.utility.icemanager;

import Ice.Current;
import askap.interfaces.TypedValueInt;
import askap.interfaces.TypedValueType;
import askap.interfaces.monitoring.MonitorPoint;
import askap.interfaces.monitoring.PointStatus;
import askap.interfaces.monitoring._MonitoringProviderDisp;

public class MonitoringProviderStub extends _MonitoringProviderDisp {

	private static int count = 0;
	
	@Override
	public MonitorPoint[] get(String[] pointnames, Current __current) {
		
		MonitorPoint pointValues[] = new MonitorPoint[pointnames.length];
		
		for (int i=0; i<pointnames.length; i++) {
			MonitorPoint point = new MonitorPoint();
			point.name = pointnames[i];
			
			// Absolute time expressed as microseconds since MJD=0.
			point.timestamp = System.currentTimeMillis() * 1000;
			
			point.status = PointStatus.OK;
			
			point.value = new TypedValueInt(TypedValueType.TypeInt, count++);
			
			pointValues[i] = point;
		}
		
		return pointValues;
	}

}
