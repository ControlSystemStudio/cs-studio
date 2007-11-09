package org.csstudio.platform.internal.simpledal;

import static org.junit.Assert.*;

import org.apache.activemq.state.ConnectionState;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.simpledal.ISimpleDalListener;
import org.junit.Before;
import org.junit.Test;

public class SimpleDalServiceTest {
	private SimpleDalService service;

	@Before
	public void setUp() throws Exception {
		service = new SimpleDalService();
	}

	@Test
	public void testRegisterForDoubleValues() throws Exception {
		ISimpleDalListener<Double> simpleDalListener = new ISimpleDalListener<Double>() {

			public void connectionStateChanged(ConnectionState connectionState) {
				System.out.println(connectionState);
			}

			public void valueChanged(Double value) {
				System.out.println(value);
			}

		};

		service.registerForDoubleValues(simpleDalListener,
				ProcessVariableAdressFactory.getInstance()
						.createProcessVariableAdress("Chiller:Pressure:1"));

		Thread.sleep(10000);
	}
}
