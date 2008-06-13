package org.csstudio.nams.common;

import junit.framework.TestCase;

import org.csstudio.nams.common.activatorUtils.OSGiServiceOffers;
import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.easymock.EasyMock;
import org.junit.Test;

public class CommonActivator_Test extends TestCase {

	@Test
	public void testBundleLifecycle() throws Throwable {
		Logger logger = EasyMock.createNiceMock(Logger.class);
		EasyMock.replay(logger);
		
		CommonActivator activator = new CommonActivator();
		
		OSGiServiceOffers serviceOffers = activator.bundleStart(logger);
		
//		assertSame(logger, ProcessVariableRegel.getLogger());
		
		Object offeredService = serviceOffers.get(ExecutionService.class);
		assertNotNull(offeredService);
		assertTrue(ExecutionService.class.isAssignableFrom(offeredService.getClass()));
		
		activator.stopBundle(logger);
	}
}
