
package org.csstudio.nams.common.material.regelwerk;

import java.util.Set;

import org.csstudio.nams.common.testhelper.ForTesting;

public class OderVersandRegel extends AbstractNodeVersandRegel {

	@Deprecated
	@ForTesting
	public OderVersandRegel() {
	    // Nothing to do
	}

	public OderVersandRegel(final VersandRegel[] versandRegeln) {
		super(versandRegeln);
	}

	@Override
	@ForTesting
	@Deprecated
	public RegelErgebnis auswerten(final Pruefliste ergebnisListe) {
		final Set<RegelErgebnis> kinderErgebnisse = this
				.gibKinderErgebnisse(ergebnisListe);
		if (kinderErgebnisse.contains(RegelErgebnis.ZUTREFFEND)) {
			return RegelErgebnis.ZUTREFFEND;
		}
		if (kinderErgebnisse.contains(RegelErgebnis.VIELLEICHT_ZUTREFFEND)) {
			return RegelErgebnis.VIELLEICHT_ZUTREFFEND;
		}
		if (kinderErgebnisse.contains(RegelErgebnis.NOCH_NICHT_GEPRUEFT)) {
			return RegelErgebnis.NOCH_NICHT_GEPRUEFT;
		}
		return RegelErgebnis.NICHT_ZUTREFFEND;
	}

	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder("Oder-Regel [");
		for (final VersandRegel regel : this.children) {
			stringBuilder.append(regel.toString());
			stringBuilder.append(",");
		}
		stringBuilder.append("]");
		return stringBuilder.toString();
	}
}
