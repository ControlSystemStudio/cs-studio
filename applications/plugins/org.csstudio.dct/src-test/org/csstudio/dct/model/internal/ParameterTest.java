/**
 * 
 */
package org.csstudio.dct.model.internal;

import static org.junit.Assert.*;

import org.csstudio.dct.model.internal.Parameter;
import org.junit.Before;
import org.junit.Test;

/**
 * Testcases for {@link Parameter}.
 * 
 * @author Sven Wende
 *
 */
public class ParameterTest {
	private Parameter parameter;
	private String name;
	private String defaultValue;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		name = "name";
		defaultValue = "defaultValue";
		parameter = new Parameter(name, defaultValue);
	}

	/**
	 * Test method for {@link org.csstudio.dct.model.internal.Parameter#getName()}.
	 */
	@Test
	public final void testGetName() {
		assertEquals(name, parameter.getName());
	}

	/**
	 * Test method for {@link org.csstudio.dct.model.internal.Parameter#setName(java.lang.String)}.
	 */
	@Test
	public final void testSetName() {
		assertEquals(name, parameter.getName());
		parameter.setName("name2");
		assertEquals("name2", parameter.getName());
	}

	/**
	 * Test method for {@link org.csstudio.dct.model.internal.Parameter#getDefaultValue()}.
	 */
	@Test
	public final void testGetDefaultValue() {
		assertEquals(defaultValue, parameter.getDefaultValue());
	}

	/**
	 * Test method for {@link org.csstudio.dct.model.internal.Parameter#setDefaultValue(java.lang.String)}.
	 */
	@Test
	public final void testSetDefaultValue() {
		assertEquals(defaultValue, parameter.getDefaultValue());
		parameter.setDefaultValue("defaultValue2");
		assertEquals("defaultValue2", parameter.getDefaultValue());
	}

	/**
	 * Test method for {@link org.csstudio.dct.model.internal.Parameter#equals}.
	 */
	@Test
	public final void testEquals() {
		assertEquals(parameter, new Parameter(parameter.getName(), parameter.getDefaultValue()));
		assertNotSame(parameter, new Parameter(parameter.getName()+"x", parameter.getDefaultValue()));
		assertNotSame(parameter, new Parameter(parameter.getName(), parameter.getDefaultValue()+"x"));
	}
}
