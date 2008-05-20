package org.csstudio.nams.common.material.regelwerk;

import org.csstudio.nams.common.testhelper.ForTesting;


public class DummyPruefliste extends Pruefliste {

	@Deprecated
	@ForTesting
	public DummyPruefliste(Regelwerkskennung regelwerkskennung) {
		super(regelwerkskennung, null);
	}

	@Override
	public WeiteresVersandVorgehen gesamtErgebnis() {
		return WeiteresVersandVorgehen.NICHT_VERSENDEN;
	}

	
}
