
package org.csstudio.nams.common.material;

public class SyncronisationsAufforderungsSystemNachchricht implements
		SystemNachricht {

	@Override
    public boolean istSyncronisationsAufforderung() {
		return true;
	}

	@Override
    public boolean istSyncronisationsBestaetigung() {
		return false;
	}
}
