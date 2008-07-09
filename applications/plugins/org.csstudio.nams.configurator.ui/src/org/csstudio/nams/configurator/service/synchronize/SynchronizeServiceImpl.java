package org.csstudio.nams.configurator.service.synchronize;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;

public class SynchronizeServiceImpl implements SynchronizeService {

	private final LocalStoreConfigurationService localStoreConfigurationService;

	public SynchronizeServiceImpl(
			LocalStoreConfigurationService localStoreConfigurationService) {
			this.localStoreConfigurationService = localStoreConfigurationService;
	}

	public void sychronizeAlarmSystem(Callback callback) {
		if( callback.pruefeObSynchronisationAusgefuehrtWerdenDarf() )
		{
			// TODO Fortfahren...
		} else {
			callback.synchronisationAbgebrochen();
		}
	}

}
