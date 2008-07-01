package org.csstudio.nams.application.department.decision.office.decision;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

import org.csstudio.nams.common.decision.Ablagefaehig;
import org.csstudio.nams.common.decision.Ausgangskorb;
import org.csstudio.nams.common.decision.Eingangskorb;
import org.csstudio.nams.common.decision.StandardAblagekorb;
import org.csstudio.nams.common.decision.Vorgangsmappe;
import org.csstudio.nams.common.decision.Vorgangsmappenkennung;
import org.csstudio.nams.common.decision.Zwischenablagekorb;
import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.Regelwerkskennung;
import org.csstudio.nams.common.material.regelwerk.Pruefliste;
import org.csstudio.nams.common.material.regelwerk.Regelwerk;
import org.csstudio.nams.common.material.regelwerk.StandardRegelwerk;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen;
import org.csstudio.nams.common.testutils.AbstractObject_TestCase;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.easymock.LogicalOperator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Sachbearbeiter_Test extends
		AbstractObject_TestCase<Sachbearbeiter> {

	private Eingangskorb<Vorgangsmappe> eingangskorb;
	private Vorgangsmappe vorgangsmappe;
	private Ausgangskorb<Vorgangsmappe> ausgangskorb;
	private Zwischenablagekorb<Vorgangsmappe> zwischenablagekorb;
	private Regelwerk regelwerk;
	protected volatile boolean eineMappeIstfertig;
	private Ausgangskorb<Terminnotiz> assistenzkorb;
	private Eingangskorb<Terminnotiz> terminnotizEingangskorb;

	private class Test_Pruefliste extends Pruefliste {

		Test_Pruefliste(Regelwerkskennung regelwerkskennung) {
			super(regelwerkskennung, null);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		eingangskorb = EasyMock.createMock(Eingangskorb.class);
		zwischenablagekorb = EasyMock.createMock(Zwischenablagekorb.class);
		assistenzkorb = EasyMock.createMock(Ausgangskorb.class);
		terminnotizEingangskorb = EasyMock.createMock(Eingangskorb.class);
		ausgangskorb = EasyMock.createMock(Ausgangskorb.class);
		regelwerk = EasyMock.createMock(Regelwerk.class);

		eineMappeIstfertig = false;

		alarmNachricht = new AlarmNachricht(/*
											 * Die ist egal, weil wir hier true
											 * testen
											 */"Test-Nachricht");

		vorgangsmappe = new Vorgangsmappe(Vorgangsmappenkennung.valueOf(
				InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }),
				new Date(123456)), alarmNachricht);
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		if (eingangskorb != null) {
			EasyMock.verify(eingangskorb);
		}
		if (zwischenablagekorb != null) {
			EasyMock.verify(zwischenablagekorb);
		}
		if (assistenzkorb != null) {
			EasyMock.verify(assistenzkorb);
		}
		if (terminnotizEingangskorb != null) {
			EasyMock.verify(terminnotizEingangskorb);
		}
		if (ausgangskorb != null) {
			EasyMock.verify(ausgangskorb);
		}
		if (regelwerk != null) {
			EasyMock.verify(regelwerk);
		}
		vorgangsmappe = null;
		alarmNachricht = null;
		super.tearDown();
	}

	static class IdComparator<T> implements Comparator<T> {
		public int compare(T expected, T actual) {
			return expected == actual ? 0 : -1;
		}
	};

	@SuppressWarnings("unchecked")
	@Test(timeout = 4000)
	public void testVerarbeiteNachrichtDieSofortEntschiedenWerdenKann()
			throws Throwable {
		// Eingangskorb
		EasyMock.expect(eingangskorb.entnehmeAeltestenEingang()).andReturn(
				vorgangsmappe).times(1).andStubAnswer(
				new IAnswer<Vorgangsmappe>() {
					public Vorgangsmappe answer() throws Throwable {
						eineMappeIstfertig = true;
						Thread.sleep(5000);
						fail("Thread sollte längst tot sein.");
						return null;
					}
				});
		EasyMock.replay(eingangskorb);

		// Ausgangskorb
		Comparator<Vorgangsmappe> vorlagenMappenComparator = new IdComparator<Vorgangsmappe>();// Die
		// gleiche
		// Mappe
		// kam raus, d.h.
		// ich kann die
		// von mir refenzierte Mappe auf das Ergebnix ;)
		// prüfen.
		ausgangskorb.ablegen(EasyMock.cmp(vorgangsmappe,
				vorlagenMappenComparator, LogicalOperator.EQUAL));
		EasyMock.expectLastCall().once();
		EasyMock.replay(ausgangskorb);

		// Zwischenablage
		EasyMock.expect(zwischenablagekorb.iterator()).andReturn(
				new Iterator<Vorgangsmappe>() {
					public boolean hasNext() {
						return false;
					}

					public Vorgangsmappe next() {
						fail();
						return null;
					}

					public void remove() {
						fail();
					}
				});
		EasyMock.replay(zwischenablagekorb);

		// Korb der Assistenz
		EasyMock.replay(assistenzkorb);

		// eingangsKorb für notizen
		EasyMock.expect(terminnotizEingangskorb.entnehmeAeltestenEingang())
				.andStubAnswer(new IAnswer<Terminnotiz>() {
					public Terminnotiz answer() throws Throwable {
						eineMappeIstfertig = true;
						Thread.sleep(5000);
						fail("Thread sollte längst tot sein.");
						return null;
					}
				});
		EasyMock.replay(terminnotizEingangskorb);

		// Regelwerk
		aktuellesGesamtErgebnisDesRegelwerk = WeiteresVersandVorgehen.NOCH_NICHT_GEPRUEFT;
		Regelwerkskennung regelwerkskennungDesBenutztenRegelwerkes = Regelwerkskennung
				.valueOf();
		Pruefliste pruefliste = new Test_Pruefliste(regelwerkskennungDesBenutztenRegelwerkes) {
			public WeiteresVersandVorgehen gesamtErgebnis() {
				return aktuellesGesamtErgebnisDesRegelwerk;
			}
			public Millisekunden gibMillisekundenBisZurNaechstenPruefung() {
				fail();
				return null;
			}
		};

		EasyMock.expect(regelwerk.gibNeueLeerePruefliste()).andReturn(
				pruefliste);
		regelwerk.pruefeNachrichtErstmalig(EasyMock.cmp(alarmNachricht,
				new IdComparator<AlarmNachricht>(), LogicalOperator.EQUAL),
				EasyMock.cmp(pruefliste, new IdComparator<Pruefliste>() {
					@Override
					public int compare(Pruefliste expected, Pruefliste actual) {
						int compareResult = super.compare(expected, actual);
						if (compareResult == 0) {
							// TODO aktuell wird diese Zeile nicht aufgerufen,
							// dadurch schlägt der Test fehl
							aktuellesGesamtErgebnisDesRegelwerk = WeiteresVersandVorgehen.VERSENDEN;
						}
						return compareResult;
					}
				}, LogicalOperator.EQUAL));
		EasyMock.expectLastCall().once();
		EasyMock.replay(regelwerk);

		// Test beginnen
		Sachbearbeiter sachbearbeiter = new Sachbearbeiter("Horst Senkel",
				eingangskorb, terminnotizEingangskorb, zwischenablagekorb,
				assistenzkorb, ausgangskorb, regelwerk);

		sachbearbeiter.beginneArbeit();
		// Warte bis der Bearbeiter fertig sein müsste...
		for (int zaehler = 0; zaehler < 3000; zaehler += 10) {
			Thread.sleep(10);
			if (eineMappeIstfertig) {
				Thread.sleep(10);
				break;
			}
		}
		sachbearbeiter.beendeArbeit();

		// Ergebnisse in Mappe prüfen
		Pruefliste prueflisteAusDerMappe = vorgangsmappe.gibPruefliste();
		assertNotNull(prueflisteAusDerMappe);
		assertEquals(WeiteresVersandVorgehen.VERSENDEN, prueflisteAusDerMappe
				.gesamtErgebnis());

		assertEquals(regelwerkskennungDesBenutztenRegelwerkes,
				prueflisteAusDerMappe.gibRegelwerkskennung());
	}

	/*
	 * // @SuppressWarnings("unchecked") // @Test(timeout = 4000) // public void
	 * testVerarbeiteNachrichtDieErstOffenBleibt() throws Throwable // { // //
	 * Eingangskorb //
	 * EasyMock.expect(eingangskorb.entnehmeAeltestenEingang()).andReturn( //
	 * vorgangsmappe).times(1).andStubAnswer( // new IAnswer<Vorgangsmappe>() { //
	 * public Vorgangsmappe answer() throws Throwable { // eineMappeIstfertig =
	 * true; // Thread.sleep(5000); // fail("Thread sollte längst tot sein."); //
	 * return null; // } // }); // EasyMock.replay(eingangskorb); // // //
	 * Ausgangskorb // Comparator<Vorgangsmappe> vorlagenMappenComparator = new //
	 * IdComparator<Vorgangsmappe>();// Die // // gleiche // // Mappe // // kam
	 * raus, d.h. // // ich kann die // // von mir refenzierte Mappe auf das
	 * Ergebnix ;) // // prüfen. //
	 * ausgangskorb.ablegen(EasyMock.cmp(vorgangsmappe, //
	 * vorlagenMappenComparator, LogicalOperator.EQUAL)); //
	 * EasyMock.expectLastCall().once(); // EasyMock.replay(ausgangskorb); // // //
	 * Zwischenablage //
	 * EasyMock.expect(zwischenablagekorb.iterator()).andReturn( // new Iterator<Vorgangsmappe>() { //
	 * public boolean hasNext() { // return false; // } // // public
	 * Vorgangsmappe next() { // fail(); // return null; // } // // public void
	 * remove() { // fail(); // } // }); // EasyMock.replay(zwischenablagekorb); // // //
	 * Korb der Assistenz // EasyMock.replay(assistenzkorb); // // // Regelwerk //
	 * aktuellesGesamtErgebnisDesRegelwerk = //
	 * WeiteresVersandVorgehen.NOCH_NICHT_GEPRUEFT; // Regelwerkskennung
	 * regelwerkskennungDesBenutztenRegelwerkes = // Regelwerkskennung //
	 * .valueOf(); // Pruefliste pruefliste = new Pruefliste( //
	 * regelwerkskennungDesBenutztenRegelwerkes) { // // @Override // public
	 * WeiteresVersandVorgehen gesamtErgebnis() { // return
	 * aktuellesGesamtErgebnisDesRegelwerk; // } // // @Override // public
	 * Millisekunden gibMillisekundenBisZurNaechstenPruefung() { // fail(); //
	 * return null; // } // }; // //
	 * EasyMock.expect(regelwerk.gibNeueLeerePruefliste()).andReturn( //
	 * pruefliste); // regelwerk.pruefeNachricht(EasyMock.cmp(alarmNachricht, //
	 * new IdComparator<AlarmNachricht>(), LogicalOperator.EQUAL), //
	 * EasyMock.cmp(pruefliste, new IdComparator<Pruefliste>() { // @Override //
	 * public int compare(Pruefliste expected, Pruefliste actual) { // int
	 * compareResult = super.compare(expected, actual); // if (compareResult ==
	 * 0) { // aktuellesGesamtErgebnisDesRegelwerk =
	 * WeiteresVersandVorgehen.VERSENDEN; // } // return compareResult; // } // },
	 * LogicalOperator.EQUAL)); // EasyMock.expectLastCall().once(); //
	 * EasyMock.replay(regelwerk); // // // Test beginnen // Sachbearbeiter
	 * sachbearbeiter = new Sachbearbeiter("Horst Senkel", // eingangskorb,
	 * zwischenablagekorb, assistenzkorb, ausgangskorb, // regelwerk); // //
	 * sachbearbeiter.beginneArbeit(); // // // Warte bis der Bearbeiter fertig
	 * sein müsste... // while (!eineMappeIstfertig) { // Thread.yield(); // } // //
	 * sachbearbeiter.beendeArbeit(); // // // Ergebnisse in Mappe prüfen //
	 * Pruefliste prueflisteAusDerMappe = vorgangsmappe.gibPruefliste(); //
	 * assertNotNull(prueflisteAusDerMappe); //
	 * assertEquals(WeiteresVersandVorgehen.VERSENDEN, prueflisteAusDerMappe //
	 * .gesamtErgebnis()); // //
	 * assertEquals(regelwerkskennungDesBenutztenRegelwerkes, //
	 * prueflisteAusDerMappe.gibRegelwerkskennung()); // } // // @Test // public
	 * void testBearbeiteVorgang() { // // }
	 */

	@Test
	public void testArbeit() throws InterruptedException {
		Sachbearbeiter sachbearbeiter = this.getNewInstanceOfClassUnderTest();
		assertFalse("sachbearbeiter.istAmArbeiten()", sachbearbeiter
				.istAmArbeiten());
		sachbearbeiter.beginneArbeit();
		assertTrue("sachbearbeiter.istAmArbeiten()", sachbearbeiter
				.istAmArbeiten());
		sachbearbeiter.beendeArbeit();
		Thread.sleep(100);
		assertFalse("sachbearbeiter.istAmArbeiten()", sachbearbeiter
				.istAmArbeiten());
	}

	private WeiteresVersandVorgehen aktuellesGesamtErgebnisDesRegelwerk;
	private AlarmNachricht alarmNachricht;

	@Override
	protected Sachbearbeiter getNewInstanceOfClassUnderTest() {
		mocksAufIgnorierenSetzen();

		String name = "Horst Senkel";
		return erzeugeSachbearbeiter(name);
	}

	private void mocksAufIgnorierenSetzen() {
		eingangskorb = null;
		zwischenablagekorb = null;
		terminnotizEingangskorb = null;
		ausgangskorb = null;
		regelwerk = null;
		assistenzkorb = null;
	}

	private Sachbearbeiter erzeugeSachbearbeiter(String name) {
		return new Sachbearbeiter(name,
				new StandardAblagekorb<Vorgangsmappe>(),
				new StandardAblagekorb<Terminnotiz>(),
				new StandardAblagekorb<Vorgangsmappe>(),
				new StandardAblagekorb<Terminnotiz>(),
				new StandardAblagekorb<Vorgangsmappe>(), new StandardRegelwerk(
						Regelwerkskennung.valueOf()));
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		mocksAufIgnorierenSetzen();
		return new Object();
	}

	@Override
	protected Sachbearbeiter[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		mocksAufIgnorierenSetzen();
		return new Sachbearbeiter[] { erzeugeSachbearbeiter("1"),
				erzeugeSachbearbeiter("2"), erzeugeSachbearbeiter("3") };
	}

	@Test
	public void testGibNamen() {
		mocksAufIgnorierenSetzen();
		Sachbearbeiter[] sachbearbeiter = getThreeDiffrentNewInstanceOfClassUnderTest();
		assertEquals("1", sachbearbeiter[0].gibName());
		assertEquals("2", sachbearbeiter[1].gibName());
		assertEquals("3", sachbearbeiter[2].gibName());
	}

	@Test
	public void testBearbeiteVorgang() throws InterruptedException {
		mocksAufIgnorierenSetzen();

		final boolean[] isMethodBearbeiteNeuenVorgangCalled = new boolean[] { false };
		final boolean[] isMethodBearbeiteOffeneVorgaengeCalled = new boolean[] { false };
		final boolean[] isMethodBearbeiteTerminNotizCalled = new boolean[] { false };
		final boolean[] returnBearbeiteOffeneVorgaenge = new boolean[] { false };

		Sachbearbeiter bearbeiter = new Sachbearbeiter("Hans",
				new StandardAblagekorb<Vorgangsmappe>(),
				new StandardAblagekorb<Terminnotiz>(),
				new StandardAblagekorb<Vorgangsmappe>(),
				new StandardAblagekorb<Terminnotiz>(),
				new StandardAblagekorb<Vorgangsmappe>(), new StandardRegelwerk(
						Regelwerkskennung.valueOf())) {

			@Override
			protected void bearbeiteNeuenVorgang(Vorgangsmappe vorgangsmappe)
					throws InterruptedException {
				isMethodBearbeiteNeuenVorgangCalled[0] = true;
			}

			@Override
			protected boolean bearbeiteOffeneVorgaenge(
					Vorgangsmappe vorgangsmappe) throws InterruptedException {
				isMethodBearbeiteOffeneVorgaengeCalled[0] = true;
				return returnBearbeiteOffeneVorgaenge[0];
			}

			@Override
			protected void bearbeiteTerminNotiz(Terminnotiz notiz)
					throws InterruptedException {
				isMethodBearbeiteTerminNotizCalled[0] = true;
			}
		};

		// Bearbeite neuen Vorgang
		bearbeiter.bearbeiteVorgangBeimSachbearbeiter(vorgangsmappe);
		assertTrue("bearbeiteNeuenVorgang",
				isMethodBearbeiteNeuenVorgangCalled[0]);
		assertTrue("bearbeiteNeuenVorgang",
				isMethodBearbeiteOffeneVorgaengeCalled[0]);
		assertFalse("bearbeiteNeuenVorgang",
				isMethodBearbeiteTerminNotizCalled[0]);

		// Bearbeite offenen Vorgang
		isMethodBearbeiteNeuenVorgangCalled[0] = false;
		isMethodBearbeiteOffeneVorgaengeCalled[0] = false;
		isMethodBearbeiteTerminNotizCalled[0] = false;
		returnBearbeiteOffeneVorgaenge[0] = true;

		bearbeiter.bearbeiteVorgangBeimSachbearbeiter(vorgangsmappe);
		assertFalse("bearbeiteNeuenVorgang",
				isMethodBearbeiteNeuenVorgangCalled[0]);
		assertTrue("bearbeiteNeuenVorgang",
				isMethodBearbeiteOffeneVorgaengeCalled[0]);
		assertFalse("bearbeiteNeuenVorgang",
				isMethodBearbeiteTerminNotizCalled[0]);

		// Bearbeite Notiz
		isMethodBearbeiteNeuenVorgangCalled[0] = false;
		isMethodBearbeiteOffeneVorgaengeCalled[0] = false;
		isMethodBearbeiteTerminNotizCalled[0] = false;
		returnBearbeiteOffeneVorgaenge[0] = false;

		Terminnotiz terminnotiz = Terminnotiz.valueOf(vorgangsmappe
				.gibMappenkennung(), Millisekunden.valueOf(1000), bearbeiter
				.gibName());
		bearbeiter.bearbeiteVorgangBeimSachbearbeiter(terminnotiz);
		assertFalse("bearbeiteTerminNotiz",
				isMethodBearbeiteNeuenVorgangCalled[0]);
		assertFalse("bearbeiteTerminNotiz",
				isMethodBearbeiteOffeneVorgaengeCalled[0]);
		assertTrue("bearbeiteTerminNotiz",
				isMethodBearbeiteTerminNotizCalled[0]);

		// Fehler fall
		Ablagefaehig errorAblage = new Ablagefaehig() {
		};
		try {
			bearbeiter.bearbeiteVorgangBeimSachbearbeiter(errorAblage);
			assertFalse(true);
		} catch (RuntimeException re) {
			assertTrue(true);
		}
	}

	@Test
	public void testBearbeiteNeuenVorgang() throws UnknownHostException,
			InterruptedException {
		mocksAufIgnorierenSetzen();

		StandardAblagekorb<Vorgangsmappe> ablage = new StandardAblagekorb<Vorgangsmappe>();
		StandardAblagekorb<Terminnotiz> notizKorb = new StandardAblagekorb<Terminnotiz>();
		StandardAblagekorb<Vorgangsmappe> ausgangskorb = new StandardAblagekorb<Vorgangsmappe>();
		Sachbearbeiter sachbearbeiter = new Sachbearbeiter("Fritz",
				new StandardAblagekorb<Vorgangsmappe>(),
				new StandardAblagekorb<Terminnotiz>(), ablage, notizKorb,
				ausgangskorb,
				new StandardRegelwerk(Regelwerkskennung.valueOf()));

		final WeiteresVersandVorgehen[] gesammt = new WeiteresVersandVorgehen[] { WeiteresVersandVorgehen.ERNEUT_PRUEFEN };

		Vorgangsmappe vorgangsmappe = new Vorgangsmappe(Vorgangsmappenkennung
				.valueOf(InetAddress.getByName("127.0.0.1"), new Date(12345)),
				new AlarmNachricht("Alarm")) {
			private Pruefliste pruefliste = new Test_Pruefliste(Regelwerkskennung
					.valueOf()) {
				@Override
				public WeiteresVersandVorgehen gesamtErgebnis() {
					return gesammt[0];
				}
			};

			@Override
			public void setzePruefliste(Pruefliste pruefliste) {

			}

			@Override
			public Pruefliste gibPruefliste() {
				return pruefliste;
			}
		};
		sachbearbeiter.bearbeiteNeuenVorgang(vorgangsmappe);
		assertEquals(vorgangsmappe, ablage.entnehmeAeltestenEingang());
		Terminnotiz notiz = notizKorb.entnehmeAeltestenEingang();
		assertNotNull(notiz);
		assertEquals(vorgangsmappe.gibMappenkennung(), notiz
				.gibVorgangsmappenkennung());

		gesammt[0] = WeiteresVersandVorgehen.NICHT_VERSENDEN;
		sachbearbeiter.bearbeiteNeuenVorgang(vorgangsmappe);
		assertEquals(vorgangsmappe, ausgangskorb.entnehmeAeltestenEingang());

	}

	@Test
	public void testBearbeiteTerminNotiz() throws UnknownHostException,
			InterruptedException {
		mocksAufIgnorierenSetzen();

		StandardAblagekorb<Vorgangsmappe> ablage = new StandardAblagekorb<Vorgangsmappe>();
		StandardAblagekorb<Terminnotiz> notizKorb = new StandardAblagekorb<Terminnotiz>();
		StandardAblagekorb<Vorgangsmappe> ausgangskorb = new StandardAblagekorb<Vorgangsmappe>();
		Sachbearbeiter sachbearbeiter = new Sachbearbeiter("Fritz",
				new StandardAblagekorb<Vorgangsmappe>(),
				new StandardAblagekorb<Terminnotiz>(), ablage, notizKorb,
				ausgangskorb,
				new StandardRegelwerk(Regelwerkskennung.valueOf()));

		Terminnotiz terminnotiz = Terminnotiz.valueOf(Vorgangsmappenkennung
				.valueOf(InetAddress.getByName("127.0.0.8"), new Date(6789)),
				Millisekunden.valueOf(1000), "Fritz");

		sachbearbeiter.bearbeiteTerminNotiz(terminnotiz);
		vorgangsmappe.setzePruefliste(new Test_Pruefliste(Regelwerkskennung
				.valueOf()));
		ablage.ablegen(vorgangsmappe);
		sachbearbeiter.bearbeiteTerminNotiz(terminnotiz);
		terminnotiz = Terminnotiz.valueOf(vorgangsmappe.gibMappenkennung(),
				Millisekunden.valueOf(1000), "Fritz");
		sachbearbeiter.bearbeiteTerminNotiz(terminnotiz);
		Terminnotiz notiz = notizKorb.entnehmeAeltestenEingang();
		assertNotNull(notiz);
		assertEquals(vorgangsmappe.gibMappenkennung(), notiz
				.gibVorgangsmappenkennung());

		vorgangsmappe.setzePruefliste(new Test_Pruefliste(Regelwerkskennung
				.valueOf()) {
			@Override
			public WeiteresVersandVorgehen gesamtErgebnis() {
				return WeiteresVersandVorgehen.VERSENDEN;
			}
		});

		sachbearbeiter.bearbeiteTerminNotiz(terminnotiz);
		Vorgangsmappe mappeImAusgang = ausgangskorb.entnehmeAeltestenEingang();
		assertNotNull(mappeImAusgang);
		assertEquals(vorgangsmappe, mappeImAusgang);
		//		
		// gesammt[0] = WeiteresVersandVorgehen.NICHT_VERSENDEN;
		// sachbearbeiter.bearbeiteNeuenVorgang(vorgangsmappe);
		// assertEquals(vorgangsmappe, ausgangskorb.entnehmeAeltestenEingang());

	}

	@Test
	public void testBearbeiteOffeneVorgaenge() throws InterruptedException,
			UnknownHostException {
		mocksAufIgnorierenSetzen();

		StandardAblagekorb<Vorgangsmappe> zwischenablage = new StandardAblagekorb<Vorgangsmappe>();
		StandardAblagekorb<Terminnotiz> notizKorb = new StandardAblagekorb<Terminnotiz>();
		StandardAblagekorb<Vorgangsmappe> ausgangskorb = new StandardAblagekorb<Vorgangsmappe>();
		Sachbearbeiter sachbearbeiter = new Sachbearbeiter("Fritz",
				new StandardAblagekorb<Vorgangsmappe>(),
				new StandardAblagekorb<Terminnotiz>(), zwischenablage,
				notizKorb, ausgangskorb, new StandardRegelwerk(
						Regelwerkskennung.valueOf()));

		Vorgangsmappenkennung vorgangsmappenkennung = Vorgangsmappenkennung
				.valueOf(InetAddress.getByName("127.0.0.8"), new Date(6789));
		vorgangsmappenkennung = Vorgangsmappenkennung.valueOf(
				vorgangsmappenkennung, "Fritz");

		Vorgangsmappe vorgangsmappe2 = new Vorgangsmappe(vorgangsmappenkennung,
				alarmNachricht);
		vorgangsmappe.setzePruefliste(new Test_Pruefliste(Regelwerkskennung
				.valueOf()) {
			public WeiteresVersandVorgehen gesamtErgebnis() {
				return WeiteresVersandVorgehen.VERSENDEN;
			}
			@Override
			public boolean hatSichGeaendert() {
				return true;
			}
		});
		zwischenablage.ablegen(vorgangsmappe);

		sachbearbeiter.bearbeiteOffeneVorgaenge(vorgangsmappe2);

		/*
		 * Pruefe, dass uebergebene Vorgangsmappe ordentlich abgearbeitet und
		 * aus der Zwischenablage in den Ausgangskorb gelegt wurde
		 */
		Vorgangsmappe mappeImAusgang = ausgangskorb.entnehmeAeltestenEingang();
		assertNotNull(mappeImAusgang);
		assertEquals(vorgangsmappe, mappeImAusgang);
		mappeImAusgang = ausgangskorb.entnehmeAeltestenEingang();
		assertNotNull(mappeImAusgang);
		assertEquals(vorgangsmappe2, mappeImAusgang);
		assertFalse(zwischenablage.istEnthalten(vorgangsmappe2));
		assertFalse(zwischenablage.istEnthalten(vorgangsmappe));

		
		// weiterer weg
		vorgangsmappe.setzePruefliste(new Test_Pruefliste(Regelwerkskennung
				.valueOf()) {
			public WeiteresVersandVorgehen gesamtErgebnis() {
				return WeiteresVersandVorgehen.ERNEUT_PRUEFEN;
			}
			@Override
			public boolean hatSichGeaendert() {
				return true;
			}
		});

		zwischenablage.ablegen(vorgangsmappe);
		sachbearbeiter.bearbeiteOffeneVorgaenge(vorgangsmappe2);

		assertFalse(ausgangskorb.istEnthalten(vorgangsmappe));
		mappeImAusgang = ausgangskorb.entnehmeAeltestenEingang();
		assertNotNull(mappeImAusgang);
		assertEquals(vorgangsmappe2, mappeImAusgang);
		assertFalse(zwischenablage.istEnthalten(vorgangsmappe2));
		mappeImAusgang = zwischenablage.entnehmeAeltestenEingang();
		assertNotNull(mappeImAusgang);
		assertEquals(vorgangsmappe, mappeImAusgang);
		
		vorgangsmappe.setzePruefliste(new Test_Pruefliste(Regelwerkskennung
				.valueOf()) {
			public WeiteresVersandVorgehen gesamtErgebnis() {
				return WeiteresVersandVorgehen.ERNEUT_PRUEFEN;
			}
			@Override
			public boolean hatSichGeaendert() {
				return false;
			}
		});

		zwischenablage.ablegen(vorgangsmappe);
		sachbearbeiter.bearbeiteOffeneVorgaenge(vorgangsmappe2);

		assertFalse(ausgangskorb.istEnthalten(vorgangsmappe));
		assertFalse(ausgangskorb.istEnthalten(vorgangsmappe2));
		assertFalse(zwischenablage.istEnthalten(vorgangsmappe2));
		assertTrue(zwischenablage.istEnthalten(vorgangsmappe));
	}

	@Test
	public void testachteAufTerminnotizEingaenge() throws InterruptedException {
		mocksAufIgnorierenSetzen();
		
		StandardAblagekorb<Vorgangsmappe> zwischenablage = new StandardAblagekorb<Vorgangsmappe>();
		StandardAblagekorb<Terminnotiz> notizKorb = new StandardAblagekorb<Terminnotiz>();
		StandardAblagekorb<Vorgangsmappe> ausgangskorb = new StandardAblagekorb<Vorgangsmappe>();
		final boolean[] bloedeSensingVariable = { false };
		final Terminnotiz notiz = Terminnotiz.valueOf(vorgangsmappe
				.gibMappenkennung(), Millisekunden.valueOf(100), "Fritz");
		Sachbearbeiter sachbearbeiter = new Sachbearbeiter("Fritz",
				new StandardAblagekorb<Vorgangsmappe>(), notizKorb,
				zwischenablage, new StandardAblagekorb<Terminnotiz>(),
				ausgangskorb,
				new StandardRegelwerk(Regelwerkskennung.valueOf())) {

			@Override
			protected void bearbeiteVorgangBeimSachbearbeiter(
					Ablagefaehig eingang) throws InterruptedException {
				bloedeSensingVariable[0] = true;

				assertTrue(eingang instanceof Terminnotiz);
				assertTrue(notiz == eingang);
			}
		};

		sachbearbeiter.beginneArbeit();
		notizKorb.ablegen(notiz);
		Thread.sleep(500);

		assertTrue(bloedeSensingVariable[0]);
		sachbearbeiter.beendeArbeit();

	}
}
