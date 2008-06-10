package org.csstudio.nams.service.logging;

import junit.framework.TestCase;

import org.csstudio.nams.common.activatorUtils.OSGiServiceOffers;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.junit.Test;

public class LoggingServiceActivator_Test extends TestCase {

	@Test
	public void testStartBundle() throws Throwable {
		LoggingServiceActivator activator = new LoggingServiceActivator();
		
		OSGiServiceOffers serviceOffers = activator.startBundle();
		
		assertNotNull(serviceOffers);
		Object service = serviceOffers.get(Logger.class);
		assertTrue(Logger.class.isAssignableFrom(service.getClass()));
	}

}
