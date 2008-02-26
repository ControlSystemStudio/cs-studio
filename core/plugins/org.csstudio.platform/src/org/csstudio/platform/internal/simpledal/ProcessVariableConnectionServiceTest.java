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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.ValueType;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link ProcessVariableConnectionService}.
 * 
 * @author Sven Wende
 * 
 */
public class ProcessVariableConnectionServiceTest {
	private Queue<Runnable> _printQueue;

	private ProcessVariableAdressFactory _pvFactory;

	/**
	 * The service that is tested.
	 */
	private ProcessVariableConnectionService _service;

	private IProcessVariableAddress _epicsPv;

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		_pvFactory = ProcessVariableAdressFactory.getInstance();

		_printQueue = new ConcurrentLinkedQueue<Runnable>();

		Runnable printJob = new Runnable() {
			public void run() {
				while (true) {
					Runnable r;
					while ((r = _printQueue.poll()) != null) {
						r.run();
					}

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};

		Thread printThread = new Thread(printJob);
		printThread.start();

		_service = new ProcessVariableConnectionService();
		_epicsPv = ProcessVariableAdressFactory.getInstance()
				.createProcessVariableAdress("dal-epics://Chiller:Pressure:1");
	}

	@Test
	public void testCleanup() {
		assertTrue(false);
	}

	@Test
	public void testMultipleTypeAccessToSameProcessVariable() {
		assertTrue(false);
	}

	@Test
	public void testEnumCharacteristics() throws InterruptedException {
		doAsyncGetValue(
				_pvFactory
						.createProcessVariableAdress("dal-epics://Chiller:Pressure:1.HSV[enumDescriptions], enum"),
				ValueType.ENUM);
		
		doAsyncGetValue(
				_pvFactory
						.createProcessVariableAdress("dal-epics://Chiller:Pressure:1.HSV[enumValues], enum"),
				ValueType.ENUM);
	}
	
	@Test
	public void testNumericCharacteristics() throws InterruptedException {
		// Numeric Characteristics
		doAsyncGetValue(
				_pvFactory
						.createProcessVariableAdress("dal-epics://Chiller:Pressure:1[resolution]"),
				ValueType.DOUBLE);

		doAsyncGetValue(
				_pvFactory
						.createProcessVariableAdress("dal-epics://Chiller:Pressure:1[minimum]"),
				ValueType.DOUBLE);

		doAsyncGetValue(
				_pvFactory
						.createProcessVariableAdress("dal-epics://Chiller:Pressure:1[maximum]"),
				ValueType.DOUBLE);

		doAsyncGetValue(
				_pvFactory
						.createProcessVariableAdress("dal-epics://Chiller:Pressure:1[graphMin]"),
				ValueType.DOUBLE);
		doAsyncGetValue(
				_pvFactory
						.createProcessVariableAdress("dal-epics://Chiller:Pressure:1[graphMax]"),
				ValueType.DOUBLE);
		doAsyncGetValue(
				_pvFactory
						.createProcessVariableAdress("dal-epics://Chiller:Pressure:1[format]"),
				ValueType.DOUBLE);
		doAsyncGetValue(
				_pvFactory
						.createProcessVariableAdress("dal-epics://Chiller:Pressure:1[units]"),
				ValueType.DOUBLE);
		
		// FIXME: Numeric Characteristic [scaleType] is not working. Maybe its an IOC configuration problem.
//		doAsyncGetValue(
//				_pvFactory
//						.createProcessVariableAdress("dal-epics://Chiller:Pressure:1[scaleType]"),
//				ValueType.DOUBLE);
		doAsyncGetValue(
				_pvFactory
						.createProcessVariableAdress("dal-epics://Chiller:Pressure:1[warningMax]"),
				ValueType.DOUBLE);
		doAsyncGetValue(
				_pvFactory
						.createProcessVariableAdress("dal-epics://Chiller:Pressure:1[warningMin]"),
				ValueType.DOUBLE);
		
		
//		doAsyncGetValue(
//				_pvFactory
//						.createProcessVariableAdress("dal-epics://Chiller:Pressure:1[alarmMax]"),
//				ValueType.DOUBLE);
		
		// FIXME: Numeric Characteristic [alarmMin] is not working. Maybe its an IOC configuration problem.
//		doAsyncGetValue(
//				_pvFactory
//						.createProcessVariableAdress("dal-epics://Chiller:Pressure:1[alarmMin]"),
//				ValueType.DOUBLE);

		doAsyncGetValue(
				_pvFactory
						.createProcessVariableAdress("dal-epics://Chiller:Pressure:1.HSV[enumDescriptions], enum"),
				ValueType.ENUM);

	}

	@Test
	public void testAsyncGetValueOperationsForEpicsChannels() {
		assertTrue(false);
	}

	@Test
	public void testAsyncGetValueOperationsForLocalChannels() {
		assertTrue(false);
	}

	@Test
	public void testSyncGetValueOperationsForEpicsChannels() {
		doSyncGetValue(_pvFactory
				.createProcessVariableAdress("dal-epics://Random:21"),
				ValueType.DOUBLE);
		doSyncGetValue(_pvFactory
				.createProcessVariableAdress("dal-epics://Random:22"),
				ValueType.DOUBLE_SEQUENCE);
		doSyncGetValue(_pvFactory
				.createProcessVariableAdress("dal-epics://Random:23"),
				ValueType.LONG);
		doSyncGetValue(_pvFactory
				.createProcessVariableAdress("dal-epics://Random:24"),
				ValueType.LONG_SEQUENCE);
		doSyncGetValue(_pvFactory
				.createProcessVariableAdress("dal-epics://Random:25"),
				ValueType.STRING);
		doSyncGetValue(_pvFactory
				.createProcessVariableAdress("dal-epics://Random:26"),
				ValueType.STRING_SEQUENCE);
		// FIXME: Its not working for type [Object]! Check!
		// doSyncGetValueOperations(_pvFactory.createProcessVariableAdress("dal-epics://Random:27"),
		// ValueType.OBJECT);
		// FIXME: Its not working for type [Object Sequence]! Check!
		// doSyncGetValueOperations(_pvFactory.createProcessVariableAdress("dal-epics://Random:28"),
		// ValueType.OBJECT_SEQUENCE);
		// FIXME: Its not working for type [Enum]! Check!
		// doSyncGetValueOperations(_pvFactory.createProcessVariableAdress("dal-epics://Random:29"),
		// ValueType.ENUM);
	}

	@Test
	public void testSyncGetValueOperationsForLocalChannels() {
		doSyncGetValue(_pvFactory
				.createProcessVariableAdress("local://Local:21 RND:1:99:500"),
				ValueType.DOUBLE);
		doSyncGetValue(_pvFactory
				.createProcessVariableAdress("local://Local:22 RND:1:99:500"),
				ValueType.DOUBLE_SEQUENCE);
		doSyncGetValue(_pvFactory
				.createProcessVariableAdress("local://Local:23 RND:1:99:500"),
				ValueType.LONG);
		doSyncGetValue(_pvFactory
				.createProcessVariableAdress("local://Local:24 RND:1:99:500"),
				ValueType.LONG_SEQUENCE);
		doSyncGetValue(_pvFactory
				.createProcessVariableAdress("local://Local:25 RND:1:99:500"),
				ValueType.STRING);
		doSyncGetValue(_pvFactory
				.createProcessVariableAdress("local://Local:26 RND:1:99:500"),
				ValueType.STRING_SEQUENCE);
		doSyncGetValue(_pvFactory
				.createProcessVariableAdress("local://Local:27 RND:1:99:500"),
				ValueType.OBJECT);
		doSyncGetValue(_pvFactory
				.createProcessVariableAdress("local://Local:28 RND:1:99:500"),
				ValueType.OBJECT_SEQUENCE);
		doSyncGetValue(_pvFactory
				.createProcessVariableAdress("local://Local:29 RND:1:99:500"),
				ValueType.ENUM);
	}

	private void doAsyncGetValue(
			IProcessVariableAddress processVariableAddress, ValueType valueType)
			throws InterruptedException {

		ProcessVariableListener listener = new ProcessVariableListener(
				processVariableAddress, valueType);

		_service.getValueAsync(processVariableAddress, valueType, listener);

		Thread.sleep(3000);
		List<Object> values = listener.getReceivedValues();

		assertNotNull(values);
		assertFalse(values.isEmpty());

		Class expectedJavaType = valueType.getJavaType();

		for (Object value : values) {
			Class type = value.getClass();

			assertTrue("Values needs to be assignable from ["
					+ expectedJavaType + "]", expectedJavaType
					.isAssignableFrom(type));
		}

	}

	private void doSyncGetValue(IProcessVariableAddress processVariableAddress,
			ValueType valueType) {

		Object value = "";
		String error = "";
		try {
			value = _service.getValue(processVariableAddress, valueType);
		} catch (Throwable e) {
			error = e.getMessage();
		}

		printReceivedValue(processVariableAddress, valueType, value, error);

		assertNotNull(value);

		Class expectedJavaType = valueType.getJavaType();
		Class type = value.getClass();

		assertTrue("Values needs to be assignable from [" + expectedJavaType
				+ "]", expectedJavaType.isAssignableFrom(type));

	}

	/**
	 * Testcase for asynchronous GET methods of the service.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testAsyncGetValueOperations() throws InterruptedException {
		for (ValueType type : ValueType.values()) {
			Thread.sleep(1000);
			IProcessVariableValueListener listener = new ProcessVariableListener(
					_epicsPv, type);
			_service.getValueAsync(_epicsPv, type, listener);
		}

		Thread.sleep(100000);
	}

	private void print(final String s) {
		Runnable print = new Runnable() {

			public void run() {
				System.out.println(s);
			}
		};

		_printQueue.add(print);
	}

	private void printReceivedValue(
			final IProcessVariableAddress variableAddress,
			final ValueType requestedType, final Object value,
			final String error) {

		Runnable print = new Runnable() {

			public void run() {
				System.out
						.println("--------------------------------------------------------------");
				System.out.println("Process Variable	: "
						+ variableAddress.toString());
				System.out.println("Requested Type		: " + requestedType);

				StringBuffer valueAsString = new StringBuffer();

				if (value == null) {
					valueAsString.append("null");
				} else if (value instanceof Collection) {
					for (Object v : ((Collection) value)) {
						valueAsString.append(v.toString() + ",");
					}
				} else if (value instanceof double[]) {
					valueAsString.append(Arrays.toString((double[]) value));
				} else if (value instanceof long[]) {
					valueAsString.append(Arrays.toString((long[]) value));
				} else if (value instanceof String[]) {
					valueAsString.append(Arrays.toString((String[]) value));
				} else if (value instanceof Object[]) {
					valueAsString.append(Arrays.toString((Object[]) value));
				} else {
					valueAsString.append(value.toString());
				}

				System.out.println("Received Value		: "
						+ (value != null ? valueAsString.toString() : "-"));
				System.out.println("Value Type		: "
						+ (value != null ? value.getClass().getCanonicalName()
								: "-"));

				System.out.println("Errors			: "
						+ (error != null ? error : "-"));
				System.out
						.println("--------------------------------------------------------------");
			}
		};

		_printQueue.add(print);
	}

	@Test
	public void testRegisterOperationsForEpicsChannels() throws Exception {
		// EPICS channels
		doTestRegisterOperation(_pvFactory
				.createProcessVariableAdress("dal-epics://Random:11"),
				ValueType.DOUBLE);
		doTestRegisterOperation(_pvFactory
				.createProcessVariableAdress("dal-epics://Random:12"),
				ValueType.DOUBLE_SEQUENCE);
		doTestRegisterOperation(_pvFactory
				.createProcessVariableAdress("dal-epics://Random:13"),
				ValueType.LONG);
		doTestRegisterOperation(_pvFactory
				.createProcessVariableAdress("dal-epics://Random:14"),
				ValueType.LONG_SEQUENCE);
		doTestRegisterOperation(_pvFactory
				.createProcessVariableAdress("dal-epics://Random:15"),
				ValueType.STRING);
		doTestRegisterOperation(_pvFactory
				.createProcessVariableAdress("dal-epics://Random:16"),
				ValueType.STRING_SEQUENCE);
		// FIXME: Its not working for type [Object]! Check!
		// testRegisterOperation(_pvFactory.createProcessVariableAdress("dal-epics://Random:17"),
		// ValueType.OBJECT);
		// FIXME: Its not working for type [Object Sequence]! Check!
		// testRegisterOperation(_pvFactory.createProcessVariableAdress("dal-epics://Random:18"),
		// ValueType.OBJECT_SEQUENCE);
		// FIXME: Its not working for type [Enum]! Check!
		// testRegisterOperation(_pvFactory.createProcessVariableAdress("dal-epics://Random:19"),
		// ValueType.ENUM);
	}

	@Test
	public void testRegisterOperationsForLocalChannels() throws Exception {
		doTestRegisterOperation(_pvFactory
				.createProcessVariableAdress("local://Local:11 RND:1:99:500"),
				ValueType.DOUBLE);
		doTestRegisterOperation(_pvFactory
				.createProcessVariableAdress("local://Local:12 RND:1:99:500"),
				ValueType.DOUBLE_SEQUENCE);
		doTestRegisterOperation(_pvFactory
				.createProcessVariableAdress("local://Local:13 RND:1:99:500"),
				ValueType.LONG);
		doTestRegisterOperation(_pvFactory
				.createProcessVariableAdress("local://Local:14 RND:1:99:500"),
				ValueType.LONG_SEQUENCE);
		doTestRegisterOperation(_pvFactory
				.createProcessVariableAdress("local://Local:15 RND:1:99:500"),
				ValueType.STRING);
		doTestRegisterOperation(_pvFactory
				.createProcessVariableAdress("local://Local:16 RND:1:99:500"),
				ValueType.STRING_SEQUENCE);
		doTestRegisterOperation(_pvFactory
				.createProcessVariableAdress("local://Local:17 RND:1:99:500"),
				ValueType.OBJECT);
		doTestRegisterOperation(_pvFactory
				.createProcessVariableAdress("local://Local:18 RND:1:99:500"),
				ValueType.OBJECT_SEQUENCE);
		// FIXME: Its not working for type [Enum]! Check!
		// testRegisterOperation(_pvFactory.createProcessVariableAdress("local://Local:19
		// RND:1:99:500"), ValueType.ENUM);
	}

	private void doTestRegisterOperation(IProcessVariableAddress pv,
			ValueType valueType) throws Exception {
		print("Testcase: register a listener for process variable ["
				+ pv.toString() + "] and type [" + valueType + "]");

		ProcessVariableListener listener = new ProcessVariableListener(pv,
				valueType);

		_service.register(listener, pv, valueType);

		Thread.sleep(3000);

		assertTrue("The listener must receive more updates.", listener
				.getReceivedValues().size() > 0);

		for (Object value : new ArrayList(listener.getReceivedValues())) {
			Class expectedJavaType = valueType.getJavaType();
			Class type = value.getClass();

			assertNotNull(value);

			assertTrue("All values need to be assignable from ["
					+ expectedJavaType + "]", expectedJavaType
					.isAssignableFrom(type));
		}
	}

	class ProcessVariableListener implements IProcessVariableValueListener {
		private List<Object> _receivedValues;
		private ValueType _valueType;
		private IProcessVariableAddress _processVariableAddress;

		public ProcessVariableListener(
				IProcessVariableAddress processVariableAddress,
				ValueType valueType) {
			assert processVariableAddress != null;
			assert valueType != null;
			_processVariableAddress = processVariableAddress;
			_valueType = valueType;
			_receivedValues = new ArrayList<Object>();
		}

		public void connectionStateChanged(ConnectionState connectionState) {

		}

		public void errorOccured(String error) {
			printReceivedValue(_processVariableAddress, _valueType, null, error);
		}

		public void valueChanged(Object value) {
			_receivedValues.add(value);
			printReceivedValue(_processVariableAddress, _valueType, value, null);
		}

		public List<Object> getReceivedValues() {
			return _receivedValues;
		}
	};
}
