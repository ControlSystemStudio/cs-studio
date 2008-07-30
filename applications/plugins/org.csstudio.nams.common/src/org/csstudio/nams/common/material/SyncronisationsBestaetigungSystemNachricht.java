package org.csstudio.nams.common.material;

public class SyncronisationsBestaetigungSystemNachricht implements
		SystemNachricht {

	public boolean istSyncronisationsAufforderung() {
		return false;
	}

	public boolean istSyncronisationsBestaetigung() {
		return true;
	}

}
