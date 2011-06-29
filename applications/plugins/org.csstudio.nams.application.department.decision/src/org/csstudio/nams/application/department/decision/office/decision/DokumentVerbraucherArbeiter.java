
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

import org.csstudio.nams.common.decision.Ablagefaehig;
import org.csstudio.nams.common.decision.Eingangskorb;
import org.csstudio.nams.common.service.StepByStepProcessor;

/**
 * Ein Arbeits-Processor, der sequentiell Dokumente aus einem Eingangskorb einen
 * {@link DokumentenBearbeiter} zureicht.
 * 
 * @param <T>
 *            Der Typ der Dokumente, die dieser Bearbeiter sequentiell
 *            abarbeiten kann.
 * 
 * @author <a href="mailto:tr@c1-wps.de">Tobias Rathjen</a>, <a
 *         href="mailto:gs@c1-wps.de">Goesta Steen</a>, <a
 *         href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.2, 01.07.2008
 */
class DokumentVerbraucherArbeiter<T extends Ablagefaehig> extends
		StepByStepProcessor {
	private final Eingangskorb<T> eingangskorbNeuerAlarmVorgaenge;
	private final DokumentenBearbeiter<T> vorgangsmappenBearbeiter;

	public DokumentVerbraucherArbeiter(
			final DokumentenBearbeiter<T> vorgangsmappenBearbeiter,
			final Eingangskorb<T> eingangskorbNeuerAlarmVorgaenge) {
		this.vorgangsmappenBearbeiter = vorgangsmappenBearbeiter;
		this.eingangskorbNeuerAlarmVorgaenge = eingangskorbNeuerAlarmVorgaenge;
	}

	@Override
	protected void doRunOneSingleStep() throws Throwable, InterruptedException {
		this.vorgangsmappenBearbeiter
				.bearbeiteVorgang(this.eingangskorbNeuerAlarmVorgaenge
						.entnehmeAeltestenEingang());
	}
}