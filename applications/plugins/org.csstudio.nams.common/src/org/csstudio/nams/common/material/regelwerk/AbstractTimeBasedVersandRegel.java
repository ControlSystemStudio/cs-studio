
package org.csstudio.nams.common.material.regelwerk;

import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.Regelwerkskennung;

public abstract class AbstractTimeBasedVersandRegel implements VersandRegel {

	protected final VersandRegel ausloesungsregel;

	protected final VersandRegel bestaetigungsregel;
	protected final Millisekunden timeOut;
	protected Pruefliste internePruefliste;

	public AbstractTimeBasedVersandRegel(final VersandRegel ausloesungsregel,
			final VersandRegel bestaetigungsregel, final Millisekunden timeOut) {
		this.ausloesungsregel = ausloesungsregel;
		this.bestaetigungsregel = bestaetigungsregel;
		this.timeOut = timeOut;
		this.internePruefliste = new Pruefliste(Regelwerkskennung.valueOf(),
				ausloesungsregel);
	}

	@Override
    public Millisekunden pruefeNachrichtErstmalig(
			final AlarmNachricht nachricht, final Pruefliste ergebnisListe) {
		this.ausloesungsregel.pruefeNachrichtErstmalig(nachricht,
				this.internePruefliste);
		if (this.internePruefliste.gibErgebnisFuerRegel(this.ausloesungsregel) == RegelErgebnis.ZUTREFFEND) {
			ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(this,
					RegelErgebnis.VIELLEICHT_ZUTREFFEND);
			return this.timeOut;
		} else {
			ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(this,
					RegelErgebnis.NICHT_ZUTREFFEND);
			return Millisekunden.valueOf(0);
		}
	}

	// protected void mayWriteToHistory(Pruefliste pruefliste, AlarmNachricht
	// nachricht) {
	// getHistoryService().logTimeOutForTimeBased(
	// pruefliste.gibRegelwerkskennung().toString(), nachricht.toString(),
	// toString());
	// }
}
