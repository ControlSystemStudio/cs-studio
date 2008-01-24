/**
 * Owned by DESY.
 */
package org.csstudio.sds.ui.internal.editparts;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Contains functional tests for {@link EditPartService} .
 * 
 * @author Sven Wende, Stefan Hofer
 * 
 */
public final class EditPartServiceTest {
	
	/**
	 * Test method for {@link EditPartService#getInstance()}.
	 */
	@Test
	public void testGetEditPartService() {
		assertNotNull(EditPartService.getInstance());
	}

	
}
