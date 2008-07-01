package de.c1wps.desy.ams.alarmentscheidungsbuero;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;

import junit.framework.TestCase;

import org.csstudio.nams.common.decision.Eingangskorb;
import org.csstudio.nams.common.decision.StandardAblagekorb;
import org.csstudio.nams.common.decision.Vorgangsmappe;
import org.csstudio.nams.common.decision.Vorgangsmappenkennung;
import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.Regelwerkskennung;
import org.csstudio.nams.common.material.regelwerk.OderVersandRegel;
import org.csstudio.nams.common.material.regelwerk.Pruefliste;
import org.csstudio.nams.common.material.regelwerk.RegelErgebnis;
import org.csstudio.nams.common.material.regelwerk.Regelwerk;
import org.csstudio.nams.common.material.regelwerk.StandardRegelwerk;
import org.csstudio.nams.common.material.regelwerk.TimeBasedRegel;
import org.csstudio.nams.common.material.regelwerk.VersandRegel;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen;
import org.csstudio.nams.service.history.declaration.HistoryService;
import org.junit.Test;


public class AlarmEntscheidungsBuero_Test extends TestCase {

	static class SehrSimpleTextRegel implements VersandRegel {

		private final String muster;

		public SehrSimpleTextRegel(String muster) {
			this.muster = muster;
		}

		public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
				AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
			if (!bisherigesErgebnis.gibErgebnisFuerRegel(this).istEntschieden()) {
				pruefeNachrichtErstmalig(nachricht, bisherigesErgebnis);
			}
		}

		public Millisekunden pruefeNachrichtAufTimeOuts(
				Pruefliste bisherigesErgebnis,
				Millisekunden verstricheneZeitSeitErsterPruefung) {
			return null;
		}

		public Millisekunden pruefeNachrichtErstmalig(AlarmNachricht nachricht,
				Pruefliste bisherigesErgebnis) {
			if (muster.equals(nachricht.gibNachrichtenText())) {
				bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(this,
						RegelErgebnis.ZUTREFFEND);
			} else {
				bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(this,
						RegelErgebnis.NICHT_ZUTREFFEND);
			}
			return null;
		}

		public void setHistoryService(HistoryService historyService) {
			// TODO Auto-generated method stub
			
		}

	}

	@Test(timeout = 4000)
	public void testMitSimplenRegeln() throws Throwable {
		VersandRegel regel2 = new SehrSimpleTextRegel("Hallo");
		VersandRegel regel3 = new SehrSimpleTextRegel("Tach");
		VersandRegel regel = new OderVersandRegel(new VersandRegel[] { regel2,
				regel3 });

		Regelwerkskennung regelwerkskennung = Regelwerkskennung.valueOf();
		Regelwerk regelwerk = new StandardRegelwerk(regelwerkskennung, regel);

		AlarmEntscheidungsBuero buero = new AlarmEntscheidungsBuero(
				new Regelwerk[] { regelwerk }, new StandardAblagekorb<Vorgangsmappe>(), new StandardAblagekorb<Vorgangsmappe>());
		final Eingangskorb<Vorgangsmappe> alarmVorgangEingangskorb = buero
				.gibAlarmVorgangEingangskorb();
		final StandardAblagekorb<Vorgangsmappe> alarmVorgangAusgangskorb = (StandardAblagekorb<Vorgangsmappe>) buero
				.gibAlarmVorgangAusgangskorb();

		// Passende 1
		Vorgangsmappenkennung vorgangsmappenkennung = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		AlarmNachricht alarmNachricht = new AlarmNachricht("Hallo");
		Vorgangsmappe vorgangsmappe = new Vorgangsmappe(vorgangsmappenkennung,
				alarmNachricht);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe);

		// Passende 2
		Vorgangsmappenkennung vorgangsmappenkennung2 = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		AlarmNachricht alarmNachricht2 = new AlarmNachricht("Tach");
		Vorgangsmappe vorgangsmappe2 = new Vorgangsmappe(
				vorgangsmappenkennung2, alarmNachricht2);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe2);

		// Un-Passende 1
		Vorgangsmappenkennung vorgangsmappenkennung3 = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		AlarmNachricht alarmNachricht3 = new AlarmNachricht("Moin!");
		Vorgangsmappe vorgangsmappe3 = new Vorgangsmappe(
				vorgangsmappenkennung3, alarmNachricht3);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe3);

		// Pruefen 1
		Vorgangsmappe aelteste = alarmVorgangAusgangskorb
				.entnehmeAeltestenEingang();

		assertNotNull(aelteste);
		assertEquals("Hallo", aelteste
				.gibAusloesendeAlarmNachrichtDiesesVorganges()
				.gibNachrichtenText());
		assertEquals(WeiteresVersandVorgehen.VERSENDEN, aelteste
				.gibPruefliste().gesamtErgebnis());

		// Pruefen 2
		aelteste = alarmVorgangAusgangskorb.entnehmeAeltestenEingang();

		assertNotNull(aelteste);
		assertEquals("Tach", aelteste
				.gibAusloesendeAlarmNachrichtDiesesVorganges()
				.gibNachrichtenText());
		assertEquals(WeiteresVersandVorgehen.VERSENDEN, aelteste
				.gibPruefliste().gesamtErgebnis());

		// Pruefen 3
		aelteste = alarmVorgangAusgangskorb.entnehmeAeltestenEingang();

		assertNotNull(aelteste);
		assertEquals("Moin!", aelteste
				.gibAusloesendeAlarmNachrichtDiesesVorganges()
				.gibNachrichtenText());
		assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste
				.gibPruefliste().gesamtErgebnis());
	}

	@Test(timeout = 4000)
	public void testMitEinerTimeBasedRegeln() throws Throwable {
		// TODO Eine time based konstruieren, klären wie der time based vertrag
		// ist (was darf null sein) beide (den der time based und diesen)
		// anpassen!
		VersandRegel aufhebung = new SehrSimpleTextRegel("Aufhebung");
		VersandRegel bestaetigung = new SehrSimpleTextRegel("Bestaetigung");
		VersandRegel ausloeser = new SehrSimpleTextRegel("Ausloeser");

		VersandRegel timebasedRegel = new TimeBasedRegel(ausloeser, aufhebung,
				bestaetigung, Millisekunden.valueOf(1000));

		Regelwerkskennung regelwerkskennung = Regelwerkskennung.valueOf();
		Regelwerk regelwerk = new StandardRegelwerk(regelwerkskennung,
				timebasedRegel);

		AlarmEntscheidungsBuero buero = new AlarmEntscheidungsBuero(
				new Regelwerk[] { regelwerk }, new StandardAblagekorb<Vorgangsmappe>(), new StandardAblagekorb<Vorgangsmappe>());
		final Eingangskorb<Vorgangsmappe> alarmVorgangEingangskorb = buero
				.gibAlarmVorgangEingangskorb();
		final StandardAblagekorb<Vorgangsmappe> alarmVorgangAusgangskorb = (StandardAblagekorb<Vorgangsmappe>) buero
				.gibAlarmVorgangAusgangskorb();

		// Un-Passende 1
		Vorgangsmappenkennung vorgangsmappenkennung = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		AlarmNachricht alarmNachricht = new AlarmNachricht("XXO");
		Vorgangsmappe vorgangsmappe = new Vorgangsmappe(vorgangsmappenkennung,
				alarmNachricht);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe);

		// Passende 1
		Vorgangsmappenkennung vorgangsmappenkennung2 = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		AlarmNachricht alarmNachricht2 = new AlarmNachricht("Ausloeser");
		Vorgangsmappe vorgangsmappe2 = new Vorgangsmappe(
				vorgangsmappenkennung2, alarmNachricht2);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe2);

		// Un-Passende 1
		Vorgangsmappenkennung vorgangsmappenkennung3 = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		AlarmNachricht alarmNachricht3 = new AlarmNachricht("Baeh!");
		Vorgangsmappe vorgangsmappe3 = new Vorgangsmappe(
				vorgangsmappenkennung3, alarmNachricht3);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe3);

		// Passende Bestaetigung1
		Vorgangsmappenkennung vorgangsmappenkennung4 = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		AlarmNachricht alarmNachricht4 = new AlarmNachricht("Bestaetigung");
		Vorgangsmappe vorgangsmappe4 = new Vorgangsmappe(
				vorgangsmappenkennung4, alarmNachricht4);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe4);

		// Pruefen 1
		Vorgangsmappe aelteste = alarmVorgangAusgangskorb
				.entnehmeAeltestenEingang();

		assertNotNull(aelteste);
		assertEquals("XXO", aelteste
				.gibAusloesendeAlarmNachrichtDiesesVorganges()
				.gibNachrichtenText());
		assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste
				.gibPruefliste().gesamtErgebnis());

		// Pruefen 2
		aelteste = alarmVorgangAusgangskorb.entnehmeAeltestenEingang();

		assertNotNull(aelteste);
		assertEquals("Baeh!", aelteste
				.gibAusloesendeAlarmNachrichtDiesesVorganges()
				.gibNachrichtenText());
		assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste
				.gibPruefliste().gesamtErgebnis());


		// Pruefen 4
		Vorgangsmappe vorgangsmappe5 = alarmVorgangAusgangskorb.entnehmeAeltestenEingang();

		assertNotNull(vorgangsmappe5);
		assertEquals("Ausloeser", vorgangsmappe5
				.gibAusloesendeAlarmNachrichtDiesesVorganges()
				.gibNachrichtenText());
		assertEquals(WeiteresVersandVorgehen.VERSENDEN, vorgangsmappe5
				.gibPruefliste().gesamtErgebnis());
		assertEquals("Die Zeit wird nur hochgesetzt nach einem timeout!",
				Millisekunden.valueOf(0), vorgangsmappe5.gibPruefliste()
						.gibBereitsGewarteteZeit());

		// Pruefen 3
		aelteste = alarmVorgangAusgangskorb.entnehmeAeltestenEingang();
		
		assertNotNull(aelteste);
		assertEquals("Bestaetigung", aelteste
				.gibAusloesendeAlarmNachrichtDiesesVorganges()
				.gibNachrichtenText());
		assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste
				.gibPruefliste().gesamtErgebnis());
		
		
		assertEquals(aelteste.gibMappenkennung(), vorgangsmappe5
				.gibAbschliessendeMappenkennung());
	}

	public void testConstructor() {
		final int ANZAHL_REGELWERKE = 3;

		Regelwerk[] regelwerke = new Regelwerk[ANZAHL_REGELWERKE];
		for (int i = 0; i < ANZAHL_REGELWERKE; i++) {
			regelwerke[i] = new StandardRegelwerk(Regelwerkskennung.valueOf(),
					new VersandRegel() {
						// Impl hier egal!
						public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
								AlarmNachricht nachricht,
								Pruefliste bisherigesErgebnis) {
						}

						public Millisekunden pruefeNachrichtAufTimeOuts(
								Pruefliste bisherigesErgebnis,
								Millisekunden verstricheneZeitSeitErsterPruefung) {
							return null;
						}

						public Millisekunden pruefeNachrichtErstmalig(
								AlarmNachricht nachricht,
								Pruefliste ergebnisListe) {
							return null;
						}
					});
		}

		AlarmEntscheidungsBuero buero = new AlarmEntscheidungsBuero(regelwerke, new StandardAblagekorb<Vorgangsmappe>(), new StandardAblagekorb<Vorgangsmappe>());

		assertNotNull(buero.gibAbteilungsleiterFuerTest());
		assertNotNull(buero.gibAssistenzFuerTest());
		Collection<Sachbearbeiter> listOfSachbearbeiter = buero
				.gibListeDerSachbearbeiterFuerTest();
		assertNotNull(listOfSachbearbeiter);
		assertTrue("listOfSachbearbeiter.size()==" + ANZAHL_REGELWERKE,
				listOfSachbearbeiter.size() == ANZAHL_REGELWERKE);

		assertTrue("buero.getAbteilungsleiter().istAmArbeiten()", buero
				.gibAbteilungsleiterFuerTest().istAmArbeiten());
		assertTrue("buero.getAssistenz().istAmArbeiten()", buero
				.gibAssistenzFuerTest().istAmArbeiten());
		for (Sachbearbeiter bearbeiter : listOfSachbearbeiter) {
			assertTrue("buero.getListOfSachbearbeiter().istAmArbeiten()",
					bearbeiter.istAmArbeiten());
		}
	}

	public void testIntegration() throws InterruptedException,
			UnknownHostException {
		VersandRegel regel = new VersandRegel() {
			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
				bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(this,
						RegelErgebnis.ZUTREFFEND);
			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeitSeitErsterPruefung) {
				bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(this,
						RegelErgebnis.ZUTREFFEND);
				return Millisekunden.valueOf(0);
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(this,
						RegelErgebnis.ZUTREFFEND);
				return Millisekunden.valueOf(0);
			}

		};
		VersandRegel regel2 = new VersandRegel() {
			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {

			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeitSeitErsterPruefung) {
				bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(this,
						RegelErgebnis.NICHT_ZUTREFFEND);
				return Millisekunden.valueOf(0);
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(this,
						RegelErgebnis.VIELLEICHT_ZUTREFFEND);
				return Millisekunden.valueOf(500);
			}

		};
		VersandRegel regel3 = new VersandRegel() {
			public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {

			}

			public Millisekunden pruefeNachrichtAufTimeOuts(
					Pruefliste bisherigesErgebnis,
					Millisekunden verstricheneZeitSeitErsterPruefung) {

				if (verstricheneZeitSeitErsterPruefung.istKleiner(Millisekunden
						.valueOf(51))) {
					bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(
							this, RegelErgebnis.VIELLEICHT_ZUTREFFEND);
					return Millisekunden.valueOf(1500);
				}
				bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(this,
						RegelErgebnis.ZUTREFFEND);
				return Millisekunden.valueOf(0);
			}

			public Millisekunden pruefeNachrichtErstmalig(
					AlarmNachricht nachricht, Pruefliste ergebnisListe) {
				ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(this,
						RegelErgebnis.VIELLEICHT_ZUTREFFEND);
				return Millisekunden.valueOf(50);
			}

		};

		Regelwerkskennung regelwerkskennung = Regelwerkskennung.valueOf();
		Regelwerk regelwerk = new StandardRegelwerk(regelwerkskennung, regel);

		Regelwerkskennung regelwerkskennung2 = Regelwerkskennung.valueOf();
		Regelwerk regelwerk2 = new StandardRegelwerk(regelwerkskennung2, regel2);

		Regelwerkskennung regelwerkskennung3 = Regelwerkskennung.valueOf();
		Regelwerk regelwerk3 = new StandardRegelwerk(regelwerkskennung3, regel3);

		AlarmEntscheidungsBuero buero = new AlarmEntscheidungsBuero(
				new Regelwerk[] { regelwerk, regelwerk2, regelwerk3 }, new StandardAblagekorb<Vorgangsmappe>(), new StandardAblagekorb<Vorgangsmappe>());
		Eingangskorb<Vorgangsmappe> alarmVorgangEingangskorb = buero
				.gibAlarmVorgangEingangskorb();
		final StandardAblagekorb<Vorgangsmappe> alarmVorgangAusgangskorb = (StandardAblagekorb<Vorgangsmappe>) buero
				.gibAlarmVorgangAusgangskorb();

		Vorgangsmappenkennung vorgangsmappenkennung = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		AlarmNachricht alarmNachricht = new AlarmNachricht("test nachricht");
		Vorgangsmappe vorgangsmappe = new Vorgangsmappe(vorgangsmappenkennung,
				alarmNachricht);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe);

		Vorgangsmappe aelteste = alarmVorgangAusgangskorb
				.entnehmeAeltestenEingang();

		assertEquals(alarmNachricht, aelteste
				.gibAusloesendeAlarmNachrichtDiesesVorganges());
		assertTrue(aelteste.gibPruefliste().gesamtErgebnis() == WeiteresVersandVorgehen.VERSENDEN);
		assertTrue(aelteste.gibPruefliste().gibRegelwerkskennung() == regelwerk
				.gibRegelwerkskennung());

		aelteste = alarmVorgangAusgangskorb.entnehmeAeltestenEingang();

		assertEquals(alarmNachricht, aelteste
				.gibAusloesendeAlarmNachrichtDiesesVorganges());
		assertTrue(aelteste.gibPruefliste().gesamtErgebnis() == WeiteresVersandVorgehen.NICHT_VERSENDEN);
		assertTrue(aelteste.gibPruefliste().gibRegelwerkskennung() == regelwerk2
				.gibRegelwerkskennung());
		assertTrue(aelteste.gibPruefliste().gibBereitsGewarteteZeit().equals(
				Millisekunden.valueOf(500)));

		aelteste = alarmVorgangAusgangskorb.entnehmeAeltestenEingang();

		assertEquals(alarmNachricht, aelteste
				.gibAusloesendeAlarmNachrichtDiesesVorganges());
		assertTrue(aelteste.gibPruefliste().gesamtErgebnis() == WeiteresVersandVorgehen.VERSENDEN);
		assertTrue(aelteste.gibPruefliste().gibRegelwerkskennung() == regelwerk3
				.gibRegelwerkskennung());
		assertTrue(aelteste.gibPruefliste().gibBereitsGewarteteZeit().equals(
				Millisekunden.valueOf(1550)));
	}
}
