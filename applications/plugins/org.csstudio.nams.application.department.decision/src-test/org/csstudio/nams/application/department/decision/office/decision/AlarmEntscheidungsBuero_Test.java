package org.csstudio.nams.application.department.decision.office.decision;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.csstudio.nams.common.DefaultExecutionService;
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

		public SehrSimpleTextRegel(final String muster) {
			this.muster = muster;
		}

		public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
				final AlarmNachricht nachricht,
				final Pruefliste bisherigesErgebnis) {
			if (!bisherigesErgebnis.gibErgebnisFuerRegel(this).istEntschieden()) {
				this.pruefeNachrichtErstmalig(nachricht, bisherigesErgebnis);
			}
		}

		public Millisekunden pruefeNachrichtAufTimeOuts(
				final Pruefliste bisherigesErgebnis,
				final Millisekunden verstricheneZeitSeitErsterPruefung) {
			return null;
		}

		public Millisekunden pruefeNachrichtErstmalig(
				final AlarmNachricht nachricht,
				final Pruefliste bisherigesErgebnis) {
			if (this.muster.equals(nachricht.gibNachrichtenText())) {
				bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(this,
						RegelErgebnis.ZUTREFFEND);
			} else {
				bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(this,
						RegelErgebnis.NICHT_ZUTREFFEND);
			}
			return null;
		}

		public void setHistoryService(final HistoryService historyService) {
			// TODO Auto-generated method stub

		}

	}

	public void testConstructor() {
		final int ANZAHL_REGELWERKE = 3;

		final Regelwerk[] regelwerke = new Regelwerk[ANZAHL_REGELWERKE];
		for (int i = 0; i < ANZAHL_REGELWERKE; i++) {
			regelwerke[i] = new StandardRegelwerk(Regelwerkskennung.valueOf(),
					new VersandRegel() {
						// Impl hier egal!
						public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
								final AlarmNachricht nachricht,
								final Pruefliste bisherigesErgebnis) {
						}

						public Millisekunden pruefeNachrichtAufTimeOuts(
								final Pruefliste bisherigesErgebnis,
								final Millisekunden verstricheneZeitSeitErsterPruefung) {
							return null;
						}

						public Millisekunden pruefeNachrichtErstmalig(
								final AlarmNachricht nachricht,
								final Pruefliste ergebnisListe) {
							return null;
						}
					});
		}

		final AlarmEntscheidungsBuero buero = new AlarmEntscheidungsBuero(
				new DefaultExecutionService(), regelwerke,
				new StandardAblagekorb<Vorgangsmappe>(),
				new StandardAblagekorb<Vorgangsmappe>(), 10);

		Assert.assertNotNull(buero.gibAbteilungsleiterFuerTest());
		Assert.assertNotNull(buero.gibAssistenzFuerTest());
		final Collection<Sachbearbeiter> listOfSachbearbeiter = buero
				.gibListeDerSachbearbeiterFuerTest();
		Assert.assertNotNull(listOfSachbearbeiter);
		Assert.assertTrue("listOfSachbearbeiter.size()==" + ANZAHL_REGELWERKE,
				listOfSachbearbeiter.size() == ANZAHL_REGELWERKE);

		Assert.assertTrue("buero.getAbteilungsleiter().istAmArbeiten()", buero
				.gibAbteilungsleiterFuerTest().istAmArbeiten());
		Assert.assertTrue("buero.getAssistenz().istAmArbeiten()", buero
				.gibAssistenzFuerTest().istAmArbeiten());
		for (final Sachbearbeiter bearbeiter : listOfSachbearbeiter) {
			Assert.assertTrue(
					"buero.getListOfSachbearbeiter().istAmArbeiten()",
					bearbeiter.istAmArbeiten());
		}
	}

	public void testIntegration() throws InterruptedException,
			UnknownHostException {
		final VersandRegel regel = new VersandRegel() {
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
		final VersandRegel regel2 = new VersandRegel() {
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
		final VersandRegel regel3 = new VersandRegel() {
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

		final Regelwerkskennung regelwerkskennung = Regelwerkskennung.valueOf();
		final Regelwerk regelwerk = new StandardRegelwerk(regelwerkskennung,
				regel);

		final Regelwerkskennung regelwerkskennung2 = Regelwerkskennung
				.valueOf();
		final Regelwerk regelwerk2 = new StandardRegelwerk(regelwerkskennung2,
				regel2);

		final Regelwerkskennung regelwerkskennung3 = Regelwerkskennung
				.valueOf();
		final Regelwerk regelwerk3 = new StandardRegelwerk(regelwerkskennung3,
				regel3);

		final AlarmEntscheidungsBuero buero = new AlarmEntscheidungsBuero(
				new DefaultExecutionService(), new Regelwerk[] { regelwerk,
						regelwerk2, regelwerk3 },
				new StandardAblagekorb<Vorgangsmappe>(),
				new StandardAblagekorb<Vorgangsmappe>(), 10);
		final Eingangskorb<Vorgangsmappe> alarmVorgangEingangskorb = buero
				.gibAlarmVorgangEingangskorb();
		final StandardAblagekorb<Vorgangsmappe> alarmVorgangAusgangskorb = (StandardAblagekorb<Vorgangsmappe>) buero
				.gibAlarmVorgangAusgangskorb();

		final Vorgangsmappenkennung vorgangsmappenkennung = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		final AlarmNachricht alarmNachricht = new AlarmNachricht(
				"test nachricht");
		final Vorgangsmappe vorgangsmappe = new Vorgangsmappe(
				vorgangsmappenkennung, alarmNachricht);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe);

		Vorgangsmappe aelteste = alarmVorgangAusgangskorb
				.entnehmeAeltestenEingang();

		Assert.assertEquals(alarmNachricht, aelteste
				.gibAusloesendeAlarmNachrichtDiesesVorganges());
		Assert
				.assertTrue(aelteste.gibPruefliste().gesamtErgebnis() == WeiteresVersandVorgehen.VERSENDEN);
		Assert
				.assertTrue(aelteste.gibPruefliste().gibRegelwerkskennung() == regelwerk
						.gibRegelwerkskennung());

		aelteste = alarmVorgangAusgangskorb.entnehmeAeltestenEingang();

		Assert.assertEquals(alarmNachricht, aelteste
				.gibAusloesendeAlarmNachrichtDiesesVorganges());
		Assert
				.assertTrue(aelteste.gibPruefliste().gesamtErgebnis() == WeiteresVersandVorgehen.NICHT_VERSENDEN);
		Assert
				.assertTrue(aelteste.gibPruefliste().gibRegelwerkskennung() == regelwerk2
						.gibRegelwerkskennung());
		Assert.assertTrue(aelteste.gibPruefliste().gibBereitsGewarteteZeit()
				.equals(Millisekunden.valueOf(500)));

		aelteste = alarmVorgangAusgangskorb.entnehmeAeltestenEingang();

		Assert.assertEquals(alarmNachricht, aelteste
				.gibAusloesendeAlarmNachrichtDiesesVorganges());
		Assert
				.assertTrue(aelteste.gibPruefliste().gesamtErgebnis() == WeiteresVersandVorgehen.VERSENDEN);
		Assert
				.assertTrue(aelteste.gibPruefliste().gibRegelwerkskennung() == regelwerk3
						.gibRegelwerkskennung());
		Assert.assertTrue(aelteste.gibPruefliste().gibBereitsGewarteteZeit()
				.equals(Millisekunden.valueOf(1550)));
	}

	@Test(timeout = 4000)
	public void testMitEinerTimeBasedRegeln() throws Throwable {
		// TODO Eine time based konstruieren, klären wie der time based vertrag
		// ist (was darf null sein) beide (den der time based und diesen)
		// anpassen!
		final VersandRegel aufhebung = new SehrSimpleTextRegel("Aufhebung");
		final VersandRegel bestaetigung = new SehrSimpleTextRegel(
				"Bestaetigung");
		final VersandRegel ausloeser = new SehrSimpleTextRegel("Ausloeser");

		final VersandRegel timebasedRegel = new TimeBasedRegel(ausloeser,
				aufhebung, bestaetigung, Millisekunden.valueOf(1000));

		final Regelwerkskennung regelwerkskennung = Regelwerkskennung.valueOf();
		final Regelwerk regelwerk = new StandardRegelwerk(regelwerkskennung,
				timebasedRegel);

		final AlarmEntscheidungsBuero buero = new AlarmEntscheidungsBuero(
				new DefaultExecutionService(), new Regelwerk[] { regelwerk },
				new StandardAblagekorb<Vorgangsmappe>(),
				new StandardAblagekorb<Vorgangsmappe>(), 10);
		final Eingangskorb<Vorgangsmappe> alarmVorgangEingangskorb = buero
				.gibAlarmVorgangEingangskorb();
		final StandardAblagekorb<Vorgangsmappe> alarmVorgangAusgangskorb = (StandardAblagekorb<Vorgangsmappe>) buero
				.gibAlarmVorgangAusgangskorb();

		// Un-Passende 1
		final Vorgangsmappenkennung vorgangsmappenkennung = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		final AlarmNachricht alarmNachricht = new AlarmNachricht("XXO");
		final Vorgangsmappe vorgangsmappe = new Vorgangsmappe(
				vorgangsmappenkennung, alarmNachricht);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe);

		// Passende 1
		final Vorgangsmappenkennung vorgangsmappenkennung2 = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		final AlarmNachricht alarmNachricht2 = new AlarmNachricht("Ausloeser");
		final Vorgangsmappe vorgangsmappe2 = new Vorgangsmappe(
				vorgangsmappenkennung2, alarmNachricht2);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe2);

		// Un-Passende 1
		final Vorgangsmappenkennung vorgangsmappenkennung3 = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		final AlarmNachricht alarmNachricht3 = new AlarmNachricht("Baeh!");
		final Vorgangsmappe vorgangsmappe3 = new Vorgangsmappe(
				vorgangsmappenkennung3, alarmNachricht3);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe3);

		// Passende Bestaetigung1
		final Vorgangsmappenkennung vorgangsmappenkennung4 = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		final AlarmNachricht alarmNachricht4 = new AlarmNachricht(
				"Bestaetigung");
		final Vorgangsmappe vorgangsmappe4 = new Vorgangsmappe(
				vorgangsmappenkennung4, alarmNachricht4);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe4);

		// Pruefen 1
		Vorgangsmappe aelteste = alarmVorgangAusgangskorb
				.entnehmeAeltestenEingang();

		Assert.assertNotNull(aelteste);
		Assert.assertEquals("XXO", aelteste
				.gibAusloesendeAlarmNachrichtDiesesVorganges()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste
				.gibPruefliste().gesamtErgebnis());

		// Pruefen 2
		aelteste = alarmVorgangAusgangskorb.entnehmeAeltestenEingang();

		Assert.assertNotNull(aelteste);
		Assert.assertEquals("Baeh!", aelteste
				.gibAusloesendeAlarmNachrichtDiesesVorganges()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste
				.gibPruefliste().gesamtErgebnis());

		// Pruefen 4
		final Vorgangsmappe vorgangsmappe5 = alarmVorgangAusgangskorb
				.entnehmeAeltestenEingang();

		Assert.assertNotNull(vorgangsmappe5);
		Assert.assertEquals("Ausloeser", vorgangsmappe5
				.gibAusloesendeAlarmNachrichtDiesesVorganges()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.VERSENDEN, vorgangsmappe5
				.gibPruefliste().gesamtErgebnis());
		Assert.assertEquals(
				"Die Zeit wird nur hochgesetzt nach einem timeout!",
				Millisekunden.valueOf(0), vorgangsmappe5.gibPruefliste()
						.gibBereitsGewarteteZeit());

		// Pruefen 3
		aelteste = alarmVorgangAusgangskorb.entnehmeAeltestenEingang();

		Assert.assertNotNull(aelteste);
		Assert.assertEquals("Bestaetigung", aelteste
				.gibAusloesendeAlarmNachrichtDiesesVorganges()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste
				.gibPruefliste().gesamtErgebnis());

		Assert.assertEquals(aelteste.gibMappenkennung(), vorgangsmappe5
				.gibAbschliessendeMappenkennung());
	}

	@Test(timeout = 4000)
	public void testMitSimplenRegeln() throws Throwable {
		final VersandRegel regel2 = new SehrSimpleTextRegel("Hallo");
		final VersandRegel regel3 = new SehrSimpleTextRegel("Tach");
		final VersandRegel regel = new OderVersandRegel(new VersandRegel[] {
				regel2, regel3 });

		final Regelwerkskennung regelwerkskennung = Regelwerkskennung.valueOf();
		final Regelwerk regelwerk = new StandardRegelwerk(regelwerkskennung,
				regel);

		final AlarmEntscheidungsBuero buero = new AlarmEntscheidungsBuero(
				new DefaultExecutionService(), new Regelwerk[] { regelwerk },
				new StandardAblagekorb<Vorgangsmappe>(),
				new StandardAblagekorb<Vorgangsmappe>(), 10);
		final Eingangskorb<Vorgangsmappe> alarmVorgangEingangskorb = buero
				.gibAlarmVorgangEingangskorb();
		final StandardAblagekorb<Vorgangsmappe> alarmVorgangAusgangskorb = (StandardAblagekorb<Vorgangsmappe>) buero
				.gibAlarmVorgangAusgangskorb();

		// Passende 1
		final Vorgangsmappenkennung vorgangsmappenkennung = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		final AlarmNachricht alarmNachricht = new AlarmNachricht("Hallo");
		final Vorgangsmappe vorgangsmappe = new Vorgangsmappe(
				vorgangsmappenkennung, alarmNachricht);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe);

		// Passende 2
		final Vorgangsmappenkennung vorgangsmappenkennung2 = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		final AlarmNachricht alarmNachricht2 = new AlarmNachricht("Tach");
		final Vorgangsmappe vorgangsmappe2 = new Vorgangsmappe(
				vorgangsmappenkennung2, alarmNachricht2);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe2);

		// Un-Passende 1
		final Vorgangsmappenkennung vorgangsmappenkennung3 = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		final AlarmNachricht alarmNachricht3 = new AlarmNachricht("Moin!");
		final Vorgangsmappe vorgangsmappe3 = new Vorgangsmappe(
				vorgangsmappenkennung3, alarmNachricht3);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe3);

		// Pruefen 1
		Vorgangsmappe aelteste = alarmVorgangAusgangskorb
				.entnehmeAeltestenEingang();

		Assert.assertNotNull(aelteste);
		Assert.assertEquals("Hallo", aelteste
				.gibAusloesendeAlarmNachrichtDiesesVorganges()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.VERSENDEN, aelteste
				.gibPruefliste().gesamtErgebnis());

		// Pruefen 2
		aelteste = alarmVorgangAusgangskorb.entnehmeAeltestenEingang();

		Assert.assertNotNull(aelteste);
		Assert.assertEquals("Tach", aelteste
				.gibAusloesendeAlarmNachrichtDiesesVorganges()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.VERSENDEN, aelteste
				.gibPruefliste().gesamtErgebnis());

		// Pruefen 3
		aelteste = alarmVorgangAusgangskorb.entnehmeAeltestenEingang();

		Assert.assertNotNull(aelteste);
		Assert.assertEquals("Moin!", aelteste
				.gibAusloesendeAlarmNachrichtDiesesVorganges()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste
				.gibPruefliste().gesamtErgebnis());
	}
}
