package org.csstudio.platform.simpledal;

import org.apache.activemq.state.ConnectionState;

public interface ISimpleDalListener<E> {
	void valueChanged(E value);
	
	void connectionStateChanged(ConnectionState connectionState);
}
