
package org.csstudio.nams.common.material.regelwerk;

import org.csstudio.nams.common.material.Regelwerkskennung;
import org.csstudio.nams.common.testhelper.ForTesting;

public class DummyPruefliste extends Pruefliste {

	@Deprecated
	@ForTesting
	public DummyPruefliste(final Regelwerkskennung regelwerkskennung) {
		super(regelwerkskennung, null);
	}

	@Override
	public WeiteresVersandVorgehen gesamtErgebnis() {
		return WeiteresVersandVorgehen.NICHT_VERSENDEN;
	}
}
