package org.csstudio.nams.application.department.decision;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.csstudio.ams.service.preferenceservice.declaration.PreferenceService;
import org.csstudio.ams.service.preferenceservice.declaration.PreferenceServiceJMSKeys;
import org.csstudio.nams.common.material.SyncronisationsBestaetigungSystemNachricht;
import org.csstudio.nams.common.material.regelwerk.Regelwerk;
import org.csstudio.nams.common.service.ExecutionServiceMock;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ReplicationStateDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ReplicationStateDTO.ReplicationState;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfiguration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.UnknownConfigurationElementError;
import org.csstudio.nams.service.history.declaration.HistoryService;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.csstudio.nams.service.logging.declaration.LoggerMock;
import org.csstudio.nams.service.messaging.declaration.Consumer;
import org.csstudio.nams.service.messaging.declaration.ConsumerMock;
import org.csstudio.nams.service.messaging.declaration.DefaultNAMSMessage;
import org.csstudio.nams.service.messaging.declaration.MessagingService;
import org.csstudio.nams.service.messaging.declaration.MessagingServiceMock;
import org.csstudio.nams.service.messaging.declaration.MessagingSession;
import org.csstudio.nams.service.messaging.declaration.MessagingSessionMock;
import org.csstudio.nams.service.messaging.declaration.PostfachArt;
import org.csstudio.nams.service.messaging.declaration.Producer;
import org.csstudio.nams.service.messaging.declaration.ProducerMock;
import org.csstudio.nams.service.messaging.declaration.DefaultNAMSMessage.AcknowledgeHandler;
import org.csstudio.nams.service.messaging.declaration.MultiConsumersConsumer.MultiConsumerConsumerThreads;
import org.csstudio.nams.service.regelwerkbuilder.declaration.RegelwerkBuilderService;
import org.easymock.EasyMock;
import org.eclipse.equinox.app.IApplicationContext;
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
	private ExecutionServiceMock executionService;
	private ProducerMock amsToDistributorProducerMock;
	private ConsumerMock amsCommandConsumerMock;

	boolean syncronisationBestaetigungAcknowledged;

	volatile Throwable occuredThrowable = null;
	private ThreadGroup catchingThreadGroup = new ThreadGroup(Thread
			.currentThread().getThreadGroup(), "DDA") {
		@Override
		public void uncaughtException(Thread t, Throwable e) {
			occuredThrowable = e;
		}
	};

	@Test
	public void testBundleAndApplicationLifecycle() throws Throwable {
		final DecisionDepartmentActivator bundleInsance = new DecisionDepartmentActivator();
		final DecisionDepartmentActivator applicationInsance = new DecisionDepartmentActivator();

		bundleInsance.startBundle((Logger) logger,
				(MessagingService) messagingService, preferenceService,
				regelwerksBuilderService, historyService,
				localStoreConfigurationService, executionService);

		Thread.yield();

		syncronisationBestaetigungAcknowledged = false;
		amsCommandConsumerMock.mockSetNextToBeDelivered(new DefaultNAMSMessage(
				new SyncronisationsBestaetigungSystemNachricht(),
				new AcknowledgeHandler() {
					public void acknowledge() throws Throwable {
						syncronisationBestaetigungAcknowledged = true;
					}
				}));

		Runnable appRun = new Runnable() {
			public void run() {
				try {
					Thread.sleep(2000); // FIXME Was besseres übelegen!
										// (Application Konzept mit einer run
										// ist nicht gut - init müsste
										// ausgelagert werden.)

					// Hier läuft der Test!!!!!!!!!!!
					executionService
							.mockExecuteOneStepOf(MultiConsumerConsumerThreads.CONSUMER_THREAD);

					Thread.sleep(2000);

					applicationInsance.stop();

					bundleInsance.stopBundle(logger);

					assertTrue(syncronisationBestaetigungAcknowledged);

				} catch (Throwable e) {
					occuredThrowable = e;
				}
			}
		};
		new Thread(catchingThreadGroup, appRun).start();

		// ApplicationContext wird nicht verwendet!
		// Blockiert bis Test-Thread stop() rufft.
		applicationInsance.start((IApplicationContext) null);
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
				.createMock(RegelwerkBuilderService.class);
		List<Regelwerk> list = Collections.emptyList();
		EasyMock.expect(regelwerksBuilderService.gibAlleRegelwerke())
				.andReturn(list).once();

		historyService = EasyMock.createNiceMock(HistoryService.class);

		executionService = new ExecutionServiceMock();

		// Replay....
		EasyMock.replay(preferenceService, regelwerksBuilderService,
				historyService, localStoreConfigurationService);
	}

	private LocalStoreConfigurationService createLocalStoreConfigurationService()
			throws StorageError, StorageException, InconsistentConfiguration,
			UnknownConfigurationElementError {
		LocalStoreConfigurationService result = EasyMock
				.createMock(LocalStoreConfigurationService.class);

		ReplicationStateDTO rplStateIdle = new ReplicationStateDTO();
		rplStateIdle.setReplicationState(ReplicationState.FLAGVALUE_SYNCH_IDLE);
		EasyMock.expect(result.getCurrentReplicationState()).andReturn(
				rplStateIdle).once();

		ReplicationStateDTO rplStartSyncronisation = new ReplicationStateDTO();
		rplStartSyncronisation
				.setReplicationState(ReplicationState.FLAGVALUE_SYNCH_FMR_TO_DIST_SENDED);
		result.saveCurrentReplicationState(EasyMock.eq(rplStartSyncronisation));
		EasyMock.expectLastCall().once();

		return result;
	}

	private PreferenceService createPreferenceServiceMock() {
		PreferenceService result = EasyMock.createMock(PreferenceService.class);

		EasyMock
				.expect(
						result
								.getString(PreferenceServiceJMSKeys.P_JMS_AMS_PROVIDER_URL_1))
				.andReturn("P_JMS_AMS_PROVIDER_URL_1").anyTimes();
		EasyMock
				.expect(
						result
								.getString(PreferenceServiceJMSKeys.P_JMS_AMS_PROVIDER_URL_2))
				.andReturn("P_JMS_AMS_PROVIDER_URL_2").anyTimes();

		EasyMock
				.expect(
						result
								.getString(PreferenceServiceJMSKeys.P_JMS_EXTERN_PROVIDER_URL_1))
				.andReturn("P_JMS_EXTERN_PROVIDER_URL_1").anyTimes();
		EasyMock
				.expect(
						result
								.getString(PreferenceServiceJMSKeys.P_JMS_EXTERN_PROVIDER_URL_2))
				.andReturn("P_JMS_EXTERN_PROVIDER_URL_2").anyTimes();

		EasyMock
				.expect(
						result
								.getString(PreferenceServiceJMSKeys.P_JMS_EXT_TOPIC_ALARM))
				.andReturn("P_JMS_EXT_TOPIC_ALARM").anyTimes();
		EasyMock
				.expect(
						result
								.getString(PreferenceServiceJMSKeys.P_JMS_EXT_TOPIC_COMMAND))
				.andReturn("P_JMS_EXT_TOPIC_COMMAND").anyTimes();
		EasyMock
				.expect(
						result
								.getString(PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_COMMAND))
				.andReturn("P_JMS_AMS_TOPIC_COMMAND").anyTimes();

		EasyMock
				.expect(
						result
								.getString(PreferenceServiceJMSKeys.P_JMS_AMS_SENDER_PROVIDER_URL))
				.andReturn("P_JMS_AMS_SENDER_PROVIDER_URL").anyTimes();
		EasyMock
				.expect(
						result
								.getString(PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_MESSAGEMINDER))
				.andReturn("P_JMS_AMS_TOPIC_MESSAGEMINDER").anyTimes();

		return result;
	}

	private MessagingServiceMock createMessagingServiceMock() {
		// URLs
		Map<String, String[]> expectedUrlsForClientIds = new HashMap<String, String[]>();
		expectedUrlsForClientIds.put("amsConsumer", new String[] {
				"P_JMS_AMS_PROVIDER_URL_1", "P_JMS_AMS_PROVIDER_URL_2" });
		expectedUrlsForClientIds.put("extConsumer", new String[] {
				"P_JMS_EXTERN_PROVIDER_URL_1", "P_JMS_EXTERN_PROVIDER_URL_2" });
		expectedUrlsForClientIds.put("amsProducer",
				new String[] { "P_JMS_AMS_SENDER_PROVIDER_URL" });
		// Session
		Map<String, MessagingSession> sessionsForClientIds = new HashMap<String, MessagingSession>();
		sessionsForClientIds.put("amsConsumer", createAMSConsumerSession());
		sessionsForClientIds.put("extConsumer", createEXTConsumerSession());
		sessionsForClientIds.put("amsProducer", createAMSProducerSession());
		// Service
		return new MessagingServiceMock(expectedUrlsForClientIds,
				sessionsForClientIds);
	}

	private MessagingSession createAMSProducerSession() {
		// TODO Auto-generated method stub
		Map<String, PostfachArt> expectedPostfachArtenForSources = new HashMap<String, PostfachArt>();
		Map<String, Consumer> consumerForSources = new HashMap<String, Consumer>();
		Map<String, PostfachArt> expectedPostfachArtenForDestination = new HashMap<String, PostfachArt>();
		Map<String, Producer> producerForDestination = new HashMap<String, Producer>();

		expectedPostfachArtenForDestination.put(
				"P_JMS_AMS_TOPIC_MESSAGEMINDER", PostfachArt.TOPIC);

		amsToDistributorProducerMock = new ProducerMock();
		producerForDestination.put("P_JMS_AMS_TOPIC_MESSAGEMINDER",
				amsToDistributorProducerMock);

		MessagingSessionMock amsProducerSession = new MessagingSessionMock(
				expectedPostfachArtenForSources, consumerForSources,
				expectedPostfachArtenForDestination, producerForDestination);
		return amsProducerSession;
	}

	private MessagingSessionMock createAMSConsumerSession() {
		// TODO Maps befüllen
		// P_JMS_AMS_PROVIDER_URL_1
		// P_JMS_AMS_PROVIDER_URL_2
		// 
		Map<String, PostfachArt> expectedPostfachArtenForSources = new HashMap<String, PostfachArt>();
		Map<String, Consumer> consumerForSources = new HashMap<String, Consumer>();
		Map<String, PostfachArt> expectedPostfachArtenForDestination = new HashMap<String, PostfachArt>();
		Map<String, Producer> producerForDestination = new HashMap<String, Producer>();

		expectedPostfachArtenForSources.put("P_JMS_AMS_TOPIC_COMMAND",
				PostfachArt.TOPIC);
		amsCommandConsumerMock = new ConsumerMock();
		consumerForSources.put("P_JMS_AMS_TOPIC_COMMAND",
				amsCommandConsumerMock);

		MessagingSessionMock amsConsumerSession = new MessagingSessionMock(
				expectedPostfachArtenForSources, consumerForSources,
				expectedPostfachArtenForDestination, producerForDestination);
		return amsConsumerSession;
	}

	private MessagingSessionMock createEXTConsumerSession() {
		// TODO Maps befüllen
		// P_JMS_EXTERN_PROVIDER_URL_1
		// P_JMS_EXTERN_PROVIDER_URL_2
		Map<String, PostfachArt> expectedPostfachArtenForSources = new HashMap<String, PostfachArt>();
		Map<String, Consumer> consumerForSources = new HashMap<String, Consumer>();
		Map<String, PostfachArt> expectedPostfachArtenForDestination = new HashMap<String, PostfachArt>();
		Map<String, Producer> producerForDestination = new HashMap<String, Producer>();

		expectedPostfachArtenForSources.put("P_JMS_EXT_TOPIC_COMMAND",
				PostfachArt.TOPIC);
		ConsumerMock extCommandConsumerMock = new ConsumerMock();
		consumerForSources.put("P_JMS_EXT_TOPIC_COMMAND",
				extCommandConsumerMock);
		expectedPostfachArtenForSources.put("P_JMS_EXT_TOPIC_ALARM",
				PostfachArt.TOPIC);
		ConsumerMock extAlarmConsumerMock = new ConsumerMock();
		consumerForSources.put("P_JMS_EXT_TOPIC_ALARM", extAlarmConsumerMock);

		MessagingSessionMock amsConsumerSession = new MessagingSessionMock(
				expectedPostfachArtenForSources, consumerForSources,
				expectedPostfachArtenForDestination, producerForDestination);
		return amsConsumerSession;
	}

	@After
	public void tearDown() {
		// Clean-Ups.
		if (occuredThrowable != null) {
			throw new RuntimeException("Unhandled exception occurred.",
					occuredThrowable);
		}
		// Verify Mocks...
		EasyMock.verify(preferenceService, regelwerksBuilderService,
				historyService, localStoreConfigurationService);
	}
}
