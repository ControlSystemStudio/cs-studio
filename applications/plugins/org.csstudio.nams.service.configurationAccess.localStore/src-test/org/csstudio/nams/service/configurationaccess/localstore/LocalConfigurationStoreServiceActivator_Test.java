package org.csstudio.nams.service.configurationaccess.localstore;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.csstudio.nams.common.activatorUtils.OSGiServiceOffers;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ConfigurationServiceFactory;
import org.csstudio.nams.service.logging.declaration.ILogger;
import org.junit.Test;

public class LocalConfigurationStoreServiceActivator_Test extends TestCase {

	@Test
	public void testBundleLifecycle() throws Throwable {
		final LocalConfigurationStoreServiceActivator activator = new LocalConfigurationStoreServiceActivator();

		final OSGiServiceOffers serviceOffers = activator
				.startBundle(new ILogger() {

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

					public void logWarningMessage(Object caller,
							String message, Throwable throwable) {
					}

				});
		Assert.assertNotNull(serviceOffers);

		final Object offeredService = serviceOffers
				.get(ConfigurationServiceFactory.class);
		Assert.assertNotNull(offeredService);
		Assert.assertTrue(ConfigurationServiceFactory.class
				.isAssignableFrom(offeredService.getClass()));

		activator.stopBundle();
	}

}
