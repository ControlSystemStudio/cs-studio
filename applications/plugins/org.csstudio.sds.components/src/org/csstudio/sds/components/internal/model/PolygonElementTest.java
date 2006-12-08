/**
 * Owned by DESY.
 */
package org.csstudio.sds.components.internal.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link PolygonElement}.
 * 
 * @author Sven Wende
 *
 */
public final class PolygonElementTest {

	/**
	 * A test instance.
	 */
	private PolygonElement _polygonElement;
	
	/**
	 * Test setup.
	 */
	@Before
	public void setUp() {
		_polygonElement = new PolygonElement();
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.internal.model.PolygonElement#getDoubleTestProperty()}.
	 */
	@Test
	public void testGetDoubleTestProperty() {
		assertNotNull(_polygonElement.getDoubleTestProperty());
		assertTrue(_polygonElement.hasProperty(_polygonElement.getDoubleTestProperty()));
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.internal.model.PolygonElement#getTypeID()}.
	 */
	@Test
	public void testGetTypeID() {
		_polygonElement.getTypeID().equals(PolygonElement.ID);
	}

	/**
	 * Tests, if all properties where properly installed.
	 */
	@Test
	public void testProperties () {
		assertTrue(_polygonElement.hasProperty(PolygonElement.PROP_POINTS));
		assertTrue(_polygonElement.hasProperty(PolygonElement.PROP_FILL_GRADE));
		// Add further properties here
	}

}
