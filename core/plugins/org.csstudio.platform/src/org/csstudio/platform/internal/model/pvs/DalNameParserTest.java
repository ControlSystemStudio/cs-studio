/**
 * 
 */
package org.csstudio.platform.internal.model.pvs;

import static org.junit.Assert.*;

import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.DalPropertyTypes;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.ValueType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link DalNameParser}.
 * 
 * @author Sven Wende
 * 
 */
public class DalNameParserTest {
	private DalNameParser _epicsParser;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		_epicsParser = new DalNameParser(ControlSystemEnum.DAL_EPICS);
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
		// without type hint
		test("abc", ControlSystemEnum.DAL_EPICS, null, null, "abc", null);
		test("dal-epics://abc", ControlSystemEnum.DAL_EPICS, null, null, "abc",
				null);
		test("dal-epics://abc[cde]", ControlSystemEnum.DAL_EPICS, "cde", null,
				"abc", null);
		test("", ControlSystemEnum.UNKNOWN, null, null, "", null);

		// with type hint
		test("abc, " + ValueType.DOUBLE_SEQUENCE.toPortableString(),
				ControlSystemEnum.DAL_EPICS, null, null, "abc",
				ValueType.DOUBLE_SEQUENCE);
		test("dal-epics://abc, "
				+ DalPropertyTypes.DOUBLE_SEQUENCE.toPortableString(),
				ControlSystemEnum.DAL_EPICS, null, null, "abc",
				ValueType.DOUBLE_SEQUENCE);
		test("dal-epics://abc[cde], "
				+ ValueType.DOUBLE_SEQUENCE.toPortableString(),
				ControlSystemEnum.DAL_EPICS, "cde", null, "abc",
				ValueType.DOUBLE_SEQUENCE);
		test("", ControlSystemEnum.UNKNOWN, null, null, "", null);
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
			String expectedProperty, ValueType expectedTypeHint) {
		IProcessVariableAddress pv = _epicsParser.parseRawName(rawName);

		assertNotNull(pv);
		assertEquals(expectedControlSystem, pv.getControlSystem());
		assertEquals(expectedProperty, pv.getProperty());
		assertEquals(expectedCharacteristics, pv.getCharacteristic());
		assertEquals(expectedDevice, pv.getDevice());
		assertEquals(expectedTypeHint, pv.getValueTypeHint());
	}

}
