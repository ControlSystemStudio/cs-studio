package org.csstudio.nams.configurator.service.synchronize;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;

public class SynchronizeServiceImpl implements SynchronizeService {

	private final LocalStoreConfigurationService localStoreConfigurationService;

	public SynchronizeServiceImpl(
			LocalStoreConfigurationService localStoreConfigurationService) {
			this.localStoreConfigurationService = localStoreConfigurationService;
	}

	public void sychronizeAlarmSystem(final Callback callback) {
		new Thread(new Runnable() {
			public void run() {
				sychronizeAlarmSystemInternal(callback);
			}}).start();
	}
	private void sychronizeAlarmSystemInternal(final Callback callback) {
		if( callback.pruefeObSynchronisationAusgefuehrtWerdenDarf() )
		{
			
			localStoreConfigurationService.prepareSynchonization();
			
			// TODO Fortfahren...
			callback.bereiteSynchronisationVor();
			callback.sendeNachrichtAnHintergrundSystem();
			callback.wartetAufAntowrtDesHintergrundSystems();
			callback.synchronisationsDurchHintergrundsystemsErfolgreich();
		} else {
			callback.synchronisationAbgebrochen();
		}
	}

}
