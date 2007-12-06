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
public abstract class AbstractProcessVariableConnectionServiceTest {
	private Queue<Runnable> _printQueue;

	protected ProcessVariableAdressFactory _pvFactory;

	/**
	 * The service that is tested.
	 */
	protected ProcessVariableConnectionService _service;

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
	}

	@Test
	public void testCleanup() {
	}

	@Test
	public void testMultipleTypeAccessToSameProcessVariable() {
	}

	@Test
	public void testEnumCharacteristics() throws InterruptedException {
		doConnectionTest(
				_pvFactory
						.createProcessVariableAdress("dal-epics://Chiller:Pressure:1.HSV[enumDescriptions], enum"),
				ValueType.ENUM);
		
		doConnectionTest(
				_pvFactory
						.createProcessVariableAdress("dal-epics://Chiller:Pressure:1.HSV[enumValues], enum"),
				ValueType.ENUM);
	}
	
	@Test
	public void testNumericCharacteristics() throws InterruptedException {
		// Numeric Characteristics
		doConnectionTest(
				_pvFactory
						.createProcessVariableAdress("dal-epics://Chiller:Pressure:1[resolution]"),
				ValueType.DOUBLE);

		doConnectionTest(
				_pvFactory
						.createProcessVariableAdress("dal-epics://Chiller:Pressure:1[minimum]"),
				ValueType.DOUBLE);

		doConnectionTest(
				_pvFactory
						.createProcessVariableAdress("dal-epics://Chiller:Pressure:1[maximum]"),
				ValueType.DOUBLE);

		doConnectionTest(
				_pvFactory
						.createProcessVariableAdress("dal-epics://Chiller:Pressure:1[graphMin]"),
				ValueType.DOUBLE);
		doConnectionTest(
				_pvFactory
						.createProcessVariableAdress("dal-epics://Chiller:Pressure:1[graphMax]"),
				ValueType.DOUBLE);
		doConnectionTest(
				_pvFactory
						.createProcessVariableAdress("dal-epics://Chiller:Pressure:1[format]"),
				ValueType.DOUBLE);
		doConnectionTest(
				_pvFactory
						.createProcessVariableAdress("dal-epics://Chiller:Pressure:1[units]"),
				ValueType.DOUBLE);
		
		// FIXME: Numeric Characteristic [scaleType] is not working. Maybe its an IOC configuration problem.
//		doAsyncGetValue(
//				_pvFactory
//						.createProcessVariableAdress("dal-epics://Chiller:Pressure:1[scaleType]"),
//				ValueType.DOUBLE);
		doConnectionTest(
				_pvFactory
						.createProcessVariableAdress("dal-epics://Chiller:Pressure:1[warningMax]"),
				ValueType.DOUBLE);
		doConnectionTest(
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

		doConnectionTest(
				_pvFactory
						.createProcessVariableAdress("dal-epics://Chiller:Pressure:1.HSV[enumDescriptions], enum"),
				ValueType.ENUM);

	}

	@Test
	public void testEpicsChannels() throws Exception {
		doConnectionTest(_pvFactory
				.createProcessVariableAdress("dal-epics://Random:21"),
				ValueType.DOUBLE);
		doConnectionTest(_pvFactory
				.createProcessVariableAdress("dal-epics://Random:22"),
				ValueType.DOUBLE_SEQUENCE);
		doConnectionTest(_pvFactory
				.createProcessVariableAdress("dal-epics://Random:23"),
				ValueType.LONG);
		doConnectionTest(_pvFactory
				.createProcessVariableAdress("dal-epics://Random:24"),
				ValueType.LONG_SEQUENCE);
		doConnectionTest(_pvFactory
				.createProcessVariableAdress("dal-epics://Random:25"),
				ValueType.STRING);
		doConnectionTest(_pvFactory
				.createProcessVariableAdress("dal-epics://Random:26"),
				ValueType.STRING_SEQUENCE);
		// FIXME: Its not working for type [Object]! Check!
		// doAsyncGetValue(_pvFactory.createProcessVariableAdress("dal-epics://Random:27"),
		// ValueType.OBJECT);
		// FIXME: Its not working for type [Object Sequence]! Check!
		// doAsyncGetValue(_pvFactory.createProcessVariableAdress("dal-epics://Random:28"),
		// ValueType.OBJECT_SEQUENCE);
		// FIXME: Its not working for type [Enum]! Check!
		// doAsyncGetValue(_pvFactory.createProcessVariableAdress("dal-epics://Random:29"),
		// ValueType.ENUM);
	}

	@Test
	public void testLocalChannels() throws Exception {
		doConnectionTest(_pvFactory
				.createProcessVariableAdress("local://Local:21 RND:1:99:500"),
				ValueType.DOUBLE);
		doConnectionTest(_pvFactory
				.createProcessVariableAdress("local://Local:22 RND:1:99:500"),
				ValueType.DOUBLE_SEQUENCE);
		doConnectionTest(_pvFactory
				.createProcessVariableAdress("local://Local:23 RND:1:99:500"),
				ValueType.LONG);
		doConnectionTest(_pvFactory
				.createProcessVariableAdress("local://Local:24 RND:1:99:500"),
				ValueType.LONG_SEQUENCE);
		doConnectionTest(_pvFactory
				.createProcessVariableAdress("local://Local:25 RND:1:99:500"),
				ValueType.STRING);
		doConnectionTest(_pvFactory
				.createProcessVariableAdress("local://Local:26 RND:1:99:500"),
				ValueType.STRING_SEQUENCE);
		doConnectionTest(_pvFactory
				.createProcessVariableAdress("local://Local:27 RND:1:99:500"),
				ValueType.OBJECT);
		doConnectionTest(_pvFactory
				.createProcessVariableAdress("local://Local:28 RND:1:99:500"),
				ValueType.OBJECT_SEQUENCE);
		doConnectionTest(_pvFactory
				.createProcessVariableAdress("local://Local:29 RND:1:99:500"),
				ValueType.ENUM);
		
		Thread.sleep(getSleepTime());
	}

	protected abstract long getSleepTime();
	
	protected abstract void doConnectionTest(
			IProcessVariableAddress processVariableAddress, ValueType valueType)
			throws InterruptedException;
	

	protected void print(final String s) {
		Runnable print = new Runnable() {

			public void run() {
				System.out.println(s);
			}
		};

		_printQueue.add(print);
	}

	
	protected void printReceivedValue(
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
	
	protected TestListener createProcessVariableValueListener(IProcessVariableAddress processVariableAddress,
			ValueType valueType) {
		return new TestListener(processVariableAddress, valueType);
	}

	
	class TestListener implements IProcessVariableValueListener {
		private List<Object> _receivedValues;
		private ValueType _valueType;
		private IProcessVariableAddress _processVariableAddress;

		public TestListener(
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
