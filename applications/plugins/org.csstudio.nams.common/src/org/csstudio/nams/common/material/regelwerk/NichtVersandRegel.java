
package org.csstudio.nams.common.material.regelwerk;

import java.util.Set;

public class NichtVersandRegel extends AbstractNodeVersandRegel {

	public NichtVersandRegel(final VersandRegel versandRegel) {
		this.addChild(versandRegel);
	}

	@Override
	public RegelErgebnis auswerten(final Pruefliste ergebnisListe) {
		final Set<RegelErgebnis> kinderErgebnisse = this
				.gibKinderErgebnisse(ergebnisListe);
		if (kinderErgebnisse.contains(RegelErgebnis.NICHT_ZUTREFFEND)) {
			return RegelErgebnis.ZUTREFFEND;
		} else if (kinderErgebnisse.contains(RegelErgebnis.ZUTREFFEND)) {
			return RegelErgebnis.NICHT_ZUTREFFEND;
		} else {
			return RegelErgebnis.VIELLEICHT_ZUTREFFEND;
		}
	}

	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder("Nicht-Regel [");
		for (final VersandRegel regel : this.children) {
			stringBuilder.append(regel.toString());
		}
		stringBuilder.append("]");
		return stringBuilder.toString();
	}
}
