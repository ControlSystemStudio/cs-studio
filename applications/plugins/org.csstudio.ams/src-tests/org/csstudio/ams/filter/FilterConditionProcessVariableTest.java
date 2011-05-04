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
 package org.csstudio.ams.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import javax.jms.MapMessage;

import org.csstudio.ams.dbAccess.configdb.FilterConditionProcessVariableTObject;
import org.csstudio.ams.filter.FilterConditionProcessVariable.Operator;
import org.csstudio.ams.filter.FilterConditionProcessVariable.SuggestedProcessVariableType;
import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IConnector;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.IProcessVariableWriteListener;
import org.csstudio.platform.simpledal.SettableState;
import org.epics.css.dal.simple.RemoteInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FilterConditionProcessVariableTest {

	private static final String TEST_CHANNEL_NAME = "test://helloWorldChannel.VAL";
	private static final int FILTER_CONDITION_ID = 42;
	private static final int FILTER_ID = 23;
	private FilterConditionProcessVariable _filterConditionPV;
	private ConnectionServiceMock _connectionServiceMock;

	@Before
	public void setUp() throws Exception {
		_filterConditionPV = new FilterConditionProcessVariable();
	}

	private IProcessVariableAddress createDefaultPVAdress() {
		return new IProcessVariableAddress() {

		    @Override
			public String getCharacteristic() {
				fail();
				return null;
			}

			@Override
			public ControlSystemEnum getControlSystem() {
				fail();
				return null;
			}

			@Override
			public String getDevice() {
				fail();
				return null;
			}

			@Override
			public String getFullName() {
				fail();
				return null;
			}

			@Override
			public String getProperty() {
				fail();
				return null;
			}

			@Override
			public String getRawName() {
				fail();
				return null;
			}

			@Override
			public boolean isCharacteristic() {
				fail();
				return false;
			}

			@Override
            public RemoteInfo toDalRemoteInfo() {
				fail();
				return null;
			}

			@Override
            public ValueType getValueTypeHint() {
				fail();
				return null;
			}

			@Override
            public IProcessVariableAddress deriveNoCharacteristicPart() {
				fail();
				return null;
			}

			@Override
            public IProcessVariableAddress deriveCharacteristic(String characteristic) {
				fail();
				return null;
			}
		};
	}

	@SuppressWarnings("synthetic-access")
    private IProcessVariableConnectionService createMockPVConnectionService() {
		_connectionServiceMock = new ConnectionServiceMock();
		return _connectionServiceMock;
	}

	private static class ConnectionServiceMock implements
			IProcessVariableConnectionService {

		@SuppressWarnings("unchecked")
		IProcessVariableValueListener _listener;
		
		@SuppressWarnings("unchecked")
		public void sendNewValue(Object value) {
			_listener.valueChanged(value, null);
		}

		public void sendNewConnectionState(ConnectionState state) {
			_listener.connectionStateChanged(state);
		}

		@Override
        public List<IConnector> getConnectors() {
			return null;
		}

		@Override
        public SettableState checkWriteAccessSynchronously(IProcessVariableAddress pv) {
			return null;
		}

		@Override
        public void readValueAsynchronously(IProcessVariableAddress processVariableAddress, ValueType valueType,
				IProcessVariableValueListener listener) {
		}

		@Override
        public <E> E readValueSynchronously(IProcessVariableAddress processVariableAddress, ValueType valueType) throws ConnectionException {
			return null;
		}

		@Override
        public void register(IProcessVariableValueListener listener, IProcessVariableAddress pv, ValueType valueType) {
			_listener = listener;
		}

		@Override
        public void unregister(IProcessVariableValueListener listener) {
			// 
		}

		@Override
        public void writeValueAsynchronously(IProcessVariableAddress processVariableAddress, Object value, ValueType expectedValueType, IProcessVariableWriteListener listener) {
		    // 
		}

		@Override
        public boolean writeValueSynchronously(IProcessVariableAddress processVariableAddress, Object value, ValueType expectedValueType) {
			return false;
		}

        @Override
        public int getNumberOfActiveConnectors()
        {
            return 0;
        }
		
	}

	private FilterConditionProcessVariableTObject createTObjectForTypeDouble(final Operator operator) {
		FilterConditionProcessVariableTObject configuration = new FilterConditionProcessVariableTObject(
				FILTER_CONDITION_ID, TEST_CHANNEL_NAME,
				operator, SuggestedProcessVariableType.DOUBLE,
				new Double(5.0));
		return configuration;
	}
	
	private FilterConditionProcessVariableTObject createTObjectForTypeLong(final Operator operator) {
		FilterConditionProcessVariableTObject configuration = new FilterConditionProcessVariableTObject(
				FILTER_CONDITION_ID, TEST_CHANNEL_NAME,
				operator, SuggestedProcessVariableType.LONG,
				new Long(5));
		return configuration;
	}

	private FilterConditionProcessVariableTObject createTObjectForTypeString(final Operator operator) {
		FilterConditionProcessVariableTObject configuration = new FilterConditionProcessVariableTObject(
				FILTER_CONDITION_ID, TEST_CHANNEL_NAME,
				operator, SuggestedProcessVariableType.STRING,
				"Foo");
		return configuration;
	}

	@After
	public void tearDown() throws Exception {
		_filterConditionPV = null;
		_connectionServiceMock = null;
	}

	@Test
	public void testMatchOfLongValuesSmallerThan5() throws Throwable {
		//init filtercondition 
		_filterConditionPV.doInit(createTObjectForTypeLong(Operator.SMALLER),
				FILTER_CONDITION_ID, FILTER_ID,
				createMockPVConnectionService(), createDefaultPVAdress());
		
		// Without connection:
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
		
		// Now all with connection...
		_connectionServiceMock.sendNewConnectionState(ConnectionState.CONNECTED);
		
		// Without any current value:
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
		
		// With a not matching value:
		_connectionServiceMock.sendNewValue(new Long(6));
		assertFalse(_filterConditionPV.match(createDummyMapMessage()));
		
		_connectionServiceMock.sendNewValue(new Long(5));
		assertFalse(_filterConditionPV.match(createDummyMapMessage()));
		
		// With matching value:
		_connectionServiceMock.sendNewValue(new Long(4));
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
	}

	@Test
	public void testMatchOfDoubleValuesSmallerThan5() throws Throwable {
		//init filtercondition 
		_filterConditionPV.doInit(createTObjectForTypeDouble(Operator.SMALLER),
				FILTER_CONDITION_ID, FILTER_ID,
				createMockPVConnectionService(), createDefaultPVAdress());
		
		// Without connection:
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
		
		// Now all with connection...
		_connectionServiceMock.sendNewConnectionState(ConnectionState.CONNECTED);
		
		// Without any current value:
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
		
		// With a not matching value:
		_connectionServiceMock.sendNewValue(new Double(6.0));
		assertFalse(_filterConditionPV.match(createDummyMapMessage()));
		
		_connectionServiceMock.sendNewValue(new Double(5.0));
		assertFalse(_filterConditionPV.match(createDummyMapMessage()));
		
		// With matching value:
		_connectionServiceMock.sendNewValue(new Double(4.9));
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
	}

	@Test
	public void testMatchOfLongValuesGreaterThan5() throws Throwable {
		//init filtercondition 
		_filterConditionPV.doInit(createTObjectForTypeLong(Operator.GREATER),
				FILTER_CONDITION_ID, FILTER_ID,
				createMockPVConnectionService(), createDefaultPVAdress());
		// Without connection:
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
		
		// Now all with connection...
		_connectionServiceMock.sendNewConnectionState(ConnectionState.CONNECTED);
		
		// Without any current value:
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
		
		// With a not matching value:
		_connectionServiceMock.sendNewValue(new Long(4));
		assertFalse(_filterConditionPV.match(createDummyMapMessage()));
		
		_connectionServiceMock.sendNewValue(new Long(5));
		assertFalse(_filterConditionPV.match(createDummyMapMessage()));
		
		// With matching value:
		_connectionServiceMock.sendNewValue(new Long(6));
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
	}
	
	@Test
	public void testMatchOfDoubleValuesGreaterThan5() throws Throwable {
		//init filtercondition 
		_filterConditionPV.doInit(createTObjectForTypeDouble(Operator.GREATER),
				FILTER_CONDITION_ID, FILTER_ID,
				createMockPVConnectionService(), createDefaultPVAdress());
		// Without connection:
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
		
		// Now all with connection...
		_connectionServiceMock.sendNewConnectionState(ConnectionState.CONNECTED);
		
		// Without any current value:
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
		
		// With a not matching value:
		_connectionServiceMock.sendNewValue(new Double(4.0));
		assertFalse(_filterConditionPV.match(createDummyMapMessage()));
		
		_connectionServiceMock.sendNewValue(new Double(5.0));
		assertFalse(_filterConditionPV.match(createDummyMapMessage()));
		
		// With matching value:
		_connectionServiceMock.sendNewValue(new Double(6.1));
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
	}
	
	@Test
	public void testMatchOfLongValuesEquals5() throws Throwable {
		//init filtercondition 
		_filterConditionPV.doInit(createTObjectForTypeLong(Operator.EQUALS),
				FILTER_CONDITION_ID, FILTER_ID,
				createMockPVConnectionService(), createDefaultPVAdress());
		// Without connection:
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
		
		// Now all with connection...
		_connectionServiceMock.sendNewConnectionState(ConnectionState.CONNECTED);
		
		// Without any current value:
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
		
		// With a not matching value:
		_connectionServiceMock.sendNewValue(new Long(4));
		assertFalse(_filterConditionPV.match(createDummyMapMessage()));
		
		_connectionServiceMock.sendNewValue(new Long(6));
		assertFalse(_filterConditionPV.match(createDummyMapMessage()));
		
		_connectionServiceMock.sendNewValue(new Long(50));
		assertFalse(_filterConditionPV.match(createDummyMapMessage()));
		
		// With matching value:
		_connectionServiceMock.sendNewValue(new Long(5));
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
	}
	
	@Test
	public void testMatchOfDoubleValuesEquals5() throws Throwable {
		//init filtercondition 
		_filterConditionPV.doInit(createTObjectForTypeDouble(Operator.EQUALS),
				FILTER_CONDITION_ID, FILTER_ID,
				createMockPVConnectionService(), createDefaultPVAdress());
		// Without connection:
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
		
		// Now all with connection...
		_connectionServiceMock.sendNewConnectionState(ConnectionState.CONNECTED);
		
		// Without any current value:
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
		
		// With a not matching value:
		_connectionServiceMock.sendNewValue(new Double(4.0));
		assertFalse(_filterConditionPV.match(createDummyMapMessage()));
		
		_connectionServiceMock.sendNewValue(new Double(5.1));
		assertFalse(_filterConditionPV.match(createDummyMapMessage()));
		
		// With matching value:
		_connectionServiceMock.sendNewValue(new Double(5.0));
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
		
		_connectionServiceMock.sendNewValue(new Double(5.0000001));
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
	}
	
	@Test
	public void testMatchOfLongValuesUnequals5() throws Throwable {
		//init filtercondition 
		_filterConditionPV.doInit(createTObjectForTypeLong(Operator.UNEQUALS),
				FILTER_CONDITION_ID, FILTER_ID,
				createMockPVConnectionService(), createDefaultPVAdress());
		// Without connection:
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
		
		// Now all with connection...
		_connectionServiceMock.sendNewConnectionState(ConnectionState.CONNECTED);
		
		// Without any current value:
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
		
		// With a not matching value:
		_connectionServiceMock.sendNewValue(new Long(5));
		assertFalse(_filterConditionPV.match(createDummyMapMessage()));
		
		// With matching value:
		_connectionServiceMock.sendNewValue(new Long(4));
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
		
		_connectionServiceMock.sendNewValue(new Long(6));
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
		
		_connectionServiceMock.sendNewValue(new Long(50));
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
	}
	
	@Test
	public void testMatchOfDoubleValuesUnequals5() throws Throwable {
		//init filtercondition 
		_filterConditionPV.doInit(createTObjectForTypeDouble(Operator.UNEQUALS),
				FILTER_CONDITION_ID, FILTER_ID,
				createMockPVConnectionService(), createDefaultPVAdress());
		// Without connection:
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
		
		// Now all with connection...
		_connectionServiceMock.sendNewConnectionState(ConnectionState.CONNECTED);
		
		// Without any current value:
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
		
		// With a not matching value:
		_connectionServiceMock.sendNewValue(new Double(5.0));
		assertFalse(_filterConditionPV.match(createDummyMapMessage()));
		
		_connectionServiceMock.sendNewValue(new Double(5.000001));
		assertFalse(_filterConditionPV.match(createDummyMapMessage()));
		
		// With matching value:
		_connectionServiceMock.sendNewValue(new Double(4.9));
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
		
		_connectionServiceMock.sendNewValue(new Double(5.1));
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
	}
	
	@Test
	public void testMatchOfStringValueEquals() throws Throwable {
		// init filtercondition
		_filterConditionPV.doInit(createTObjectForTypeString(Operator.EQUALS),
				FILTER_CONDITION_ID, FILTER_ID,
				createMockPVConnectionService(), createDefaultPVAdress());
		
		// Without connection:
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
		
		// Now all with connection...
		_connectionServiceMock.sendNewConnectionState(ConnectionState.CONNECTED);
		
		// Without any current value:
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
		
		// With a not matching value:
		_connectionServiceMock.sendNewValue("NotFoo");
		assertFalse(_filterConditionPV.match(createDummyMapMessage()));
		
		// With matching value:
		_connectionServiceMock.sendNewValue("Foo");
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
	}
	
	@Test
	public void testMatchOfStringValueUnequals() throws Throwable {
		// init filtercondition
		_filterConditionPV.doInit(createTObjectForTypeString(Operator.UNEQUALS),
				FILTER_CONDITION_ID, FILTER_ID,
				createMockPVConnectionService(), createDefaultPVAdress());
		
		// Without connection:
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
		
		// Now all with connection...
		_connectionServiceMock.sendNewConnectionState(ConnectionState.CONNECTED);
		
		// Without any current value:
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
		
		// With a not matching value:
		_connectionServiceMock.sendNewValue("Foo");
		assertFalse(_filterConditionPV.match(createDummyMapMessage()));
		
		// With matching value:
		_connectionServiceMock.sendNewValue("NotFoo");
		assertTrue(_filterConditionPV.match(createDummyMapMessage()));
	}
	
	@SuppressWarnings("unchecked")
	private MapMessage createDummyMapMessage() {
		return new MockMapMessage() {};
	}

}
