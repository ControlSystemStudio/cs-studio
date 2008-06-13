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

import java.util.Set;

import junit.framework.TestCase;

import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.Regelwerkskennung;
import org.csstudio.nams.common.material.regelwerk.AbstractNodeVersandRegel;
import org.csstudio.nams.common.material.regelwerk.AbstractVersandRegel;
import org.csstudio.nams.common.material.regelwerk.Pruefliste;
import org.csstudio.nams.common.material.regelwerk.RegelErgebnis;
import org.csstudio.nams.common.material.regelwerk.VersandRegel;
import org.junit.Test;


/**
 * TODO Add comment here.
 * 
 * 
 * @author <a href="mailto:tr@c1-wps.de">Tobias Rathjen</a>, <a
 *         href="mailto:gs@c1-wps.de">Goesta Steen</a>, <a
 *         href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.1, 09.04.2008
 */

public class AbstractNodeVersandRegel_Test extends TestCase {

	/**
	 * Test method for
	 * {@link org.csstudio.nams.common.material.regelwerk.AbstractNodeVersandRegel#addChild(org.csstudio.nams.common.material.regelwerk.VersandRegel)}.
	 */
	@Test
	public void testAddChild() {
		AbstractNodeVersandRegel parentRegel = new AbstractNodeVersandRegel() {
			@Override
			public RegelErgebnis auswerten(Pruefliste ergebnisListe) {
				return null;
			}
		};
		VersandRegel childRegel = new AbstractNodeVersandRegel() {
			@Override
			public RegelErgebnis auswerten(Pruefliste ergebnisListe) {
				return null;
			}
		};
		parentRegel.addChild(childRegel);
		assertTrue(parentRegel.children.size() == 1);
		assertTrue(parentRegel.children.contains(childRegel));
	}

	/**
	 * Test method for
	 * {@link org.csstudio.nams.common.material.regelwerk.AbstractNodeVersandRegel#gibKinderErgebnisse(org.csstudio.nams.common.material.regelwerk.Pruefliste)}.
	 */
	@Test
	public void testGibKinderErgebnisse() {
		AbstractNodeVersandRegel parentRegel = new AbstractNodeVersandRegel() {
			@Override
			public RegelErgebnis auswerten(Pruefliste ergebnisListe) {
				return null;
			}
		};
		VersandRegel childRegel = new AbstractNodeVersandRegel() {
			@Override
			public RegelErgebnis auswerten(Pruefliste ergebnisListe) {
				return null;
			}
		};
		AbstractNodeVersandRegel childRegel2 = new AbstractNodeVersandRegel() {
			@Override
			public RegelErgebnis auswerten(Pruefliste ergebnisListe) {
				return null;
			}
		};
		Pruefliste pruefliste = new Pruefliste(Regelwerkskennung.valueOf(),
				parentRegel);
		Set<RegelErgebnis> kinderErgebnisse = parentRegel
				.gibKinderErgebnisse(pruefliste);

		assertTrue(kinderErgebnisse.isEmpty());

		parentRegel.addChild(childRegel);
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(childRegel, RegelErgebnis.ZUTREFFEND);

		kinderErgebnisse = parentRegel.gibKinderErgebnisse(pruefliste);
		assertTrue(kinderErgebnisse.size() == 1);
		assertTrue(kinderErgebnisse.contains(RegelErgebnis.ZUTREFFEND));

		parentRegel.addChild(childRegel2);
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(childRegel2,
				RegelErgebnis.NICHT_ZUTREFFEND);

		kinderErgebnisse = parentRegel.gibKinderErgebnisse(pruefliste);
		assertTrue(kinderErgebnisse.size() == 2);
		assertTrue(kinderErgebnisse.contains(RegelErgebnis.ZUTREFFEND));
		assertTrue(kinderErgebnisse.contains(RegelErgebnis.NICHT_ZUTREFFEND));
	}

	/**
	 * Test method for
	 * {@link org.csstudio.nams.common.material.regelwerk.AbstractNodeVersandRegel#pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(de.c1wps.desy.ams.allgemeines.AlarmNachricht, org.csstudio.nams.common.material.regelwerk.Pruefliste)}.
	 */
	@Test
	public void testPruefeNachrichtAufBestaetigungsUndAufhebungsNachrichtErfolgreich() {
		AlarmNachricht alarmNachricht = new AlarmNachricht("nachricht");

		final boolean[] child1PruefeAufgerufen = { false };
		final boolean[] child2PruefeAufgerufen = { false };

		AbstractNodeVersandRegel parentRegel = new AbstractNodeVersandRegel() {
			@Override
			public RegelErgebnis auswerten(Pruefliste ergebnisListe) {
				return RegelErgebnis.ZUTREFFEND;
			}
		};
		VersandRegel childRegel = new AbstractVersandRegel() {

			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				child1PruefeAufgerufen[0] = true;
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeit) {
				fail();
				return null;
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				fail();
				return null;
			}
		};
		VersandRegel childRegel2 = new AbstractVersandRegel() {

			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				child2PruefeAufgerufen[0] = true;
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeit) {
				fail();
				return null;
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				fail();
				return null;
			}
		};
		Pruefliste pruefliste = new Pruefliste(Regelwerkskennung.valueOf(),
				parentRegel);

		parentRegel.addChild(childRegel);
		parentRegel.addChild(childRegel2);

		parentRegel.pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
				alarmNachricht, pruefliste);

		assertTrue(child1PruefeAufgerufen[0]);
		assertTrue(child2PruefeAufgerufen[0]);

		assertTrue(pruefliste.gibErgebnisFuerRegel(parentRegel) == RegelErgebnis.ZUTREFFEND);
	}

	/**
	 * Test method for
	 * {@link org.csstudio.nams.common.material.regelwerk.AbstractNodeVersandRegel#pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(de.c1wps.desy.ams.allgemeines.AlarmNachricht, org.csstudio.nams.common.material.regelwerk.Pruefliste)}.
	 */
	@Test
	public void testPruefeNachrichtAufBestaetigungsUndAufhebungsNachrichtSchonEntschieden() {
		AlarmNachricht alarmNachricht = new AlarmNachricht("nachricht");

		final boolean[] child1PruefeAufgerufen = { false };
		final boolean[] child2PruefeAufgerufen = { false };

		AbstractNodeVersandRegel parentRegel = new AbstractNodeVersandRegel() {
			@Override
			public RegelErgebnis auswerten(Pruefliste ergebnisListe) {
				return RegelErgebnis.ZUTREFFEND;
			}
		};
		VersandRegel childRegel = new AbstractVersandRegel() {

			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				child1PruefeAufgerufen[0] = true;
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeit) {
				fail();
				return null;
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				fail();
				return null;
			}
		};
		VersandRegel childRegel2 = new AbstractVersandRegel() {

			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				child2PruefeAufgerufen[0] = true;
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeit) {
				fail();
				return null;
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				fail();
				return null;
			}
		};
		Pruefliste pruefliste = new Pruefliste(Regelwerkskennung.valueOf(),
				parentRegel);

		parentRegel.addChild(childRegel);
		parentRegel.addChild(childRegel2);

		pruefliste
				.setzeErgebnisFuerRegelFallsVeraendert(parentRegel, RegelErgebnis.ZUTREFFEND);

		parentRegel.pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
				alarmNachricht, pruefliste);

		assertFalse(child1PruefeAufgerufen[0]);
		assertFalse(child2PruefeAufgerufen[0]);

		assertTrue(pruefliste.gibErgebnisFuerRegel(parentRegel) == RegelErgebnis.ZUTREFFEND);
	}

	/**
	 * Test method for
	 * {@link org.csstudio.nams.common.material.regelwerk.AbstractNodeVersandRegel#pruefeNachrichtAufTimeOuts(de.c1wps.desy.ams.allgemeines.AlarmNachricht, org.csstudio.nams.common.material.regelwerk.Pruefliste, org.csstudio.nams.common.fachwert.Millisekunden)}.
	 */
	@Test
	public void testPruefeNachrichtAufTimeOuts() {
		final boolean[] child1PruefeAufgerufen = { false };
		final boolean[] child2PruefeAufgerufen = { false };

		AbstractNodeVersandRegel parentRegel = new AbstractNodeVersandRegel() {
			@Override
			public RegelErgebnis auswerten(Pruefliste ergebnisListe) {
				return RegelErgebnis.ZUTREFFEND;
			}
		};
		VersandRegel childRegel = new AbstractVersandRegel() {

			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				fail();
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeit) {
				child1PruefeAufgerufen[0] = true;
				return Millisekunden.valueOf(200);
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				fail();
				return null;
			}
		};
		VersandRegel childRegel2 = new AbstractVersandRegel() {

			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				fail();
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeit) {
				child2PruefeAufgerufen[0] = true;
				return Millisekunden.valueOf(300);
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				fail();
				return null;
			}
		};
		Pruefliste pruefliste = new Pruefliste(Regelwerkskennung.valueOf(),
				parentRegel);

		parentRegel.addChild(childRegel);
		parentRegel.addChild(childRegel2);

		Millisekunden wartezeit = parentRegel
				.pruefeNachrichtAufTimeOuts(pruefliste, Millisekunden
						.valueOf(100));

		assertTrue(child1PruefeAufgerufen[0]);
		assertTrue(child2PruefeAufgerufen[0]);
		assertEquals(Millisekunden.valueOf(200), wartezeit);
		assertEquals(pruefliste.gibErgebnisFuerRegel(parentRegel),
				RegelErgebnis.ZUTREFFEND);

		child1PruefeAufgerufen[0] = false;
		child2PruefeAufgerufen[0] = false;

		wartezeit = parentRegel.pruefeNachrichtAufTimeOuts(
				pruefliste, Millisekunden.valueOf(100));
		
		assertFalse(child1PruefeAufgerufen[0]);
		assertFalse(child2PruefeAufgerufen[0]);
		assertNull( wartezeit);
		assertEquals(pruefliste.gibErgebnisFuerRegel(parentRegel),
				RegelErgebnis.ZUTREFFEND);
	}

	/**
	 * Test method for
	 * {@link org.csstudio.nams.common.material.regelwerk.AbstractNodeVersandRegel#pruefeNachrichtErstmalig(de.c1wps.desy.ams.allgemeines.AlarmNachricht, org.csstudio.nams.common.material.regelwerk.Pruefliste)}.
	 */
	@Test
	public void testPruefeNachrichtErstmalig() {
		final boolean[] child1PruefeAufgerufen = { false };
		final boolean[] child2PruefeAufgerufen = { false };

		AbstractNodeVersandRegel parentRegel = new AbstractNodeVersandRegel() {
			@Override
			public RegelErgebnis auswerten(Pruefliste ergebnisListe) {
				return RegelErgebnis.ZUTREFFEND;
			}
		};
		VersandRegel childRegel = new AbstractVersandRegel() {

			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				fail();
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeit) {
				fail();
				return null;
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				child1PruefeAufgerufen[0] = true;
				return  Millisekunden.valueOf(100);
			}
		};
		VersandRegel childRegel2 = new AbstractVersandRegel() {

			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				fail();
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeit) {
				fail();
				return Millisekunden.valueOf(0);
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				child2PruefeAufgerufen[0] = true;
				return Millisekunden.valueOf(200);
			}
		};
		Pruefliste pruefliste = new Pruefliste(Regelwerkskennung.valueOf(),
				parentRegel);

		parentRegel.addChild(childRegel);
		parentRegel.addChild(childRegel2);
		
		Millisekunden wartezeit = parentRegel.pruefeNachrichtErstmalig(new AlarmNachricht("nachricht"), pruefliste);
		
		assertTrue(child1PruefeAufgerufen[0]);
		assertTrue(child2PruefeAufgerufen[0]);
		assertEquals(Millisekunden.valueOf(100), wartezeit);
		assertEquals(pruefliste.gibErgebnisFuerRegel(parentRegel),
				RegelErgebnis.ZUTREFFEND);

		child1PruefeAufgerufen[0] = false;
		child2PruefeAufgerufen[0] = false;

		wartezeit = parentRegel.pruefeNachrichtErstmalig(new AlarmNachricht("nachricht"), pruefliste);
		
		assertFalse(child1PruefeAufgerufen[0]);
		assertFalse(child2PruefeAufgerufen[0]);
		assertNull( wartezeit);
		assertEquals(pruefliste.gibErgebnisFuerRegel(parentRegel),
				RegelErgebnis.ZUTREFFEND);
		
	}

}
