package de.c1wps.desy.ams.allgemeines.regelwerk;

import org.csstudio.nams.common.material.AlarmNachricht;
import org.junit.Ignore;
import org.junit.Test;

import de.c1wps.desy.ams.AbstractObject_TestCase;
import de.c1wps.desy.ams.allgemeines.Millisekunden;

public class StandardRegelwerk_Test extends
		AbstractObject_TestCase<StandardRegelwerk> {
	
	@Test
	@Ignore("Not finished yet!")
	public void _testRegelwerkMitEinerNachrichtUndTimeout() {
		AlarmNachricht dummyAlarmNachricht = new AlarmNachricht(
				"Test-Nachricht");
		Regelwerk regelwerk = new StandardRegelwerk(Regelwerkskennung.valueOf());
		// TODO Regelwerk konfigurieren!

		// Ein Regelwerk konstruieren.
		// Regelwerk regelwerk = EasyMock.createMock(Regelwerk.class);
		// EasyMock.expect(regelwerk.gibNeueLeerePruefliste()).once();

		Pruefliste pruefliste = regelwerk.gibNeueLeerePruefliste();
		assertNotNull(pruefliste);
		assertEquals(WeiteresVersandVorgehen.NOCH_NICHT_GEPRUEFT, pruefliste
				.gesamtErgebnis());

		regelwerk.pruefeNachrichtErstmalig(dummyAlarmNachricht, pruefliste);
		assertEquals(WeiteresVersandVorgehen.ERNEUT_PRUEFEN, pruefliste
				.gesamtErgebnis());
		assertEquals(Millisekunden.valueOf(2000), pruefliste
				.gibMillisekundenBisZurNaechstenPruefung());

		// 2000 ms "später"... ;)
		regelwerk.pruefeNachrichtAufTimeOuts(pruefliste, Millisekunden.valueOf(2000));

		assertEquals(WeiteresVersandVorgehen.VERSENDEN, pruefliste
				.gesamtErgebnis());
	}

	public void testRegelwerkMitBestaetigungsnachricht() {

	}
	
	@Test
	public void testGibDummyPruefliste() {
		Pruefliste dummyPruefliste = new StandardRegelwerk(Regelwerkskennung.valueOf()).gibDummyPrueflisteNichtSenden();
		assertTrue("dummyPrueflisteNichtSenden instanceof DummyPruefliste", dummyPruefliste instanceof DummyPruefliste);
		assertTrue("dummyPruefliste.gesamtErgebnis().equals(WeiteresVersandVorgehen.NICHT_VERSENDEN)", dummyPruefliste.gesamtErgebnis().equals(WeiteresVersandVorgehen.NICHT_VERSENDEN));
	}

	@Override
	protected StandardRegelwerk getNewInstanceOfClassUnderTest() {
		return new StandardRegelwerk(Regelwerkskennung.valueOf());
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected StandardRegelwerk[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		return new StandardRegelwerk[] { new StandardRegelwerk(Regelwerkskennung.valueOf()),
				new StandardRegelwerk(Regelwerkskennung.valueOf()), new StandardRegelwerk(Regelwerkskennung.valueOf()) };
	}
}
