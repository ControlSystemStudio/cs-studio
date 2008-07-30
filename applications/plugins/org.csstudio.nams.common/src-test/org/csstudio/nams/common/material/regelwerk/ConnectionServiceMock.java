/**
 * An almost empty mock for {@link IProcessVariableConnectionService}.
 */
package org.csstudio.nams.common.material.regelwerk;

import java.util.List;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IConnectorStatistic;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.SettableState;
import org.csstudio.platform.simpledal.ValueType;
import org.epics.css.dal.Timestamp;
import org.junit.Assert;

class ConnectionServiceMock implements IProcessVariableConnectionService {

	@SuppressWarnings("unchecked")
	IProcessVariableValueListener _listener;

	public int getConnectorCount() {
		Assert.fail("unexpected call of method.");
		return 0;
	}

	public List<IConnectorStatistic> getConnectorStatistic() {
		Assert.fail("unexpected call of method.");
		return null;
	}

	public Object getValue(
			final IProcessVariableAddress processVariableAddress,
			final ValueType valueType) throws ConnectionException {
		Assert.fail("unexpected call of method.");
		return null;
	}

	public double getValueAsDouble(
			final IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		Assert.fail("unexpected call of method.");
		return 0;
	}

	public double[] getValueAsDoubleSequence(
			final IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		Assert.fail("unexpected call of method.");
		return null;
	}

	@SuppressWarnings("unchecked")
	public Enum getValueAsEnum(
			final IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		Assert.fail("unexpected call of method.");
		return null;
	}

	public long getValueAsLong(
			final IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		Assert.fail("unexpected call of method.");
		return 0;
	}

	public long[] getValueAsLongSequence(
			final IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		Assert.fail("unexpected call of method.");
		return null;
	}

	public Object getValueAsObject(
			final IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		Assert.fail("unexpected call of method.");
		return null;
	}

	public Object[] getValueAsObjectSequence(
			final IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		Assert.fail("unexpected call of method.");
		return null;
	}

	public String getValueAsString(
			final IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		Assert.fail("unexpected call of method.");
		return null;
	}

	public String[] getValueAsStringSequence(
			final IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		Assert.fail("unexpected call of method.");
		return null;
	}

	public void getValueAsync(
			final IProcessVariableAddress processVariableAddress,
			final ValueType valueType,
			final IProcessVariableValueListener<Double> listener) {
		Assert.fail("unexpected call of method.");
	}

	public void getValueAsyncAsDouble(
			final IProcessVariableAddress processVariableAddress,
			final IProcessVariableValueListener<Double> listener) {
		Assert.fail("unexpected call of method.");
	}

	public void getValueAsyncAsDoubleSequence(
			final IProcessVariableAddress processVariableAddress,
			final IProcessVariableValueListener<double[]> listener) {
		Assert.fail("unexpected call of method.");
	}

	@SuppressWarnings("unchecked")
	public void getValueAsyncAsEnum(
			final IProcessVariableAddress processVariableAddress,
			final IProcessVariableValueListener<Enum> listener) {
		Assert.fail("unexpected call of method.");
	}

	public void getValueAsyncAsLong(
			final IProcessVariableAddress processVariableAddress,
			final IProcessVariableValueListener<Long> listener) {
		Assert.fail("unexpected call of method.");
	}

	public void getValueAsyncAsLongSequence(
			final IProcessVariableAddress processVariableAddress,
			final IProcessVariableValueListener<long[]> listener) {
		Assert.fail("unexpected call of method.");
	}

	public void getValueAsyncAsObject(
			final IProcessVariableAddress processVariableAddress,
			final IProcessVariableValueListener<Object> listener) {
		Assert.fail("unexpected call of method.");
	}

	public void getValueAsyncAsObjectSequence(
			final IProcessVariableAddress processVariableAddress,
			final IProcessVariableValueListener<Object[]> listener) {
		Assert.fail("unexpected call of method.");
	}

	public void getValueAsyncAsString(
			final IProcessVariableAddress processVariableAddress,
			final IProcessVariableValueListener<String> listener) {
		Assert.fail("unexpected call of method.");
	}

	public void getValueAsyncAsStringSequence(
			final IProcessVariableAddress processVariableAddress,
			final IProcessVariableValueListener<String[]> listener) {
		Assert.fail("unexpected call of method.");
	}

	public SettableState isSettable(final IProcessVariableAddress pv) {
		Assert.fail("unexpected call of method.");
		return null;
	}

	@SuppressWarnings("unchecked")
	public void register(final IProcessVariableValueListener listener,
			final IProcessVariableAddress pv, final ValueType valueType) {
		Assert.fail("unexpected call of method.");
	}

	public void registerForDoubleSequenceValues(
			final IProcessVariableValueListener<double[]> listener,
			final IProcessVariableAddress pv) {
		Assert.fail("unexpected call of method.");
	}

	public void registerForDoubleValues(
			final IProcessVariableValueListener<Double> listener,
			final IProcessVariableAddress pv) {
		this._listener = listener;
	}

	@SuppressWarnings("unchecked")
	public void registerForEnumValues(
			final IProcessVariableValueListener<Enum> listener,
			final IProcessVariableAddress pv) {
		Assert.fail("unexpected call of method.");
	}

	public void registerForLongSequenceValues(
			final IProcessVariableValueListener<long[]> listener,
			final IProcessVariableAddress pv) {
		Assert.fail("unexpected call of method.");
	}

	public void registerForLongValues(
			final IProcessVariableValueListener<Long> listener,
			final IProcessVariableAddress pv) {
		this._listener = listener;
	}

	public void registerForObjectSequenceValues(
			final IProcessVariableValueListener<Object[]> listener,
			final IProcessVariableAddress pv) {
		Assert.fail("unexpected call of method.");
	}

	public void registerForObjectValues(
			final IProcessVariableValueListener<Object> listener,
			final IProcessVariableAddress pv) {
		Assert.fail("unexpected call of method.");
	}

	public void registerForStringSequenceValues(
			final IProcessVariableValueListener<String[]> listener,
			final IProcessVariableAddress pv) {
		Assert.fail("unexpected call of method.");
	}

	public void registerForStringValues(
			final IProcessVariableValueListener<String> listener,
			final IProcessVariableAddress pv) {
		this._listener = listener;
	}

	public void sendNewConnectionState(final ConnectionState state) {
		this._listener.connectionStateChanged(state);
	}

	@SuppressWarnings("unchecked")
	public void sendNewValue(final Object value) {
		this._listener.valueChanged(value, new Timestamp());
	}

	public boolean setValue(
			final IProcessVariableAddress processVariableAddress,
			final double value) {
		Assert.fail("unexpected call of method.");
		return false;
	}

	public boolean setValue(
			final IProcessVariableAddress processVariableAddress,
			final double[] value) {
		Assert.fail("unexpected call of method.");
		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean setValue(
			final IProcessVariableAddress processVariableAddress,
			final Enum value) {
		Assert.fail("unexpected call of method.");
		return false;
	}

	public boolean setValue(
			final IProcessVariableAddress processVariableAddress,
			final long value) {
		Assert.fail("unexpected call of method.");
		return false;
	}

	public boolean setValue(
			final IProcessVariableAddress processVariableAddress,
			final long[] value) {
		Assert.fail("unexpected call of method.");
		return false;
	}

	public boolean setValue(
			final IProcessVariableAddress processVariableAddress,
			final Object value, final ValueType expectedValueType) {
		Assert.fail("unexpected call of method.");
		return false;
	}

	public boolean setValue(
			final IProcessVariableAddress processVariableAddress,
			final Object[] value) {
		Assert.fail("unexpected call of method.");
		return false;
	}

	public boolean setValue(
			final IProcessVariableAddress processVariableAddress,
			final String value) {
		Assert.fail("unexpected call of method.");
		return false;
	}

	public boolean setValue(
			final IProcessVariableAddress processVariableAddress,
			final String[] value) {
		Assert.fail("unexpected call of method.");
		return false;
	}

	@SuppressWarnings("unchecked")
	public void unregister(final IProcessVariableValueListener listener) {
		Assert.fail("unexpected call of method.");
	}
}