package org.csstudio.nams.service.history;

import junit.framework.TestCase;

import org.csstudio.nams.common.activatorUtils.OSGiServiceOffers;
import org.csstudio.nams.service.history.declaration.HistoryService;
import org.csstudio.nams.service.history.extensionPoint.HistoryServiceFactory;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.easymock.EasyMock;
import org.junit.Test;

public class HistoryActivator_Test extends TestCase {

	@Test
	public void testStartBundle() throws Throwable {
		Logger logger = EasyMock.createNiceMock(Logger.class);
		HistoryService historyService = EasyMock.createMock(HistoryService.class);
		HistoryServiceFactory factory = EasyMock.createMock(HistoryServiceFactory.class);
		EasyMock.expect(factory.createService()).andReturn(historyService);
		EasyMock.replay(logger, historyService, factory);
		
		HistoryActivator activator = new HistoryActivator();
		
		OSGiServiceOffers serviceOffers = activator.startBundle(logger, factory);
		
		assertNotNull(serviceOffers);
		Object service = serviceOffers.get(HistoryService.class);
		assertTrue(HistoryService.class.isAssignableFrom(service.getClass()));
		assertSame(historyService, service);
	}

}
