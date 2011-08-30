
/*
 * Copyright (c) C1 WPS mbH, HAMBURG, GERMANY. All Rights Reserved.
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

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.csstudio.nams.application.department.decision.ThreadTypesOfDecisionDepartment;
import org.csstudio.nams.common.decision.Arbeitsfaehig;
import org.csstudio.nams.common.decision.Eingangskorb;
import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.service.ExecutionService;

/**
 * Terminassitenzen informieren über zuvor zugereichte Termine.
 * 
 * @author <a href="mailto:tr@c1-wps.de">Tobias Rathjen</a>, <a
 *         href="mailto:gs@c1-wps.de">Goesta Steen</a>, <a
 *         href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.1, 31.03.2008
 */
public class TerminAssistenz implements Arbeitsfaehig {
	private class Terminbearbeitung implements
			DokumentenBearbeiter<Terminnotiz> {
		private final Map<String, Eingangskorb<Terminnotiz>> eingangskoerbeDerSachbearbeiterNachNamen;

		public Terminbearbeitung(
				final Map<String, Eingangskorb<Terminnotiz>> eingangskoerbeDerSachbearbeiterNachNamen) {
			this.eingangskoerbeDerSachbearbeiterNachNamen = eingangskoerbeDerSachbearbeiterNachNamen;
		}

		public void bearbeiteVorgang(final Terminnotiz eingang)
				throws InterruptedException {
			final String name = eingang
					.gibNamenDesZuInformierendenSachbearbeiters();
			final Millisekunden wartezeit = eingang.gibWartezeit();
			final Eingangskorb<Terminnotiz> eingangskorb = this.eingangskoerbeDerSachbearbeiterNachNamen
					.get(name);
			if (eingangskorb == null) {
				throw new RuntimeException(
						"Zu jedem Namen sollte es ein Korb geben.");
			}
			final TimerTask futureTask = new TimerTask() {
				@Override
				public void run() {
					try {
						eingangskorb.ablegen(eingang);
					} catch (InterruptedException e) {
						throw new RuntimeException(
								"Ablegen in einen Eingangskorb schlug fehl.", e);
					}
				}
			};
			TerminAssistenz.this.timer.schedule(futureTask, wartezeit
					.alsLongVonMillisekunden());
		}
	}

	private final DokumentVerbraucherArbeiter<Terminnotiz> dokumentVerbraucherArbeiter;
	private final Timer timer;

	private final ExecutionService executionService;

	public TerminAssistenz(
			final ExecutionService executionService,
			final Eingangskorb<Terminnotiz> eingehendeTerminnotizen,
			final Map<String, Eingangskorb<Terminnotiz>> eingangskoerbeDerSachbearbeiterNachNamen,
			final Timer timer) {
		this.executionService = executionService;
		this.timer = timer;
		this.dokumentVerbraucherArbeiter = new DokumentVerbraucherArbeiter<Terminnotiz>(
				new Terminbearbeitung(eingangskoerbeDerSachbearbeiterNachNamen),
				eingehendeTerminnotizen);
	}

	public void beendeArbeit() {
		this.dokumentVerbraucherArbeiter.stopWorking();
	}

	public void beginneArbeit() {
		this.executionService.executeAsynchronsly(
				ThreadTypesOfDecisionDepartment.TERMINASSISTENZ,
				this.dokumentVerbraucherArbeiter);
		while (!this.dokumentVerbraucherArbeiter.isCurrentlyRunning()) {
			Thread.yield();
		}
	}

	public boolean istAmArbeiten() {
		return this.dokumentVerbraucherArbeiter.isCurrentlyRunning();
	}
}
