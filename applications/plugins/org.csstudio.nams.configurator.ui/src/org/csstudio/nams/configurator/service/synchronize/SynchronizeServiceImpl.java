package org.csstudio.nams.configurator.service.synchronize;

import org.csstudio.nams.common.material.SyncronisationsAufforderungsSystemNachchricht;
import org.csstudio.nams.common.material.SystemNachricht;
import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.common.service.StepByStepProcessor;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ConfigurationServiceFactory;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.DatabaseType;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.csstudio.nams.service.messaging.declaration.Consumer;
import org.csstudio.nams.service.messaging.declaration.MessagingService;
import org.csstudio.nams.service.messaging.declaration.MessagingSession;
import org.csstudio.nams.service.messaging.declaration.NAMSMessage;
import org.csstudio.nams.service.messaging.declaration.PostfachArt;
import org.csstudio.nams.service.messaging.declaration.Producer;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceService;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceDatabaseKeys;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceJMSKeys;

public class SynchronizeServiceImpl implements SynchronizeService {

	private final Logger logger;
	private final ExecutionService executionService;
	private final ConfigurationServiceFactory configurationServiceFactory;
	private final PreferenceService preferenceService;
	private final MessagingService messagingService;

	public SynchronizeServiceImpl(final Logger logger,
			final ExecutionService executionService,
			final PreferenceService preferenceService,
			final ConfigurationServiceFactory configurationServiceFactory,
			MessagingService messagingService) {
		this.logger = logger;
		this.executionService = executionService;
		this.preferenceService = preferenceService;
		this.configurationServiceFactory = configurationServiceFactory;
		this.messagingService = messagingService;
	}

	public void sychronizeAlarmSystem(final Callback callback) {
		this.executionService.executeAsynchronsly(ThreadTypes.SYNCHRONIZER,
				new StepByStepProcessor() {
					@Override
					protected void doRunOneSingleStep() throws Throwable,
							InterruptedException {
						SynchronizeServiceImpl.this
								.sychronizeAlarmSystemInternal(callback);
						this.done();
					}
				});
	}

	private void sychronizeAlarmSystemInternal(final Callback callback) {
		if (callback.pruefeObSynchronisationAusgefuehrtWerdenDarf()) {
			callback.bereiteSynchronisationVor();

			try {
				final LocalStoreConfigurationService localStoreConfigurationService = this.configurationServiceFactory
						.getConfigurationService(
								this.preferenceService
										.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_CONNECTION),
								DatabaseType.Oracle10g,
								this.preferenceService
										.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_USER),
								this.preferenceService
										.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_PASSWORD));
				localStoreConfigurationService.prepareSynchonization();
			} catch (final Throwable t) {
				this.logger.logErrorMessage(this,
						"Error on preparation of synchronisation", t);
				callback.fehlerBeimVorbereitenDerSynchronisation(t);
				callback.synchronisationAbgebrochen();
				return;
			}

			// TODO Real fortfahren...
			// FIXME erst mal lokale topics fuer den ersten desz test
			callback.sendeNachrichtAnHintergrundSystem();

			try {
				MessagingSession messagingSession = this.messagingService
						.createNewMessagingSession(
								"syncServiceProducer",
								new String[] { preferenceService
										.getString(PreferenceServiceJMSKeys.P_JMS_AMS_SENDER_PROVIDER_URL) });
				Producer producer = messagingSession
						.createProducer(
								preferenceService
										.getString(PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_COMMAND),
								PostfachArt.TOPIC);

				producer.sendeSystemnachricht(new SyncronisationsAufforderungsSystemNachchricht());
				
				producer.tryToClose();
				messagingSession.close();
			} catch (Throwable t) {
				this.logger.logErrorMessage(this,
						"Error on sending synchronization message", t);
				callback
						.synchronisationsDurchHintergrundsystemsFehlgeschalgen(t
								.getMessage());
				callback.synchronisationAbgebrochen();
				return;
			}

			callback.wartetAufAntowrtDesHintergrundSystems();
			
			try {
				MessagingSession messagingSession = this.messagingService
						.createNewMessagingSession(
								"syncServiceConsumer",
								new String[] {
										preferenceService
												.getString(PreferenceServiceJMSKeys.P_JMS_AMS_PROVIDER_URL_1),
										preferenceService
												.getString(PreferenceServiceJMSKeys.P_JMS_AMS_PROVIDER_URL_2) });
				Consumer consumer = messagingSession
						.createConsumer(
								preferenceService
										.getString(PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_COMMAND),
								PostfachArt.TOPIC);
				NAMSMessage message;
				while ((message = consumer.receiveMessage()) != null) {
					if (message.enthaeltSystemnachricht()) {
						SystemNachricht systemachricht = message.alsSystemachricht();
						if (systemachricht.istSyncronisationsBestaetigung()) {
							message.acknowledge();
							break;
						}
					}
					message.acknowledge();
				}
				if (message == null) {
					throw new Throwable("Error on receiving synchronization message");
				}
			} catch (Throwable t) {
				this.logger.logErrorMessage(this,
						"Error on receiving synchronization message", t);
				callback
						.synchronisationsDurchHintergrundsystemsFehlgeschalgen(t
								.getMessage());
				callback.synchronisationAbgebrochen();
				return;
			}
			
			callback.synchronisationsDurchHintergrundsystemsErfolgreich();
		} else {
			callback.synchronisationAbgebrochen();
		}
	}

}
