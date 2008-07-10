package org.csstudio.nams.configurator.service.synchronize;

import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.common.service.StepByStepProcessor;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.logging.declaration.Logger;

public class SynchronizeServiceImpl implements SynchronizeService {

	private final LocalStoreConfigurationService localStoreConfigurationService;
	private final Logger logger;
	private final ExecutionService executionService;

	public SynchronizeServiceImpl(
			Logger logger,
			ExecutionService executionService,
			LocalStoreConfigurationService localStoreConfigurationService) {
			this.logger = logger;
			this.executionService = executionService;
			this.localStoreConfigurationService = localStoreConfigurationService;
	}

	public void sychronizeAlarmSystem(final Callback callback) {
		executionService.executeAsynchronsly(ThreadTypes.SYNCHRONIZER, new StepByStepProcessor() {
			@Override
			protected void doRunOneSingleStep() throws Throwable,
					InterruptedException {
				sychronizeAlarmSystemInternal(callback);
			}
		});
	}
	private void sychronizeAlarmSystemInternal(final Callback callback) {
		if( callback.pruefeObSynchronisationAusgefuehrtWerdenDarf() )
		{
			callback.bereiteSynchronisationVor();
			
			try {
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
