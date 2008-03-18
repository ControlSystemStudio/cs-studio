/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
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
	 * Returns the number of active connectors.
	 * 
	 * @return the number of active connectors
	 */
	int getConnectorCount();

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
	 * @param listener
	 *            the listener
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

	/**
	 * Returns true if the specified process variable can be manipulated by the
	 * current user and false if the user is not allowed to set any values for
	 * that process variable
	 * 
	 * @param pv
	 *            the process variable address
	 * 
	 * @return true if the user can set a value for the specified process
	 *         variable, false otherwise
	 */
	boolean isSettable(IProcessVariableAddress pv);

}
