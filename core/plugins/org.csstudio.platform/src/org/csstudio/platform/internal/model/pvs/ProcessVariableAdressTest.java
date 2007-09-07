/**
 * 
 */
package org.csstudio.platform.internal.model.pvs;

import static org.junit.Assert.*;

import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link ProcessVariableAdress}.
 * 
 * @author Sven Wende
 *
 */
public class ProcessVariableAdressTest {

	private ProcessVariableAdress _pv1;
	private ProcessVariableAdress _pv1_1;
	private ProcessVariableAdress _pv2;
	private ProcessVariableAdress _pv3;
	/**
	 * Setup.
	 */
	@Before
	public void setUp() {
		_pv1 = new ProcessVariableAdress("pv1", ControlSystemEnum.DAL_EPICS, "d1", "p1", "c1");
		_pv1_1 = new ProcessVariableAdress("pv1", ControlSystemEnum.DAL_EPICS, "d1", "p1", "c1");
		_pv2 = new ProcessVariableAdress("pv2", ControlSystemEnum.DAL_SIMULATOR, "d2", "p2", "c2");
		_pv3 = new ProcessVariableAdress("pv3", ControlSystemEnum.UNKNOWN, null, "p3", null);
	}

	/**
	 * Test method for {@link org.csstudio.platform.internal.model.pvs.ProcessVariableAdress#getCharacteristic()}.
	 */
	@Test
	public void testGetCharacteristic() {
		assertNotNull(_pv1.getCharacteristic());
		assertNotNull(_pv2.getCharacteristic());
		assertNull(_pv3.getCharacteristic());
		assertEquals("c1",_pv1.getCharacteristic());
		assertEquals("c2",_pv2.getCharacteristic());
	}

	/**
	 * Test method for {@link org.csstudio.platform.internal.model.pvs.ProcessVariableAdress#getDevice()}.
	 */
	@Test
	public void testGetDevice() {
		assertNotNull(_pv1.getDevice());
		assertNotNull(_pv2.getDevice());
		assertNull(_pv3.getDevice());
		assertEquals("d1",_pv1.getDevice());
		assertEquals("d2",_pv2.getDevice());
	}

	/**
	 * Test method for {@link org.csstudio.platform.internal.model.pvs.ProcessVariableAdress#getProperty()}.
	 */
	@Test
	public void testGetProperty() {
		assertNotNull(_pv1.getProperty());
		assertNotNull(_pv2.getProperty());
		assertNotNull(_pv3.getProperty());
		assertEquals("p1",_pv1.getProperty());
		assertEquals("p2",_pv2.getProperty());
		assertEquals("p3",_pv3.getProperty());
	}

	/**
	 * Test method for {@link org.csstudio.platform.internal.model.pvs.ProcessVariableAdress#toDalRemoteInfo()}.
	 */
	@Test
	public void testToDalRemoteInfo() {
		assertNotNull(_pv1.toDalRemoteInfo());
		assertNotNull(_pv2.toDalRemoteInfo());
		assertNull(_pv3.toDalRemoteInfo());
	}

	/**
	 * Test method for {@link org.csstudio.platform.internal.model.pvs.ProcessVariableAdress#getFullName()}.
	 */
	@Test
	public void testGetFullName() {
		assertNotNull(_pv1.getFullName());
		assertNotNull(_pv2.getFullName());
		assertNotNull(_pv3.getFullName());
	}

	/**
	 * Test method for {@link org.csstudio.platform.internal.model.pvs.ProcessVariableAdress#getControlSystem()}.
	 */
	@Test
	public void testGetControlSystem() {
		assertNotNull(_pv1.getControlSystem());
		assertNotNull(_pv2.getControlSystem());
		assertNotNull(_pv3.getControlSystem());
	}

	/**
	 * Test method for {@link org.csstudio.platform.internal.model.pvs.ProcessVariableAdress#getRawName()}.
	 */
	@Test
	public void testGetRawName() {
		assertNotNull(_pv1.getRawName());
		assertNotNull(_pv2.getRawName());
		assertNotNull(_pv3.getRawName());
	}

	/**
	 * Test method for {@link org.csstudio.platform.internal.model.pvs.ProcessVariableAdress#isCharacteristic()}.
	 */
	@Test
	public void testIsCharacteristic() {
		assertTrue(_pv1.isCharacteristic());
		assertTrue(_pv2.isCharacteristic());
		assertFalse(_pv3.isCharacteristic());
	}

	/**
	 * Test method for {@link org.csstudio.platform.internal.model.pvs.ProcessVariableAdress#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		assertFalse(_pv1.equals(_pv2));
		assertFalse(_pv1.equals(_pv3));
		assertFalse(_pv2.equals(_pv3));
		assertEquals(_pv1, _pv1);
		assertEquals(_pv2, _pv2);
		assertEquals(_pv3, _pv3);
		assertEquals(_pv1, _pv1_1);
	}

	/**
	 * Test method for {@link org.csstudio.platform.internal.model.pvs.ProcessVariableAdress#toString()}.
	 */
	@Test
	public void testToString() {
		assertNotNull(_pv1.toString());
		assertNotNull(_pv2.toString());
		assertNotNull(_pv3.toString());
	}

}
