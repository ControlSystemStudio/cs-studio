package org.csstudio.platform.model.rfc;

import org.epics.css.dal.context.RemoteInfo;

public interface IProcessVariableAdress {
	ControlSystemEnum getControlSystem();
	String getDevice();
	String getProperty();
	String getCharacteristic();
	RemoteInfo toDalRemoteInfo();
	String getRawName();
	String getFullName();
	boolean isCharacteristic();
}
