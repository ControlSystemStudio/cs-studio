package org.csstudio.nams.common.material.regelwerk;

import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.Regelwerkskennung;

public abstract class AbstractTimeBasedVersandRegel extends
		AbstractVersandRegel {
	
	public AbstractTimeBasedVersandRegel(VersandRegel ausloesungsregel,
			VersandRegel bestaetigungsregel, Millisekunden timeOut){
				this.ausloesungsregel = ausloesungsregel;
				this.bestaetigungsregel = bestaetigungsregel;
				this.timeOut = timeOut;
				internePruefliste = new Pruefliste(Regelwerkskennung.valueOf(),
						ausloesungsregel);
	}
	
	protected final VersandRegel ausloesungsregel;
	protected final VersandRegel bestaetigungsregel;
	protected final Millisekunden timeOut;
	protected Pruefliste internePruefliste;
	
	public Millisekunden pruefeNachrichtErstmalig(AlarmNachricht nachricht,
			Pruefliste ergebnisListe) {
		ausloesungsregel.pruefeNachrichtErstmalig(nachricht, internePruefliste);
		if (internePruefliste.gibErgebnisFuerRegel(ausloesungsregel) == RegelErgebnis.ZUTREFFEND) {
			ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(this,
					RegelErgebnis.VIELLEICHT_ZUTREFFEND);
			return timeOut;
		} else {
			ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(this,
					RegelErgebnis.NICHT_ZUTREFFEND);
			return Millisekunden.valueOf(0);
		}
	}
	
	protected void mayWriteToHistory(Pruefliste pruefliste, AlarmNachricht nachricht) {
		getHistoryService().logTimeOutForTimeBased(
				pruefliste.gibRegelwerkskennung().toString(), nachricht.toString(), toString());
	}
}
