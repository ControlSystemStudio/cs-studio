/**
 * Owned by DESY.
 */
package org.csstudio.sds.components.internal.model;

import static org.junit.Assert.*;

import org.csstudio.sds.model.DisplayModelElement;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link RectangleElementFactory}.
 * 
 * @author Sven Wende
 *
 */
public final class RectangleElementFactoryTest {

	/**
	 * A element instanc for testing issues.
	 */
	private RectangleElementFactory _elementFactory;
	
	/**
	 * Test setup.
	 */
	@Before
	public void setUp()  {
		_elementFactory= new RectangleElementFactory();
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.internal.model.RectangleElementFactory#createModelElement()}.
	 */
	@Test
	public void testCreateModelElement() {
		DisplayModelElement element = _elementFactory.createModelElement();
		assertNotNull(element);
		assertTrue(element instanceof RectangleElement);
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.internal.model.RectangleElementFactory#getModelElementType()}.
	 */
	@Test
	public void testGetModelElementType() {
		assertEquals(RectangleElement.class, _elementFactory.getModelElementType());
	}

}
