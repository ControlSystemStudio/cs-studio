package org.csstudio.platform.simpledal;

public interface IProcessVariableValueListener<E> {
	void valueChanged(E value);

	void connectionStateChanged(ConnectionState connectionState);
}
