package org.csstudio.sds.internal.connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.sds.internal.model.test.TestWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.logic.ParameterDescriptor;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link ConnectionService}.
 * 
 * @author swende
 * 
 */
public final class ConnectionServiceTestWithMock {

	private Mockery mockery;

	private IConnectorFactory _connectorFactory;

	private Connector _connector1;

	private Connector _connector2;

	private Connector _connector3;

	private Connector _connector4;

	private Connector _connector5;

	private Connector _connector6;

	/**
	 * The service under test.
	 */
	private ConnectionService _service;

	/**
	 * Sample display model.
	 */
	private DisplayModel _displayModel1;

	/**
	 * Sample display model.
	 */
	private DisplayModel _displayModel2;

	/**
	 * Sample input channel name.
	 */
	private static final String INPUT_CHANNEL_1 = "INPUT_CHANNEL_1";

	/**
	 * Sample input channel name.
	 */
	private static final String INPUT_CHANNEL_2 = "INPUT_CHANNEL_2";

	/**
	 * Sample input channel name.
	 */
	private static final String INPUT_CHANNEL_3 = "INPUT_CHANNEL_3";

	/**
	 * Sample input channel name.
	 */
	private static final String INPUT_CHANNEL_4 = "INPUT_CHANNEL_4";

	/**
	 * Sample output channel name.
	 */
	private static final String OUTPUT_CHANNEL_1 = "OUTPUT_CHANNEL_1";

	/**
	 * Sample refresh rate.
	 */
	private static final int REFRESH_RATE = 45;

	/**
	 * Sample alias name.
	 */
	private static final String TEST_ALIAS_NAME = "TEST_ALIAS_NAME";

	/**
	 * Sample alias value.
	 */
	private static final String TEST_ALIAS_VALUE = "TEST_ALIAS_VALUE";

	private DisplayModel createDisplayModel(String[] inChannels,
			String outChannel) {
		DisplayModel m = new DisplayModel();

		TestWidgetModel w = new TestWidgetModel();
		WidgetProperty p = w.getProperty(TestWidgetModel.PROP_TEST);
		DynamicsDescriptor d = new DynamicsDescriptor();

		for (String channelName : inChannels) {
			d
					.addInputChannel(new ParameterDescriptor(channelName,
							Double.class));
		}

		if (outChannel != null) {
			d
					.setOutputChannel(new ParameterDescriptor(outChannel,
							Double.class));
		}
		p.setDynamicsDescriptor(d);

		w.addAlias(TEST_ALIAS_NAME, TEST_ALIAS_VALUE);

		m.addWidget(w);

		return m;
	}

	/**
	 * @throws java.lang.Exception
	 *             an Exception
	 */
	@Before
	public void setUp() throws Exception {
		// create a mock for the connector factory
		mockery = new Mockery() {
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};

		// prepare mocks
		_connectorFactory = mockery.mock(IConnectorFactory.class);
		_connector1 = mockery.mock(Connector.class);
		_connector2 = mockery.mock(Connector.class);
		_connector3 = mockery.mock(Connector.class);
		_connector4 = mockery.mock(Connector.class);
		_connector5 = mockery.mock(Connector.class);
		_connector6 = mockery.mock(Connector.class);

		// create connection service
		_service = ConnectionService.getInstance();
		_service.overrideFactories(_connectorFactory);

		// setup a first display model with 3 input channels (1-3) and 1 output
		// channel
		_displayModel1 = createDisplayModel(new String[] { INPUT_CHANNEL_1,
				INPUT_CHANNEL_2, INPUT_CHANNEL_3 }, null);

		// setup a second display model with 2 input channels (3,4) (Note:
		// channel 3 is used in both models)
		_displayModel2 = createDisplayModel(new String[] { INPUT_CHANNEL_2,
				INPUT_CHANNEL_3, INPUT_CHANNEL_4 }, null);

	}

	@Test
	public void testConnectDisconnectProcedureMock() {
		mockery.checking(new Expectations() {
			{
				// max. 6 connectors should be created
				atMost(6).of(_connectorFactory).createConnector(
						with(any(IProcessVariableAddress.class)));

				will(onConsecutiveCalls(returnValue(_connector1),
						returnValue(_connector2), returnValue(_connector3),
						returnValue(_connector4), returnValue(_connector5),
						returnValue(_connector6)));

				// each of the 6 connectors should be once connected
				one(_connector1).doConnect();
				one(_connector2).doConnect();
				one(_connector3).doConnect();
				one(_connector4).doConnect();
				one(_connector5).doConnect();
				one(_connector6).doConnect();

				// each of the 6 connectors should be once disconnected
				one(_connector1).doDisconnect();
				one(_connector2).doDisconnect();
				one(_connector3).doDisconnect();
				one(_connector4).doDisconnect();
				one(_connector5).doDisconnect();
				one(_connector6).doDisconnect();
			}
		});

		// connect and disconnect model 1 and 2
		_service.connect(_displayModel1, REFRESH_RATE);
		assertEquals(3, _service.getStatesByChannel().size());
		assertEquals(1, _service.getStatesByWidgetModel().size());
		
		_service.connect(_displayModel2, REFRESH_RATE);
		assertEquals(6, _service.getStatesByChannel().size());
		assertEquals(2, _service.getStatesByWidgetModel().size());
		
		_service.disconnectModel(_displayModel1);
		assertEquals(3, _service.getStatesByChannel().size());
		assertEquals(1, _service.getStatesByWidgetModel().size());
		
		_service.disconnectModel(_displayModel2);
		assertTrue(_service.getStatesByChannel().isEmpty());
		assertTrue(_service.getStatesByWidgetModel().isEmpty());
		
		// check mocks
		mockery.assertIsSatisfied();
	}
}
