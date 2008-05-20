package de.c1wps.desy.ams.allgemeines.regelwerk;

import org.csstudio.nams.common.material.regelwerk.Pruefliste;
import org.csstudio.nams.common.material.regelwerk.Regelwerkskennung;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen;
import org.csstudio.nams.common.testutils.AbstractObject_TestCase;

public class Pruefliste_Test extends AbstractObject_TestCase<Pruefliste> {
	public void testPruefliste() {
		Pruefliste pruefliste = new Pruefliste(Regelwerkskennung.valueOf(), null);

		assertEquals(WeiteresVersandVorgehen.NOCH_NICHT_GEPRUEFT, pruefliste
				.gesamtErgebnis());
	}

	public void testRegelwerkskennung() {
		Regelwerkskennung regelwerkskennung1 = Regelwerkskennung.valueOf();
		Regelwerkskennung regelwerkskennung2 = Regelwerkskennung.valueOf();
		Pruefliste pruefliste1 = new Pruefliste(regelwerkskennung1, null);
		Pruefliste pruefliste2 = new Pruefliste(regelwerkskennung1, null);
		Pruefliste pruefliste3 = new Pruefliste(regelwerkskennung2, null);

		assertEquals(pruefliste1.gibRegelwerkskennung(), pruefliste2
				.gibRegelwerkskennung());
		assertFalse(regelwerkskennung1.equals(regelwerkskennung2));
		assertFalse(pruefliste1.gibRegelwerkskennung().equals(
				pruefliste3.gibRegelwerkskennung()));

	}

	@Override
	protected Pruefliste getNewInstanceOfClassUnderTest() {
		return new Pruefliste(Regelwerkskennung.valueOf(), null);
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected Pruefliste[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		return new Pruefliste[] { new Pruefliste(Regelwerkskennung.valueOf(), null),
				new Pruefliste(Regelwerkskennung.valueOf(), null),
				new Pruefliste(Regelwerkskennung.valueOf(), null) };
	}
}
