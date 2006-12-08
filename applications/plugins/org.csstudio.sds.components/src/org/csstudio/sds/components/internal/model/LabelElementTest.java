/**
 * Owned by DESY.
 */
package org.csstudio.sds.components.internal.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link LabelElement}.
 * 
 * @author Sven Wende
 *
 */
public final class LabelElementTest {

	/**
	 * A test instance.
	 */
	private LabelElement _labelElement;
	
	/**
	 * Test setup.
	 */
	@Before
	public void setUp() {
		_labelElement = new LabelElement();
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.internal.model.LabelElement#getDoubleTestProperty()}.
	 */
	@Test
	public void testGetDoubleTestProperty() {
		assertNotNull(_labelElement.getDoubleTestProperty());
		assertTrue(_labelElement.hasProperty(_labelElement.getDoubleTestProperty()));
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.internal.model.LabelElement#getTypeID()}.
	 */
	@Test
	public void testGetTypeID() {
		_labelElement.getTypeID().equals(LabelElement.ID);
	}

	/**
	 * Tests, if all properties where properly installed.
	 */
	@Test
	public void testProperties () {
		assertTrue(_labelElement.hasProperty(LabelElement.PROP_LABEL));
		// Add further properties here
	}

}
