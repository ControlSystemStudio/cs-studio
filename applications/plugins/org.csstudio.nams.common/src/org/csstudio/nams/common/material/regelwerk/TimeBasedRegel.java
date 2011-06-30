
package org.csstudio.nams.common.material.regelwerk;

import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.AlarmNachricht;

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
public class TimeBasedRegel extends AbstractTimeBasedVersandRegel implements
		VersandRegel {

	private final VersandRegel aufhebungsregel;

	/**
	 * TODO ggf. in einer factory bauen. tr: der BuilderService benutzt diesen
	 * Konstruktor, eine Factory erscheint mir unsinnig
	 */
	public TimeBasedRegel(final VersandRegel ausloesungsregel,
			final VersandRegel aufhebungsregel,
			final VersandRegel bestaetigungsregel, final Millisekunden timeOut) {
		super(ausloesungsregel, bestaetigungsregel, timeOut);
		this.aufhebungsregel = aufhebungsregel;
	}

	@Override
    public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
			final AlarmNachricht nachricht, final Pruefliste bisherigesErgebnis) {
		if (!bisherigesErgebnis.gibErgebnisFuerRegel(this).istEntschieden()) {
			if (this.bestaetigungsregel != null) {

				this.bestaetigungsregel.pruefeNachrichtErstmalig(nachricht,
						this.internePruefliste);
				final RegelErgebnis bestaetigungsregelErgebnis = this.internePruefliste
						.gibErgebnisFuerRegel(this.bestaetigungsregel);

				if (bestaetigungsregelErgebnis == RegelErgebnis.ZUTREFFEND) {
					bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(
							this, RegelErgebnis.ZUTREFFEND);
					return;
				}
			}

			if (this.aufhebungsregel != null) {

				this.aufhebungsregel.pruefeNachrichtErstmalig(nachricht,
						this.internePruefliste);
				final RegelErgebnis aufhebungsregelErgebnis = this.internePruefliste
						.gibErgebnisFuerRegel(this.aufhebungsregel);

				if (aufhebungsregelErgebnis == RegelErgebnis.ZUTREFFEND) {
					bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(
							this, RegelErgebnis.NICHT_ZUTREFFEND);
					return;
				}
			}
			bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(this,
					RegelErgebnis.VIELLEICHT_ZUTREFFEND);
		}
	}

	@Override
    public Millisekunden pruefeNachrichtAufTimeOuts(
			final Pruefliste bisherigesErgebnis,
			final Millisekunden verstricheneZeit) {
		if (!bisherigesErgebnis.gibErgebnisFuerRegel(this).istEntschieden()) {
			if (verstricheneZeit.istKleiner(this.timeOut)) {
				return this.timeOut.differenz(verstricheneZeit);
			} else {
				// TODO TimeBased und TimeBasedBeiBeastaetigung zusammenfuegen
				// Das Ergebnis ueber den Konstruktor setzen
				bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(this,
						RegelErgebnis.ZUTREFFEND);
			}
			// if
			// (bisherigesErgebnis.gibErgebnisFuerRegel(this).istEntschieden()){
			// mayWriteToHistory(bisherigesErgebnis, initialeNachricht);
			// }
		}
		return null;
	}

	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder(
				"(TimebasedRegel: TimeBehavior: Alarm bei TimeOut: ");
		stringBuilder.append(this.timeOut);
		stringBuilder.append(" Startregel: ");
		stringBuilder.append(this.ausloesungsregel);
		stringBuilder.append(" Aufhebungsregel: ");
		stringBuilder.append(this.ausloesungsregel);
		stringBuilder.append(")");
		return stringBuilder.toString();
	}

}
