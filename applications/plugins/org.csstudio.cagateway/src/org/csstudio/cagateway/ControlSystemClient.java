package org.csstudio.cagateway;

public interface ControlSystemClient {
	
	Object findChannelName(String channelName);
	
	void registerObjectInRemoteControlSystem(RemoteControlSystemCallback callback, Object object);
}