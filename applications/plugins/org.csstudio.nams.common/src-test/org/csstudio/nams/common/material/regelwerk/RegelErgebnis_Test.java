package org.csstudio.nams.common.material.regelwerk;

import junit.framework.Assert;

import org.csstudio.nams.common.testutils.AbstractObject_TestCase;
import org.junit.Test;

public class RegelErgebnis_Test extends AbstractObject_TestCase<RegelErgebnis>/*
 * TODO
 * TestCase
 * fuer
 * Enums
 * erstellen!
 */{

	@Test
	public void testIstEntschieden() {
		Assert.assertTrue(RegelErgebnis.NICHT_ZUTREFFEND.istEntschieden());
		Assert.assertTrue(RegelErgebnis.ZUTREFFEND.istEntschieden());
		Assert
				.assertFalse(RegelErgebnis.VIELLEICHT_ZUTREFFEND
						.istEntschieden());
		Assert.assertFalse(RegelErgebnis.NOCH_NICHT_GEPRUEFT.istEntschieden());
	}

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
}
