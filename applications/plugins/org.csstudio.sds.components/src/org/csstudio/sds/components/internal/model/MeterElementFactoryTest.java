/**
 * Owned by DESY.
 */
package org.csstudio.sds.components.internal.model;

import static org.junit.Assert.*;

import org.csstudio.sds.model.DisplayModelElement;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link MeterElementFactory}.
 * 
 * @author Sven Wende
 *
 */
public final class MeterElementFactoryTest {

	/**
	 * A element instanc for testing issues.
	 */
	private MeterElementFactory _elementFactory;
	
	/**
	 * Test setup.
	 */
	@Before
	public void setUp()  {
		_elementFactory= new MeterElementFactory();
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.internal.model.MeterElementFactory#createModelElement()}.
	 */
	@Test
	public void testCreateModelElement() {
		DisplayModelElement element = _elementFactory.createModelElement();
		assertNotNull(element);
		assertTrue(element instanceof MeterElement);
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.internal.model.MeterElementFactory#getModelElementType()}.
	 */
	@Test
	public void testGetModelElementType() {
		assertEquals(MeterElement.class, _elementFactory.getModelElementType());
	}

}
