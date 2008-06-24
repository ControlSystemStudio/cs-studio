package org.csstudio.platform.simpledal;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;

public interface IConnectorStatistic {

	int getListenerCount();
	
	ConnectionState getLatestConnectionState();

	Object getLatestValue();

	String getLatestError();

	/**
	 * Returns the process variable address.
	 * 
	 * @return the process variable address
	 */
	IProcessVariableAddress getProcessVariableAddress();

	ValueType getValueType();

}