package org.csstudio.nams.application.department.decision.office.decision;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import junit.framework.Assert;

import org.csstudio.nams.common.DefaultExecutionService;
import org.csstudio.nams.common.decision.Ablagefaehig;
import org.csstudio.nams.common.decision.Ausgangskorb;
import org.csstudio.nams.common.decision.BeobachbarerEingangskorb;
import org.csstudio.nams.common.decision.Eingangskorb;
import org.csstudio.nams.common.decision.ExecutorBeobachtbarerEingangskorb;
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
import org.csstudio.nams.common.testutils.AbstractTestObject;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.easymock.LogicalOperator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Sachbearbeiter_Test extends
		AbstractTestObject<Sachbearbeiter> {

	static class IdComparator<T> implements Comparator<T> {
		public int compare(final T expected, final T actual) {
			return expected == actual ? 0 : -1;
		}
	}

	private class Test_Pruefliste extends Pruefliste {

		Test_Pruefliste(final Regelwerkskennung regelwerkskennung) {
			super(regelwerkskennung, null);
		}

	}
	
	private class DirectExecutor implements Executor {
	     public void execute(Runnable r) {
	         r.run();
	     }
	}

	protected volatile boolean eineMappeIstfertig;
	private BeobachbarerEingangskorb<Vorgangsmappe> eingangskorb;
	private Vorgangsmappe vorgangsmappe;
	private Ausgangskorb<Vorgangsmappe> ausgangskorb;
	private Zwischenablagekorb<Vorgangsmappe> zwischenablagekorb;
	private Regelwerk regelwerk;

	private Ausgangskorb<Terminnotiz> assistenzkorb;

	private BeobachbarerEingangskorb<Terminnotiz> terminnotizEingangskorb;

	private WeiteresVersandVorgehen aktuellesGesamtErgebnisDesRegelwerk;

	private AlarmNachricht alarmNachricht;;

	@Test
	public void testachteAufTerminnotizEingaenge() throws InterruptedException {
		this.mocksAufIgnorierenSetzen();

		final ExecutorBeobachtbarerEingangskorb<Vorgangsmappe> zwischenablage = new ExecutorBeobachtbarerEingangskorb<Vorgangsmappe>(new DirectExecutor());
		final ExecutorBeobachtbarerEingangskorb<Terminnotiz> notizKorb = new ExecutorBeobachtbarerEingangskorb<Terminnotiz>(new DirectExecutor());
		final StandardAblagekorb<Vorgangsmappe> ausgangskorb = new StandardAblagekorb<Vorgangsmappe>();
		final boolean[] bloedeSensingVariable = { false };
		final Terminnotiz notiz = Terminnotiz.valueOf(this.vorgangsmappe
				.gibMappenkennung(), Millisekunden.valueOf(100), "Fritz");
		final Sachbearbeiter sachbearbeiter = new Sachbearbeiter(
				"Fritz",
				new ExecutorBeobachtbarerEingangskorb<Vorgangsmappe>(new DirectExecutor()), 
				notizKorb,
				zwischenablage, new StandardAblagekorb<Terminnotiz>(),
				ausgangskorb,
				new StandardRegelwerk(Regelwerkskennung.valueOf())) {

			@Override
			protected void bearbeiteVorgangBeimSachbearbeiter(
					Ablagefaehig eingang) throws InterruptedException {
				bloedeSensingVariable[0] = true;

				Assert.assertTrue(eingang instanceof Terminnotiz);
				Assert.assertTrue(notiz == eingang);
			}
		};

		sachbearbeiter.beginneArbeit();
		notizKorb.ablegen(notiz);
//		Thread.sleep(500);

		Assert.assertTrue(bloedeSensingVariable[0]);
		sachbearbeiter.beendeArbeit();

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
		final Sachbearbeiter sachbearbeiter = this
				.getNewInstanceOfClassUnderTest();
		Assert.assertFalse("sachbearbeiter.istAmArbeiten()", sachbearbeiter
				.istAmArbeiten());
		sachbearbeiter.beginneArbeit();
		Assert.assertTrue("sachbearbeiter.istAmArbeiten()", sachbearbeiter
				.istAmArbeiten());
		sachbearbeiter.beendeArbeit();
		Thread.sleep(100);
		Assert.assertFalse("sachbearbeiter.istAmArbeiten()", sachbearbeiter
				.istAmArbeiten());
	}

	@Test
	public void testBearbeiteNeuenVorgang() throws UnknownHostException,
			InterruptedException {
		this.mocksAufIgnorierenSetzen();

		final ExecutorBeobachtbarerEingangskorb<Vorgangsmappe> ablage = new ExecutorBeobachtbarerEingangskorb<Vorgangsmappe>(new DirectExecutor());
		final ExecutorBeobachtbarerEingangskorb<Terminnotiz> notizKorb = new ExecutorBeobachtbarerEingangskorb<Terminnotiz>(new DirectExecutor());
		final StandardAblagekorb<Vorgangsmappe> ausgangskorb = new StandardAblagekorb<Vorgangsmappe>();
		final Sachbearbeiter sachbearbeiter = new Sachbearbeiter(
				"Fritz",
				new ExecutorBeobachtbarerEingangskorb<Vorgangsmappe>(new DirectExecutor()),
				new ExecutorBeobachtbarerEingangskorb<Terminnotiz>(new DirectExecutor()), ablage, notizKorb,
				ausgangskorb,
				new StandardRegelwerk(Regelwerkskennung.valueOf()));

		final WeiteresVersandVorgehen[] gesammt = new WeiteresVersandVorgehen[] { WeiteresVersandVorgehen.ERNEUT_PRUEFEN };

		final Vorgangsmappe vorgangsmappe = new Vorgangsmappe(
				Vorgangsmappenkennung.valueOf(InetAddress
						.getByName("127.0.0.1"), new Date(12345)),
				new AlarmNachricht("Alarm")) {
			private Pruefliste pruefliste = new Test_Pruefliste(
					Regelwerkskennung.valueOf()) {
				@Override
				public WeiteresVersandVorgehen gesamtErgebnis() {
					return gesammt[0];
				}
			};

			@Override
			public Pruefliste gibPruefliste() {
				return this.pruefliste;
			}

			@Override
			public void setzePruefliste(Pruefliste pruefliste) {

			}
		};
		sachbearbeiter.bearbeiteNeuenVorgang(vorgangsmappe);
		Assert.assertEquals(vorgangsmappe, ablage.entnehmeAeltestenEingang());
		final Terminnotiz notiz = notizKorb.entnehmeAeltestenEingang();
		Assert.assertNotNull(notiz);
		Assert.assertEquals(vorgangsmappe.gibMappenkennung(), notiz
				.gibVorgangsmappenkennung());

		gesammt[0] = WeiteresVersandVorgehen.NICHT_VERSENDEN;
		sachbearbeiter.bearbeiteNeuenVorgang(vorgangsmappe);
		Assert.assertEquals(vorgangsmappe, ausgangskorb
				.entnehmeAeltestenEingang());

	}

	@Test
	public void testBearbeiteOffeneVorgaenge() throws InterruptedException,
			UnknownHostException {
		this.mocksAufIgnorierenSetzen();

		final ExecutorBeobachtbarerEingangskorb<Vorgangsmappe> zwischenablage = new ExecutorBeobachtbarerEingangskorb<Vorgangsmappe>(new DirectExecutor());
		final ExecutorBeobachtbarerEingangskorb<Terminnotiz> notizKorb = new ExecutorBeobachtbarerEingangskorb<Terminnotiz>(new DirectExecutor());
		final StandardAblagekorb<Vorgangsmappe> ausgangskorb = new StandardAblagekorb<Vorgangsmappe>();
		final Sachbearbeiter sachbearbeiter = new Sachbearbeiter(
				"Fritz",
				new ExecutorBeobachtbarerEingangskorb<Vorgangsmappe>(new DirectExecutor()),
				new ExecutorBeobachtbarerEingangskorb<Terminnotiz>(new DirectExecutor()), zwischenablage,
				notizKorb, ausgangskorb, new StandardRegelwerk(
						Regelwerkskennung.valueOf()));

		Vorgangsmappenkennung vorgangsmappenkennung = Vorgangsmappenkennung
				.valueOf(InetAddress.getByName("127.0.0.8"), new Date(6789));
		vorgangsmappenkennung = Vorgangsmappenkennung.valueOf(
				vorgangsmappenkennung, "Fritz");

		final Vorgangsmappe vorgangsmappe2 = new Vorgangsmappe(
				vorgangsmappenkennung, this.alarmNachricht);
		this.vorgangsmappe.setzePruefliste(new Test_Pruefliste(
				Regelwerkskennung.valueOf()) {
			@Override
			public WeiteresVersandVorgehen gesamtErgebnis() {
				return WeiteresVersandVorgehen.VERSENDEN;
			}

			@Override
			public boolean hatSichGeaendert() {
				return true;
			}
		});
		zwischenablage.ablegen(this.vorgangsmappe);

		sachbearbeiter.bearbeiteOffeneVorgaenge(vorgangsmappe2);

		/*
		 * Pruefe, dass uebergebene Vorgangsmappe ordentlich abgearbeitet und
		 * aus der Zwischenablage in den Ausgangskorb gelegt wurde
		 */
		Vorgangsmappe mappeImAusgang = ausgangskorb.entnehmeAeltestenEingang();
		Assert.assertNotNull(mappeImAusgang);
		Assert.assertEquals(this.vorgangsmappe, mappeImAusgang);
		mappeImAusgang = ausgangskorb.entnehmeAeltestenEingang();
		Assert.assertNotNull(mappeImAusgang);
		Assert.assertEquals(vorgangsmappe2, mappeImAusgang);
		Assert.assertFalse(zwischenablage.istEnthalten(vorgangsmappe2));
		Assert.assertFalse(zwischenablage.istEnthalten(this.vorgangsmappe));

		// weiterer weg
		this.vorgangsmappe.setzePruefliste(new Test_Pruefliste(
				Regelwerkskennung.valueOf()) {
			@Override
			public WeiteresVersandVorgehen gesamtErgebnis() {
				return WeiteresVersandVorgehen.ERNEUT_PRUEFEN;
			}

			@Override
			public boolean hatSichGeaendert() {
				return true;
			}
		});

		zwischenablage.ablegen(this.vorgangsmappe);
		sachbearbeiter.bearbeiteOffeneVorgaenge(vorgangsmappe2);

		Assert.assertFalse(ausgangskorb.istEnthalten(this.vorgangsmappe));
		mappeImAusgang = ausgangskorb.entnehmeAeltestenEingang();
		Assert.assertNotNull(mappeImAusgang);
		Assert.assertEquals(vorgangsmappe2, mappeImAusgang);
		Assert.assertFalse(zwischenablage.istEnthalten(vorgangsmappe2));
		mappeImAusgang = zwischenablage.entnehmeAeltestenEingang();
		Assert.assertNotNull(mappeImAusgang);
		Assert.assertEquals(this.vorgangsmappe, mappeImAusgang);

		this.vorgangsmappe.setzePruefliste(new Test_Pruefliste(
				Regelwerkskennung.valueOf()) {
			@Override
			public WeiteresVersandVorgehen gesamtErgebnis() {
				return WeiteresVersandVorgehen.ERNEUT_PRUEFEN;
			}

			@Override
			public boolean hatSichGeaendert() {
				return false;
			}
		});

		zwischenablage.ablegen(this.vorgangsmappe);
		sachbearbeiter.bearbeiteOffeneVorgaenge(vorgangsmappe2);

		Assert.assertFalse(ausgangskorb.istEnthalten(this.vorgangsmappe));
		Assert.assertFalse(ausgangskorb.istEnthalten(vorgangsmappe2));
		Assert.assertFalse(zwischenablage.istEnthalten(vorgangsmappe2));
		Assert.assertTrue(zwischenablage.istEnthalten(this.vorgangsmappe));
	}

	@Test
	public void testBearbeiteTerminNotiz() throws UnknownHostException,
			InterruptedException {
		this.mocksAufIgnorierenSetzen();

		final ExecutorBeobachtbarerEingangskorb<Vorgangsmappe> ablage = new ExecutorBeobachtbarerEingangskorb<Vorgangsmappe>(new DirectExecutor());
		final ExecutorBeobachtbarerEingangskorb<Terminnotiz> notizKorb = new ExecutorBeobachtbarerEingangskorb<Terminnotiz>(new DirectExecutor());
		final StandardAblagekorb<Vorgangsmappe> ausgangskorb = new StandardAblagekorb<Vorgangsmappe>();
		final Sachbearbeiter sachbearbeiter = new Sachbearbeiter(
				"Fritz",
				new ExecutorBeobachtbarerEingangskorb<Vorgangsmappe>(new DirectExecutor()),
				new ExecutorBeobachtbarerEingangskorb<Terminnotiz>(new DirectExecutor()), ablage, notizKorb,
				ausgangskorb,
				new StandardRegelwerk(Regelwerkskennung.valueOf()));

		Terminnotiz terminnotiz = Terminnotiz.valueOf(Vorgangsmappenkennung
				.valueOf(InetAddress.getByName("127.0.0.8"), new Date(6789)),
				Millisekunden.valueOf(1000), "Fritz");

		sachbearbeiter.bearbeiteTerminNotiz(terminnotiz);
		this.vorgangsmappe.setzePruefliste(new Test_Pruefliste(
				Regelwerkskennung.valueOf()));
		ablage.ablegen(this.vorgangsmappe);
		sachbearbeiter.bearbeiteTerminNotiz(terminnotiz);
		terminnotiz = Terminnotiz.valueOf(
				this.vorgangsmappe.gibMappenkennung(), Millisekunden
						.valueOf(1000), "Fritz");
		sachbearbeiter.bearbeiteTerminNotiz(terminnotiz);
		final Terminnotiz notiz = notizKorb.entnehmeAeltestenEingang();
		Assert.assertNotNull(notiz);
		Assert.assertEquals(this.vorgangsmappe.gibMappenkennung(), notiz
				.gibVorgangsmappenkennung());

		this.vorgangsmappe.setzePruefliste(new Test_Pruefliste(
				Regelwerkskennung.valueOf()) {
			@Override
			public WeiteresVersandVorgehen gesamtErgebnis() {
				return WeiteresVersandVorgehen.VERSENDEN;
			}
		});

		sachbearbeiter.bearbeiteTerminNotiz(terminnotiz);
		final Vorgangsmappe mappeImAusgang = ausgangskorb
				.entnehmeAeltestenEingang();
		Assert.assertNotNull(mappeImAusgang);
		Assert.assertEquals(this.vorgangsmappe, mappeImAusgang);
		//		
		// gesammt[0] = WeiteresVersandVorgehen.NICHT_VERSENDEN;
		// sachbearbeiter.bearbeiteNeuenVorgang(vorgangsmappe);
		// assertEquals(vorgangsmappe, ausgangskorb.entnehmeAeltestenEingang());

	}

	@Test
	public void testBearbeiteVorgang() throws InterruptedException {
		this.mocksAufIgnorierenSetzen();

		final boolean[] isMethodBearbeiteNeuenVorgangCalled = new boolean[] { false };
		final boolean[] isMethodBearbeiteOffeneVorgaengeCalled = new boolean[] { false };
		final boolean[] isMethodBearbeiteTerminNotizCalled = new boolean[] { false };
		final boolean[] returnBearbeiteOffeneVorgaenge = new boolean[] { false };

		final Sachbearbeiter bearbeiter = new Sachbearbeiter(
				"Hans",
				new ExecutorBeobachtbarerEingangskorb<Vorgangsmappe>(new DirectExecutor()),
				new ExecutorBeobachtbarerEingangskorb<Terminnotiz>(new DirectExecutor()),
				new ExecutorBeobachtbarerEingangskorb<Vorgangsmappe>(new DirectExecutor()),
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
		bearbeiter.bearbeiteVorgangBeimSachbearbeiter(this.vorgangsmappe);
		Assert.assertTrue("bearbeiteNeuenVorgang",
				isMethodBearbeiteNeuenVorgangCalled[0]);
		Assert.assertTrue("bearbeiteNeuenVorgang",
				isMethodBearbeiteOffeneVorgaengeCalled[0]);
		Assert.assertFalse("bearbeiteNeuenVorgang",
				isMethodBearbeiteTerminNotizCalled[0]);

		// Bearbeite offenen Vorgang
		isMethodBearbeiteNeuenVorgangCalled[0] = false;
		isMethodBearbeiteOffeneVorgaengeCalled[0] = false;
		isMethodBearbeiteTerminNotizCalled[0] = false;
		returnBearbeiteOffeneVorgaenge[0] = true;

		bearbeiter.bearbeiteVorgangBeimSachbearbeiter(this.vorgangsmappe);
		Assert.assertFalse("bearbeiteNeuenVorgang",
				isMethodBearbeiteNeuenVorgangCalled[0]);
		Assert.assertTrue("bearbeiteNeuenVorgang",
				isMethodBearbeiteOffeneVorgaengeCalled[0]);
		Assert.assertFalse("bearbeiteNeuenVorgang",
				isMethodBearbeiteTerminNotizCalled[0]);

		// Bearbeite Notiz
		isMethodBearbeiteNeuenVorgangCalled[0] = false;
		isMethodBearbeiteOffeneVorgaengeCalled[0] = false;
		isMethodBearbeiteTerminNotizCalled[0] = false;
		returnBearbeiteOffeneVorgaenge[0] = false;

		final Terminnotiz terminnotiz = Terminnotiz.valueOf(this.vorgangsmappe
				.gibMappenkennung(), Millisekunden.valueOf(1000), bearbeiter
				.gibName());
		bearbeiter.bearbeiteVorgangBeimSachbearbeiter(terminnotiz);
		Assert.assertFalse("bearbeiteTerminNotiz",
				isMethodBearbeiteNeuenVorgangCalled[0]);
		Assert.assertFalse("bearbeiteTerminNotiz",
				isMethodBearbeiteOffeneVorgaengeCalled[0]);
		Assert.assertTrue("bearbeiteTerminNotiz",
				isMethodBearbeiteTerminNotizCalled[0]);

		// Fehler fall
		final Ablagefaehig errorAblage = new Ablagefaehig() {
		};
		try {
			bearbeiter.bearbeiteVorgangBeimSachbearbeiter(errorAblage);
			Assert.assertFalse(true);
		} catch (final RuntimeException re) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testGibNamen() {
		this.mocksAufIgnorierenSetzen();
		final Sachbearbeiter[] sachbearbeiter = this
				.getThreeDiffrentNewInstanceOfClassUnderTest();
		Assert.assertEquals("1", sachbearbeiter[0].gibName());
		Assert.assertEquals("2", sachbearbeiter[1].gibName());
		Assert.assertEquals("3", sachbearbeiter[2].gibName());
	}

	@SuppressWarnings("unchecked")
	@Test(timeout = 4000)
	public void testVerarbeiteNachrichtDieSofortEntschiedenWerdenKann()
			throws Throwable {
		// Eingangskorb
		EasyMock.expect(this.eingangskorb.entnehmeAeltestenEingang())
				.andReturn(this.vorgangsmappe).times(1).andStubAnswer(
						new IAnswer<Vorgangsmappe>() {
							public Vorgangsmappe answer() throws Throwable {
								Sachbearbeiter_Test.this.eineMappeIstfertig = true;
								Thread.sleep(5000);
								Assert.fail("Thread sollte längst tot sein.");
								return null;
							}
						});
		EasyMock.replay(this.eingangskorb);

		// Ausgangskorb
		final Comparator<Vorgangsmappe> vorlagenMappenComparator = new IdComparator<Vorgangsmappe>();// Die
		// gleiche
		// Mappe
		// kam raus, d.h.
		// ich kann die
		// von mir refenzierte Mappe auf das Ergebnix ;)
		// prüfen.
		this.ausgangskorb.ablegen(EasyMock.cmp(this.vorgangsmappe,
				vorlagenMappenComparator, LogicalOperator.EQUAL));
		EasyMock.expectLastCall().once();
		EasyMock.replay(this.ausgangskorb);

		// Zwischenablage
		EasyMock.expect(this.zwischenablagekorb.iterator()).andReturn(
				new Iterator<Vorgangsmappe>() {
					public boolean hasNext() {
						return false;
					}

					public Vorgangsmappe next() {
						Assert.fail();
						return null;
					}

					public void remove() {
						Assert.fail();
					}
				});
		EasyMock.replay(this.zwischenablagekorb);

		// Korb der Assistenz
		EasyMock.replay(this.assistenzkorb);

		// eingangsKorb für notizen
		EasyMock
				.expect(this.terminnotizEingangskorb.entnehmeAeltestenEingang())
				.andStubAnswer(new IAnswer<Terminnotiz>() {
					public Terminnotiz answer() throws Throwable {
						Sachbearbeiter_Test.this.eineMappeIstfertig = true;
						Thread.sleep(5000);
						Assert.fail("Thread sollte längst tot sein.");
						return null;
					}
				});
		EasyMock.replay(this.terminnotizEingangskorb);

		// Regelwerk
		this.aktuellesGesamtErgebnisDesRegelwerk = WeiteresVersandVorgehen.NOCH_NICHT_GEPRUEFT;
		final Regelwerkskennung regelwerkskennungDesBenutztenRegelwerkes = Regelwerkskennung
				.valueOf();
		final Pruefliste pruefliste = new Test_Pruefliste(
				regelwerkskennungDesBenutztenRegelwerkes) {
			@Override
			public WeiteresVersandVorgehen gesamtErgebnis() {
				return Sachbearbeiter_Test.this.aktuellesGesamtErgebnisDesRegelwerk;
			}

			@Override
			public Millisekunden gibMillisekundenBisZurNaechstenPruefung() {
				Assert.fail();
				return null;
			}
		};

		EasyMock.expect(this.regelwerk.gibNeueLeerePruefliste()).andReturn(
				pruefliste);
		this.regelwerk.pruefeNachrichtErstmalig(EasyMock.cmp(
				this.alarmNachricht, new IdComparator<AlarmNachricht>(),
				LogicalOperator.EQUAL), EasyMock.cmp(pruefliste,
				new IdComparator<Pruefliste>() {
					@Override
					public int compare(final Pruefliste expected,
							final Pruefliste actual) {
						final int compareResult = super.compare(expected,
								actual);
						if (compareResult == 0) {
							// TODO aktuell wird diese Zeile nicht aufgerufen,
							// dadurch schlägt der Test fehl
							Sachbearbeiter_Test.this.aktuellesGesamtErgebnisDesRegelwerk = WeiteresVersandVorgehen.VERSENDEN;
						}
						return compareResult;
					}
				}, LogicalOperator.EQUAL));
		EasyMock.expectLastCall().once();
		EasyMock.replay(this.regelwerk);

		// Test beginnen
		final Sachbearbeiter sachbearbeiter = new Sachbearbeiter(
				"Horst Senkel",
				this.eingangskorb, this.terminnotizEingangskorb,
				this.zwischenablagekorb, this.assistenzkorb, this.ausgangskorb,
				this.regelwerk);

		sachbearbeiter.beginneArbeit();
		// Warte bis der Bearbeiter fertig sein müsste...
		for (int zaehler = 0; zaehler < 3000; zaehler += 10) {
			Thread.sleep(10);
			if (this.eineMappeIstfertig) {
				Thread.sleep(10);
				break;
			}
		}
		sachbearbeiter.beendeArbeit();

		// Ergebnisse in Mappe prüfen
		final Pruefliste prueflisteAusDerMappe = this.vorgangsmappe
				.gibPruefliste();
		Assert.assertNotNull(prueflisteAusDerMappe);
		Assert.assertEquals(WeiteresVersandVorgehen.VERSENDEN,
				prueflisteAusDerMappe.gesamtErgebnis());

		Assert.assertEquals(regelwerkskennungDesBenutztenRegelwerkes,
				prueflisteAusDerMappe.gibRegelwerkskennung());
	}

	@Override
	protected Sachbearbeiter getNewInstanceOfClassUnderTest() {
		this.mocksAufIgnorierenSetzen();

		final String name = "Horst Senkel";
		return this.erzeugeSachbearbeiter(name);
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		this.mocksAufIgnorierenSetzen();
		return new Object();
	}

	@Override
	protected Sachbearbeiter[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		this.mocksAufIgnorierenSetzen();
		return new Sachbearbeiter[] { this.erzeugeSachbearbeiter("1"),
				this.erzeugeSachbearbeiter("2"),
				this.erzeugeSachbearbeiter("3") };
	}

	@SuppressWarnings("unchecked")
	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		this.eingangskorb = EasyMock.createMock(BeobachbarerEingangskorb.class);
		this.zwischenablagekorb = EasyMock.createMock(Zwischenablagekorb.class);
		this.assistenzkorb = EasyMock.createMock(Ausgangskorb.class);
		this.terminnotizEingangskorb = EasyMock.createMock(BeobachbarerEingangskorb.class);
		this.ausgangskorb = EasyMock.createMock(Ausgangskorb.class);
		this.regelwerk = EasyMock.createMock(Regelwerk.class);

		this.eineMappeIstfertig = false;

		this.alarmNachricht = new AlarmNachricht(/*
													 * Die ist egal, weil wir
													 * hier true testen
													 */"Test-Nachricht");

		this.vorgangsmappe = new Vorgangsmappe(Vorgangsmappenkennung.valueOf(
				InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }),
				new Date(123456)), this.alarmNachricht);
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		if (this.eingangskorb != null) {
			EasyMock.verify(this.eingangskorb);
		}
		if (this.zwischenablagekorb != null) {
			EasyMock.verify(this.zwischenablagekorb);
		}
		if (this.assistenzkorb != null) {
			EasyMock.verify(this.assistenzkorb);
		}
		if (this.terminnotizEingangskorb != null) {
			EasyMock.verify(this.terminnotizEingangskorb);
		}
		if (this.ausgangskorb != null) {
			EasyMock.verify(this.ausgangskorb);
		}
		if (this.regelwerk != null) {
			EasyMock.verify(this.regelwerk);
		}
		this.vorgangsmappe = null;
		this.alarmNachricht = null;
		super.tearDown();
	}

	private Sachbearbeiter erzeugeSachbearbeiter(final String name) {
		return new Sachbearbeiter(name,
				new ExecutorBeobachtbarerEingangskorb<Vorgangsmappe>(new DirectExecutor()),
				new ExecutorBeobachtbarerEingangskorb<Terminnotiz>(new DirectExecutor()),
				new ExecutorBeobachtbarerEingangskorb<Vorgangsmappe>(new DirectExecutor()),
				new StandardAblagekorb<Terminnotiz>(),
				new StandardAblagekorb<Vorgangsmappe>(), new StandardRegelwerk(
						Regelwerkskennung.valueOf()));
	}

	private void mocksAufIgnorierenSetzen() {
		this.eingangskorb = null;
		this.zwischenablagekorb = null;
		this.terminnotizEingangskorb = null;
		this.ausgangskorb = null;
		this.regelwerk = null;
		this.assistenzkorb = null;
	}
}
