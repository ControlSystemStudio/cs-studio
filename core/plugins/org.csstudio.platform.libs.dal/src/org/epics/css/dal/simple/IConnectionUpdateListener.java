package org.epics.css.dal.simple;

import org.epics.css.dal.context.ConnectionState;

public interface IConnectionUpdateListener {
	
	public void connectionChanged(String channelName, ConnectionState state);

}
