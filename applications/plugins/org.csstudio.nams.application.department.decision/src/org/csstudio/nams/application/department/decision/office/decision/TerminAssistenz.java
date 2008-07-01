package org.csstudio.nams.application.department.decision.office.decision;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.csstudio.nams.common.decision.Arbeitsfaehig;
import org.csstudio.nams.common.decision.Eingangskorb;
import org.csstudio.nams.common.fachwert.Millisekunden;


/**
 * Terminassitenzen informieren über zuvor zugereichte Termine.
 *
 * @author <a href="mailto:tr@c1-wps.de">Tobias Rathjen</a>,
 *         <a href="mailto:gs@c1-wps.de">Goesta Steen</a>,
 *         <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.1, 31.03.2008
 */
public class TerminAssistenz implements Arbeitsfaehig {
	private final DokumentVerbraucherArbeiter<Terminnotiz> dokumentVerbraucherArbeiter;
	private final Timer timer;

	public TerminAssistenz(
			Eingangskorb<Terminnotiz> eingehendeTerminnotizen,
			Map<String, Eingangskorb<Terminnotiz>> eingangskoerbeDerSachbearbeiterNachNamen,
			Timer timer) {
		this.timer = timer;
		dokumentVerbraucherArbeiter = new DokumentVerbraucherArbeiter<Terminnotiz>(new Terminbearbeitung(
				eingangskoerbeDerSachbearbeiterNachNamen),
				eingehendeTerminnotizen);
	}

	
	public void beginneArbeit() {
		dokumentVerbraucherArbeiter.start();
	}
	
	public boolean istAmArbeiten() {
		return dokumentVerbraucherArbeiter.isAlive();
	}
	
	public void beendeArbeit() {
		dokumentVerbraucherArbeiter.beendeArbeit();
	}
	
	private class Terminbearbeitung implements
			DokumentenBearbeiter<Terminnotiz> {
		private final Map<String, Eingangskorb<Terminnotiz>> eingangskoerbeDerSachbearbeiterNachNamen;

		public Terminbearbeitung(
				Map<String, Eingangskorb<Terminnotiz>> eingangskoerbeDerSachbearbeiterNachNamen) {
			this.eingangskoerbeDerSachbearbeiterNachNamen = eingangskoerbeDerSachbearbeiterNachNamen;
		}

		public void bearbeiteVorgang(final Terminnotiz eingang)
				throws InterruptedException {
			final String name = eingang.gibNamenDesZuInformierendenSachbearbeiters();
			final Millisekunden wartezeit = eingang.gibWartezeit();
			final Eingangskorb<Terminnotiz> eingangskorb = eingangskoerbeDerSachbearbeiterNachNamen.get(name);
			if( eingangskorb == null )
			{
				throw new RuntimeException("Zu jedem Namen sollte es ein Korb geben.");
			}
			TimerTask futureTask = new TimerTask() {
				public void run() {
						try {
							eingangskorb.ablegen(eingang);
						} catch (InterruptedException e) {
							throw new RuntimeException("Ablegen in einen Eingangskorb schlug fehl.", e);
						}
				}
			};
			TerminAssistenz.this.timer.schedule(futureTask, wartezeit.alsLongVonMillisekunden());
		}
	}
}
