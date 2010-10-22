package org.csstudio.cagateway;

import java.sql.Timestamp;
import java.util.HashMap;

import ttf.doocs.clnt.EqAdr;
import ttf.doocs.clnt.EqCall;
import gov.aps.jca.CAException;
import gov.aps.jca.CAStatus;
import gov.aps.jca.cas.ProcessVariable;
import gov.aps.jca.cas.ProcessVariableEventCallback;
import gov.aps.jca.cas.ProcessVariableReadCallback;
import gov.aps.jca.cas.ProcessVariableWriteCallback;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Double;
import gov.aps.jca.dbr.DBR_TIME_Double;
import gov.aps.jca.dbr.TimeStamp;

public class PV extends ProcessVariable{

	
	private final HashMap<String, EqAdr> availableRemoteDevices;
	private final String aliasName;
	private TimeStamp 	epicsTimeStamp = null;

	

	public TimeStamp getEpicsTimsStamp() {
		return epicsTimeStamp;
	}

	public void setEpicsTimsStamp(TimeStamp epicsTimeStamp) {
		this.epicsTimeStamp = epicsTimeStamp;
	}

	public String getAliasName() {
		return aliasName;
	}

	public HashMap<String, EqAdr> getAvailableRemoteDevices() {
		return availableRemoteDevices;
	}

	public PV(String name, ProcessVariableEventCallback eventCallback, HashMap<String, EqAdr> availableRemoteDevices) {
		super(name, eventCallback);
		aliasName = name;
		this.availableRemoteDevices = availableRemoteDevices;
		
		
//		DoocsClient.getInstance().registerObjectInRemoteControlSystem(new Callback(), this);
	}
	
	class Callback implements RemoteControlSystemCallback {

		public void onChange(String object, Object oldValue, Object newValue) {
			DBR_Double d = new DBR_Double();
			
			d.getDoubleValue()[0] = (Double)newValue;
			eventCallback.postEvent(1, d);	
		}
		
	}
	
	@Override
	public DBRType getType() {
		return DBRType.DOUBLE;
	}

	@Override
	public CAStatus read(DBR arg0, ProcessVariableReadCallback arg1)
			throws CAException {
		System.out.println("Read value: ");
		//((DBR_TIME_Double)arg0).getDoubleValue()[0] = ((Mock)DoocsClient.getInstance().findChannelName(name) ).getPV();
//		((DBR_TIME_Double)arg0).setSeverity(0);
//		((DBR_TIME_Double)arg0).setStatus(0);
		((DBR_TIME_Double)arg0).setTimeStamp( epicsTimeStamp);

		return CAStatus.NORMAL;
	}

	@Override
	public CAStatus write(DBR arg0, ProcessVariableWriteCallback arg1)
			throws CAException {
		System.out.println("Writing value: ");
//		((DBR_TIME_Double)arg0).getDoubleValue()[0] = ((Mock)DoocsClient.getInstance().findChannelName(name) ).getPV();
		
//		System.out.println("Writing value: " +((Mock)DoocsClient.getInstance().findChannelName(name) ).getPV());
		
		return CAStatus.NORMAL;
	}

	public void setEpicsTimsStamp(long getTime) {
		// TODO Auto-generated method stub
		this.epicsTimeStamp = new TimeStamp(getTime); 
		
	}
}