package org.csstudio.platform.internal.simpledal;

import static org.junit.Assert.*;

import org.apache.activemq.state.ConnectionState;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.junit.Before;
import org.junit.Test;

public class ProcessVariableConnectionServiceTest {
	private ProcessVariableConnectionService service;

	@Before
	public void setUp() throws Exception {
		service = new ProcessVariableConnectionService();
	}

	@Test
	public void testRegisterForDoubleValues() throws Exception {
		IProcessVariableValueListener<Double> simpleDalListener = new IProcessVariableValueListener<Double>() {

			public void valueChanged(Double value) {
				System.out.println(value);
			}

			public void connectionStateChanged(
					org.csstudio.platform.simpledal.ConnectionState connectionState) {
				System.out.println(connectionState);
			}

		};

		service.registerForDoubleValues(simpleDalListener,
				ProcessVariableAdressFactory.getInstance()
						.createProcessVariableAdress("Random:12"));

		service.registerForDoubleValues(simpleDalListener,
				ProcessVariableAdressFactory.getInstance()
						.createProcessVariableAdress("Random:12.HSV"));

		
		Thread.sleep(1000);
		
		simpleDalListener = null;
		
		System.gc();
		
		Thread.sleep(100000);
		
	}
}
