package org.csstudio.nams.service.logging;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.csstudio.nams.common.activatorUtils.OSGiServiceOffers;
import org.csstudio.nams.service.logging.declaration.ILogger;
import org.junit.Test;

public class LoggingServiceActivator_Test extends TestCase {

	@Test
	public void testStartBundle() throws Throwable {
		final LoggingServiceActivator activator = new LoggingServiceActivator();

		final OSGiServiceOffers serviceOffers = activator.startBundle();

		Assert.assertNotNull(serviceOffers);
		final Object service = serviceOffers.get(ILogger.class);
		Assert.assertTrue(ILogger.class.isAssignableFrom(service.getClass()));
	}

}
