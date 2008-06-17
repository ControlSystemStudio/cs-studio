package org.csstudio.nams.common.material.regelwerk;

import java.util.Set;

import org.csstudio.nams.common.testhelper.ForTesting;

public class UndVersandRegel extends AbstractNodeVersandRegel {

	public UndVersandRegel(VersandRegel[] versandRegels) {
		super(versandRegels);
	}

	@Deprecated
	@ForTesting
	public RegelErgebnis auswerten(Pruefliste ergebnisListe) {
		Set<RegelErgebnis> kinderErgebnisse = gibKinderErgebnisse(ergebnisListe);
		if (kinderErgebnisse.contains(RegelErgebnis.NICHT_ZUTREFFEND))
			return RegelErgebnis.NICHT_ZUTREFFEND;
		if (kinderErgebnisse.contains(RegelErgebnis.NOCH_NICHT_GEPRUEFT))
			return RegelErgebnis.NOCH_NICHT_GEPRUEFT;
		if (kinderErgebnisse.contains(RegelErgebnis.VIELLEICHT_ZUTREFFEND))
			return RegelErgebnis.VIELLEICHT_ZUTREFFEND;
		return RegelErgebnis.ZUTREFFEND;
	}

	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("Und-Regel [");
		for (VersandRegel regel : children) {
			stringBuilder.append(regel.toString());
			stringBuilder.append(",");
		}
		stringBuilder.append("]");
		return stringBuilder.toString();
	}
}
