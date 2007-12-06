package org.csstudio.platform.internal.simpledal;

import org.csstudio.platform.internal.simpledal.local.ILocalChannelListener;
import org.csstudio.platform.internal.simpledal.local.LocalChannelPool;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.ValueType;

/**
 * Local Connectors are connected to a simulated channels that live in the local
 * JVM.
 * 
 * See {@link LocalChannelPool}.
 * 
 * @author Sven Wende
 * 
 */
@SuppressWarnings("unchecked")
class LocalConnector extends AbstractConnector implements ILocalChannelListener {

	private Object _latestReceivedValue=null;
	
	/**
	 * Constructor.
	 */
	public LocalConnector(IProcessVariableAddress pvAddress, ValueType valueType) {
		super(pvAddress, valueType);
		assert valueType != null;
	}

	public void valueChanged(Object value) {
		doForwardValue(value);
		_latestReceivedValue = value;
	}

	public ValueType getExpectedValueType() {
		return getValueType();
	}

}
