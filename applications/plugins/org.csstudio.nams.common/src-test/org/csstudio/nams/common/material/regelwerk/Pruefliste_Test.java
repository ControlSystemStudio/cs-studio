package org.csstudio.nams.common.material.regelwerk;

import junit.framework.Assert;

import org.csstudio.nams.common.material.Regelwerkskennung;
import org.csstudio.nams.common.testutils.AbstractObject_TestCase;

public class Pruefliste_Test extends AbstractObject_TestCase<Pruefliste> {
	public void testPruefliste() {
		final Pruefliste pruefliste = new Pruefliste(Regelwerkskennung
				.valueOf(), null);

		Assert.assertEquals(WeiteresVersandVorgehen.NOCH_NICHT_GEPRUEFT,
				pruefliste.gesamtErgebnis());
	}

	public void testRegelwerkskennung() {
		final Regelwerkskennung regelwerkskennung1 = Regelwerkskennung
				.valueOf();
		final Regelwerkskennung regelwerkskennung2 = Regelwerkskennung
				.valueOf();
		final Pruefliste pruefliste1 = new Pruefliste(regelwerkskennung1, null);
		final Pruefliste pruefliste2 = new Pruefliste(regelwerkskennung1, null);
		final Pruefliste pruefliste3 = new Pruefliste(regelwerkskennung2, null);

		Assert.assertEquals(pruefliste1.gibRegelwerkskennung(), pruefliste2
				.gibRegelwerkskennung());
		Assert.assertFalse(regelwerkskennung1.equals(regelwerkskennung2));
		Assert.assertFalse(pruefliste1.gibRegelwerkskennung().equals(
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
		return new Pruefliste[] {
				new Pruefliste(Regelwerkskennung.valueOf(), null),
				new Pruefliste(Regelwerkskennung.valueOf(), null),
				new Pruefliste(Regelwerkskennung.valueOf(), null) };
	}
}
