/**
 * An almost empty mock for {@link IProcessVariableConnectionService}.
 */
package de.c1wps.desy.ams.allgemeines.regelwerk;

import java.util.List;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IConnectorStatistic;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.SettableState;
import org.csstudio.platform.simpledal.ValueType;
import org.junit.Assert;

class ConnectionServiceMock implements IProcessVariableConnectionService {

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
		Assert.fail("unexpected call of method.");
		return null;
	}

	public double getValueAsDouble(
			IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		Assert.fail("unexpected call of method.");
		return 0;
	}

	public double[] getValueAsDoubleSequence(
			IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		Assert.fail("unexpected call of method.");
		return null;
	}

	@SuppressWarnings("unchecked")
	public Enum getValueAsEnum(IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		Assert.fail("unexpected call of method.");
		return null;
	}

	public long getValueAsLong(IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		Assert.fail("unexpected call of method.");
		return 0;
	}

	public long[] getValueAsLongSequence(
			IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		Assert.fail("unexpected call of method.");
		return null;
	}

	public Object getValueAsObject(
			IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		Assert.fail("unexpected call of method.");
		return null;
	}

	public Object[] getValueAsObjectSequence(
			IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		Assert.fail("unexpected call of method.");
		return null;
	}

	public String getValueAsString(
			IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		Assert.fail("unexpected call of method.");
		return null;
	}

	public String[] getValueAsStringSequence(
			IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		Assert.fail("unexpected call of method.");
		return null;
	}

	public void getValueAsync(IProcessVariableAddress processVariableAddress,
			ValueType valueType, IProcessVariableValueListener<Double> listener) {
		Assert.fail("unexpected call of method.");
	}

	public void getValueAsyncAsDouble(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<Double> listener) {
		Assert.fail("unexpected call of method.");
	}

	public void getValueAsyncAsDoubleSequence(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<double[]> listener) {
		Assert.fail("unexpected call of method.");
	}

	@SuppressWarnings("unchecked")
	public void getValueAsyncAsEnum(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<Enum> listener) {
		Assert.fail("unexpected call of method.");
	}

	public void getValueAsyncAsLong(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<Long> listener) {
		Assert.fail("unexpected call of method.");
	}

	public void getValueAsyncAsLongSequence(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<long[]> listener) {
		Assert.fail("unexpected call of method.");
	}

	public void getValueAsyncAsObject(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<Object> listener) {
		Assert.fail("unexpected call of method.");
	}

	public void getValueAsyncAsObjectSequence(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<Object[]> listener) {
		Assert.fail("unexpected call of method.");
	}

	public void getValueAsyncAsString(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<String> listener) {
		Assert.fail("unexpected call of method.");
	}

	public void getValueAsyncAsStringSequence(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<String[]> listener) {
		Assert.fail("unexpected call of method.");
	}

	@SuppressWarnings("unchecked")
	public void register(IProcessVariableValueListener listener,
			IProcessVariableAddress pv, ValueType valueType) {
		Assert.fail("unexpected call of method.");
	}

	public void registerForDoubleSequenceValues(
			IProcessVariableValueListener<double[]> listener,
			IProcessVariableAddress pv) {
		Assert.fail("unexpected call of method.");
	}

	@SuppressWarnings("unchecked")
	public void registerForEnumValues(
			IProcessVariableValueListener<Enum> listener,
			IProcessVariableAddress pv) {
		Assert.fail("unexpected call of method.");
	}

	public void registerForLongSequenceValues(
			IProcessVariableValueListener<long[]> listener,
			IProcessVariableAddress pv) {
		Assert.fail("unexpected call of method.");
	}

	public void registerForObjectSequenceValues(
			IProcessVariableValueListener<Object[]> listener,
			IProcessVariableAddress pv) {
		Assert.fail("unexpected call of method.");
	}

	public void registerForObjectValues(
			IProcessVariableValueListener<Object> listener,
			IProcessVariableAddress pv) {
		Assert.fail("unexpected call of method.");
	}

	public void registerForStringSequenceValues(
			IProcessVariableValueListener<String[]> listener,
			IProcessVariableAddress pv) {
		Assert.fail("unexpected call of method.");
	}

	public boolean setValue(IProcessVariableAddress processVariableAddress,
			long value) {
		Assert.fail("unexpected call of method.");
		return false;
	}

	public boolean setValue(IProcessVariableAddress processVariableAddress,
			long[] value) {
		Assert.fail("unexpected call of method.");
		return false;
	}

	public boolean setValue(IProcessVariableAddress processVariableAddress,
			double value) {
		Assert.fail("unexpected call of method.");
		return false;
	}

	public boolean setValue(IProcessVariableAddress processVariableAddress,
			double[] value) {
		Assert.fail("unexpected call of method.");
		return false;
	}

	public boolean setValue(IProcessVariableAddress processVariableAddress,
			String value) {
		Assert.fail("unexpected call of method.");
		return false;
	}

	public boolean setValue(IProcessVariableAddress processVariableAddress,
			String[] value) {
		Assert.fail("unexpected call of method.");
		return false;
	}

	public boolean setValue(IProcessVariableAddress processVariableAddress,
			Object value, ValueType expectedValueType) {
		Assert.fail("unexpected call of method.");
		return false;
	}

	public boolean setValue(IProcessVariableAddress processVariableAddress,
			Object[] value) {
		Assert.fail("unexpected call of method.");
		return false;
	}

	
	@SuppressWarnings("unchecked")
	public boolean setValue(IProcessVariableAddress processVariableAddress,
			Enum value) {
		Assert.fail("unexpected call of method.");
		return false;
	}

	@SuppressWarnings("unchecked")
	public void unregister(IProcessVariableValueListener listener) {
		Assert.fail("unexpected call of method.");
	}

	public int getConnectorCount() {
		Assert.fail("unexpected call of method.");
		return 0;
	}

	public SettableState isSettable(IProcessVariableAddress pv) {
		Assert.fail("unexpected call of method.");
		return null;
	}

	public List<IConnectorStatistic> getConnectorStatistic() {
		Assert.fail("unexpected call of method.");
		return null;
	}
}