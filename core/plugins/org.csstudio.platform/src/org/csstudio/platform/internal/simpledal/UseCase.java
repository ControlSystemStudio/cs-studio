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
