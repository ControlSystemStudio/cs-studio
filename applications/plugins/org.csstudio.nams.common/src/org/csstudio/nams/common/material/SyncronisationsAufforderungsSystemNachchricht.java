package org.csstudio.nams.common.material;

public class SyncronisationsAufforderungsSystemNachchricht extends SystemNachricht {

	@Override
	public boolean istSyncronisationsAufforderung() {
		return true;
	}

	@Override
	public boolean istSyncronisationsBestaetigung() {
		return false;
	}

}
