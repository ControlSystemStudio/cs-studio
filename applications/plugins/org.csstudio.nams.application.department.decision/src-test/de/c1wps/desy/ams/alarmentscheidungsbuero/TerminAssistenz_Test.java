package de.c1wps.desy.ams.alarmentscheidungsbuero;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import org.junit.Test;

import de.c1wps.desy.ams.AbstractObject_TestCase;
import de.c1wps.desy.ams.allgemeines.Eingangskorb;
import de.c1wps.desy.ams.allgemeines.Millisekunden;
import de.c1wps.desy.ams.allgemeines.StandardAblagekorb;
import de.c1wps.desy.ams.allgemeines.Vorgangsmappenkennung;

public class TerminAssistenz_Test extends
		AbstractObject_TestCase<TerminAssistenz> {

	@Override
	protected TerminAssistenz getNewInstanceOfClassUnderTest() {
		Eingangskorb<Terminnotiz> korb = new StandardAblagekorb<Terminnotiz>();
		Map<String, Eingangskorb<Terminnotiz>> sachbearbeiterKoerbe = new HashMap<String, Eingangskorb<Terminnotiz>>();
		sachbearbeiterKoerbe.put("test", new StandardAblagekorb<Terminnotiz>());
		return new TerminAssistenz(korb, sachbearbeiterKoerbe, new Timer(
				"TerminAssistenz"));
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected TerminAssistenz[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		return new TerminAssistenz[] { getNewInstanceOfClassUnderTest(),
				getNewInstanceOfClassUnderTest(),
				getNewInstanceOfClassUnderTest() };
	}

	@Test
	public void testAssistenz() throws UnknownHostException, InterruptedException {
		Eingangskorb<Terminnotiz> assistenzEingangskorb = new StandardAblagekorb<Terminnotiz>();
		StandardAblagekorb<Terminnotiz> sachbearbeitersKorb = new StandardAblagekorb<Terminnotiz>();
		Map<String, Eingangskorb<Terminnotiz>> sachbearbeiterKoerbe = new HashMap<String, Eingangskorb<Terminnotiz>>();
		sachbearbeiterKoerbe.put("test", sachbearbeitersKorb);
		Timer timer = new Timer("TerminAssistenz");

		final Terminnotiz terminnotiz = Terminnotiz.valueOf(
				Vorgangsmappenkennung.valueOf(InetAddress
						.getByAddress(new byte[] { 127, 0, 0, 1 }),
						new Date(42)), Millisekunden.valueOf(42), "test");

		TerminAssistenz assistenz = new TerminAssistenz(assistenzEingangskorb,
				sachbearbeiterKoerbe, timer);

		assistenz.beginneArbeit();

		assistenzEingangskorb.ablegen(terminnotiz);

		// Warte bis der Bearbeiter fertig sein müsste...
		for (int zaehler = 0; zaehler < 3000; zaehler += 10) {
			if (sachbearbeitersKorb.istEnthalten(terminnotiz)) {
				break;
			}
			Thread.sleep(10);
		}

		assistenz.beendeArbeit();

		Terminnotiz ausDemKorb = sachbearbeitersKorb.entnehmeAeltestenEingang();
		assertNotNull(ausDemKorb);
		assertEquals(terminnotiz, ausDemKorb);
		assertTrue(terminnotiz == ausDemKorb);

		timer.cancel();
	}
	
	@Test
	public void testArbeit() throws InterruptedException {
		TerminAssistenz terminassistenz = this.getNewInstanceOfClassUnderTest();
		assertFalse("terminassistenz.istAmArbeiten()", terminassistenz.istAmArbeiten());
		terminassistenz.beginneArbeit();
		assertTrue("terminassistenz.istAmArbeiten()", terminassistenz.istAmArbeiten());
		terminassistenz.beendeArbeit();
		Thread.sleep(100);
		assertFalse("terminassistenz.istAmArbeiten()", terminassistenz.istAmArbeiten());
	}

}
