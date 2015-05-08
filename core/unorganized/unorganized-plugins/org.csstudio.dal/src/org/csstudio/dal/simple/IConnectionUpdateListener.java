package org.csstudio.dal.simple;

import org.csstudio.dal.context.ConnectionState;

public interface IConnectionUpdateListener {

    public void connectionChanged(String channelName, ConnectionState state);

}
