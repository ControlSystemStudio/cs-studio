package org.csstudio.nams.configurator.service.synchronize;

import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.common.service.StepByStepProcessor;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ConfigurationServiceFactory;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.DatabaseType;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceService;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceDatabaseKeys;

public class SynchronizeServiceImpl implements SynchronizeService {

	private final Logger logger;
	private final ExecutionService executionService;
	private final ConfigurationServiceFactory configurationServiceFactory;
	private final PreferenceService preferenceService;

	public SynchronizeServiceImpl(
			Logger logger,
			ExecutionService executionService,
			PreferenceService preferenceService,
			ConfigurationServiceFactory configurationServiceFactory) {
			this.logger = logger;
			this.executionService = executionService;
			this.preferenceService = preferenceService;
			this.configurationServiceFactory = configurationServiceFactory;
	}


	public void sychronizeAlarmSystem(final Callback callback) {
		executionService.executeAsynchronsly(ThreadTypes.SYNCHRONIZER, new StepByStepProcessor() {
			@Override
			protected void doRunOneSingleStep() throws Throwable,
					InterruptedException {
				sychronizeAlarmSystemInternal(callback);
				this.done();
			}
		});
	}
	private void sychronizeAlarmSystemInternal(final Callback callback) {
		if( callback.pruefeObSynchronisationAusgefuehrtWerdenDarf() )
		{
			callback.bereiteSynchronisationVor();
			
			try {
				LocalStoreConfigurationService localStoreConfigurationService = 
				configurationServiceFactory.getConfigurationService(
						preferenceService
						.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_CONNECTION),
				DatabaseType.Oracle10g, 
				preferenceService
						.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_USER),
				preferenceService
						.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_PASSWORD));
				localStoreConfigurationService.prepareSynchonization();
			} catch (Throwable t) {
				logger.logErrorMessage(this, "Error on preparation of synchronisation", t);
				callback.fehlerBeimVorbereitenDerSynchronisation(t);
				callback.synchronisationAbgebrochen();
				return ;
			}
			
			// TODO Real fortfahren...
			callback.sendeNachrichtAnHintergrundSystem();
			callback.wartetAufAntowrtDesHintergrundSystems();
			callback.synchronisationsDurchHintergrundsystemsErfolgreich();
		} else {
			callback.synchronisationAbgebrochen();
		}
	}

}
