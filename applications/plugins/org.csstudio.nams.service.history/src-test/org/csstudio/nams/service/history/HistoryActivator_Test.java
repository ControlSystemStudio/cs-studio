package org.csstudio.nams.service.history;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.csstudio.nams.common.activatorUtils.OSGiServiceOffers;
import org.csstudio.nams.service.history.declaration.HistoryService;
import org.csstudio.nams.service.history.extensionPoint.HistoryServiceFactory;
import org.csstudio.nams.service.logging.declaration.ILogger;
import org.easymock.EasyMock;
import org.junit.Test;

public class HistoryActivator_Test extends TestCase {

	@Test
	public void testStartBundle() throws Throwable {
		final ILogger logger = EasyMock.createNiceMock(ILogger.class);
		final HistoryService historyService = EasyMock
				.createMock(HistoryService.class);
		final HistoryServiceFactory factory = EasyMock
				.createMock(HistoryServiceFactory.class);
		EasyMock.expect(factory.createService()).andReturn(historyService);
		EasyMock.replay(logger, historyService, factory);

		final HistoryActivator activator = new HistoryActivator();

		final OSGiServiceOffers serviceOffers = activator.startBundle(logger,
				factory);

		Assert.assertNotNull(serviceOffers);
		final Object service = serviceOffers.get(HistoryService.class);
		Assert.assertTrue(HistoryService.class.isAssignableFrom(service
				.getClass()));
		Assert.assertSame(historyService, service);
	}

}
