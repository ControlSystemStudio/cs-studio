
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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.csstudio.nams.common.decision.Ablagefaehig;
import org.csstudio.nams.common.decision.Ausgangskorb;
import org.csstudio.nams.common.decision.BeobachbarerEingangskorb;
import org.csstudio.nams.common.decision.Eingangskorb;
import org.csstudio.nams.common.decision.ExecutorBeobachtbarerEingangskorb;
import org.csstudio.nams.common.decision.StandardAblagekorb;
import org.csstudio.nams.common.decision.Vorgangsmappe;
import org.csstudio.nams.common.material.regelwerk.Regelwerk;
import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.common.wam.Arbeitsumgebung;

/**
 * Repraesentiert die Abteilung Alarm-Entscheidungs-Buero mit Ihrer gesammten
 * Einrichtung. Dieses Buero trifft die Entscheidung, ob eine Nachricht
 * versendet werden soll oder nicht.
 * 
 * Note: Dieses ist die einzige exportierte Klasse dieses Sub-Systems.
 */
@Arbeitsumgebung
public class AlarmEntscheidungsBuero {

	private final Eingangskorb<Vorgangsmappe> alarmVorgangEingangskorb;
	private final Ausgangskorb<Vorgangsmappe> ausgangskorb;

	private final TerminAssistenz _assistenz;
	private final Abteilungsleiter _abteilungsleiter;
	private final List<Sachbearbeiter> _sachbearbeiterList;

	/**
	 * Legt ein neues Alarmbuero an. Es wird zugesichert, das nur hier
	 * angeforderte system-externe Komponenten in diesem sub-System verwendet
	 * werden.
	 * 
	 * TODO Logger-Service hinzufuegen/reinreichen um das Logging testbar zu
	 * machen, da dieses wichtig fuer Nachweiszwecke ist.
	 * 
	 * @param historyService
	 */
	public AlarmEntscheidungsBuero(final ExecutionService executionService,
			final Regelwerk[] regelwerke,
			final Eingangskorb<Vorgangsmappe> alarmVorgangEingangskorb,
			final Ausgangskorb<Vorgangsmappe> alarmVorgangAusgangskorb) {
		this.alarmVorgangEingangskorb = alarmVorgangEingangskorb;
		this.ausgangskorb = alarmVorgangAusgangskorb;
		this._sachbearbeiterList = new LinkedList<Sachbearbeiter>();
		// Sachbearbeiter und Koerbe anlegen:
		final StandardAblagekorb<Terminnotiz> terminAssistenzAblagekorb = new StandardAblagekorb<Terminnotiz>();
		final Eingangskorb<Ablagefaehig>[] eingangskoerbeSachbearbeiter = this
				.erzeugeEingangskoerbeArray(regelwerke.length);
		final Map<String, Eingangskorb<Ablagefaehig>> terminEingangskoerbeDerSachbearbeiter = new HashMap<String, Eingangskorb<Ablagefaehig>>();

		final Executor threadPool = Executors.newFixedThreadPool(10);
		
		for (int zaehler = 0; zaehler < regelwerke.length; zaehler++) {

			final BeobachbarerEingangskorb<Ablagefaehig> eingangskorb = new ExecutorBeobachtbarerEingangskorb<Ablagefaehig>(threadPool);
			eingangskoerbeSachbearbeiter[zaehler] = eingangskorb;
			final Sachbearbeiter sachbearbeiter = new Sachbearbeiter(
					"" + zaehler, 
					eingangskorb,
					new StandardAblagekorb<Vorgangsmappe>(),
					terminAssistenzAblagekorb,
					this.ausgangskorb,
					regelwerke[zaehler]
			);

			terminEingangskoerbeDerSachbearbeiter.put(sachbearbeiter.gibName(),
					eingangskorb);
			this._sachbearbeiterList.add(sachbearbeiter);
			sachbearbeiter.beginneArbeit();
		}

		this._assistenz = new TerminAssistenz(executionService,
				terminAssistenzAblagekorb,
				terminEingangskoerbeDerSachbearbeiter, new Timer());
		this._assistenz.beginneArbeit();

		this._abteilungsleiter = new Abteilungsleiter(executionService, this
				.gibAlarmVorgangEingangskorb(), eingangskoerbeSachbearbeiter);
		// Starten...
		this._abteilungsleiter.beginneArbeit();
	}

	/**
	 * Beendet die Arbeit des Büros. Diese Operation kehrt zurück, wenn alle
	 * Arbeitsgänge erldigt sind und alle offenen Vorgänge in den Ausgangskorb
	 * zum senden gelegt wurden.
	 */
	public void beendeArbeitUndSendeSofortAlleOffeneneVorgaenge() {
		// Terminassistenz beenden...
		this._assistenz.beendeArbeit();
		// Sachbearbeiter in den Feierabend schicken...
		for (final Sachbearbeiter sachbearbeiter : this._sachbearbeiterList) {
			sachbearbeiter.beendeArbeit();
		}
		// Andere Threads zu ende arbeiten lassen
		Thread.yield();
		// Abteilungsleiter in den Feierabend schicken...
		this._abteilungsleiter.beendeArbeit();
	}

	/**
	 * Liefert den Ausgangskorb, in dem die bearbeiteten Vorgaenge abgelegt
	 * werden.
	 */
	public Ausgangskorb<Vorgangsmappe> gibAlarmVorgangAusgangskorb() {
		return this.ausgangskorb;
	}

	/**
	 * Gibt zugriff auf den Eingangskorb für Alarmvorgaenge. Die Mappe wird so
	 * verwendet, wie diese hineingereicht wird. Das Kapitel
	 * "AlarmEntscheidungsbuero" sollte extern nicht veraendert werden.
	 * 
	 * @return Einen Eingangskorb fuer neue Vorgaenge.
	 */
	public Eingangskorb<Vorgangsmappe> gibAlarmVorgangEingangskorb() {
		return this.alarmVorgangEingangskorb;
	}

	/**
	 * Inspector fuer Tests. Liefert die Referenz auf den Abteilungsleiter.
	 */
	Abteilungsleiter gibAbteilungsleiterFuerTest() {
		return this._abteilungsleiter;
	}

	/**
	 * Inspector fuer Tests. Liefert die Referenz auf die Terminassistenz.
	 */
	TerminAssistenz gibAssistenzFuerTest() {
		return this._assistenz;
	}

	/**
	 * Inspector fuer Tests. Liefert die Referenzen auf alle Sachbearbeiter.
	 */
	Collection<Sachbearbeiter> gibListeDerSachbearbeiterFuerTest() {
		return this._sachbearbeiterList;
	}

	@SuppressWarnings("unchecked")
	private Eingangskorb<Ablagefaehig>[] erzeugeEingangskoerbeArray(
			final int length) {
		return new Eingangskorb[length];
	}
}
