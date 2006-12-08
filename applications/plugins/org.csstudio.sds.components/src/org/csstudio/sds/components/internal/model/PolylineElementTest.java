/**
 * Owned by DESY.
 */
package org.csstudio.sds.components.internal.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link PolylineElement}.
 * 
 * @author Sven Wende
 *
 */
public final class PolylineElementTest {

	/**
	 * A test instance.
	 */
	private PolylineElement _polylineElement;
	
	/**
	 * Test setup.
	 */
	@Before
	public void setUp() {
		_polylineElement = new PolylineElement();
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.internal.model.PolylineElement#getDoubleTestProperty()}.
	 */
	@Test
	public void testGetDoubleTestProperty() {
		assertNotNull(_polylineElement.getDoubleTestProperty());
		assertTrue(_polylineElement.hasProperty(_polylineElement.getDoubleTestProperty()));
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.internal.model.PolylineElement#getTypeID()}.
	 */
	@Test
	public void testGetTypeID() {
		_polylineElement.getTypeID().equals(PolylineElement.ID);
	}

	/**
	 * Tests, if all properties where properly installed.
	 */
	@Test
	public void testProperties () {
		assertTrue(_polylineElement.hasProperty(PolylineElement.PROP_POINTS));
		assertTrue(_polylineElement.hasProperty(PolylineElement.PROP_FILL_GRADE));
		// Add further properties here
	}

}
