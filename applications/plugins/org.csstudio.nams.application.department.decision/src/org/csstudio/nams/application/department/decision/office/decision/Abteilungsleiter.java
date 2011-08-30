
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

import org.csstudio.nams.application.department.decision.ThreadTypesOfDecisionDepartment;
import org.csstudio.nams.common.decision.Arbeitsfaehig;
import org.csstudio.nams.common.decision.Eingangskorb;
import org.csstudio.nams.common.decision.Vorgangsmappe;
import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.common.wam.Automat;

/**
 * Der Abteilungsleiter ist verantwortlich für die Bearbeitung von Nachrichten.
 */
@Automat
class Abteilungsleiter implements DokumentenBearbeiter<Vorgangsmappe>,
		Arbeitsfaehig {
	private final DokumentVerbraucherArbeiter<Vorgangsmappe> achteAufEingaenge;
	private final Eingangskorb<Vorgangsmappe>[] sachbearbeiterEingangkoerbe;
	private final ExecutionService executionService;

	/**
	 * Legt den Abteilungsleiter an, der die Alarmvorgänge an seine
	 * Sachbearbeiter verteilt.
	 * 
	 * @param eingangskorbNeuerAlarmVorgaenge
	 * @param sachbearbeiterEingangkoerbe
	 */
	public Abteilungsleiter(final ExecutionService executionService,
			final Eingangskorb<Vorgangsmappe> eingangskorbNeuerAlarmVorgaenge,
			final Eingangskorb<Vorgangsmappe>[] sachbearbeiterEingangkoerbe) {
		this.executionService = executionService;
		this.sachbearbeiterEingangkoerbe = sachbearbeiterEingangkoerbe;

		this.achteAufEingaenge = new DokumentVerbraucherArbeiter<Vorgangsmappe>(
				this, eingangskorbNeuerAlarmVorgaenge);
	}

	/**
	 * Delegiert Vorgaende an die {@link Sachbearbeiter}.
	 */
	public void bearbeiteVorgang(final Vorgangsmappe mappe) {
		// Nachricht kopieren.
		for (final Eingangskorb<Vorgangsmappe> eingangskorb : this.sachbearbeiterEingangkoerbe) {
			final Vorgangsmappe erstelleKopieFuer = mappe
					.erstelleKopieFuer(eingangskorb.toString());
			try {
				eingangskorb.ablegen(erstelleKopieFuer);
			} catch (final InterruptedException e) {
				// TODO Ist ein Interrupt hier OK? Wenn ja, wie mit der
				// Vorgangsmappe verfahren, wenn nein, wie den Fehler behandeln?
				e.printStackTrace();
			}
		}
	}

	/**
	 * Beendet die Arbeit.
	 */
	public void beendeArbeit() {
		this.achteAufEingaenge.stopWorking();
	}

	/**
	 * Beginnt mit der Arbeit, das auslesen neuer Nachrichten und das delegieren
	 * der Aufgaben etc..
	 */
	public void beginneArbeit() {
		this.executionService.executeAsynchronsly(
				ThreadTypesOfDecisionDepartment.ABTEILUNGSLEITER,
				this.achteAufEingaenge);

		while (!this.achteAufEingaenge.isCurrentlyRunning()) {
			Thread.yield();
		}
	}

	public boolean istAmArbeiten() {
		return this.achteAufEingaenge.isCurrentlyRunning();
	}
}
