package org.csstudio.platform.model.rfc;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PvAdressFactoryTest {
	private PvAdressFactory _factory;

	@Before
	public void setUp() throws Exception {
		_factory = PvAdressFactory.getInstance();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateProcessVariableAdress() {
		// test raw names, which contain a control system prefix
		testPv("epics://any/any/any", ControlSystemEnum.EPICS, "any/any/any",
				null, null);
		testPv("tine://any|any.any", ControlSystemEnum.TINE, "any|any.any",
				null, null);
		testPv("tango://any-any//any|ss<ss>", ControlSystemEnum.TANGO,
				"any-any//any|ss<ss>", null, null);

		// test raw names, without a control system prefix
		testPv("any/any/any", _factory.getDefaultControlSystem(),
				"any/any/any", null, null);

		// test raw names, which contain characteristics
		testPv("dal-epics://any.any.any[graphMax]", ControlSystemEnum.DAL_EPICS, "any.any.any",
				null, "graphMax");
		
		testPv("dal-tine://any/any/any[graphMin]", ControlSystemEnum.DAL_TINE, "any/any/any",
				null, "graphMin");
		
		testPv("dal-tango://any--any--any[graphMin]", ControlSystemEnum.DAL_TANGO, "any--any--any",
				null, "graphMin");
		
		// test some invalid names
		testPv("dal-epics://any||any||any[graphMax]/bad", ControlSystemEnum.UNKNOWN, "any||any||any[graphMax]/bad",
				null, null);
		testPv("dal-epics://any||any||any[graphMax", ControlSystemEnum.UNKNOWN, "any||any||any[graphMax",
				null, null);
		testPv("dal-epics://any||any||anygraphMax]", ControlSystemEnum.UNKNOWN, "any||any||anygraphMax]",
				null, null);
		testPv("dal-epics://[graphMax]", ControlSystemEnum.UNKNOWN, "[graphMax]",
				null, null);
		testPv("dal-epics://", ControlSystemEnum.UNKNOWN, "",
				null, null);
		
		testPv("//any||any||any[graphMax]2", ControlSystemEnum.UNKNOWN, "//any||any||any[graphMax]2",
				null, null);
		testPv("dal-tine://any||any||any[graphMin]2", ControlSystemEnum.UNKNOWN, "any||any||any[graphMin]2",
				null, null);

	}

	private void testPv(String rawName,
			ControlSystemEnum expectedControlSystem, String expectedProperty,
			String expectedDevice, String expectedCharacteristic) {
		IProcessVariableAdress pv = _factory
				.createProcessVariableAdress(rawName);

		assertEquals(expectedControlSystem, pv.getControlSystem());
		assertEquals(expectedProperty, pv.getProperty());
		assertEquals(expectedDevice, pv.getDevice());
		assertEquals(expectedCharacteristic, pv.getCharacteristic());

	}
}
