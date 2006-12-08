/**
 * Owned by DESY.
 */
package org.csstudio.sds.components.internal.model;

import static org.junit.Assert.*;

import org.csstudio.sds.model.DisplayModelElement;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link PolygonElementFactory}.
 * 
 * @author Sven Wende
 *
 */
public final class PolygonElementFactoryTest {

	/**
	 * A element instanc for testing issues.
	 */
	private PolygonElementFactory _elementFactory;
	
	/**
	 * Test setup.
	 */
	@Before
	public void setUp()  {
		_elementFactory= new PolygonElementFactory();
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.internal.model.PolygonElementFactory#createModelElement()}.
	 */
	@Test
	public void testCreateModelElement() {
		DisplayModelElement element = _elementFactory.createModelElement();
		assertNotNull(element);
		assertTrue(element instanceof PolygonElement);
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.internal.model.PolygonElementFactory#getModelElementType()}.
	 */
	@Test
	public void testGetModelElementType() {
		assertEquals(PolygonElement.class, _elementFactory.getModelElementType());
	}

}
