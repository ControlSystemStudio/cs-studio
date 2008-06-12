package org.csstudio.nams.application.department.decision;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.csstudio.ams.service.preferenceservice.declaration.PreferenceService;
import org.csstudio.ams.service.preferenceservice.declaration.PreferenceServiceJMSKeys;
import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.InconsistentConfiguration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ReplicationStateDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.StorageException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.UnknownConfigurationElementError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ReplicationStateDTO.ReplicationState;
import org.csstudio.nams.service.history.declaration.HistoryService;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.csstudio.nams.service.logging.declaration.LoggerMock;
import org.csstudio.nams.service.messaging.declaration.Consumer;
import org.csstudio.nams.service.messaging.declaration.MessagingService;
import org.csstudio.nams.service.messaging.declaration.MessagingServiceMock;
import org.csstudio.nams.service.messaging.declaration.MessagingSession;
import org.csstudio.nams.service.messaging.declaration.MessagingSessionMock;
import org.csstudio.nams.service.messaging.declaration.PostfachArt;
import org.csstudio.nams.service.messaging.declaration.Producer;
import org.csstudio.nams.service.regelwerkbuilder.declaration.RegelwerkBuilderService;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.org.apache.regexp.internal.recompile;

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

		bundleInsance.startBundle((Logger) logger,
				(MessagingService) messagingService, preferenceService,
				regelwerksBuilderService, historyService,
				localStoreConfigurationService, executionService);

		Thread.yield();

		bundleInsance.stopBundle(logger);

	}

	@Before
	public void setUp() throws Exception {
		// ** Prepare Mocks...
		// Logger
		logger = new LoggerMock();

		// * MessagingService
		messagingService = createMessagingServiceMock();

		// * PrefeenceService
		preferenceService = createPreferenceServiceMock();
		
		// * LocalStoreConfigurationService
		localStoreConfigurationService = createLocalStoreConfigurationService();

		// * Andere...
		// * Andere...
		regelwerksBuilderService = EasyMock
		.createNiceMock(RegelwerkBuilderService.class);
		historyService = EasyMock.createNiceMock(HistoryService.class);
		executionService = EasyMock.createNiceMock(ExecutionService.class);

		// Replay....
		EasyMock.replay(preferenceService, regelwerksBuilderService,
				historyService, localStoreConfigurationService,
				executionService);
	}

	private LocalStoreConfigurationService createLocalStoreConfigurationService() throws StorageError,
			StorageException, InconsistentConfiguration,
			UnknownConfigurationElementError {
		LocalStoreConfigurationService result = EasyMock
				.createNiceMock(LocalStoreConfigurationService.class);
		
		ReplicationStateDTO rplStateIdle = new ReplicationStateDTO();
		rplStateIdle.setReplicationState(ReplicationState.FLAGVALUE_SYNCH_IDLE);
		EasyMock.expect(result.getCurrentReplicationState()).andReturn(rplStateIdle).once();
		
		ReplicationStateDTO rplStartSyncronisation = new ReplicationStateDTO();
		rplStartSyncronisation.setReplicationState(ReplicationState.FLAGVALUE_SYNCH_FMR_TO_DIST_SENDED);
		result.saveCurrentReplicationState(EasyMock.eq(rplStartSyncronisation));
		EasyMock.expectLastCall().once();
		
		return result;
	}

	private PreferenceService createPreferenceServiceMock() {
		PreferenceService result = EasyMock.createNiceMock(PreferenceService.class);
		
		EasyMock.expect(result
				.getString(PreferenceServiceJMSKeys.P_JMS_AMS_PROVIDER_URL_1))
				.andReturn("P_JMS_AMS_PROVIDER_URL_1").anyTimes();
		EasyMock.expect(result
				.getString(PreferenceServiceJMSKeys.P_JMS_AMS_PROVIDER_URL_2))
				.andReturn("P_JMS_AMS_PROVIDER_URL_2").anyTimes();
		
		EasyMock.expect(result
				.getString(PreferenceServiceJMSKeys.P_JMS_EXTERN_PROVIDER_URL_1))
				.andReturn("P_JMS_EXTERN_PROVIDER_URL_1").anyTimes();
		EasyMock.expect(result
				.getString(PreferenceServiceJMSKeys.P_JMS_EXTERN_PROVIDER_URL_2))
				.andReturn("P_JMS_EXTERN_PROVIDER_URL_2").anyTimes();
		
		EasyMock.expect(result
				.getString(PreferenceServiceJMSKeys.P_JMS_EXT_TOPIC_ALARM))
				.andReturn("P_JMS_EXT_TOPIC_ALARM").anyTimes();
		EasyMock.expect(result
				.getString(PreferenceServiceJMSKeys.P_JMS_EXT_TOPIC_COMMAND))
				.andReturn("P_JMS_EXT_TOPIC_COMMAND").anyTimes();
		EasyMock.expect(result
				.getString(PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_COMMAND))
				.andReturn("P_JMS_AMS_TOPIC_COMMAND").anyTimes();
		
		EasyMock.expect(result
				.getString(PreferenceServiceJMSKeys.P_JMS_AMS_SENDER_PROVIDER_URL))
				.andReturn("P_JMS_AMS_SENDER_PROVIDER_URL").anyTimes();
		EasyMock.expect(result
				.getString(PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_MESSAGEMINDER))
				.andReturn("P_JMS_AMS_TOPIC_MESSAGEMINDER").anyTimes();

		return result;
	}

	private static MessagingServiceMock createMessagingServiceMock() {
		// URLs
		Map<String, String[]> expectedUrlsForClientIds = new HashMap<String, String[]>();
		expectedUrlsForClientIds.put("amsConsumer", new String[] {
				"jms://loclahost/123", "jms://localhost/abc" });
		expectedUrlsForClientIds.put("extConsumer",
				new String[] { "jms://loclahost/456" });
		// Session
		Map<String, MessagingSession> sessionsForClientIds = new HashMap<String, MessagingSession>();
		sessionsForClientIds.put("amsConsumer", createAMSConsumerSession());
		sessionsForClientIds.put("extConsumer", createEXTConsumerSession());
		// Service
		return new MessagingServiceMock(expectedUrlsForClientIds,
				sessionsForClientIds);
	}

	private static MessagingSessionMock createAMSConsumerSession() {
		// TODO Maps befüllen
		// P_JMS_AMS_PROVIDER_URL_1
		// P_JMS_AMS_PROVIDER_URL_2
		// 
		Map<String, PostfachArt> expectedPostfachArtenForSources = new HashMap<String, PostfachArt>();
		Map<String, Consumer> consumerForSources = new HashMap<String, Consumer>();
		Map<String, PostfachArt> expectedPostfachArtenForDestination = new HashMap<String, PostfachArt>();
		Map<String, Producer> producerForDestination = new HashMap<String, Producer>();
		MessagingSessionMock amsConsumerSession = new MessagingSessionMock(
				expectedPostfachArtenForSources, consumerForSources,
				expectedPostfachArtenForDestination, producerForDestination);
		return amsConsumerSession;
	}

	private static MessagingSessionMock createEXTConsumerSession() {
		// TODO Maps befüllen
		// P_JMS_EXTERN_PROVIDER_URL_1
		// P_JMS_EXTERN_PROVIDER_URL_2
		Map<String, PostfachArt> expectedPostfachArtenForSources = new HashMap<String, PostfachArt>();
		Map<String, Consumer> consumerForSources = new HashMap<String, Consumer>();
		Map<String, PostfachArt> expectedPostfachArtenForDestination = new HashMap<String, PostfachArt>();
		Map<String, Producer> producerForDestination = new HashMap<String, Producer>();
		MessagingSessionMock amsConsumerSession = new MessagingSessionMock(
				expectedPostfachArtenForSources, consumerForSources,
				expectedPostfachArtenForDestination, producerForDestination);
		return amsConsumerSession;
	}

	@After
	public void tearDown() {
		// Verify Mocks...
		EasyMock.verify(preferenceService, regelwerksBuilderService,
				historyService, localStoreConfigurationService,
				executionService);
	}
}
