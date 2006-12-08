/**
 * Owned by DESY.
 */
package org.csstudio.sds.components.internal.model;

import static org.junit.Assert.*;

import org.csstudio.sds.model.DisplayModelElement;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link PolylineElementFactory}.
 * 
 * @author Sven Wende
 *
 */
public final class PolylineElementFactoryTest {

	/**
	 * A element instanc for testing issues.
	 */
	private PolylineElementFactory _elementFactory;
	
	/**
	 * Test setup.
	 */
	@Before
	public void setUp()  {
		_elementFactory= new PolylineElementFactory();
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.internal.model.PolylineElementFactory#createModelElement()}.
	 */
	@Test
	public void testCreateModelElement() {
		DisplayModelElement element = _elementFactory.createModelElement();
		assertNotNull(element);
		assertTrue(element instanceof PolylineElement);
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.internal.model.PolylineElementFactory#getModelElementType()}.
	 */
	@Test
	public void testGetModelElementType() {
		assertEquals(PolylineElement.class, _elementFactory.getModelElementType());
	}

}
