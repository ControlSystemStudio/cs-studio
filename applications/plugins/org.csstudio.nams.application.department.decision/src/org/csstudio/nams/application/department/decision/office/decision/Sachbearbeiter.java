
/* 
 * Copyright (c) 2008 C1 WPS mbH, 
 * HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR
 * PURPOSE AND  NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, 
 * REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL
 * PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER 
 * EXCEPT UNDER THIS DISCLAIMER.
 * C1 WPS HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE 
 * SOFTWARE THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND 
 * OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU 
 * MAY FIND A COPY AT
 * {@link http://www.eclipse.org/org/documents/epl-v10.html}.
 */

package org.csstudio.nams.application.department.decision.office.decision;

import java.util.Iterator;

import org.csstudio.nams.application.department.decision.ThreadTypesOfDecisionDepartment;
import org.csstudio.nams.common.decision.Ablagefaehig;
import org.csstudio.nams.common.decision.Arbeitsfaehig;
import org.csstudio.nams.common.decision.Ausgangskorb;
import org.csstudio.nams.common.decision.Eingangskorb;
import org.csstudio.nams.common.decision.StandardAblagekorb;
import org.csstudio.nams.common.decision.Vorgangsmappe;
import org.csstudio.nams.common.decision.Vorgangsmappenkennung;
import org.csstudio.nams.common.decision.Zwischenablagekorb;
import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.regelwerk.Pruefliste;
import org.csstudio.nams.common.material.regelwerk.Regelwerk;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen;
import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.common.wam.Automat;

/**
 * Sachbearbeiter marks in cooperation with the TerminAssistenz Messages as to
 * be sent.
 * 
 * @author <a href="mailto:tr@c1-wps.de">Tobias Rathjen</a>, <a
 *         href="mailto:gs@c1-wps.de">Goesta Steen</a>, <a
 *         href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.1-{$date}
 */
@Automat
class Sachbearbeiter implements Arbeitsfaehig {
	private final DokumentVerbraucherArbeiter<Ablagefaehig> achteAufInterneEingaenge;
	private final Ausgangskorb<Vorgangsmappe> ausgangkorb;
	private final Regelwerk regelwerk;
	private final Zwischenablagekorb<Vorgangsmappe> ablagekorbFuerOffeneVorgaenge;
	private final Ausgangskorb<Terminnotiz> ausgangskorbZurAssistenz;
	private final String nameDesSachbearbeiters;
	private final DokumentVerbraucherArbeiter<Vorgangsmappe> achteAufVorgangsmappenEingaenge;
	private final DokumentVerbraucherArbeiter<Terminnotiz> achteAufTerminnotizEingaenge;
	// private Logger log = Logger.getLogger(Sachbearbeiter.class.getName());
	// private final HistoryService historyService;
	private final ExecutionService executionService;

	/**
	 * 
	 * @param nameDesSachbearbeiters
	 * @param eingangskorbFuerNeueVorgangsmappen
	 * @param eingangskorbFuerTerminnotizen
	 * @param ablagekorbFuerOffeneVorgaenge
	 * @param ausgangskorbZurAssistenz
	 * @param ausgangskorbFuerBearbeiteteVorgangsmappen
	 * @param regelwerk
	 * @param historyService
	 */
	public Sachbearbeiter(
			final ExecutionService executionService,
			final String nameDesSachbearbeiters,
			final Eingangskorb<Vorgangsmappe> eingangskorbFuerNeueVorgangsmappen,
			final Eingangskorb<Terminnotiz> eingangskorbFuerTerminnotizen,
			final Zwischenablagekorb<Vorgangsmappe> ablagekorbFuerOffeneVorgaenge,
			final Ausgangskorb<Terminnotiz> ausgangskorbZurAssistenz,
			final Ausgangskorb<Vorgangsmappe> ausgangskorbFuerBearbeiteteVorgangsmappen,
			final Regelwerk regelwerk
	// ,
	// HistoryService historyService
	) {

		this.executionService = executionService;
		this.nameDesSachbearbeiters = nameDesSachbearbeiters;
		this.ablagekorbFuerOffeneVorgaenge = ablagekorbFuerOffeneVorgaenge;
		this.ausgangskorbZurAssistenz = ausgangskorbZurAssistenz;
		this.ausgangkorb = ausgangskorbFuerBearbeiteteVorgangsmappen;
		this.regelwerk = regelwerk;
		// this.historyService = historyService;

		final Eingangskorb<Ablagefaehig> internerEingangskorb = new StandardAblagekorb<Ablagefaehig>();

		this.achteAufVorgangsmappenEingaenge = new DokumentVerbraucherArbeiter<Vorgangsmappe>(
				new DokumentenBearbeiter<Vorgangsmappe>() {
					public void bearbeiteVorgang(
							final Vorgangsmappe aeltestenEingang)
							throws InterruptedException {
						internerEingangskorb.ablegen(aeltestenEingang);
					}
				}, eingangskorbFuerNeueVorgangsmappen);

		this.achteAufTerminnotizEingaenge = new DokumentVerbraucherArbeiter<Terminnotiz>(
				new DokumentenBearbeiter<Terminnotiz>() {
					public void bearbeiteVorgang(
							final Terminnotiz aeltestenEingang)
							throws InterruptedException {
						internerEingangskorb.ablegen(aeltestenEingang);
					}
				}, eingangskorbFuerTerminnotizen);

		this.achteAufInterneEingaenge = new DokumentVerbraucherArbeiter<Ablagefaehig>(
				new DokumentenBearbeiter<Ablagefaehig>() {
					public void bearbeiteVorgang(
							final Ablagefaehig aeltestenEingang)
							throws InterruptedException {
						Sachbearbeiter.this
								.bearbeiteVorgangBeimSachbearbeiter(aeltestenEingang);
					}
				}, internerEingangskorb);
	}

	/**
	 * Beendet die Arbeit.
	 */
	public void beendeArbeit() {
		this.achteAufVorgangsmappenEingaenge.stopWorking();
		this.achteAufTerminnotizEingaenge.stopWorking();
		this.achteAufInterneEingaenge.stopWorking();

		// TODO Sende offene Vorgänge...
	}

	/**
	 * Beginnt mit der Arbeit.
	 */
	public void beginneArbeit() {
		this.executionService.executeAsynchronsly(
				ThreadTypesOfDecisionDepartment.SACHBEARBEITER,
				this.achteAufVorgangsmappenEingaenge);
		while (!this.achteAufVorgangsmappenEingaenge.isCurrentlyRunning()) {
			Thread.yield();
		}

		this.executionService.executeAsynchronsly(
				ThreadTypesOfDecisionDepartment.SACHBEARBEITER,
				this.achteAufTerminnotizEingaenge);
		while (!this.achteAufTerminnotizEingaenge.isCurrentlyRunning()) {
			Thread.yield();
		}

		this.executionService.executeAsynchronsly(
				ThreadTypesOfDecisionDepartment.SACHBEARBEITER,
				this.achteAufInterneEingaenge);
		while (!this.achteAufInterneEingaenge.isCurrentlyRunning()) {
			Thread.yield();
		}
	}

	public String gibName() {
		return this.nameDesSachbearbeiters;
	}

	public boolean istAmArbeiten() {
		return this.achteAufInterneEingaenge.isCurrentlyRunning()
				&& this.achteAufVorgangsmappenEingaenge.isCurrentlyRunning()
				&& this.achteAufTerminnotizEingaenge.isCurrentlyRunning();
	}

	protected void bearbeiteNeuenVorgang(final Vorgangsmappe vorgangsmappe)
			throws InterruptedException {
		final AlarmNachricht neueAlarmNachricht = vorgangsmappe
				.gibAusloesendeAlarmNachrichtDiesesVorganges();

		vorgangsmappe.setzePruefliste(this.regelwerk.gibNeueLeerePruefliste());
		this.regelwerk.pruefeNachrichtErstmalig(neueAlarmNachricht,
				vorgangsmappe.gibPruefliste());

		if (this.pruefungAbgeschlossen(vorgangsmappe.gibPruefliste()
				.gesamtErgebnis())) {
			vorgangsmappe.pruefungAbgeschlossenDurch(vorgangsmappe
					.gibMappenkennung());
			this.ausgangkorb.ablegen(vorgangsmappe);

			// FIXME History Eintrag erstellen. Klaeren ob hier oder in dem
			// PreferenceServiceActivator.

		} else {
			this.ablagekorbFuerOffeneVorgaenge.ablegen(vorgangsmappe);
			final Millisekunden zeitBisZumErneutenFragen = vorgangsmappe
					.gibPruefliste().gibMillisekundenBisZurNaechstenPruefung();
			final Terminnotiz terminnotiz = Terminnotiz.valueOf(vorgangsmappe
					.gibMappenkennung(), zeitBisZumErneutenFragen,
					this.nameDesSachbearbeiters);
			this.ausgangskorbZurAssistenz.ablegen(terminnotiz);
		}
	}

	/**
	 * Sachbearbeiter bekommt die aelteste Vorgangsmappe aus dem Eingangskorb
	 * uebergeben und arbeitet diesen Vorgang ab.
	 * 
	 * @param vorgangsmappe
	 *            Die aelteste Vorgangsmappe aus dem Eingangskorb
	 * @return True, wenn Alarmnachricht aus der uebergebenen Vorgangsmappe
	 *         Auswirkungen auf die in der Zwischenablage enthaltenen
	 *         Vorgangsmappen hat, andernfalls false.
	 * @throws InterruptedException
	 */
	protected boolean bearbeiteOffeneVorgaenge(final Vorgangsmappe vorgangsmappe)
			throws InterruptedException {

		final AlarmNachricht neueAlarmNachricht = vorgangsmappe
				.gibAusloesendeAlarmNachrichtDiesesVorganges();

		boolean alarmNachrichtHatAuswirkungAufOffeneVorgaenge = false;
		final Iterator<Vorgangsmappe> iterator = this.ablagekorbFuerOffeneVorgaenge
				.iterator();
		while (iterator.hasNext()) {
			final Vorgangsmappe offenerVorgang = iterator.next();
			final Pruefliste altePruefliste = offenerVorgang.gibPruefliste();
			altePruefliste.setzeAufNichtVeraendert();
			this.regelwerk
					.pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
							neueAlarmNachricht, altePruefliste);

			if (altePruefliste.hatSichGeaendert()) {
				if (this.pruefungAbgeschlossen(altePruefliste.gesamtErgebnis())) {
					// Kennung der neuen Mappe in die noch offene Mappe
					// hinzufuegen
					offenerVorgang.pruefungAbgeschlossenDurch(vorgangsmappe
							.gibMappenkennung());

					// Offenen Vorgang in den Ausgangskorb
					this.ausgangkorb.ablegen(offenerVorgang);

					// FIXME Sollte auch in der History geloggt werden. Mit Desy
					// (MC) klaeren.

					// entferne Mappe aus Ablagekorb fuer offenen Vorgaenge
					iterator.remove();
				}
				alarmNachrichtHatAuswirkungAufOffeneVorgaenge = true;
			}
		}

		if (alarmNachrichtHatAuswirkungAufOffeneVorgaenge) {
			// neue Vorgangsmappe auch in den Ausgangskorb (zum
			// loggen)
			// DummyPruefliste ersetzen
			vorgangsmappe.setzePruefliste(this.regelwerk
					.gibDummyPrueflisteNichtSenden());
			this.ausgangkorb.ablegen(vorgangsmappe);
		}

		return alarmNachrichtHatAuswirkungAufOffeneVorgaenge;
	}

	protected void bearbeiteTerminNotiz(final Terminnotiz notiz)
			throws InterruptedException {
		final Millisekunden zeitSeitLetzterBearbeitung = notiz.gibWartezeit();
		final Vorgangsmappenkennung mappenKennung = notiz
				.gibVorgangsmappenkennung();

		final Iterator<Vorgangsmappe> mappenIterator = this.ablagekorbFuerOffeneVorgaenge
				.iterator();
		while (mappenIterator.hasNext()) {
			final Vorgangsmappe offenerVorgang = mappenIterator.next();
			if (offenerVorgang.gibMappenkennung().equals(mappenKennung)) {
				this.regelwerk.pruefeNachrichtAufTimeOuts(offenerVorgang
						.gibPruefliste(), zeitSeitLetzterBearbeitung);
				if (this.pruefungAbgeschlossen(offenerVorgang.gibPruefliste()
						.gesamtErgebnis())) {

					// FIXME History nicht in AlarmNachrichten loggen. Soll in
					// die Application!
					// historyService.logTimeOutForTimeBased(regelwerk.gibRegelwerkskennung(),
					// offenerVorgang);

					offenerVorgang.abgeschlossenDurchTimeOut();
					this.ausgangkorb.ablegen(offenerVorgang);

					mappenIterator.remove();
				} else {
					final Millisekunden zeitBisZumErneutenFragen = offenerVorgang
							.gibPruefliste()
							.gibMillisekundenBisZurNaechstenPruefung();
					final Terminnotiz terminnotiz = Terminnotiz.valueOf(
							offenerVorgang.gibMappenkennung(),
							zeitBisZumErneutenFragen,
							this.nameDesSachbearbeiters);
					this.ausgangskorbZurAssistenz.ablegen(terminnotiz);
				}
			}
		}
	}

	// protected 4 testing
	protected void bearbeiteVorgangBeimSachbearbeiter(final Ablagefaehig eingang)
			throws InterruptedException {
		if (eingang instanceof Vorgangsmappe) {
			final Vorgangsmappe vorgangsmappe = (Vorgangsmappe) eingang;
			// Prüfe ob Alarmnachricht was mit Offenenvorgaengen zu tun hat
			boolean alarmNachrichtHatAuswirkungAufOffeneVorgaenge = this
					.bearbeiteOffeneVorgaenge(vorgangsmappe);

			// Alarmnachricht mit neuer Pruefliste prüfen
			if (!alarmNachrichtHatAuswirkungAufOffeneVorgaenge) {
				this.bearbeiteNeuenVorgang(vorgangsmappe);
			}
		} else if (eingang instanceof Terminnotiz) {
			// Terminnotiz bearbeiten
			final Terminnotiz notiz = (Terminnotiz) eingang;
			this.bearbeiteTerminNotiz(notiz);
		} else {
			throw new RuntimeException(
					"Eingang kann nicht bearbeitet werden! (einfang ist weder Vorgangsmappe noch Terminnotiz");
		}
	}

	private boolean pruefungAbgeschlossen(
			final WeiteresVersandVorgehen gesamtErgebnis) {
		switch (gesamtErgebnis) {
		case VERSENDEN:
		case NICHT_VERSENDEN:
			return true;
		}
		return false;
	}
}
