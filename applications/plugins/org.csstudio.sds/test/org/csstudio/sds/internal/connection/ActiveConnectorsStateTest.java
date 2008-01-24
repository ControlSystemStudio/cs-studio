/**
 * 
 */
package org.csstudio.sds.internal.connection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.simpledal.ValueType;
import org.csstudio.sds.internal.connection.dal.SystemConnector;
import org.eclipse.osgi.framework.adaptor.EventPublisher;
import org.epics.css.dal.context.ConnectionState;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * Test class for {@link ActiveConnectorsState}.
 * 
 * @author Sven Wende
 * 
 */
public final class ActiveConnectorsStateTest {

	/**
	 * A test state.
	 */
	private ActiveConnectorsState _state;

	/**
	 */
	@Before
	public void setUp() {
		_state = new ActiveConnectorsState();
	}

	/**
	 * Test method for
	 * {@link org.csstudio.sds.internal.connection.ActiveConnectorsState#addConnector(org.csstudio.sds.internal.connection.ChannelReference, java.lang.Object)}
	 * and
	 * {@link org.csstudio.sds.internal.connection.ActiveConnectorsState#removeConnector(ChannelReference, Object)}.
	 */
	@Test
	public void testAddRemoveConnectors() throws Exception {
		IProcessVariableAddress reference1 = ProcessVariableAdressFactory.getInstance().createProcessVariableAdress("epics://Channel1");
		IProcessVariableAddress reference2 = ProcessVariableAdressFactory.getInstance().createProcessVariableAdress("epics://Channel2");

		SystemConnector connector1 = new SystemConnector(reference1, ValueType.DOUBLE, null);
		SystemConnector connector2 = new SystemConnector(reference2, ValueType.DOUBLE, null);
		SystemConnector connector3 = new SystemConnector(reference2, ValueType.DOUBLE, null);

		assertTrue(_state.getConnectors().isEmpty());

		// add several listeners
		_state.addConnector(reference1, connector1);
		_state.addConnector(reference2, connector2);
		_state.addConnector(reference2, connector3);

		assertTrue(_state.getConnectors().size() == 2);
		assertTrue(_state.getConnectors().containsKey(reference1));
		assertTrue(_state.getConnectors().containsKey(reference2));

		assertTrue(_state.getConnectors().get(reference1).contains(connector1));
		assertTrue(_state.getConnectors().get(reference2).contains(connector2));
		assertTrue(_state.getConnectors().get(reference2).contains(connector3));

		// remove some listeners
		_state.removeConnector(reference1, connector1);
		assertFalse(_state.getConnectors().containsKey(reference1));

		// illegal remove operations
		_state.removeConnector(reference2, connector1);
		assertTrue(_state.getConnectors().containsKey(reference2));
		assertTrue(_state.getConnectors().get(reference2).contains(connector2));
		assertTrue(_state.getConnectors().get(reference2).contains(connector3));

		// further remove
		_state.removeConnector(reference2, connector2);
		assertTrue(_state.getConnectors().containsKey(reference2));
		assertFalse(_state.getConnectors().get(reference2).contains(connector2));
		assertTrue(_state.getConnectors().get(reference2).contains(connector3));

		_state.removeConnector(reference2, connector3);
		assertTrue(_state.getConnectors().isEmpty());
	}

	/**
	 * A read connector for testing.
	 * 
	 * @author swende
	 * 
	 */
	final class TestConnector extends Connector {

		/**
		 * Constructor.
		 * 
		 * @param processVariable
		 *            the process variable
		 */
		public TestConnector(final IProcessVariableAddress processVariable) {
			super(processVariable);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void doConnect() {

		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void doDisconnect() {

		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void doProcessManualValueChange(final Object newValue) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void handleConnectionStateTransition(
				final ConnectionState oldState, final ConnectionState newState) {
		}
	}

}
