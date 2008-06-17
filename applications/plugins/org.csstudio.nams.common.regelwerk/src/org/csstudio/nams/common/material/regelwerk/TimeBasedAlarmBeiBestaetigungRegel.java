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
public class TimeBasedAlarmBeiBestaetigungRegel extends AbstractVersandRegel
		implements VersandRegel {

	private final VersandRegel ausloesungsregel;
	private final VersandRegel bestaetigungsregel;
	private final Millisekunden timeOut;
	private Pruefliste internePruefliste;

	/**
	 * TODO ggf. in einer factory bauen. tr: der BuilderService benutzt diesen
	 * Konstruktor, eine Factory erscheint mir unsinnig
	 */
	public TimeBasedAlarmBeiBestaetigungRegel(VersandRegel ausloesungsregel,
			VersandRegel bestaetigungsregel, Millisekunden timeOut) {
		this.ausloesungsregel = ausloesungsregel;
		this.bestaetigungsregel = bestaetigungsregel;
		this.timeOut = timeOut;

		// FIXME soll hier nicht erzeugt werden!
		internePruefliste = new Pruefliste(Regelwerkskennung.valueOf(),
				ausloesungsregel);
	}

	public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
			AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
		if (!bisherigesErgebnis.gibErgebnisFuerRegel(this).istEntschieden()) {
			// bestaetigungsregel.pruefeNachrichtErstmalig(nachricht,
			// bisherigesErgebnis);
			// RegelErgebnis bestaetigungsregelErgebnis = bisherigesErgebnis
			// .gibErgebnisFuerRegel(bestaetigungsregel);

			bestaetigungsregel.pruefeNachrichtErstmalig(nachricht,
					internePruefliste);
			RegelErgebnis bestaetigungsregelErgebnis = internePruefliste
					.gibErgebnisFuerRegel(bestaetigungsregel);

			if (bestaetigungsregelErgebnis == RegelErgebnis.ZUTREFFEND) {
				bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(this,
						RegelErgebnis.ZUTREFFEND);
				return;
			}
		}
	}

	public Millisekunden pruefeNachrichtAufTimeOuts(
			Pruefliste bisherigesErgebnis, Millisekunden verstricheneZeit) {
		if (!bisherigesErgebnis.gibErgebnisFuerRegel(this).istEntschieden()) {
			if (verstricheneZeit.istKleiner(timeOut)) {
				return timeOut.differenz(verstricheneZeit);
			} else {
				bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(this,
						RegelErgebnis.NICHT_ZUTREFFEND);
			}
		}
		return null;
	}

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
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("(TimebasedRegel: TimeBehavior: Alarm bei Bestätigung: ");
		stringBuilder.append(timeOut);
		stringBuilder.append(" Startregel: ");
		stringBuilder.append(ausloesungsregel);
		stringBuilder.append(" Aufhebungsregel: ");
		stringBuilder.append(ausloesungsregel);
		stringBuilder.append(")");
		return stringBuilder.toString();
	}
}
