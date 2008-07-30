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

import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.Regelwerkskennung;
import org.junit.Test;

/**
 * Test for AbstractNodeVersandRegel
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
		final AbstractNodeVersandRegel parentRegel = new AbstractNodeVersandRegel() {
			@Override
			public RegelErgebnis auswerten(Pruefliste ergebnisListe) {
				return null;
			}
		};
		final VersandRegel childRegel = new AbstractNodeVersandRegel() {
			@Override
			public RegelErgebnis auswerten(Pruefliste ergebnisListe) {
				return null;
			}
		};
		parentRegel.addChild(childRegel);
		Assert.assertTrue(parentRegel.children.size() == 1);
		Assert.assertTrue(parentRegel.children.contains(childRegel));
	}

	/**
	 * Test method for
	 * {@link org.csstudio.nams.common.material.regelwerk.AbstractNodeVersandRegel#gibKinderErgebnisse(org.csstudio.nams.common.material.regelwerk.Pruefliste)}.
	 */
	@Test
	public void testGibKinderErgebnisse() {
		final AbstractNodeVersandRegel parentRegel = new AbstractNodeVersandRegel() {
			@Override
			public RegelErgebnis auswerten(Pruefliste ergebnisListe) {
				return null;
			}
		};
		final VersandRegel childRegel = new AbstractNodeVersandRegel() {
			@Override
			public RegelErgebnis auswerten(Pruefliste ergebnisListe) {
				return null;
			}
		};
		final AbstractNodeVersandRegel childRegel2 = new AbstractNodeVersandRegel() {
			@Override
			public RegelErgebnis auswerten(Pruefliste ergebnisListe) {
				return null;
			}
		};
		final Pruefliste pruefliste = new Pruefliste(Regelwerkskennung
				.valueOf(), parentRegel);
		Set<RegelErgebnis> kinderErgebnisse = parentRegel
				.gibKinderErgebnisse(pruefliste);

		Assert.assertTrue(kinderErgebnisse.isEmpty());

		parentRegel.addChild(childRegel);
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(childRegel,
				RegelErgebnis.ZUTREFFEND);

		kinderErgebnisse = parentRegel.gibKinderErgebnisse(pruefliste);
		Assert.assertTrue(kinderErgebnisse.size() == 1);
		Assert.assertTrue(kinderErgebnisse.contains(RegelErgebnis.ZUTREFFEND));

		parentRegel.addChild(childRegel2);
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(childRegel2,
				RegelErgebnis.NICHT_ZUTREFFEND);

		kinderErgebnisse = parentRegel.gibKinderErgebnisse(pruefliste);
		Assert.assertTrue(kinderErgebnisse.size() == 2);
		Assert.assertTrue(kinderErgebnisse.contains(RegelErgebnis.ZUTREFFEND));
		Assert.assertTrue(kinderErgebnisse
				.contains(RegelErgebnis.NICHT_ZUTREFFEND));
	}

	/**
	 * Test method for
	 * {@link org.csstudio.nams.common.material.regelwerk.AbstractNodeVersandRegel#pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(de.c1wps.desy.ams.allgemeines.AlarmNachricht, org.csstudio.nams.common.material.regelwerk.Pruefliste)}.
	 */
	@Test
	public void testPruefeNachrichtAufBestaetigungsUndAufhebungsNachrichtErfolgreich() {
		final AlarmNachricht alarmNachricht = new AlarmNachricht("nachricht");

		final boolean[] child1PruefeAufgerufen = { false };
		final boolean[] child2PruefeAufgerufen = { false };

		final AbstractNodeVersandRegel parentRegel = new AbstractNodeVersandRegel() {
			@Override
			public RegelErgebnis auswerten(Pruefliste ergebnisListe) {
				return RegelErgebnis.ZUTREFFEND;
			}
		};
		final VersandRegel childRegel = new VersandRegel() {

			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				child1PruefeAufgerufen[0] = true;
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeit) {
				Assert.fail();
				return null;
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				Assert.fail();
				return null;
			}
		};
		final VersandRegel childRegel2 = new VersandRegel() {

			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				child2PruefeAufgerufen[0] = true;
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeit) {
				Assert.fail();
				return null;
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				Assert.fail();
				return null;
			}
		};
		final Pruefliste pruefliste = new Pruefliste(Regelwerkskennung
				.valueOf(), parentRegel);

		parentRegel.addChild(childRegel);
		parentRegel.addChild(childRegel2);

		parentRegel.pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
				alarmNachricht, pruefliste);

		Assert.assertTrue(child1PruefeAufgerufen[0]);
		Assert.assertTrue(child2PruefeAufgerufen[0]);

		Assert
				.assertTrue(pruefliste.gibErgebnisFuerRegel(parentRegel) == RegelErgebnis.ZUTREFFEND);
	}

	/**
	 * Test method for
	 * {@link org.csstudio.nams.common.material.regelwerk.AbstractNodeVersandRegel#pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(de.c1wps.desy.ams.allgemeines.AlarmNachricht, org.csstudio.nams.common.material.regelwerk.Pruefliste)}.
	 */
	@Test
	public void testPruefeNachrichtAufBestaetigungsUndAufhebungsNachrichtSchonEntschieden() {
		final AlarmNachricht alarmNachricht = new AlarmNachricht("nachricht");

		final boolean[] child1PruefeAufgerufen = { false };
		final boolean[] child2PruefeAufgerufen = { false };

		final AbstractNodeVersandRegel parentRegel = new AbstractNodeVersandRegel() {
			@Override
			public RegelErgebnis auswerten(Pruefliste ergebnisListe) {
				return RegelErgebnis.ZUTREFFEND;
			}
		};
		final VersandRegel childRegel = new VersandRegel() {

			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				child1PruefeAufgerufen[0] = true;
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeit) {
				Assert.fail();
				return null;
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				Assert.fail();
				return null;
			}
		};
		final VersandRegel childRegel2 = new VersandRegel() {

			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				child2PruefeAufgerufen[0] = true;
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeit) {
				Assert.fail();
				return null;
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				Assert.fail();
				return null;
			}
		};
		final Pruefliste pruefliste = new Pruefliste(Regelwerkskennung
				.valueOf(), parentRegel);

		parentRegel.addChild(childRegel);
		parentRegel.addChild(childRegel2);

		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(parentRegel,
				RegelErgebnis.ZUTREFFEND);

		parentRegel.pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
				alarmNachricht, pruefliste);

		Assert.assertFalse(child1PruefeAufgerufen[0]);
		Assert.assertFalse(child2PruefeAufgerufen[0]);

		Assert
				.assertTrue(pruefliste.gibErgebnisFuerRegel(parentRegel) == RegelErgebnis.ZUTREFFEND);
	}

	/**
	 * Test method for
	 * {@link org.csstudio.nams.common.material.regelwerk.AbstractNodeVersandRegel#pruefeNachrichtAufTimeOuts(de.c1wps.desy.ams.allgemeines.AlarmNachricht, org.csstudio.nams.common.material.regelwerk.Pruefliste, org.csstudio.nams.common.fachwert.Millisekunden)}.
	 */
	@Test
	public void testPruefeNachrichtAufTimeOuts() {
		final boolean[] child1PruefeAufgerufen = { false };
		final boolean[] child2PruefeAufgerufen = { false };

		final AbstractNodeVersandRegel parentRegel = new AbstractNodeVersandRegel() {
			@Override
			public RegelErgebnis auswerten(Pruefliste ergebnisListe) {
				return RegelErgebnis.ZUTREFFEND;
			}
		};
		final VersandRegel childRegel = new VersandRegel() {

			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				Assert.fail();
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeit) {
				child1PruefeAufgerufen[0] = true;
				return Millisekunden.valueOf(200);
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				Assert.fail();
				return null;
			}
		};
		final VersandRegel childRegel2 = new VersandRegel() {

			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				Assert.fail();
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeit) {
				child2PruefeAufgerufen[0] = true;
				return Millisekunden.valueOf(300);
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				Assert.fail();
				return null;
			}
		};
		final Pruefliste pruefliste = new Pruefliste(Regelwerkskennung
				.valueOf(), parentRegel);

		parentRegel.addChild(childRegel);
		parentRegel.addChild(childRegel2);

		Millisekunden wartezeit = parentRegel.pruefeNachrichtAufTimeOuts(
				pruefliste, Millisekunden.valueOf(100));

		Assert.assertTrue(child1PruefeAufgerufen[0]);
		Assert.assertTrue(child2PruefeAufgerufen[0]);
		Assert.assertEquals(Millisekunden.valueOf(200), wartezeit);
		Assert.assertEquals(pruefliste.gibErgebnisFuerRegel(parentRegel),
				RegelErgebnis.ZUTREFFEND);

		child1PruefeAufgerufen[0] = false;
		child2PruefeAufgerufen[0] = false;

		wartezeit = parentRegel.pruefeNachrichtAufTimeOuts(pruefliste,
				Millisekunden.valueOf(100));

		Assert.assertFalse(child1PruefeAufgerufen[0]);
		Assert.assertFalse(child2PruefeAufgerufen[0]);
		Assert.assertNull(wartezeit);
		Assert.assertEquals(pruefliste.gibErgebnisFuerRegel(parentRegel),
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

		final AbstractNodeVersandRegel parentRegel = new AbstractNodeVersandRegel() {
			@Override
			public RegelErgebnis auswerten(Pruefliste ergebnisListe) {
				return RegelErgebnis.ZUTREFFEND;
			}
		};
		final VersandRegel childRegel = new VersandRegel() {

			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				Assert.fail();
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeit) {
				Assert.fail();
				return null;
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				child1PruefeAufgerufen[0] = true;
				return Millisekunden.valueOf(100);
			}
		};
		final VersandRegel childRegel2 = new VersandRegel() {

			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				Assert.fail();
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeit) {
				Assert.fail();
				return Millisekunden.valueOf(0);
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				child2PruefeAufgerufen[0] = true;
				return Millisekunden.valueOf(200);
			}
		};
		final Pruefliste pruefliste = new Pruefliste(Regelwerkskennung
				.valueOf(), parentRegel);

		parentRegel.addChild(childRegel);
		parentRegel.addChild(childRegel2);

		Millisekunden wartezeit = parentRegel.pruefeNachrichtErstmalig(
				new AlarmNachricht("nachricht"), pruefliste);

		Assert.assertTrue(child1PruefeAufgerufen[0]);
		Assert.assertTrue(child2PruefeAufgerufen[0]);
		Assert.assertEquals(Millisekunden.valueOf(100), wartezeit);
		Assert.assertEquals(pruefliste.gibErgebnisFuerRegel(parentRegel),
				RegelErgebnis.ZUTREFFEND);

		child1PruefeAufgerufen[0] = false;
		child2PruefeAufgerufen[0] = false;

		wartezeit = parentRegel.pruefeNachrichtErstmalig(new AlarmNachricht(
				"nachricht"), pruefliste);

		Assert.assertFalse(child1PruefeAufgerufen[0]);
		Assert.assertFalse(child2PruefeAufgerufen[0]);
		Assert.assertNull(wartezeit);
		Assert.assertEquals(pruefliste.gibErgebnisFuerRegel(parentRegel),
				RegelErgebnis.ZUTREFFEND);

	}

}
