
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

	private final Logger _logger;
	private final ExecutionService _executionService;
	private final ConfigurationServiceFactory _configurationServiceFactory;
	private final PreferenceService _preferenceService;
	private final MessagingService _messagingService;

	public SynchronizeServiceImpl(final Logger logger,
			final ExecutionService executionService,
			final PreferenceService preferenceService,
			final ConfigurationServiceFactory configurationServiceFactory,
			MessagingService messagingService) {
		this._logger = logger;
		this._executionService = executionService;
		this._preferenceService = preferenceService;
		this._configurationServiceFactory = configurationServiceFactory;
		this._messagingService = messagingService;
	}

	@Override
    public void sychronizeAlarmSystem(final Callback callback) {
		this._executionService.executeAsynchronsly(ThreadTypes.SYNCHRONIZER,
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
				final LocalStoreConfigurationService localStoreConfigurationService = this._configurationServiceFactory
						.getConfigurationService(
								this._preferenceService
										.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_CONNECTION),
								DatabaseType.Oracle10g,
								this._preferenceService
										.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_USER),
								this._preferenceService
										.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_PASSWORD));
				localStoreConfigurationService.prepareSynchonization();
			} catch (final Throwable t) {
				this._logger.logErrorMessage(this,
						"Error on preparation of synchronisation", t); //$NON-NLS-1$
				callback.fehlerBeimVorbereitenDerSynchronisation(t);
				callback.synchronisationAbgebrochen();
				return;
			}

			callback.sendeNachrichtAnHintergrundSystem();

			try {
				MessagingSession messagingSession = this._messagingService
						.createNewMessagingSession(
								// TODO aus dem prefservice
								_preferenceService.getString(PreferenceServiceJMSKeys.P_JMS_EXT_SYNCHRONIZE_PRODUCER_ID),
//								"syncServiceProducer",
								new String[] { _preferenceService
										.getString(PreferenceServiceJMSKeys.P_JMS_EXTERN_SENDER_PROVIDER_URL) });
				Producer producer = messagingSession
						.createProducer(
								_preferenceService
										.getString(PreferenceServiceJMSKeys.P_JMS_EXT_TOPIC_COMMAND),
								PostfachArt.TOPIC);

				producer.sendeSystemnachricht(new SyncronisationsAufforderungsSystemNachchricht());
				
				producer.tryToClose();
				messagingSession.close();
			} catch (Throwable t) {
				this._logger.logErrorMessage(this,
						"Error on sending synchronization message", t); //$NON-NLS-1$
				callback
						.synchronisationsDurchHintergrundsystemsFehlgeschalgen(t
								.getMessage());
				callback.synchronisationAbgebrochen();
				return;
			}

			callback.wartetAufAntowrtDesHintergrundSystems();
			
			MessagingSession messagingSession = null;
			Consumer consumer = null;
			try {
				messagingSession = this._messagingService
						.createNewMessagingSession(
								// TODO aus dem prefservice
								_preferenceService.getString(PreferenceServiceJMSKeys.P_JMS_EXT_SYNCHRONIZE_CONSUMER_ID),
//								"syncServiceConsumer",
								new String[] {
										_preferenceService
												.getString(PreferenceServiceJMSKeys.P_JMS_EXTERN_PROVIDER_URL_1),
										_preferenceService
												.getString(PreferenceServiceJMSKeys.P_JMS_EXTERN_PROVIDER_URL_2) });
				consumer = messagingSession
						.createConsumer(
								_preferenceService
										.getString(PreferenceServiceJMSKeys.P_JMS_EXT_TOPIC_COMMAND),
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
					throw new Throwable("Error on receiving synchronization message"); //$NON-NLS-1$
				}
			} catch (Throwable t) {
				this._logger.logErrorMessage(this,
						"Error on receiving synchronization message", t); //$NON-NLS-1$
				callback
						.synchronisationsDurchHintergrundsystemsFehlgeschalgen(t
								.getMessage());
				callback.synchronisationAbgebrochen();
				return;
			} finally {
				if (consumer != null) {
					consumer.close();
				}
				if (messagingSession != null) {
					messagingSession.close();
				}
			}
			
			callback.synchronisationsDurchHintergrundsystemsErfolgreich();
		} else {
			callback.synchronisationAbgebrochen();
		}
	}

}
