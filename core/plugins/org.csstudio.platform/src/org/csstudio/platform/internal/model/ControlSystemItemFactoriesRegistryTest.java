/**
 * Owned by DESY.
 */
package org.csstudio.platform.internal.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.csstudio.platform.model.AbstractControlSystemItem;
import org.csstudio.platform.model.AbstractControlSystemItemFactory;
import org.csstudio.platform.model.IProcessVariable;
import org.junit.Test;

/**
 * Test class for {@link ControlSystemItemFactoriesRegistry}.
 * 
 * @author Sven Wende
 *
 */
public final class ControlSystemItemFactoriesRegistryTest {

	/**
	 * Test method for {@link org.csstudio.platform.internal.model.ControlSystemItemFactoriesRegistry#getInstance()}.
	 */
	@Test
	public void testGetInstance() {
		ControlSystemItemFactoriesRegistry registry = ControlSystemItemFactoriesRegistry.getInstance();
		assertNotNull(registry);
		
		ControlSystemItemFactoriesRegistry registry2 = ControlSystemItemFactoriesRegistry.getInstance();
		assertEquals(registry, registry2);
	}

	/**
	 * Test method for {@link org.csstudio.platform.internal.model.ControlSystemItemFactoriesRegistry#getControlSystemItemFactory(java.lang.String)}.
	 */
	@Test
	public void testGetControlSystemItemFactory() {
		// get an existing factory
		AbstractControlSystemItemFactory factory = ControlSystemItemFactoriesRegistry.getInstance().getControlSystemItemFactory(IProcessVariable.TYPE_ID);
		assertNotNull(factory);
		
	}

}
