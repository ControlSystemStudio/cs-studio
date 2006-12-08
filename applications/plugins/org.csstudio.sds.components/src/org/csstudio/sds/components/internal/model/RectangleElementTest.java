/**
 * Owned by DESY.
 */
package org.csstudio.sds.components.internal.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link RectangleElement}.
 * 
 * @author Sven Wende
 * 
 */
public final class RectangleElementTest {

	/**
	 * A test instance.
	 */
	private RectangleElement _rectangleElement;

	/**
	 * Test setup.
	 */
	@Before
	public void setUp() {
		_rectangleElement = new RectangleElement();
	}

	/**
	 * Test method for
	 * {@link org.csstudio.sds.components.internal.model.RectangleElement#getDoubleTestProperty()}.
	 */
	@Test
	public void testGetDoubleTestProperty() {
		assertNotNull(_rectangleElement.getDoubleTestProperty());
		assertTrue(_rectangleElement.hasProperty(_rectangleElement.getDoubleTestProperty()));
	}

	/**
	 * Test method for
	 * {@link org.csstudio.sds.components.internal.model.RectangleElement#getTypeID()}.
	 */
	@Test
	public void testGetTypeID() {
		_rectangleElement.getTypeID().equals(RectangleElement.ID);
	}

	/**
	 * Tests, if all properties where properly installed.
	 */
	@Test
	public void testProperties() {
		assertTrue(_rectangleElement
				.hasProperty(RectangleElement.PROP_FILL_PERCENTAGE));
		// Add further properties here
	}

}
