/**
 * Owned by DESY.
 */
package org.csstudio.sds.components.internal.model;

import static org.junit.Assert.*;

import org.csstudio.sds.model.DisplayModelElement;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link LabelElementFactory}.
 * 
 * @author Sven Wende
 *
 */
public final class LabelElementFactoryTest {

	/**
	 * A element instanc for testing issues.
	 */
	private LabelElementFactory _elementFactory;
	
	/**
	 * Test setup.
	 */
	@Before
	public void setUp()  {
		_elementFactory= new LabelElementFactory();
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.internal.model.LabelElementFactory#createModelElement()}.
	 */
	@Test
	public void testCreateModelElement() {
		DisplayModelElement element = _elementFactory.createModelElement();
		assertNotNull(element);
		assertTrue(element instanceof LabelElement);
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.internal.model.LabelElementFactory#getModelElementType()}.
	 */
	@Test
	public void testGetModelElementType() {
		assertEquals(LabelElement.class, _elementFactory.getModelElementType());
	}

}
