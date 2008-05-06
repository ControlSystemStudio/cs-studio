package de.c1wps.desy.ams.allgemeines.regelwerk;

public class DummyPruefliste extends Pruefliste {

	DummyPruefliste(Regelwerkskennung regelwerkskennung) {
		super(regelwerkskennung, null);
	}

	@Override
	public WeiteresVersandVorgehen gesamtErgebnis() {
		return WeiteresVersandVorgehen.NICHT_VERSENDEN;
	}

	
}
