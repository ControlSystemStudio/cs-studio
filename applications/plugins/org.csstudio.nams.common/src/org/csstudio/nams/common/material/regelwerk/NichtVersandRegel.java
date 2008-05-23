package org.csstudio.nams.common.material.regelwerk;

import java.util.Set;

public class NichtVersandRegel extends AbstractNodeVersandRegel {

	public NichtVersandRegel(VersandRegel versandRegel){
		addChild(versandRegel);
	}
	
	@Override
	public RegelErgebnis auswerten(Pruefliste ergebnisListe) {
		Set<RegelErgebnis> kinderErgebnisse = gibKinderErgebnisse(ergebnisListe);
		if (kinderErgebnisse.contains(RegelErgebnis.NICHT_ZUTREFFEND)){
			return RegelErgebnis.ZUTREFFEND;
		} else if (kinderErgebnisse.contains(RegelErgebnis.ZUTREFFEND)){
			return RegelErgebnis.NICHT_ZUTREFFEND;
		}	else {
			return RegelErgebnis.VIELLEICHT_ZUTREFFEND;
		}
	}

}
