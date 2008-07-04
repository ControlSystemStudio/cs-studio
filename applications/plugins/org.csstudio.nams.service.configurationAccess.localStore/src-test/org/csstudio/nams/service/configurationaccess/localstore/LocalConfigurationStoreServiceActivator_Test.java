package org.csstudio.nams.service.configurationaccess.localstore;

import junit.framework.TestCase;

import org.csstudio.nams.common.activatorUtils.OSGiServiceOffers;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ConfigurationServiceFactory;
import org.junit.Test;

public class LocalConfigurationStoreServiceActivator_Test extends TestCase {

	@Test
	public void testBundleLifecycle() throws Throwable {
		LocalConfigurationStoreServiceActivator activator = new LocalConfigurationStoreServiceActivator();
		
		OSGiServiceOffers serviceOffers = activator.startBundle();
		assertNotNull(serviceOffers);
		
		Object offeredService = serviceOffers.get(ConfigurationServiceFactory.class);
		assertNotNull(offeredService);
		assertTrue(ConfigurationServiceFactory.class.isAssignableFrom(offeredService.getClass()));
		
		activator.stopBundle();
	}

}
