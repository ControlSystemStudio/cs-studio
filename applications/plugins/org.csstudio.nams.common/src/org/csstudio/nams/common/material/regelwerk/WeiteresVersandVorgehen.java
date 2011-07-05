
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

package org.csstudio.nams.common.material.regelwerk;

import org.csstudio.nams.common.decision.Vorgangsmappe;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.wam.Fachwert;

/**
 * Eine Aufzaehlung von möglichen weiteren Vorgehen bezüglich des Versandes der
 * {@link AlarmNachricht} in einer {@link Vorgangsmappe}.
 * 
 * 
 * @author <a href="mailto:tr@c1-wps.de">Tobias Rathjen</a>, <a
 *         href="mailto:gs@c1-wps.de">Goesta Steen</a>, <a
 *         href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.1, 28.03.2008
 */
@Fachwert
public enum WeiteresVersandVorgehen {
	/**
	 * Gibt an, dass die Alarmnacht gesendet werden soll.
	 */
	VERSENDEN,

	/**
	 * Gibt an, dass die Alarmnacht NICHT gesendet werden soll.
	 */
	NICHT_VERSENDEN,

	/**
	 * Gibt an, dass die Alarmnacht erneut geprüft werden muss um eine
	 * Entscheidung bzgl. des Versandes treffen zu können.
	 */
	ERNEUT_PRUEFEN,

	/**
	 * Gibt an, dass die Alarmnacht noch NICHT geprüft wurde.
	 */
	NOCH_NICHT_GEPRUEFT;

	public static WeiteresVersandVorgehen fromRegelErgebnis(
			final RegelErgebnis regelErgebnis) {
		switch (regelErgebnis) {
		case NICHT_ZUTREFFEND:
			return WeiteresVersandVorgehen.NICHT_VERSENDEN;
		case NOCH_NICHT_GEPRUEFT:
			return WeiteresVersandVorgehen.NOCH_NICHT_GEPRUEFT;
		case VIELLEICHT_ZUTREFFEND:
			return WeiteresVersandVorgehen.ERNEUT_PRUEFEN;
		default:// case ZUTREFFEND: sollte ein fünfter fall existieren trozdem
				// versenden(stichwort BUGfix)
			return WeiteresVersandVorgehen.VERSENDEN;
		}
	}
}
