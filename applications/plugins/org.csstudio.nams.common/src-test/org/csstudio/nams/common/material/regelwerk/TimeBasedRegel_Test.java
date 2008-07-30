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

import junit.framework.Assert;

import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.Regelwerkskennung;
import org.csstudio.nams.common.testutils.AbstractObject_TestCase;
import org.junit.Test;

/**
 * Test for TimeBasedRegel
 * 
 * 
 * @author <a href="mailto:tr@c1-wps.de">Tobias Rathjen</a>, <a
 *         href="mailto:gs@c1-wps.de">Goesta Steen</a>, <a
 *         href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.1, 10.04.2008
 */

public class TimeBasedRegel_Test extends
		AbstractObject_TestCase<TimeBasedRegel> {

	private VersandRegel _ausloesungsRegel;
	private VersandRegel _bestaetigungsRegel;
	private VersandRegel _aufhebungsRegel;

	/**
	 * Test method for
	 * {@link org.csstudio.nams.common.material.regelwerk.TimeBasedRegel#pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(de.c1wps.desy.ams.allgemeines.AlarmNachricht, org.csstudio.nams.common.material.regelwerk.Pruefliste)}.
	 */
	@Test
	public void testPruefeNachrichtAufBestaetigungsUndAufhebungsNachrichtAufBereitsFertigNichtZutreffend() {
		final Pruefliste pruefliste = new Pruefliste(Regelwerkskennung
				.valueOf(), null);
		final AlarmNachricht nachricht = new AlarmNachricht("Hallo ihr");

		final VersandRegel regel = new VersandRegel() {

			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				Assert.fail("Shouldn't be called");
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeitSeitErsterPruefung) {
				Assert.fail("Shouldn't be called");
				return null;
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				Assert.fail("Shouldn't be called");
				return null;
			}

		};

		final TimeBasedRegel timeBasedRegel = new TimeBasedRegel(
				this._ausloesungsRegel, regel, regel, Millisekunden
						.valueOf(100));

		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(timeBasedRegel,
				RegelErgebnis.NICHT_ZUTREFFEND);
		pruefliste.setzeAufNichtVeraendert();

		timeBasedRegel.pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
				nachricht, pruefliste);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pruefliste
				.gibErgebnisFuerRegel(timeBasedRegel));
		Assert.assertFalse(pruefliste.hatSichGeaendert());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.nams.common.material.regelwerk.TimeBasedRegel#pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(de.c1wps.desy.ams.allgemeines.AlarmNachricht, org.csstudio.nams.common.material.regelwerk.Pruefliste)}.
	 */
	@Test
	public void testPruefeNachrichtAufBestaetigungsUndAufhebungsNachrichtAufBereitsFertigZutreffend() {
		final Pruefliste pruefliste = new Pruefliste(Regelwerkskennung
				.valueOf(), null);
		final AlarmNachricht nachricht = new AlarmNachricht("Hallo ihr");

		final VersandRegel regel = new VersandRegel() {

			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				Assert.fail("Shouldn't be called");
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeitSeitErsterPruefung) {
				Assert.fail("Shouldn't be called");
				return null;
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				Assert.fail("Shouldn't be called");
				return null;
			}

		};

		final TimeBasedRegel timeBasedRegel = new TimeBasedRegel(
				this._ausloesungsRegel, regel, regel, Millisekunden
						.valueOf(100));

		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(timeBasedRegel,
				RegelErgebnis.ZUTREFFEND);
		pruefliste.setzeAufNichtVeraendert();

		timeBasedRegel.pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
				nachricht, pruefliste);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pruefliste
				.gibErgebnisFuerRegel(timeBasedRegel));
		Assert.assertFalse(pruefliste.hatSichGeaendert());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.nams.common.material.regelwerk.TimeBasedRegel#pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(de.c1wps.desy.ams.allgemeines.AlarmNachricht, org.csstudio.nams.common.material.regelwerk.Pruefliste)}.
	 */
	@Test
	public void testPruefeNachrichtAufBestaetigungsUndAufhebungsNachrichtAufNichtFertig() {
		final Pruefliste pruefliste = new Pruefliste(Regelwerkskennung
				.valueOf(), null);
		final AlarmNachricht nachricht = new AlarmNachricht("Hallo ihr");

		final VersandRegel regel = new VersandRegel() {

			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				Assert.fail("Shouldn't be called");
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeitSeitErsterPruefung) {
				Assert.fail("Shouldn't be called");
				return null;
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(this,
						RegelErgebnis.NICHT_ZUTREFFEND);
				return Millisekunden.valueOf(0);
			}

		};

		final TimeBasedRegel timeBasedRegel = new TimeBasedRegel(
				this._ausloesungsRegel, regel, regel, Millisekunden
						.valueOf(100));

		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(timeBasedRegel,
				RegelErgebnis.VIELLEICHT_ZUTREFFEND);
		timeBasedRegel.pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
				nachricht, pruefliste);
		Assert.assertEquals(RegelErgebnis.VIELLEICHT_ZUTREFFEND, pruefliste
				.gibErgebnisFuerRegel(timeBasedRegel));
	}

	/**
	 * Test method for
	 * {@link org.csstudio.nams.common.material.regelwerk.TimeBasedRegel#pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(de.c1wps.desy.ams.allgemeines.AlarmNachricht, org.csstudio.nams.common.material.regelwerk.Pruefliste)}.
	 */
	@Test
	public void testPruefeNachrichtAufBestaetigungsUndAufhebungsNachrichtAufNichtOK() {
		final Pruefliste pruefliste = new Pruefliste(Regelwerkskennung
				.valueOf(), null);
		final AlarmNachricht nachricht = new AlarmNachricht("Hallo ihr");

		final VersandRegel bestaetigungsRegel = new VersandRegel() {

			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				Assert.fail("Shouldn't be called");
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeitSeitErsterPruefung) {
				Assert.fail("Shouldn't be called");
				return null;
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(this,
						RegelErgebnis.NICHT_ZUTREFFEND);
				return Millisekunden.valueOf(0);
			}

		};

		final TimeBasedRegel timeBasedRegel = new TimeBasedRegel(
				this._ausloesungsRegel, this._aufhebungsRegel,
				bestaetigungsRegel, Millisekunden.valueOf(100));

		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(timeBasedRegel,
				RegelErgebnis.VIELLEICHT_ZUTREFFEND);
		timeBasedRegel.pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
				nachricht, pruefliste);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pruefliste
				.gibErgebnisFuerRegel(timeBasedRegel));
	}

	/**
	 * Test method for
	 * {@link org.csstudio.nams.common.material.regelwerk.TimeBasedRegel#pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(de.c1wps.desy.ams.allgemeines.AlarmNachricht, org.csstudio.nams.common.material.regelwerk.Pruefliste)}.
	 */
	@Test
	public void testPruefeNachrichtAufBestaetigungsUndAufhebungsNachrichtAufOK() {
		final Pruefliste pruefliste = new Pruefliste(Regelwerkskennung
				.valueOf(), null);
		final AlarmNachricht nachricht = new AlarmNachricht("Hallo ihr");

		final TimeBasedRegel timeBasedRegel = this
				.getNewInstanceOfClassUnderTest();
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(timeBasedRegel,
				RegelErgebnis.VIELLEICHT_ZUTREFFEND);
		timeBasedRegel.pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
				nachricht, pruefliste);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pruefliste
				.gibErgebnisFuerRegel(timeBasedRegel));
	}

	/**
	 * Test method for
	 * {@link org.csstudio.nams.common.material.regelwerk.TimeBasedRegel#pruefeNachrichtAufTimeOuts(org.csstudio.nams.common.material.regelwerk.Pruefliste, org.csstudio.nams.common.fachwert.Millisekunden)}.
	 */
	@Test
	public void testPruefeNachrichtAufTimeOutsBevorTimeOut() {
		final Pruefliste pruefliste = new Pruefliste(Regelwerkskennung
				.valueOf(), null);
		// AlarmNachricht nachricht = new AlarmNachricht("Hallo ihr");

		final TimeBasedRegel tbRegel = this.getNewInstanceOfClassUnderTest();

		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(tbRegel,
				RegelErgebnis.VIELLEICHT_ZUTREFFEND);
		pruefliste.setzeAufNichtVeraendert();

		tbRegel.pruefeNachrichtAufTimeOuts(pruefliste, Millisekunden
				.valueOf(10));

		Assert.assertFalse("pruefliste.hatSichGeaendert()", pruefliste
				.hatSichGeaendert());
		Assert.assertEquals(RegelErgebnis.VIELLEICHT_ZUTREFFEND, pruefliste
				.gibErgebnisFuerRegel(tbRegel));
	}

	/**
	 * Test method for
	 * {@link org.csstudio.nams.common.material.regelwerk.TimeBasedRegel#pruefeNachrichtAufTimeOuts(org.csstudio.nams.common.material.regelwerk.Pruefliste, org.csstudio.nams.common.fachwert.Millisekunden)}.
	 */
	@Test
	public void testPruefeNachrichtAufTimeOutsNachTimeOut() {
		final Pruefliste pruefliste = new Pruefliste(Regelwerkskennung
				.valueOf(), null);
		// AlarmNachricht nachricht = new AlarmNachricht("Hallo ihr");

		final TimeBasedRegel tbRegel = this.getNewInstanceOfClassUnderTest();

		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(tbRegel,
				RegelErgebnis.VIELLEICHT_ZUTREFFEND);
		pruefliste.setzeAufNichtVeraendert();

		tbRegel.pruefeNachrichtAufTimeOuts(pruefliste, Millisekunden
				.valueOf(200));

		Assert.assertTrue("pruefliste.hatSichGeaendert()", pruefliste
				.hatSichGeaendert());
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pruefliste
				.gibErgebnisFuerRegel(tbRegel));
	}

	/**
	 * Test method for
	 * {@link org.csstudio.nams.common.material.regelwerk.TimeBasedRegel#pruefeNachrichtErstmalig(de.c1wps.desy.ams.allgemeines.AlarmNachricht, org.csstudio.nams.common.material.regelwerk.Pruefliste)}.
	 */
	@Test
	public void testPruefeNachrichtErstmaligAusloesungNichtZutreffend() {
		final Pruefliste pruefliste = new Pruefliste(Regelwerkskennung
				.valueOf(), null);
		final AlarmNachricht nachricht = new AlarmNachricht("Hallo ihr");

		final VersandRegel ausloesungsRegel = new VersandRegel() {

			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				Assert.fail("Shouldn't be called");
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeitSeitErsterPruefung) {
				Assert.fail("Shouldn't be called");
				return null;
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(this,
						RegelErgebnis.NICHT_ZUTREFFEND);
				return Millisekunden.valueOf(0);
			}

		};

		final TimeBasedRegel tbRegel = new TimeBasedRegel(ausloesungsRegel,
				this._aufhebungsRegel, this._bestaetigungsRegel, Millisekunden
						.valueOf(100));

		tbRegel.pruefeNachrichtErstmalig(nachricht, pruefliste);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pruefliste
				.gibErgebnisFuerRegel(tbRegel));
	}

	/**
	 * Test method for
	 * {@link org.csstudio.nams.common.material.regelwerk.TimeBasedRegel#pruefeNachrichtErstmalig(de.c1wps.desy.ams.allgemeines.AlarmNachricht, org.csstudio.nams.common.material.regelwerk.Pruefliste)}.
	 */
	@Test
	public void testPruefeNachrichtErstmaligAusloesungZutreffend() {
		final Pruefliste pruefliste = new Pruefliste(Regelwerkskennung
				.valueOf(), null);
		final AlarmNachricht nachricht = new AlarmNachricht("Hallo ihr");

		final TimeBasedRegel tbRegel = this.getNewInstanceOfClassUnderTest();

		tbRegel.pruefeNachrichtErstmalig(nachricht, pruefliste);
		Assert.assertEquals(RegelErgebnis.VIELLEICHT_ZUTREFFEND, pruefliste
				.gibErgebnisFuerRegel(tbRegel));
	}

	@Override
	protected TimeBasedRegel getNewInstanceOfClassUnderTest() {
		return new TimeBasedRegel(this._ausloesungsRegel,
				this._aufhebungsRegel, this._bestaetigungsRegel, Millisekunden
						.valueOf(100));
	}

	// /**
	// * Test method for {@link
	// de.c1wps.desy.ams.allgemeines.regelwerk.TimeBasedRegel#ermittleVerbleibendeWartezeit(de.c1wps.desy.ams.allgemeines.Millisekunden)}.
	// */
	// @Test
	// public void testGibverbleibendeWartezeit() {
	// TimeBasedRegel tbRegel = this.getNewInstanceOfClassUnderTest();
	//		
	// assertEquals(Millisekunden.valueOf(90),
	// tbRegel.ermittleVerbleibendeWartezeit(Millisekunden.valueOf(10)));
	// assertEquals(Millisekunden.valueOf(80),
	// tbRegel.ermittleVerbleibendeWartezeit(Millisekunden.valueOf(20)));
	//		
	// assertEquals(Millisekunden.valueOf(50),
	// tbRegel.ermittleVerbleibendeWartezeit(Millisekunden.valueOf(50)));
	// }

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return "Hallo";
	}

	@Override
	protected TimeBasedRegel[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		final TimeBasedRegel[] result = new TimeBasedRegel[3];
		result[0] = new TimeBasedRegel(this._ausloesungsRegel,
				this._aufhebungsRegel, this._bestaetigungsRegel, Millisekunden
						.valueOf(100));
		result[1] = new TimeBasedRegel(this._ausloesungsRegel,
				this._aufhebungsRegel, this._bestaetigungsRegel, Millisekunden
						.valueOf(300));
		result[2] = new TimeBasedRegel(this._ausloesungsRegel,
				this._aufhebungsRegel, this._bestaetigungsRegel, Millisekunden
						.valueOf(500));
		return result;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this._ausloesungsRegel = new VersandRegel() {
			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					final AlarmNachricht nachricht,
					final Pruefliste bisherigesErgebnis) {
				Assert.fail("Shouldn't be called");
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					final Pruefliste bisherigesErgebnis,
					final Millisekunden verstricheneZeitSeitErsterPruefung) {
				Assert.fail("Shouldn't be called");
				return null;
			}

			public Millisekunden pruefeNachrichtErstmalig(
					final AlarmNachricht nachricht,
					final Pruefliste ergebnisListe) {
				ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(this,
						RegelErgebnis.ZUTREFFEND);
				return Millisekunden.valueOf(0);
			}
		};
		this._bestaetigungsRegel = new VersandRegel() {
			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					final AlarmNachricht nachricht,
					final Pruefliste bisherigesErgebnis) {
				Assert.fail("Shouldn't be called");
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					final Pruefliste bisherigesErgebnis,
					final Millisekunden verstricheneZeitSeitErsterPruefung) {
				Assert.fail("Shouldn't be called");
				return null;
			}

			public Millisekunden pruefeNachrichtErstmalig(
					final AlarmNachricht nachricht,
					final Pruefliste ergebnisListe) {
				ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(this,
						RegelErgebnis.ZUTREFFEND);
				return Millisekunden.valueOf(0);
			}
		};
		this._aufhebungsRegel = new VersandRegel() {
			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					final AlarmNachricht nachricht,
					final Pruefliste bisherigesErgebnis) {
				Assert.fail("Shouldn't be called");
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					final Pruefliste bisherigesErgebnis,
					final Millisekunden verstricheneZeitSeitErsterPruefung) {
				Assert.fail("Shouldn't be called");
				return null;
			}

			public Millisekunden pruefeNachrichtErstmalig(
					final AlarmNachricht nachricht,
					final Pruefliste ergebnisListe) {
				ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(this,
						RegelErgebnis.ZUTREFFEND);
				return Millisekunden.valueOf(0);
			}
		};
	}

}
