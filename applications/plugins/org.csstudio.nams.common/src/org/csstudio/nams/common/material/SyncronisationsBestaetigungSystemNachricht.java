package org.csstudio.nams.common.material;

public class SyncronisationsBestaetigungSystemNachricht extends SystemNachricht {

	@Override
	public boolean istSyncronisationsAufforderung() {
		return false;
	}

	@Override
	public boolean istSyncronisationsBestaetigung() {
		return true;
	}

}
