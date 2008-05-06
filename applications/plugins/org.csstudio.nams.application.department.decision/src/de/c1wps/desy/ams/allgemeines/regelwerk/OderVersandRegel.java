package de.c1wps.desy.ams.allgemeines.regelwerk;

import java.util.Set;

public class OderVersandRegel extends AbstractNodeVersandRegel {

	public OderVersandRegel(VersandRegel[] versandRegeln) {
		super(versandRegeln);
	}

	@Deprecated
	OderVersandRegel()
	{
		
	}
			
	
	@Override
	RegelErgebnis auswerten(Pruefliste ergebnisListe) {
		Set<RegelErgebnis> kinderErgebnisse = gibKinderErgebnisse(ergebnisListe);
		if (kinderErgebnisse.contains(RegelErgebnis.ZUTREFFEND))
			return RegelErgebnis.ZUTREFFEND;
		if (kinderErgebnisse.contains(RegelErgebnis.VIELLEICHT_ZUTREFFEND))
			return RegelErgebnis.VIELLEICHT_ZUTREFFEND;
		if (kinderErgebnisse.contains(RegelErgebnis.NOCH_NICHT_GEPRUEFT))
			return RegelErgebnis.NOCH_NICHT_GEPRUEFT;
		return RegelErgebnis.NICHT_ZUTREFFEND;
	}

}
