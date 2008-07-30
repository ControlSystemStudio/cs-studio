package org.csstudio.nams.common.material;

public class SyncronisationsAufforderungsSystemNachchricht implements
		SystemNachricht {

	public boolean istSyncronisationsAufforderung() {
		return true;
	}

	public boolean istSyncronisationsBestaetigung() {
		return false;
	}

}
