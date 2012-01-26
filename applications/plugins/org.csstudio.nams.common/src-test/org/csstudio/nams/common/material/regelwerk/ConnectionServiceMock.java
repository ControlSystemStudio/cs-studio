/**
 * An almost empty mock for {@link IProcessVariableConnectionService}.
 */
package org.csstudio.nams.common.material.regelwerk;

import java.util.List;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IConnector;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.IProcessVariableWriteListener;
import org.csstudio.platform.simpledal.SettableState;
import org.csstudio.dal.Timestamp;
import org.junit.Assert;

class ConnectionServiceMock implements IProcessVariableConnectionService {

	@SuppressWarnings("unchecked")
	IProcessVariableValueListener _listener;


	public void sendNewConnectionState(final ConnectionState state) {
		this._listener.connectionStateChanged(state);
	}

	@SuppressWarnings("unchecked")
	public void sendNewValue(final Object value) {
		this._listener.valueChanged(value, new Timestamp());
	}

	public List<IConnector> getConnectors() {
		Assert.fail("unexpected call of method.");
		return null;
	}

	public SettableState checkWriteAccessSynchronously(IProcessVariableAddress pv) {
		Assert.fail("unexpected call of method.");
		return null;
	}

	public void readValueAsynchronously(IProcessVariableAddress processVariableAddress, ValueType valueType,
			IProcessVariableValueListener listener) {
		Assert.fail("unexpected call of method.");
		
	}

	public <E> E readValueSynchronously(IProcessVariableAddress processVariableAddress, ValueType valueType) throws ConnectionException {
		Assert.fail("unexpected call of method.");
		return null;
	}

	public void register(IProcessVariableValueListener listener, IProcessVariableAddress pv, ValueType valueType) {
		_listener = listener;
		
	}

	public void unregister(IProcessVariableValueListener listener) {
		Assert.fail("unexpected call of method.");
	}

	public void writeValueAsynchronously(IProcessVariableAddress processVariableAddress, Object value, ValueType expectedValueType, IProcessVariableWriteListener listener) {
		Assert.fail("unexpected call of method.");
	}

	public boolean writeValueSynchronously(IProcessVariableAddress processVariableAddress, Object value, ValueType expectedValueType) {
		Assert.fail("unexpected call of method.");
		return false;
	}

	public int getNumberOfActiveConnectors() {
		// TODO Auto-generated method stub
		return 0;
	}

}