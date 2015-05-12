package org.csstudio.sds.ui.internal.connectionview;

import org.csstudio.dal.context.ConnectionState;

public class ConnectionDescriptor {

    private final String _channel;
    private ConnectionState _connectionState;

    public ConnectionDescriptor(String channel) {
        _channel = channel;
        _connectionState = ConnectionState.INITIAL;
    }

    public String getDescription() {
        return _channel + " [" + _connectionState + "]";
    }

    public void setConnectionState(ConnectionState state) {
        _connectionState = state;
    }

}
