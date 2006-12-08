/**
 * Owned by DESY.
 */
package org.csstudio.sds.components.internal.model;

import static org.junit.Assert.*;

import org.csstudio.sds.model.DisplayModelElement;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link EllipseElementFactory}.
 * 
 * @author Sven Wende
 *
 */
public final class EllipseElementFactoryTest {

	/**
	 * A element instanc for testing issues.
	 */
	private EllipseElementFactory _elementFactory;
	
	/**
	 * Test setup.
	 */
	@Before
	public void setUp()  {
		_elementFactory= new EllipseElementFactory();
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.internal.model.EllipseElementFactory#createModelElement()}.
	 */
	@Test
	public void testCreateModelElement() {
		DisplayModelElement element = _elementFactory.createModelElement();
		assertNotNull(element);
		assertTrue(element instanceof EllipseElement);
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.internal.model.EllipseElementFactory#getModelElementType()}.
	 */
	@Test
	public void testGetModelElementType() {
		assertEquals(EllipseElement.class, _elementFactory.getModelElementType());
	}

}
