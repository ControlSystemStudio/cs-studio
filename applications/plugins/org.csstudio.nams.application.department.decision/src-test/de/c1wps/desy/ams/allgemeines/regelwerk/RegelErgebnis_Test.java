package de.c1wps.desy.ams.allgemeines.regelwerk;

import org.junit.Test;

import de.c1wps.desy.ams.AbstractObject_TestCase;

public class RegelErgebnis_Test extends AbstractObject_TestCase<RegelErgebnis>/*
																				 * TODO
																				 * TestCase
																				 * fuer
																				 * Enums
																				 * erstellen!
																				 */{

	@Override
	protected RegelErgebnis getNewInstanceOfClassUnderTest() {
		return RegelErgebnis.NICHT_ZUTREFFEND;
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected RegelErgebnis[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		return new RegelErgebnis[] { RegelErgebnis.NICHT_ZUTREFFEND,
				RegelErgebnis.VIELLEICHT_ZUTREFFEND, RegelErgebnis.ZUTREFFEND };
	}

	@Test
	public void testIstEntschieden()
	{
		assertTrue(RegelErgebnis.NICHT_ZUTREFFEND.istEntschieden());
		assertTrue(RegelErgebnis.ZUTREFFEND.istEntschieden());
		assertFalse(RegelErgebnis.VIELLEICHT_ZUTREFFEND.istEntschieden());
		assertFalse(RegelErgebnis.NOCH_NICHT_GEPRUEFT.istEntschieden());
	}
}
