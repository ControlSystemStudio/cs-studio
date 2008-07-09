package org.csstudio.nams.configurator.service.synchronize;

public class SynchronizeServiceImpl implements SynchronizeService {

	public void sychronizeAlarmSystem(Callback callback) {
		if( callback.pruefeObUngesicherteAenderungenDasSynchronisierenVerhindern() )
		{
			// TODO Fortfahren...
		} else {
			callback.synchronisationAbgebrochen();
		}
	}

}
