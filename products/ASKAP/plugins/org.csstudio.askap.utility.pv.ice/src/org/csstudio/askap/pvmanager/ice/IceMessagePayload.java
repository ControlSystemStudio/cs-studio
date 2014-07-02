package org.csstudio.askap.pvmanager.ice;

import askap.interfaces.monitoring.MonitorPoint;

public class IceMessagePayload {
	
	String pointName = "";
	
	MonitorPoint pointValue = null;
	
	
	public IceMessagePayload() {
		
	}


	public String getPointName() {
		return pointName;
	}


	public void setPointName(String pointName) {
		this.pointName = pointName;
	}


	public MonitorPoint getPointValue() {
		return pointValue;
	}


	public void setPointValue(MonitorPoint pointValue) {
		this.pointValue = pointValue;
	}
	
	

}
