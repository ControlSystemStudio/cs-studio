/**
 * Owned by DESY.
 */
package org.csstudio.sds.components.internal.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link EllipseElement}.
 * @author Sven Wende
 *
 */
public final class EllipseElementTest {

	/**
	 * A test instance.
	 */
	private EllipseElement _ellipseElement;
	
	/**
	 * Test setup.
	 */
	@Before
	public void setUp() {
		_ellipseElement = new EllipseElement();
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.internal.model.EllipseElement#getDoubleTestProperty()}.
	 */
	@Test
	public void testGetDoubleTestProperty() {
		assertNotNull(_ellipseElement.getDoubleTestProperty());
		assertTrue(_ellipseElement.hasProperty(_ellipseElement.getDoubleTestProperty()));
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.internal.model.EllipseElement#getTypeID()}.
	 */
	@Test
	public void testGetTypeID() {
		_ellipseElement.getTypeID().equals(EllipseElement.ID);
	}

	/**
	 * Tests, if all properties where properly installed.
	 */
	@Test
	public void testProperties () {
		assertTrue(_ellipseElement.hasProperty(EllipseElement.PROP_FILL_PERCENTAGE));
		// Add further properties here
	}

}
