/**
 * 
 */
package org.csstudio.platform.internal.model.pvs;

import static org.junit.Assert.*;

import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link SimpleNameParser}.
 * 
 * @author Sven Wende
 * 
 */
public class SimpleNameParserTest {
	private SimpleNameParser _epicsParser;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		_epicsParser = new SimpleNameParser(ControlSystemEnum.EPICS);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.internal.model.pvs.DalNameParser#doParse(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testParse() {
		test("abc", ControlSystemEnum.EPICS, null, null, "abc");
		test("epics://abc", ControlSystemEnum.EPICS, null, null, "abc");
		test("epics://abc[cde]", ControlSystemEnum.EPICS, null, null,
				"abc[cde]");
		test("", ControlSystemEnum.UNKNOWN, null, null, "");
	}

	/**
	 * Tests the specified raw name and checks whether the returned pv does
	 * match the requirements.
	 * 
	 * @param rawName
	 *            the raw name, which is used as input for the parser
	 * @param expectedCharacteristics
	 *            the expected characteristic part
	 * @param expectedDevice
	 *            the expected device part
	 * @param expectedProperty
	 *            the expected property part
	 */
	private void test(String rawName, ControlSystemEnum expectedControlSystem,
			String expectedCharacteristics, String expectedDevice,
			String expectedProperty) {
		IProcessVariableAddress pv = _epicsParser.parseRawName(rawName);

		assertNotNull(pv);
		assertEquals(expectedControlSystem, pv.getControlSystem());
		assertEquals(expectedProperty, pv.getProperty());
		assertEquals(expectedCharacteristics, pv.getCharacteristic());
		assertEquals(expectedDevice, pv.getDevice());
	}

}
