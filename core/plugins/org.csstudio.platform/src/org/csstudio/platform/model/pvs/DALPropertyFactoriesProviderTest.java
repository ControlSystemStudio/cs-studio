/**
 * 
 */
package org.csstudio.platform.model.pvs;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link DALPropertyFactoriesProvider}.
 * 
 * @author Sven Wende
 * 
 */
public class DALPropertyFactoriesProviderTest {
	DALPropertyFactoriesProvider _provider;

	/**
	 * Setup.
	 */
	@Before
	public void setUp() {
		_provider = DALPropertyFactoriesProvider.getInstance();
	}

	/**
	 * Tear down.
	 */
	@After
	public void tearDown() {
	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.model.pvs.DALPropertyFactoriesProvider#getInstance()}.
	 */
	@Test
	public void testGetInstance() {
		assertNotNull(_provider);
	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.model.pvs.DALPropertyFactoriesProvider#getPropertyFactory(org.csstudio.platform.model.pvs.ControlSystemEnum)}.
	 */
	@Test
	public void testGetPropertyFactory() {
		assertNotNull(_provider.getPropertyFactory(ControlSystemEnum.DAL_EPICS));
		assertNotNull(_provider.getPropertyFactory(ControlSystemEnum.DAL_TINE));
		assertNotNull(_provider.getPropertyFactory(ControlSystemEnum.DAL_SIMULATOR));
	}

}
