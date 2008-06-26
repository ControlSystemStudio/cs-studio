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
 package org.csstudio.platform.model.pvs;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProcessVariableAdressFactoryTest {
	private ProcessVariableAdressFactory _factory;

	@Before
	public void setUp() throws Exception {
		_factory = ProcessVariableAdressFactory.getInstance();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateProcessVariableAdress() {
		// test raw names, which contain a control system prefix
//		testPv("epics://any/any/any", ControlSystemEnum.EPICS, "any/any/any",
//				null, null);
//		testPv("tine://any|any.any", ControlSystemEnum.TINE, "any|any.any",
//				null, null);
//		testPv("tango://any-any//any|ss<ss>", ControlSystemEnum.TANGO,
//				"any-any//any|ss<ss>", null, null);

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
		
		// test names with type hints
		
		testPv("dal-epics://any, doubleSeq", ControlSystemEnum.DAL_EPICS, "any",
				null, null);

	}

	private void testPv(String rawName,
			ControlSystemEnum expectedControlSystem, String expectedProperty,
			String expectedDevice, String expectedCharacteristic) {
		IProcessVariableAddress pv = _factory
				.createProcessVariableAdress(rawName);

		assertEquals(expectedControlSystem, pv.getControlSystem());
		assertEquals(expectedProperty, pv.getProperty());
		assertEquals(expectedDevice, pv.getDevice());
		assertEquals(expectedCharacteristic, pv.getCharacteristic());

	}
}
