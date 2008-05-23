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

import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.regelwerk.Pruefliste;
import org.csstudio.nams.common.material.regelwerk.RegelErgebnis;
import org.csstudio.nams.common.material.regelwerk.Regelwerkskennung;
import org.csstudio.nams.common.material.regelwerk.TimeBasedAlarmBeiBestaetigungRegel;
import org.csstudio.nams.common.material.regelwerk.TimeBasedRegel;
import org.csstudio.nams.common.material.regelwerk.VersandRegel;
import org.csstudio.nams.common.testutils.AbstractObject_TestCase;
import org.junit.Test;

/**
 * TODO Add comment here.
 * 
 *
 * @author <a href="mailto:tr@c1-wps.de">Tobias Rathjen</a>,
 *         <a href="mailto:gs@c1-wps.de">Goesta Steen</a>,
 *         <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.1, 10.04.2008
 */

public class TimeBasedRegelAlarmBeiBestaetigung_Test extends AbstractObject_TestCase<TimeBasedAlarmBeiBestaetigungRegel> {
	
	private VersandRegel _ausloesungsRegel;
	private VersandRegel _bestaetigungsRegel;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		_ausloesungsRegel = new VersandRegel() {
			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				fail("Shouldn't be called");
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeitSeitErsterPruefung) {
				fail("Shouldn't be called");
				return null;
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(this, RegelErgebnis.ZUTREFFEND);
				return Millisekunden.valueOf(0);
			}
		};
		_bestaetigungsRegel = new VersandRegel() {
			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				fail("Shouldn't be called");
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeitSeitErsterPruefung) {
				fail("Shouldn't be called");
				return null;
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(this, RegelErgebnis.ZUTREFFEND);
				return Millisekunden.valueOf(0);
			}
		};
	}

	/**
	 * Test method for {@link org.csstudio.nams.common.material.regelwerk.TimeBasedRegel#pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(de.c1wps.desy.ams.allgemeines.AlarmNachricht, org.csstudio.nams.common.material.regelwerk.Pruefliste)}.
	 */
	@Test
	public void testPruefeNachrichtAufBestaetigungsUndAufhebungsNachrichtAufOK() {
		Pruefliste pruefliste = new Pruefliste(Regelwerkskennung.valueOf(), null);
		AlarmNachricht nachricht = new AlarmNachricht("Hallo ihr");
		
		TimeBasedAlarmBeiBestaetigungRegel timeBasedRegel = this.getNewInstanceOfClassUnderTest();
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(timeBasedRegel, RegelErgebnis.VIELLEICHT_ZUTREFFEND);
		timeBasedRegel.pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(nachricht, pruefliste);
		assertEquals(RegelErgebnis.ZUTREFFEND, pruefliste.gibErgebnisFuerRegel(timeBasedRegel));
	}
	
	/**
	 * Test method for {@link org.csstudio.nams.common.material.regelwerk.TimeBasedRegel#pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(de.c1wps.desy.ams.allgemeines.AlarmNachricht, org.csstudio.nams.common.material.regelwerk.Pruefliste)}.
	 */
	@Test
	public void testPruefeNachrichtAufBestaetigungsUndAufhebungsNachrichtAufNichtOK() {
		Pruefliste pruefliste = new Pruefliste(Regelwerkskennung.valueOf(), null);
		AlarmNachricht nachricht = new AlarmNachricht("Hallo ihr");
		
		VersandRegel bestaetigungsRegel = new VersandRegel() {

			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				fail("Shouldn't be called");
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeitSeitErsterPruefung) {
				fail("Shouldn't be called");
				return null;
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(this, RegelErgebnis.NICHT_ZUTREFFEND);
				return Millisekunden.valueOf(0);
			}
			
		};
		
		TimeBasedAlarmBeiBestaetigungRegel timeBasedRegel = new TimeBasedAlarmBeiBestaetigungRegel(_ausloesungsRegel, bestaetigungsRegel, Millisekunden.valueOf(100));
		
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(timeBasedRegel, RegelErgebnis.VIELLEICHT_ZUTREFFEND);
		timeBasedRegel.pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(nachricht, pruefliste);
		assertEquals(RegelErgebnis.VIELLEICHT_ZUTREFFEND, pruefliste.gibErgebnisFuerRegel(timeBasedRegel));
	}
	
	/**
	 * Test method for {@link org.csstudio.nams.common.material.regelwerk.TimeBasedRegel#pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(de.c1wps.desy.ams.allgemeines.AlarmNachricht, org.csstudio.nams.common.material.regelwerk.Pruefliste)}.
	 */
	@Test
	public void testPruefeNachrichtAufBestaetigungsUndAufhebungsNachrichtAufNichtFertig() {
		Pruefliste pruefliste = new Pruefliste(Regelwerkskennung.valueOf(), null);
		AlarmNachricht nachricht = new AlarmNachricht("Hallo ihr");
		
		VersandRegel regel = new VersandRegel() {

			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				fail("Shouldn't be called");
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeitSeitErsterPruefung) {
				fail("Shouldn't be called");
				return null;
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(this, RegelErgebnis.NICHT_ZUTREFFEND);
				return Millisekunden.valueOf(0);
			}
			
		};
		
		
		TimeBasedAlarmBeiBestaetigungRegel timeBasedRegel = new TimeBasedAlarmBeiBestaetigungRegel(_ausloesungsRegel, regel, Millisekunden.valueOf(100));
		
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(timeBasedRegel, RegelErgebnis.VIELLEICHT_ZUTREFFEND);
		timeBasedRegel.pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(nachricht, pruefliste);
		assertEquals(RegelErgebnis.VIELLEICHT_ZUTREFFEND, pruefliste.gibErgebnisFuerRegel(timeBasedRegel));
	}
	
	/**
	 * Test method for {@link org.csstudio.nams.common.material.regelwerk.TimeBasedRegel#pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(de.c1wps.desy.ams.allgemeines.AlarmNachricht, org.csstudio.nams.common.material.regelwerk.Pruefliste)}.
	 */
	@Test
	public void testPruefeNachrichtAufBestaetigungsUndAufhebungsNachrichtAufBereitsFertigZutreffend() {
		Pruefliste pruefliste = new Pruefliste(Regelwerkskennung.valueOf(), null);
		AlarmNachricht nachricht = new AlarmNachricht("Hallo ihr");
		
		VersandRegel regel = new VersandRegel() {

			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				fail("Shouldn't be called");
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeitSeitErsterPruefung) {
				fail("Shouldn't be called");
				return null;
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				fail("Shouldn't be called");
				return null;
			}
			
		};
		
		TimeBasedAlarmBeiBestaetigungRegel timeBasedRegel = new TimeBasedAlarmBeiBestaetigungRegel(_ausloesungsRegel, regel, Millisekunden.valueOf(100));
		
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(timeBasedRegel, RegelErgebnis.ZUTREFFEND);
		pruefliste.setzeAufNichtVeraendert();
		
		timeBasedRegel.pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(nachricht, pruefliste);
		assertEquals(RegelErgebnis.ZUTREFFEND, pruefliste.gibErgebnisFuerRegel(timeBasedRegel));
		assertFalse(pruefliste.hatSichGeaendert());
	}
	
	/**
	 * Test method for {@link org.csstudio.nams.common.material.regelwerk.TimeBasedRegel#pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(de.c1wps.desy.ams.allgemeines.AlarmNachricht, org.csstudio.nams.common.material.regelwerk.Pruefliste)}.
	 */
	@Test
	public void testPruefeNachrichtAufBestaetigungsUndAufhebungsNachrichtAufBereitsFertigNichtZutreffend() {
		Pruefliste pruefliste = new Pruefliste(Regelwerkskennung.valueOf(), null);
		AlarmNachricht nachricht = new AlarmNachricht("Hallo ihr");
		
		VersandRegel regel = new VersandRegel() {

			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				fail("Shouldn't be called");
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeitSeitErsterPruefung) {
				fail("Shouldn't be called");
				return null;
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				fail("Shouldn't be called");
				return null;
			}
			
		};
		
		TimeBasedAlarmBeiBestaetigungRegel timeBasedRegel = new TimeBasedAlarmBeiBestaetigungRegel(_ausloesungsRegel, regel, Millisekunden.valueOf(100));
		
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(timeBasedRegel, RegelErgebnis.NICHT_ZUTREFFEND);
		pruefliste.setzeAufNichtVeraendert();
		
		timeBasedRegel.pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(nachricht, pruefliste);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pruefliste.gibErgebnisFuerRegel(timeBasedRegel));
		assertFalse(pruefliste.hatSichGeaendert());
	}

	/**
	 * Test method for {@link org.csstudio.nams.common.material.regelwerk.TimeBasedRegel#pruefeNachrichtAufTimeOuts(org.csstudio.nams.common.material.regelwerk.Pruefliste, org.csstudio.nams.common.fachwert.Millisekunden)}.
	 */
	@Test
	public void testPruefeNachrichtAufTimeOutsBevorTimeOut() {
		Pruefliste pruefliste = new Pruefliste(Regelwerkskennung.valueOf(), null);
//		AlarmNachricht nachricht = new AlarmNachricht("Hallo ihr");
		
		TimeBasedAlarmBeiBestaetigungRegel tbRegel = this.getNewInstanceOfClassUnderTest();
		
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(tbRegel, RegelErgebnis.VIELLEICHT_ZUTREFFEND);
		pruefliste.setzeAufNichtVeraendert();
		
		tbRegel.pruefeNachrichtAufTimeOuts(pruefliste, Millisekunden.valueOf(10));
		
		assertFalse("pruefliste.hatSichGeaendert()", pruefliste.hatSichGeaendert());
		assertEquals(RegelErgebnis.VIELLEICHT_ZUTREFFEND, pruefliste.gibErgebnisFuerRegel(tbRegel));
	}
	
	/**
	 * Test method for {@link org.csstudio.nams.common.material.regelwerk.TimeBasedRegel#pruefeNachrichtAufTimeOuts(org.csstudio.nams.common.material.regelwerk.Pruefliste, org.csstudio.nams.common.fachwert.Millisekunden)}.
	 */
	@Test
	public void testPruefeNachrichtAufTimeOutsNachTimeOut() {
		Pruefliste pruefliste = new Pruefliste(Regelwerkskennung.valueOf(), null);
//		AlarmNachricht nachricht = new AlarmNachricht("Hallo ihr");
		
		TimeBasedAlarmBeiBestaetigungRegel tbRegel = this.getNewInstanceOfClassUnderTest();
		
		pruefliste.setzeErgebnisFuerRegelFallsVeraendert(tbRegel, RegelErgebnis.VIELLEICHT_ZUTREFFEND);
		pruefliste.setzeAufNichtVeraendert();
		
		tbRegel.pruefeNachrichtAufTimeOuts(pruefliste, Millisekunden.valueOf(200));
		
		assertTrue("pruefliste.hatSichGeaendert()", pruefliste.hatSichGeaendert());
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pruefliste.gibErgebnisFuerRegel(tbRegel));
	}

	/**
	 * Test method for {@link org.csstudio.nams.common.material.regelwerk.TimeBasedRegel#pruefeNachrichtErstmalig(de.c1wps.desy.ams.allgemeines.AlarmNachricht, org.csstudio.nams.common.material.regelwerk.Pruefliste)}.
	 */
	@Test
	public void testPruefeNachrichtErstmaligAusloesungZutreffend() {
		Pruefliste pruefliste = new Pruefliste(Regelwerkskennung.valueOf(), null);
		AlarmNachricht nachricht = new AlarmNachricht("Hallo ihr");
		
		TimeBasedAlarmBeiBestaetigungRegel tbRegel = this.getNewInstanceOfClassUnderTest();
				
		tbRegel.pruefeNachrichtErstmalig(nachricht, pruefliste);
		assertEquals(RegelErgebnis.VIELLEICHT_ZUTREFFEND, pruefliste.gibErgebnisFuerRegel(tbRegel));
	}
	
	/**
	 * Test method for {@link org.csstudio.nams.common.material.regelwerk.TimeBasedRegel#pruefeNachrichtErstmalig(de.c1wps.desy.ams.allgemeines.AlarmNachricht, org.csstudio.nams.common.material.regelwerk.Pruefliste)}.
	 */
	@Test
	public void testPruefeNachrichtErstmaligAusloesungNichtZutreffend() {
		Pruefliste pruefliste = new Pruefliste(Regelwerkskennung.valueOf(), null);
		AlarmNachricht nachricht = new AlarmNachricht("Hallo ihr");
		
		VersandRegel ausloesungsRegel = new VersandRegel() {

			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				fail("Shouldn't be called");
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeitSeitErsterPruefung) {
				fail("Shouldn't be called");
				return null;
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(this, RegelErgebnis.NICHT_ZUTREFFEND);
				return Millisekunden.valueOf(0);
			}
			
		};
		
		
		TimeBasedAlarmBeiBestaetigungRegel tbRegel = new TimeBasedAlarmBeiBestaetigungRegel(ausloesungsRegel, _bestaetigungsRegel, Millisekunden.valueOf(100));
				
		tbRegel.pruefeNachrichtErstmalig(nachricht, pruefliste);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pruefliste.gibErgebnisFuerRegel(tbRegel));
	}

//	/**
//	 * Test method for {@link de.c1wps.desy.ams.allgemeines.regelwerk.TimeBasedRegel#ermittleVerbleibendeWartezeit(de.c1wps.desy.ams.allgemeines.Millisekunden)}.
//	 */
//	@Test
//	public void testGibverbleibendeWartezeit() {
//		TimeBasedRegel tbRegel = this.getNewInstanceOfClassUnderTest();
//		
//		assertEquals(Millisekunden.valueOf(90), tbRegel.ermittleVerbleibendeWartezeit(Millisekunden.valueOf(10)));
//		assertEquals(Millisekunden.valueOf(80), tbRegel.ermittleVerbleibendeWartezeit(Millisekunden.valueOf(20)));
//		
//		assertEquals(Millisekunden.valueOf(50), tbRegel.ermittleVerbleibendeWartezeit(Millisekunden.valueOf(50)));
//	}

	@Override
	protected TimeBasedAlarmBeiBestaetigungRegel getNewInstanceOfClassUnderTest() {
		return new TimeBasedAlarmBeiBestaetigungRegel(_ausloesungsRegel, _bestaetigungsRegel, Millisekunden.valueOf(100));
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return "Hallo";
	}

	@Override
	protected TimeBasedAlarmBeiBestaetigungRegel[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		TimeBasedAlarmBeiBestaetigungRegel[] result = new TimeBasedAlarmBeiBestaetigungRegel[3];
		result[0] = new TimeBasedAlarmBeiBestaetigungRegel(_ausloesungsRegel, _bestaetigungsRegel, Millisekunden.valueOf(100));
		result[1] = new TimeBasedAlarmBeiBestaetigungRegel(_ausloesungsRegel, _bestaetigungsRegel, Millisekunden.valueOf(300));
		result[2] = new TimeBasedAlarmBeiBestaetigungRegel(_ausloesungsRegel, _bestaetigungsRegel, Millisekunden.valueOf(500));
		return result;
	}

}
