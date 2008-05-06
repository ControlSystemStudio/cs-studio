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

package de.c1wps.desy.ams.allgemeines.regelwerk;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.Test;

import de.c1wps.desy.ams.allgemeines.AlarmNachricht;
import de.c1wps.desy.ams.allgemeines.Millisekunden;

/**
 * TODO Add comment here.
 * 
 *
 * @author <a href="mailto:tr@c1-wps.de">Tobias Rathjen</a>,
 *         <a href="mailto:gs@c1-wps.de">Goesta Steen</a>,
 *         <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.1, 09.04.2008
 */

public class UndVersandRegel_Test extends TestCase {

	/**
	 * Test method for {@link de.c1wps.desy.ams.allgemeines.regelwerk.UndVersandRegel#auswerten(de.c1wps.desy.ams.allgemeines.regelwerk.Pruefliste)}.
	 */
	@Test
	public void testAuswerten() {
		
		VersandRegel versandRegel = new VersandRegel() {
			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
			}
			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeit) {
				return Millisekunden.valueOf(0);
			}
			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				return Millisekunden.valueOf(0);
			}
		};

		VersandRegel versandRegel2 = new VersandRegel() {
			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
			}
			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeit) {
				return Millisekunden.valueOf(0);
			}
			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				return Millisekunden.valueOf(0);
			}
		};
		
		UndVersandRegel undRegel = new UndVersandRegel(new VersandRegel[] {});
		undRegel.addChild(versandRegel);
		undRegel.addChild(versandRegel2);
		Pruefliste pruefliste = new Pruefliste(Regelwerkskennung.valueOf(), undRegel);
		
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(versandRegel, RegelErgebnis.NICHT_ZUTREFFEND);
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(versandRegel2, RegelErgebnis.NICHT_ZUTREFFEND);
		
		assertTrue(undRegel.auswerten(pruefliste) == RegelErgebnis.NICHT_ZUTREFFEND);
		
		
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(versandRegel, RegelErgebnis.NICHT_ZUTREFFEND);
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(versandRegel2, RegelErgebnis.VIELLEICHT_ZUTREFFEND);
		
		assertTrue(undRegel.auswerten(pruefliste) == RegelErgebnis.NICHT_ZUTREFFEND);
		
		
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(versandRegel, RegelErgebnis.NICHT_ZUTREFFEND);
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(versandRegel2, RegelErgebnis.ZUTREFFEND);
		
		assertTrue(undRegel.auswerten(pruefliste) == RegelErgebnis.NICHT_ZUTREFFEND);
		
		
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(versandRegel, RegelErgebnis.VIELLEICHT_ZUTREFFEND);
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(versandRegel2, RegelErgebnis.NICHT_ZUTREFFEND);
		
		assertTrue(undRegel.auswerten(pruefliste) == RegelErgebnis.NICHT_ZUTREFFEND);
		
		
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(versandRegel, RegelErgebnis.VIELLEICHT_ZUTREFFEND);
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(versandRegel2, RegelErgebnis.VIELLEICHT_ZUTREFFEND);
		
		assertTrue(undRegel.auswerten(pruefliste) == RegelErgebnis.VIELLEICHT_ZUTREFFEND);
		
		
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(versandRegel, RegelErgebnis.VIELLEICHT_ZUTREFFEND);
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(versandRegel2, RegelErgebnis.ZUTREFFEND);
		
		assertTrue(undRegel.auswerten(pruefliste) == RegelErgebnis.VIELLEICHT_ZUTREFFEND);

		
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(versandRegel, RegelErgebnis.ZUTREFFEND);
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(versandRegel2, RegelErgebnis.NICHT_ZUTREFFEND);
		
		assertTrue(undRegel.auswerten(pruefliste) == RegelErgebnis.NICHT_ZUTREFFEND);
		
		
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(versandRegel, RegelErgebnis.ZUTREFFEND);
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(versandRegel2, RegelErgebnis.VIELLEICHT_ZUTREFFEND);
		
		assertTrue(undRegel.auswerten(pruefliste) == RegelErgebnis.VIELLEICHT_ZUTREFFEND);

		
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(versandRegel, RegelErgebnis.ZUTREFFEND);
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(versandRegel2, RegelErgebnis.ZUTREFFEND);
		
		assertTrue(undRegel.auswerten(pruefliste) == RegelErgebnis.ZUTREFFEND);
		
		
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(versandRegel, RegelErgebnis.NOCH_NICHT_GEPRUEFT);
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(versandRegel2, RegelErgebnis.VIELLEICHT_ZUTREFFEND);
		
		assertTrue(undRegel.auswerten(pruefliste) == RegelErgebnis.NOCH_NICHT_GEPRUEFT);

	}

}
