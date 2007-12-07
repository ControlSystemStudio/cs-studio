package org.csstudio.platform.internal.simpledal;

import java.util.Queue;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.csstudio.platform.simpledal.ProcessVariableValueAdapter;
import org.junit.Before;
import org.junit.Before;
import org.junit.Test;

public class UseCase {
	protected ProcessVariableAdressFactory _addressFactory;

	/**
	 * The service that is tested.
	 */
	protected IProcessVariableConnectionService _connectionService;

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		// the factory for pv addresses
		_addressFactory = ProcessVariableAdressFactory.getInstance();

		// the connection service
		_connectionService = ProcessVariableConnectionServiceFactory
				.getProcessVariableConnectionService();
	}

	@Test
	public void testSynchronousGet() {
		try {
			double value;

			System.out.println("testSynchronousGet()");

			// getting a simple value
			value = _connectionService.getValueAsDouble(_addressFactory
					.createProcessVariableAdress("dal-epics://Random:11"));
			System.out.println("simple -> " + value);

			// getting a characteristic
			value = _connectionService
					.getValueAsDouble(_addressFactory
							.createProcessVariableAdress("dal-epics://Random:11[graphMax]"));
			System.out.println("characteristic -> " + value);

			// getting a local value
			value = _connectionService.getValueAsDouble(_addressFactory
					.createProcessVariableAdress("local://something"));
			System.out.println("local -> " + value);

			// getting a local dynamic value
			value = _connectionService
					.getValueAsDouble(_addressFactory
							.createProcessVariableAdress("local://something RND:1:99:500"));
			System.out.println("local dynamic -> " + value);

		} catch (ConnectionException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAsynchronousGet() {
		System.out.println("testAsynchronousGet()");

		// getting a simple value
		_connectionService.getValueAsyncAsDouble(_addressFactory
				.createProcessVariableAdress("dal-epics://Random:11"),
				new ProcessVariableValueAdapter<Double>() {
					@Override
					public void valueChanged(Double value) {
						System.out.println("simple -> " + value);
					}
				});

		// getting a characteristic
		_connectionService
				.getValueAsyncAsDouble(
						_addressFactory
								.createProcessVariableAdress("dal-epics://Random:11[graphMax]"),
						new ProcessVariableValueAdapter<Double>() {
							@Override
							public void valueChanged(Double value) {
								System.out
										.println("characteristic -> " + value);
							}
						});

		// getting a local value
		_connectionService.getValueAsyncAsDouble(_addressFactory
				.createProcessVariableAdress("local://something"),
				new ProcessVariableValueAdapter<Double>() {
					@Override
					public void valueChanged(Double value) {
						System.out.println("local static -> " + value);
					}
				});

		// getting a local dynamic value
		_connectionService.getValueAsyncAsDouble(_addressFactory
				.createProcessVariableAdress("local://something RND:1:99:500"),
				new ProcessVariableValueAdapter<Double>() {
					@Override
					public void valueChanged(Double value) {
						System.out.println("local dynamic -> " + value);
					}
				});
	}

	@Test
	public void testRegisterListener() throws InterruptedException {
		System.out.println("testRegisterListener()");

		// a listener
		IProcessVariableValueListener<Double> listener = new IProcessVariableValueListener<Double>() {

			public void errorOccured(String error) {
				System.out.println(error);
			}

			public void valueChanged(Double value) {
				System.out.println(value);
			}

			public void connectionStateChanged(ConnectionState connectionState) {

			}

		};

		// getting a simple value
		_connectionService.registerForDoubleValues(
				new ProcessVariableValueAdapter<Double>() {
					@Override
					public void valueChanged(Double value) {
						System.out.println("simple -> " + value);
					}
				}, _addressFactory
						.createProcessVariableAdress("dal-epics://Random:11"));

		// getting a local dynamic value
		_connectionService
				.registerForDoubleValues(
						new ProcessVariableValueAdapter<Double>() {
							@Override
							public void valueChanged(Double value) {
								System.out.println("local dynamic -> " + value);
							}
						},
						_addressFactory
								.createProcessVariableAdress("local://something RND:1:99:1500"));
		
		Thread.sleep(10000);
	}

}
