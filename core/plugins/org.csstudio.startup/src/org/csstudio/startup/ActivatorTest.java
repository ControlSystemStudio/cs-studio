/**
 * 
 */
package org.csstudio.startup;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Test class for {@link org.csstudio.startup.Activator}.
 * 
 * @author awill
 * 
 */
public class ActivatorTest {

	/**
	 * Test method for {@link org.csstudio.startup.Activator#getDefault()}.
	 */
	@Test
	public final void testGetDefault() {
		assertNotNull(Activator.getDefault());
	}

}
