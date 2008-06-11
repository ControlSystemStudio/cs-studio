package org.csstudio.nams.application.department.decision;

import junit.framework.TestCase;

import org.csstudio.ams.service.preferenceservice.declaration.PreferenceService;
import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.history.declaration.HistoryService;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.csstudio.nams.service.messaging.declaration.MessagingService;
import org.csstudio.nams.service.regelwerkbuilder.declaration.RegelwerkBuilderService;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DecisionDepartmentActivator_Test extends TestCase {

	private Logger logger;
	private MessagingService messagingService;
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
				logger, 
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
		// Prepare Mocks...
		logger = EasyMock.createNiceMock(Logger.class);
		messagingService = EasyMock.createNiceMock(MessagingService.class);
		preferenceService = EasyMock.createNiceMock(PreferenceService.class);
		regelwerksBuilderService = EasyMock.createNiceMock(RegelwerkBuilderService.class);
		historyService = EasyMock.createNiceMock(HistoryService.class);
		localStoreConfigurationService = EasyMock.createNiceMock(LocalStoreConfigurationService.class);
		executionService = EasyMock.createNiceMock(ExecutionService.class);
		
		// Replay....
		EasyMock.replay(logger, messagingService, preferenceService, regelwerksBuilderService, historyService, localStoreConfigurationService, executionService);
	}
	
	@After
	public void tearDown() {
		// Verify Mocks...
		EasyMock.verify(logger, messagingService, preferenceService, regelwerksBuilderService, historyService, localStoreConfigurationService, executionService);
	}
}
