package org.csstudio.platform.model.rfc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test class for {@link ProcessVariable}.
 * 
 * Note: The test produces a bunch of console output that can be used to get an
 * impression of the process variable API.
 * 
 * @author Sven Wende
 * 
 */
public class ProcessVariableTest {
	private boolean _printToConsole = true;

	/**
	 * @throws Exception
	 */
	@Test
	public void testProcessVariableString() throws Exception {

		// RemoteInfo ri = new
		// RemoteInfo("//tine/myDevice/myProperty[myCharacteristic]");
		// print(ri);

		// test string conversions
		String input;
		ProcessVariable pv;

		// with control system, device, property, characteristic
		print("new ProcessVariable(\"//tine/myDevice/myProperty[myCharacteristic]\")");
		pv = new ProcessVariable("//tine/myDevice/myProperty[myCharacteristic]");
		print(pv);
		assertEquals(pv.getControlSystemEnum(), ControlSystemEnum.TINE);
		assertEquals(pv.getDevice(), "myDevice");
		assertEquals(pv.getProperty(), "myProperty");
		assertEquals(pv.getCharacteristic(), "myCharacteristic");
		assertEquals(pv.toFullString(),
				"//tine/myDevice/myProperty[myCharacteristic]");

		// with control system, device, property
		print("new ProcessVariable(\"//tine/myDevice/myProperty\")");
		pv = new ProcessVariable("//tine/myDevice/myProperty");
		print(pv);
		assertEquals(pv.getControlSystemEnum(), ControlSystemEnum.TINE);
		assertEquals(pv.getDevice(), "myDevice");
		assertEquals(pv.getProperty(), "myProperty");
		assertEquals(pv.getCharacteristic(), null);
		assertEquals(pv.toFullString(), "//tine/myDevice/myProperty");

		// with control system, property, characteristic
		print("new ProcessVariable(\"//tine/myProperty[myCharacteristic]\")");
		pv = new ProcessVariable("//tine/myProperty[myCharacteristic]");
		print(pv);
		assertEquals(pv.getControlSystemEnum(), ControlSystemEnum.TINE);
		assertEquals(pv.getDevice(), null);
		assertEquals(pv.getProperty(), "myProperty");
		assertEquals(pv.getCharacteristic(), "myCharacteristic");
		assertEquals(pv.toFullString(), "//tine/myProperty[myCharacteristic]");

		// with device, property, characteristic
		print("new ProcessVariable(\"/myDevice/myProperty[myCharacteristic]\")");
		pv = new ProcessVariable("/myDevice/myProperty[myCharacteristic]");
		print(pv);
		assertEquals(pv.getControlSystemEnum(),
				SomeWhere.DEFAULT_CONTROL_SYSTEM);
		assertEquals(pv.getDevice(), "myDevice");
		assertEquals(pv.getProperty(), "myProperty");
		assertEquals(pv.getCharacteristic(), "myCharacteristic");
		assertEquals(pv.toFullString(), "//"
				+ SomeWhere.DEFAULT_CONTROL_SYSTEM
						.getProcessVariableUriRepresentation()
				+ "/myDevice/myProperty[myCharacteristic]");

		// with property, characteristic
		print("new ProcessVariable(\"myProperty[myCharacteristic]\")");
		pv = new ProcessVariable("myProperty[myCharacteristic]");
		print(pv);
		assertEquals(pv.getControlSystemEnum(),
				SomeWhere.DEFAULT_CONTROL_SYSTEM);
		assertEquals(pv.getDevice(), null);
		assertEquals(pv.getProperty(), "myProperty");
		assertEquals(pv.getCharacteristic(), "myCharacteristic");
		assertEquals(pv.toFullString(), "//"
				+ SomeWhere.DEFAULT_CONTROL_SYSTEM
						.getProcessVariableUriRepresentation()
				+ "/myProperty[myCharacteristic]");

		// with property
		print("new ProcessVariable(\"myProperty\")");
		pv = new ProcessVariable("myProperty");
		print(pv);
		assertEquals(pv.getControlSystemEnum(),
				SomeWhere.DEFAULT_CONTROL_SYSTEM);
		assertEquals(pv.getDevice(), null);
		assertEquals(pv.getProperty(), "myProperty");
		assertEquals(pv.getCharacteristic(), null);
		assertEquals(pv.toFullString(), "//"
				+ SomeWhere.DEFAULT_CONTROL_SYSTEM
						.getProcessVariableUriRepresentation() + "/myProperty");

		// test constructor

		// with control system, device, property, characteristic
		print("new ProcessVariable(ControlSystemEnum.TANGO,	\"myDevice\", \"myProperty\", \"myCharacteristic\")");
		pv = new ProcessVariable(ControlSystemEnum.TANGO, "myDevice",
				"myProperty", "myCharacteristic");
		print(pv);

		// with control system, property, characteristic
		print("new ProcessVariable(ControlSystemEnum.TANGO, null, \"myProperty\",\"myCharacteristic\")");
		pv = new ProcessVariable(ControlSystemEnum.TANGO, null, "myProperty",
				"myCharacteristic");
		print(pv);

		// with control system, property
		print("new ProcessVariable(ControlSystemEnum.TANGO,	null, \"myProperty\",null)");
		pv = new ProcessVariable(ControlSystemEnum.TANGO, null, "myProperty",
				null);
		print(pv);

		// with device, property, characteristic
		print("new ProcessVariable(null, \"myDevice\", \"myProperty\", \"myCharacteristic\")");
		pv = new ProcessVariable(null, "myDevice", "myProperty",
				"myCharacteristic");
		print(pv);

		// with device, property
		print("new ProcessVariable(null, \"myDevice\", \"myProperty\", null)");
		pv = new ProcessVariable(null, "myDevice", "myProperty", null);
		print(pv);

	}

	private void print(final Object o) {
		if (_printToConsole) {
			System.out.println("--------------------------");
			if (o instanceof ProcessVariable) {
				((ProcessVariable) o).print(System.out);
			} else {
				System.out.println(o.toString());
			}
		}
	}

}
