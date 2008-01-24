package org.csstudio.sds.internal.connection.dal;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.csstudio.platform.simpledal.ValueType;
import org.csstudio.platform.util.PerformanceUtil;
import org.csstudio.sds.internal.connection.ChannelInputProcessor;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.properties.IPropertyChangeListener;

/**
 * Connector implementation that deals with the Data Access Layer (DAL).
 * 
 * @author Sven Wende
 * 
 */
public final class SystemConnector implements IProcessVariableValueListener,
		IPropertyChangeListener {

	/**
	 * The process variable, that is managed by this connector.
	 */
	private IProcessVariableAddress _processVariableAddress;

	/**
	 * The event processor to which new dynamic values will be forwarded. Will
	 * be injected during runtime via a setter method.
	 */
	private ChannelInputProcessor _eventProcessor;

	/**
	 * The expected value type.
	 */
	private ValueType _valueType;

	/**
	 * Constructor.
	 * 
	 * @param processVariableAddress
	 *            the address of the process variable this connector should
	 *            connect to
	 * @param valueType
	 *            the expected value type
	 * @param processor
	 *            the event processor
	 */
	public SystemConnector(
			final IProcessVariableAddress processVariableAddress,
			ValueType valueType, ChannelInputProcessor processor) {
		assert processVariableAddress != null;
		assert valueType != null;
		// assert processor != null;
		_processVariableAddress = processVariableAddress;
		_valueType = valueType;
		_eventProcessor = processor;
		
		PerformanceUtil.getInstance().constructorCalled(this);
	}

	public void connectionStateChanged(
			org.csstudio.platform.simpledal.ConnectionState connectionState) {
		// FIXME: UI und hier auf SimpleState umstellen
		_eventProcessor.connectionStateChanged(connectionState.getDalState());
	}

	public void valueChanged(Object value) {
		if (_eventProcessor != null) {
			_eventProcessor.valueChanged(value);
		}
	}

	public void dynamicsDescriptorChanged(DynamicsDescriptor dynamicsDescriptor) {
		// nothing to do
	}

	public void propertyManualValueChanged(Object manualValue) {
		System.out.println("MAN VAL --> " + manualValue);
		ProcessVariableConnectionServiceFactory
				.getProcessVariableConnectionService().setValue(
						_processVariableAddress, manualValue, _valueType);
	}

	public void propertyValueChanged(Object oldValue, Object newValue) {
		// nothing to do

	}

	public void errorOccured(String error) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		PerformanceUtil.getInstance().finalizedCalled(this);
	}

}
