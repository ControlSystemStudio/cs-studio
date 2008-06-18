package org.csstudio.nams.common.material.regelwerk;

import org.csstudio.nams.common.material.AlarmNachricht;

public abstract class AbstractTimeBasedVersandRegel extends
		AbstractVersandRegel {

	protected void mayWriteToHistory(Pruefliste pruefliste, AlarmNachricht nachricht) {
		getHistoryService().logTimeOutForTimeBased(
				pruefliste.gibRegelwerkskennung().toString(), nachricht.toString(), toString());
	}
}
