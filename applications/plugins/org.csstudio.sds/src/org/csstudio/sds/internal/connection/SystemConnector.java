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
 package org.csstudio.sds.internal.connection;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.csstudio.platform.simpledal.ValueType;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.IPropertyChangeListener;
import org.epics.css.dal.Timestamp;

/**
 * Connector implementation that deals with the Data Access Layer (DAL).
 * 
 * @author Sven Wende
 * 
 */
@SuppressWarnings("unchecked")
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
	}

	public void connectionStateChanged(ConnectionState connectionState) {
		_eventProcessor.connectionStateChanged(connectionState);
	}

	public void valueChanged(Object value, Timestamp timestamp) {
		if (_eventProcessor != null && value!=null) {
			_eventProcessor.valueChanged(value);
		}
	}

	public void dynamicsDescriptorChanged(DynamicsDescriptor dynamicsDescriptor) {
		// nothing to do
	}

	public void propertyManualValueChanged(String propertyId, Object manualValue) {
		ProcessVariableConnectionServiceFactory.getDefault()
				.getProcessVariableConnectionService().writeValueAsynchronously(
						_processVariableAddress, manualValue, _valueType, null);
	}

	public void propertyValueChanged(Object oldValue, Object newValue) {
		// nothing to do

	}

	public void errorOccured(String error) {
	}	
}
