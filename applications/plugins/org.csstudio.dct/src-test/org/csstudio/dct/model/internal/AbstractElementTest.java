/**
 * 
 */
package org.csstudio.dct.model.internal;

import static org.junit.Assert.*;

import java.util.UUID;

import org.csstudio.dct.model.internal.AbstractElement;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link AbstractElement}.
 * 
 * @author Sven Wende
 * 
 */
public class AbstractElementTest {
	private static final String NAME = "test";
	private static final UUID ID = UUID.randomUUID();

	private AbstractElement element;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		element = new AbstractElement(NAME, ID) {
		};
	}
	
	/**
	 * Test method for
	 * {@link org.csstudio.dct.model.internal.AbstractElement#getId()}.
	 */
	@Test
	public final void testGetId() {
		assertEquals(ID, element.getId());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.model.internal.AbstractElement#getName()}.
	 */
	@Test
	public final void testGetName() {
		assertEquals("test", element.getName());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.model.internal.AbstractElement#setName(java.lang.String)}
	 * .
	 */
	@Test
	public final void testSetName() {
		String newName = "test2";
		element.setName(newName);
		assertEquals(newName, element.getName());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.model.internal.AbstractElement#equals(Object)}.
	 */
	@Test
	public final void testEqualsHashCode() {
		AbstractElement element2 = new AbstractElement(NAME, ID) {};
		assertEquals(element, element2);
		assertEquals(element.hashCode(), element2.hashCode());
		
		AbstractElement element3 = new AbstractElement("othername", ID) {};
		assertNotSame(element, element3);
		assertNotSame(element.hashCode(), element3.hashCode());
		
		AbstractElement element4 = new AbstractElement(NAME, UUID.randomUUID()) {};
		assertNotSame(element, element4);
		assertNotSame(element.hashCode(), element4.hashCode());
	}

}
