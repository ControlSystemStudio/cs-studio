package org.csstudio.nams.application.department.decision;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.csstudio.ams.service.preferenceservice.declaration.PreferenceService;
import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.history.declaration.HistoryService;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.csstudio.nams.service.logging.declaration.LoggerMock;
import org.csstudio.nams.service.messaging.declaration.MessagingServiceMock;
import org.csstudio.nams.service.messaging.declaration.MessagingSession;
import org.csstudio.nams.service.messaging.declaration.MessagingSessionMock;
import org.csstudio.nams.service.regelwerkbuilder.declaration.RegelwerkBuilderService;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DecisionDepartmentActivator_Test extends TestCase {

	private LoggerMock logger;
	private MessagingServiceMock messagingService;
	private PreferenceService preferenceService;
	private RegelwerkBuilderService regelwerksBuilderService;
	private HistoryService historyService;
	private LocalStoreConfigurationService localStoreConfigurationService;
	private ExecutionService executionService;

	@Test
	public void testBundleAndApplicationLifecycle() throws Exception {
		DecisionDepartmentActivator bundleInsance = new DecisionDepartmentActivator();
		DecisionDepartmentActivator applicationInsance = new DecisionDepartmentActivator();
		
		bundleInsance.startBundle(
				(Logger) logger, 
				messagingService, 
				preferenceService, 
				regelwerksBuilderService, 
				historyService, 
				localStoreConfigurationService, 
				executionService);
		
		Thread.yield();
		
		bundleInsance.stopBundle(logger);
		
	}

	@Before
	public void setUp() {
		// ** Prepare Mocks...
		// Logger
		logger = new LoggerMock();
		
		// MessagingService
		Map<String, String[]> expectedUrlsForClientIds = new HashMap<String, String[]>();
		expectedUrlsForClientIds.put("amsConsumer", new String[] {"jms://loclahost/123", "jms://localhost/abc"});
		expectedUrlsForClientIds.put("extConsumer", new String[] {"jms://loclahost/456"});
		Map<String, MessagingSession> sessionsForClientIds = new HashMap<String, MessagingSession>();
		// TODO Mocks fertigstellen...
//		sessionsForClientIds.put("amsConsumer", new MessagingSessionMock());
//		sessionsForClientIds.put("extConsumer", new MessagingSessionMock());
		messagingService = new MessagingServiceMock(expectedUrlsForClientIds, sessionsForClientIds);
		
		// Andere...
		preferenceService = EasyMock.createNiceMock(PreferenceService.class);
		regelwerksBuilderService = EasyMock.createNiceMock(RegelwerkBuilderService.class);
		historyService = EasyMock.createNiceMock(HistoryService.class);
		localStoreConfigurationService = EasyMock.createNiceMock(LocalStoreConfigurationService.class);
		executionService = EasyMock.createNiceMock(ExecutionService.class);
		
		// Replay....
		EasyMock.replay(preferenceService, regelwerksBuilderService, historyService, localStoreConfigurationService, executionService);
	}
	
	@After
	public void tearDown() {
		// Verify Mocks...
		EasyMock.verify(preferenceService, regelwerksBuilderService, historyService, localStoreConfigurationService, executionService);
	}
}
