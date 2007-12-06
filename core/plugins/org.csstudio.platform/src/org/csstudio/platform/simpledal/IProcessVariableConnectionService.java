package org.csstudio.platform.simpledal;

import org.csstudio.platform.model.pvs.DalPropertyTypes;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;

/**
 * A service to connect to a process variable.
 * 
 * You may use the {@link ProcessVariableConnectionServiceFactory} to create
 * instances of this service.
 * 
 * @author C1 WPS / SW, MZ
 */
public interface IProcessVariableConnectionService {

	/**
	 * Sets a value for the specified process variable.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * 
	 * @param value
	 *            the value
	 * 
	 * @return true, if the value was set successful, false otherwise
	 */
	boolean setValue(IProcessVariableAddress processVariableAddress, long value);

	/**
	 * Sets a value for the specified process variable.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * 
	 * @param value
	 *            the value
	 * 
	 * @return true, if the value was set successful, false otherwise
	 */
	boolean setValue(IProcessVariableAddress processVariableAddress,
			long[] value);

	/**
	 * Sets a value for the specified process variable.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * 
	 * @param value
	 *            the value
	 * 
	 * @return true, if the value was set successful, false otherwise
	 */
	boolean setValue(IProcessVariableAddress processVariableAddress,
			double value);

	/**
	 * Sets a long value for the specified process variable.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * 
	 * @param value
	 *            the value
	 * 
	 * @return true, if the value was set successful, false otherwise
	 */
	boolean setValue(IProcessVariableAddress processVariableAddress,
			double[] value);

	/**
	 * Sets a long value for the specified process variable.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * 
	 * @param value
	 *            the value
	 * 
	 * @return true, if the value was set successful, false otherwise
	 */
	boolean setValue(IProcessVariableAddress processVariableAddress,
			String value);

	/**
	 * Sets a long value for the specified process variable.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * 
	 * @param value
	 *            the value
	 * 
	 * @return true, if the value was set successful, false otherwise
	 */
	boolean setValue(IProcessVariableAddress processVariableAddress,
			String[] value);

	/**
	 * Sets a long value for the specified process variable.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * @param value
	 *            the value
	 * @param expectedValueType
	 *            the expected value type
	 * 
	 * @return true, if the value was set successful, false otherwise
	 */
	boolean setValue(IProcessVariableAddress processVariableAddress,
			Object value, ValueType expectedValueType);

	/**
	 * Sets a long value for the specified process variable.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * 
	 * @param value
	 *            the value
	 * 
	 * @return true, if the value was set successful, false otherwise
	 */
	boolean setValue(IProcessVariableAddress processVariableAddress,
			Object[] value);

	/**
	 * Sets a long value for the specified process variable.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * 
	 * @param value
	 *            the value
	 * 
	 * @return true, if the value was set successful, false otherwise
	 */
	boolean setValue(IProcessVariableAddress processVariableAddress, Enum value);

	/**
	 * Gets a value via an asynchronous call. When the value is read, the
	 * listener will receive a callback on it
	 * {@link IProcessVariableValueListener#valueChanged(Object)} method.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * @param valueType
	 *            the expected value type
	 * @param listener
	 *            the callback listener
	 */
	void getValueAsync(IProcessVariableAddress processVariableAddress,
			ValueType valueType, IProcessVariableValueListener<Double> listener);

	/**
	 * Gets a value via an asynchronous call. When the value is read, the
	 * listener will receive a callback on it
	 * {@link IProcessVariableValueListener#valueChanged(Object)} method.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * 
	 * @param listener
	 *            the callback listener
	 */
	void getValueAsyncAsLong(IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<Long> listener);

	/**
	 * Gets a value via an asynchronous call. When the value is read, the
	 * listener will receive a callback on it
	 * {@link IProcessVariableValueListener#valueChanged(Object)} method.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * 
	 * @param listener
	 *            the callback listener
	 */
	void getValueAsyncAsDouble(IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<Double> listener);

	/**
	 * Gets a value via an asynchronous call. When the value is read, the
	 * listener will receive a callback on it
	 * {@link IProcessVariableValueListener#valueChanged(Object)} method.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * 
	 * @param listener
	 *            the callback listener
	 */
	void getValueAsyncAsString(IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<String> listener);

	/**
	 * Gets a value via an asynchronous call. When the value is read, the
	 * listener will receive a callback on it
	 * {@link IProcessVariableValueListener#valueChanged(Object)} method.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * 
	 * @param listener
	 *            the callback listener
	 */
	void getValueAsyncAsObject(IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<Object> listener);

	/**
	 * Gets a value via an asynchronous call. When the value is read, the
	 * listener will receive a callback on it
	 * {@link IProcessVariableValueListener#valueChanged(Object)} method.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * 
	 * @param listener
	 *            the callback listener
	 */
	void getValueAsyncAsEnum(IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<Enum> listener);

	/**
	 * Gets a value via an asynchronous call. When the value is read, the
	 * listener will receive a callback on it
	 * {@link IProcessVariableValueListener#valueChanged(Object)} method.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * 
	 * @param listener
	 *            the callback listener
	 */
	void getValueAsyncAsLongSequence(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<long[]> listener);

	/**
	 * Gets a value via an asynchronous call. When the value is read, the
	 * listener will receive a callback on it
	 * {@link IProcessVariableValueListener#valueChanged(Object)} method.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * 
	 * @param listener
	 *            the callback listener
	 */
	void getValueAsyncAsDoubleSequence(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<double[]> listener);

	/**
	 * Gets a value via an asynchronous call. When the value is read, the
	 * listener will receive a callback on it
	 * {@link IProcessVariableValueListener#valueChanged(Object)} method.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * 
	 * @param listener
	 *            the callback listener
	 */
	void getValueAsyncAsStringSequence(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<String[]> listener);

	/**
	 * Gets a value via an asynchronous call. When the value is read, the
	 * listener will receive a callback on it
	 * {@link IProcessVariableValueListener#valueChanged(Object)} method.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * 
	 * @param listener
	 *            the callback listener
	 */
	void getValueAsyncAsObjectSequence(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<Object[]> listener);

	/**
	 * Synchronous access to the current value of the specified process
	 * variable.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * @param valueType
	 *            the expected value type
	 * @return the current value
	 */
	Object getValue(IProcessVariableAddress processVariableAddress,
			ValueType valueType) throws ConnectionException;

	/**
	 * Synchronous access to the current value of the specified process
	 * variable.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * @return the current value
	 */
	long getValueAsLong(IProcessVariableAddress processVariableAddress)
			throws ConnectionException;

	/**
	 * Synchronous access to the current value of the specified process
	 * variable.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * @return the current value
	 */
	double getValueAsDouble(IProcessVariableAddress processVariableAddress)
			throws ConnectionException;

	/**
	 * Synchronous access to the current value of the specified process
	 * variable.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * @return the current value
	 */
	String getValueAsString(IProcessVariableAddress processVariableAddress)
			throws ConnectionException;

	/**
	 * Synchronous access to the current value of the specified process
	 * variable.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * @return the current value
	 */
	Object getValueAsObject(IProcessVariableAddress processVariableAddress)
			throws ConnectionException;

	/**
	 * Synchronous access to the current value of the specified process
	 * variable.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * @return the current value
	 */
	Enum getValueAsEnum(IProcessVariableAddress processVariableAddress)
			throws ConnectionException;

	/**
	 * Synchronous access to the current value of the specified process
	 * variable.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * @return the current value
	 */
	long[] getValueAsLongSequence(IProcessVariableAddress processVariableAddress)
			throws ConnectionException;

	/**
	 * Synchronous access to the current value of the specified process
	 * variable.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * @return the current value
	 */
	double[] getValueAsDoubleSequence(
			IProcessVariableAddress processVariableAddress)
			throws ConnectionException;

	/**
	 * Synchronous access to the current value of the specified process
	 * variable.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * @return the current value
	 */
	Object[] getValueAsObjectSequence(
			IProcessVariableAddress processVariableAddress)
			throws ConnectionException;

	/**
	 * Synchronous access to the current value of the specified process
	 * variable.
	 * 
	 * @param processVariableAddress
	 *            the process variable
	 * @return the current value
	 */
	String[] getValueAsStringSequence(
			IProcessVariableAddress processVariableAddress)
			throws ConnectionException;

	
	/**
	 * Unregisters the specified listener.
	 * 
	 * @param listener the listener
	 */
	void unregister(IProcessVariableValueListener listener);
	
	/**
	 * Registers a listener for the specified process variable.
	 * 
	 * @param listener
	 *            the listener to be informed about value changes
	 * @param pv
	 *            the process variable address
	 * @param valueType
	 *            the expected value type
	 */
	void register(IProcessVariableValueListener listener,
			IProcessVariableAddress pv, ValueType valueType);

	/**
	 * Registers a listener for the specified process variables.
	 * 
	 * @param listener
	 *            the listener to be informed about value changes
	 * @param pv
	 *            the process variable address
	 */
	void registerForLongValues(IProcessVariableValueListener<Long> listener,
			IProcessVariableAddress pv);

	/**
	 * Registers a listener for the specified process variables.
	 * 
	 * @param listener
	 *            the listener to be informed about value changes
	 * @param pv
	 *            the process variable address
	 */
	void registerForStringValues(
			IProcessVariableValueListener<String> listener,
			IProcessVariableAddress pv);

	/**
	 * Registers a listener for the specified process variables.
	 * 
	 * @param listener
	 *            the listener to be informed about value changes
	 * @param pv
	 *            the process variable address
	 */
	void registerForObjectValues(
			IProcessVariableValueListener<Object> listener,
			IProcessVariableAddress pv);

	/**
	 * Registers a listener for the specified process variables.
	 * 
	 * @param listener
	 *            the listener to be informed about value changes
	 * @param pv
	 *            the process variable address
	 */
	void registerForDoubleValues(
			IProcessVariableValueListener<Double> listener,
			IProcessVariableAddress pv);

	/**
	 * Registers a listener for the specified process variables.
	 * 
	 * @param listener
	 *            the listener to be informed about value changes
	 * @param pv
	 *            the process variable address
	 */
	void registerForEnumValues(IProcessVariableValueListener<Enum> listener,
			IProcessVariableAddress pv);

	/**
	 * Registers a listener for the specified process variables.
	 * 
	 * @param listener
	 *            the listener to be informed about value changes
	 * @param pv
	 *            the process variable address
	 */
	void registerForDoubleSequenceValues(
			IProcessVariableValueListener<double[]> listener,
			IProcessVariableAddress pv);

	/**
	 * Registers a listener for the specified process variables.
	 * 
	 * @param listener
	 *            the listener to be informed about value changes
	 * @param pv
	 *            the process variable address
	 */
	void registerForLongSequenceValues(
			IProcessVariableValueListener<long[]> listener,
			IProcessVariableAddress pv);

	/**
	 * Registers a listener for the specified process variables.
	 * 
	 * @param listener
	 *            the listener to be informed about value changes
	 * @param pv
	 *            the process variable address
	 */
	void registerForStringSequenceValues(
			IProcessVariableValueListener<String[]> listener,
			IProcessVariableAddress pv);

	/**
	 * Registers a listener for the specified process variables.
	 * 
	 * @param listener
	 *            the listener to be informed about value changes
	 * @param pv
	 *            the process variable address
	 */
	void registerForObjectSequenceValues(
			IProcessVariableValueListener<Object[]> listener,
			IProcessVariableAddress pv);

}
