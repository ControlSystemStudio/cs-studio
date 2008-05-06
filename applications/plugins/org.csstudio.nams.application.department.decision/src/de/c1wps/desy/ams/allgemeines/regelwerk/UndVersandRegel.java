package de.c1wps.desy.ams.allgemeines.regelwerk;

import java.util.Set;

public class UndVersandRegel extends AbstractNodeVersandRegel {

	public UndVersandRegel(VersandRegel[] versandRegels) {
		super(versandRegels);
	}

	RegelErgebnis auswerten(Pruefliste ergebnisListe) {
		Set<RegelErgebnis> kinderErgebnisse = gibKinderErgebnisse(ergebnisListe);
		if (kinderErgebnisse.contains(RegelErgebnis.NICHT_ZUTREFFEND))
			return RegelErgebnis.NICHT_ZUTREFFEND;
		if (kinderErgebnisse.contains(RegelErgebnis.NOCH_NICHT_GEPRUEFT))
			return RegelErgebnis.NOCH_NICHT_GEPRUEFT;
		if (kinderErgebnisse.contains(RegelErgebnis.VIELLEICHT_ZUTREFFEND))
			return RegelErgebnis.VIELLEICHT_ZUTREFFEND;
		return RegelErgebnis.ZUTREFFEND;
	}

}
