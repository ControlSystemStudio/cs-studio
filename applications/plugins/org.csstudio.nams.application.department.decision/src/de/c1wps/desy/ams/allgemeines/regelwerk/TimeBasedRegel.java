package de.c1wps.desy.ams.allgemeines.regelwerk;

import de.c1wps.desy.ams.allgemeines.AlarmNachricht;
import de.c1wps.desy.ams.allgemeines.Millisekunden;

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
public class TimeBasedRegel implements VersandRegel {

	private final VersandRegel ausloesungsregel;
	private final VersandRegel aufhebungsregel;
	private final VersandRegel bestaetigungsregel;
	private final Millisekunden timeOut;
	private Pruefliste internePruefliste;

	/**
	 * TODO ggf. in einer factory bauen.
	 */
	public TimeBasedRegel(VersandRegel ausloesungsregel, VersandRegel aufhebungsregel,
			VersandRegel bestaetigungsregel, Millisekunden timeOut) {
		this.ausloesungsregel = ausloesungsregel;
		this.aufhebungsregel = aufhebungsregel;
		this.bestaetigungsregel = bestaetigungsregel;
		this.timeOut = timeOut;
		
		// FIXME soll hier nicht erzeugt werden!
		internePruefliste = new Pruefliste(Regelwerkskennung.valueOf(), ausloesungsregel);
	}

	public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
			AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
		if (!bisherigesErgebnis.gibErgebnisFuerRegel(this).istEntschieden()) {
//			bestaetigungsregel.pruefeNachrichtErstmalig(nachricht,
//					bisherigesErgebnis);
//			RegelErgebnis bestaetigungsregelErgebnis = bisherigesErgebnis
//			.gibErgebnisFuerRegel(bestaetigungsregel);

			bestaetigungsregel.pruefeNachrichtErstmalig(nachricht,
					internePruefliste);
			RegelErgebnis bestaetigungsregelErgebnis = internePruefliste
			.gibErgebnisFuerRegel(bestaetigungsregel);
			
			if (bestaetigungsregelErgebnis == RegelErgebnis.ZUTREFFEND) {
				bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(this,
						RegelErgebnis.ZUTREFFEND);
				return;
			}
			
//			aufhebungsregel.pruefeNachrichtErstmalig(nachricht, bisherigesErgebnis);
//			RegelErgebnis aufhebungsregelErgebnis = bisherigesErgebnis
//			.gibErgebnisFuerRegel(aufhebungsregel);
			
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
			Pruefliste bisherigesErgebnis, Millisekunden verstricheneZeit) {
		if (!bisherigesErgebnis.gibErgebnisFuerRegel(this).istEntschieden()) {
			if (verstricheneZeit.istKleiner(timeOut)) {
				// TODO Muss VIELLEICHT_ZUTREFFEND gesetzt werden?
				// Muss Dirty-Flag der Pruefliste gesetzt werden? 
				// Goesta:"Nein, beides nicht"
				// bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(this,
				// RegelErgebnis.VIELLEICHT_ZUTREFFEND);
				return timeOut.differenz(verstricheneZeit);
			} else {
				bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(this,
						RegelErgebnis.ZUTREFFEND);
			}
		}
		return null;
	}

	public Millisekunden pruefeNachrichtErstmalig(AlarmNachricht nachricht,
			Pruefliste ergebnisListe) {
//		ausloesungsregel.pruefeNachrichtErstmalig(nachricht, ergebnisListe);
//
//		if (ergebnisListe.gibErgebnisFuerRegel(ausloesungsregel) == RegelErgebnis.ZUTREFFEND) {
		
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

//	public Millisekunden ermittleVerbleibendeWartezeit(
//			Millisekunden bereitsVerstricheneWarteZeit) {
//		return timeOut.differenz(bereitsVerstricheneWarteZeit);
//	}

}
