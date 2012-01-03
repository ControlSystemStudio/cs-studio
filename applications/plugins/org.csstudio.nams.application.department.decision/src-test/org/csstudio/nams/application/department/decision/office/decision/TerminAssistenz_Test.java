package org.csstudio.nams.application.department.decision.office.decision;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import junit.framework.Assert;

import org.csstudio.nams.common.DefaultExecutionService;
import org.csstudio.nams.common.decision.Ablagefaehig;
import org.csstudio.nams.common.decision.Eingangskorb;
import org.csstudio.nams.common.decision.StandardAblagekorb;
import org.csstudio.nams.common.decision.Vorgangsmappenkennung;
import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.testutils.AbstractTestObject;
import org.junit.Test;

public class TerminAssistenz_Test extends
		AbstractTestObject<TerminAssistenz> {

	@Test
	public void testArbeit() throws InterruptedException {
		final TerminAssistenz terminassistenz = this
				.getNewInstanceOfClassUnderTest();
		Assert.assertFalse("terminassistenz.istAmArbeiten()", terminassistenz
				.istAmArbeiten());
		terminassistenz.beginneArbeit();
		Assert.assertTrue("terminassistenz.istAmArbeiten()", terminassistenz
				.istAmArbeiten());
		terminassistenz.beendeArbeit();
		Thread.sleep(100);
		Assert.assertFalse("terminassistenz.istAmArbeiten()", terminassistenz
				.istAmArbeiten());
	}

	@Test
	public void testAssistenz() throws UnknownHostException,
			InterruptedException {
		final Eingangskorb<Terminnotiz> assistenzEingangskorb = new StandardAblagekorb<Terminnotiz>();
		final StandardAblagekorb<Ablagefaehig> sachbearbeitersKorb = new StandardAblagekorb<Ablagefaehig>();
		final Map<String, Eingangskorb<Ablagefaehig>> sachbearbeiterKoerbe = new HashMap<String, Eingangskorb<Ablagefaehig>>();
		sachbearbeiterKoerbe.put("test", sachbearbeitersKorb);
		final Timer timer = new Timer("TerminAssistenz");

		final Terminnotiz terminnotiz = Terminnotiz.valueOf(
				Vorgangsmappenkennung.valueOf(InetAddress
						.getByAddress(new byte[] { 127, 0, 0, 1 }),
						new Date(42)), Millisekunden.valueOf(42), "test");

		final TerminAssistenz assistenz = new TerminAssistenz(
				new DefaultExecutionService(), assistenzEingangskorb,
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

		final Ablagefaehig ausDemKorb = sachbearbeitersKorb
				.entnehmeAeltestenEingang();
		Assert.assertNotNull(ausDemKorb);
		Assert.assertEquals(terminnotiz, ausDemKorb);
		Assert.assertTrue(terminnotiz == ausDemKorb);

		timer.cancel();
	}

	@Override
	protected TerminAssistenz getNewInstanceOfClassUnderTest() {
		final Eingangskorb<Terminnotiz> korb = new StandardAblagekorb<Terminnotiz>();
		final Map<String, Eingangskorb<Ablagefaehig>> sachbearbeiterKoerbe = new HashMap<String, Eingangskorb<Ablagefaehig>>();
		sachbearbeiterKoerbe.put("test", new StandardAblagekorb<Ablagefaehig>());
		return new TerminAssistenz(new DefaultExecutionService(), korb,
				sachbearbeiterKoerbe, new Timer("TerminAssistenz"));
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected TerminAssistenz[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		return new TerminAssistenz[] { this.getNewInstanceOfClassUnderTest(),
				this.getNewInstanceOfClassUnderTest(),
				this.getNewInstanceOfClassUnderTest() };
	}

}
