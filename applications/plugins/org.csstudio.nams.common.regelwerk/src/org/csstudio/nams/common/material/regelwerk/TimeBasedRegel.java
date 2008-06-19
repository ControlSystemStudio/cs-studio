package org.csstudio.nams.common.material.regelwerk;

import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.Regelwerkskennung;

/**
 * TimeBasedRegeln dürfen keine(!) TimeBasedRegeln als Ausloesungs, Bestätigungs
 * oder Aufhebungsregel besitzen.
 * 
 * 
 * @author <a href="mailto:tr@c1-wps.de">Tobias Rathjen</a>, <a
 *         href="mailto:gs@c1-wps.de">Goesta Steen</a>, <a
 *         href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.1, 08.04.2008
 */
public class TimeBasedRegel extends AbstractTimeBasedVersandRegel implements VersandRegel {

	
	private final VersandRegel aufhebungsregel;


	/**
	 * TODO ggf. in einer factory bauen. tr: der BuilderService benutzt diesen
	 * Konstruktor, eine Factory erscheint mir unsinnig
	 */
	public TimeBasedRegel(VersandRegel ausloesungsregel, VersandRegel aufhebungsregel,
			VersandRegel bestaetigungsregel, Millisekunden timeOut) {
		super(ausloesungsregel,bestaetigungsregel, timeOut);
		this.aufhebungsregel = aufhebungsregel;
	}

	public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
			AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
		if (!bisherigesErgebnis.gibErgebnisFuerRegel(this).istEntschieden()) {
			bestaetigungsregel.pruefeNachrichtErstmalig(nachricht,
					internePruefliste);
			RegelErgebnis bestaetigungsregelErgebnis = internePruefliste
			.gibErgebnisFuerRegel(bestaetigungsregel);
			
			if (bestaetigungsregelErgebnis == RegelErgebnis.ZUTREFFEND) {
				bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(this,
						RegelErgebnis.ZUTREFFEND);
				return;
			}
			
			aufhebungsregel.pruefeNachrichtErstmalig(nachricht, internePruefliste);
			RegelErgebnis aufhebungsregelErgebnis = internePruefliste
			.gibErgebnisFuerRegel(aufhebungsregel);
			
			if (aufhebungsregelErgebnis == RegelErgebnis.ZUTREFFEND) {
				bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(this,
						RegelErgebnis.NICHT_ZUTREFFEND);
				return;
			}
			bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(this,
					RegelErgebnis.VIELLEICHT_ZUTREFFEND);
		}
	}

	public Millisekunden pruefeNachrichtAufTimeOuts(
			Pruefliste bisherigesErgebnis, Millisekunden verstricheneZeit, AlarmNachricht initialeNachricht) {
		if (!bisherigesErgebnis.gibErgebnisFuerRegel(this).istEntschieden()) {
			if (verstricheneZeit.istKleiner(timeOut)) {
				return timeOut.differenz(verstricheneZeit);
			} else {
				bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(this,
						RegelErgebnis.ZUTREFFEND);
			}
//			if (bisherigesErgebnis.gibErgebnisFuerRegel(this).istEntschieden()){
//				mayWriteToHistory(bisherigesErgebnis, initialeNachricht);
//			}
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("(TimebasedRegel: TimeBehavior: Alarm bei TimeOut: ");
		stringBuilder.append(timeOut);
		stringBuilder.append(" Startregel: ");
		stringBuilder.append(ausloesungsregel);
		stringBuilder.append(" Aufhebungsregel: ");
		stringBuilder.append(ausloesungsregel);
		stringBuilder.append(")");
		return stringBuilder.toString();
	}

}
