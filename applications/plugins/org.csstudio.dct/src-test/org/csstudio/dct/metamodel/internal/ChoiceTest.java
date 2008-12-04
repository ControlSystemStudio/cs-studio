/**
 * 
 */
package org.csstudio.dct.metamodel.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link Choice}.
 * 
 * @author Sven Wende
 *
 */
public class ChoiceTest {

	private Choice choice;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		choice = new Choice("id", "description");
	}

	/**
	 * Test method for {@link org.csstudio.dct.metamodel.internal.Choice#getDescription()}.
	 */
	@Test
	public final void testGetDescription() {
		assertEquals("description", choice.getDescription());
	}

	/**
	 * Test method for {@link org.csstudio.dct.metamodel.internal.Choice#getId()}.
	 */
	@Test
	public final void testGetId() {
		assertEquals("id", choice.getId());
	}

}
