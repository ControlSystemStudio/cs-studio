/**
 * FIXME Almost empty ConnectionServiceMock
 */
package de.c1wps.desy.ams.allgemeines.regelwerk;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.SettableState;
import org.csstudio.platform.simpledal.ValueType;

class ConnectionServiceMock implements
		IProcessVariableConnectionService {

	@SuppressWarnings("unchecked")
	IProcessVariableValueListener _listener;

	public void registerForDoubleValues(
			IProcessVariableValueListener<Double> listener,
			IProcessVariableAddress pv) {
		_listener = listener;
	}

	public void registerForLongValues(
			IProcessVariableValueListener<Long> listener,
			IProcessVariableAddress pv) {
		_listener = listener;
	}

	public void registerForStringValues(
			IProcessVariableValueListener<String> listener,
			IProcessVariableAddress pv) {
		_listener = listener;
	}

	@SuppressWarnings("unchecked")
	public void sendNewValue(Object value) {
		_listener.valueChanged(value);
	}

	public void sendNewConnectionState(ConnectionState state) {
		_listener.connectionStateChanged(state);
	}

	public Object getValue(IProcessVariableAddress processVariableAddress,
			ValueType valueType) throws ConnectionException {
		return null;
	}

	public double getValueAsDouble(
			IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		return 0;
	}

	public double[] getValueAsDoubleSequence(
			IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		return null;
	}

	public Enum getValueAsEnum(
			IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		return null;
	}

	public long getValueAsLong(
			IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		return 0;
	}

	public long[] getValueAsLongSequence(
			IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		return null;
	}

	public Object getValueAsObject(
			IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		return null;
	}

	public Object[] getValueAsObjectSequence(
			IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		return null;
	}

	public String getValueAsString(
			IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		return null;
	}

	public String[] getValueAsStringSequence(
			IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		return null;
	}

	public void getValueAsync(
			IProcessVariableAddress processVariableAddress,
			ValueType valueType,
			IProcessVariableValueListener<Double> listener) {
	}

	public void getValueAsyncAsDouble(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<Double> listener) {
	}

	public void getValueAsyncAsDoubleSequence(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<double[]> listener) {
	}

	public void getValueAsyncAsEnum(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<Enum> listener) {
	}

	public void getValueAsyncAsLong(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<Long> listener) {
	}

	public void getValueAsyncAsLongSequence(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<long[]> listener) {
	}

	public void getValueAsyncAsObject(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<Object> listener) {
	}

	public void getValueAsyncAsObjectSequence(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<Object[]> listener) {
	}

	public void getValueAsyncAsString(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<String> listener) {
	}

	public void getValueAsyncAsStringSequence(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<String[]> listener) {
	}

	public void register(IProcessVariableValueListener listener,
			IProcessVariableAddress pv, ValueType valueType) {
	}

	public void registerForDoubleSequenceValues(
			IProcessVariableValueListener<double[]> listener,
			IProcessVariableAddress pv) {
	}

	public void registerForEnumValues(
			IProcessVariableValueListener<Enum> listener,
			IProcessVariableAddress pv) {
	}

	public void registerForLongSequenceValues(
			IProcessVariableValueListener<long[]> listener,
			IProcessVariableAddress pv) {
	}

	public void registerForObjectSequenceValues(
			IProcessVariableValueListener<Object[]> listener,
			IProcessVariableAddress pv) {
	}

	public void registerForObjectValues(
			IProcessVariableValueListener<Object> listener,
			IProcessVariableAddress pv) {
	}

	public void registerForStringSequenceValues(
			IProcessVariableValueListener<String[]> listener,
			IProcessVariableAddress pv) {
	}

	public boolean setValue(IProcessVariableAddress processVariableAddress,
			long value) {
		return false;
	}

	public boolean setValue(IProcessVariableAddress processVariableAddress,
			long[] value) {
		return false;
	}

	public boolean setValue(IProcessVariableAddress processVariableAddress,
			double value) {
		return false;
	}

	public boolean setValue(IProcessVariableAddress processVariableAddress,
			double[] value) {
		return false;
	}

	public boolean setValue(IProcessVariableAddress processVariableAddress,
			String value) {
		return false;
	}

	public boolean setValue(IProcessVariableAddress processVariableAddress,
			String[] value) {
		return false;
	}

	public boolean setValue(IProcessVariableAddress processVariableAddress,
			Object value, ValueType expectedValueType) {
		return false;
	}

	public boolean setValue(IProcessVariableAddress processVariableAddress,
			Object[] value) {
		return false;
	}

	public boolean setValue(IProcessVariableAddress processVariableAddress,
			Enum value) {
		return false;
	}

	public void unregister(IProcessVariableValueListener listener) {
	}

	public int getConnectorCount() {
		return 0;
	}

    public SettableState isSettable(IProcessVariableAddress pv)
    {
        return null;
    }
}