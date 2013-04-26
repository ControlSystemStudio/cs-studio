package org.remotercp.common.tracker;

public interface IGenericServiceListener<S> {

	public void bindService(S service);
	public void unbindService(S service);
	
}
