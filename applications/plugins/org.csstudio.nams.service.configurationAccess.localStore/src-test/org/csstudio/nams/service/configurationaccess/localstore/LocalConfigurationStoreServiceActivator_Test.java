package org.csstudio.nams.service.configurationaccess.localstore;

import junit.framework.TestCase;

import org.csstudio.nams.common.activatorUtils.OSGiServiceOffers;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ConfigurationServiceFactory;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.junit.Test;

public class LocalConfigurationStoreServiceActivator_Test extends TestCase {

	@Test
	public void testBundleLifecycle() throws Throwable {
		LocalConfigurationStoreServiceActivator activator = new LocalConfigurationStoreServiceActivator();
		
		OSGiServiceOffers serviceOffers = activator.startBundle(new Logger() {

			public void logDebugMessage(Object caller, String message) {
			}

			public void logDebugMessage(Object caller, String message,
					Throwable throwable) {
			}

			public void logErrorMessage(Object caller, String message) {
			}

			public void logErrorMessage(Object caller, String message,
					Throwable throwable) {
			}

			public void logFatalMessage(Object caller, String message) {
			}

			public void logFatalMessage(Object caller, String message,
					Throwable throwable) {
			}

			public void logInfoMessage(Object caller, String message) {
			}

			public void logInfoMessage(Object caller, String message,
					Throwable throwable) {
			}

			public void logWarningMessage(Object caller, String message) {
			}

			public void logWarningMessage(Object caller, String message,
					Throwable throwable) {
			}
			
		});
		assertNotNull(serviceOffers);
		
		Object offeredService = serviceOffers.get(ConfigurationServiceFactory.class);
		assertNotNull(offeredService);
		assertTrue(ConfigurationServiceFactory.class.isAssignableFrom(offeredService.getClass()));
		
		activator.stopBundle();
	}

}
