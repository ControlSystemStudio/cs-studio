package org.csstudio.nams.common;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.csstudio.nams.common.activatorUtils.OSGiServiceOffers;
import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.easymock.EasyMock;
import org.junit.Test;

public class CommonActivator_Test extends TestCase {

	@Test
	public void testBundleLifecycle() throws Throwable {
		final Logger logger = EasyMock.createNiceMock(Logger.class);
		EasyMock.replay(logger);

		final CommonActivator activator = new CommonActivator();

		final OSGiServiceOffers serviceOffers = activator.bundleStart(logger);

		// assertSame(logger, ProcessVariableRegel.getLogger());

		final Object offeredService = serviceOffers.get(ExecutionService.class);
		Assert.assertNotNull(offeredService);
		Assert.assertTrue(ExecutionService.class
				.isAssignableFrom(offeredService.getClass()));

		activator.stopBundle(logger);
	}
}
