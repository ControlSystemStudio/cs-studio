package org.csstudio.nams.common.fachwert;

import junit.framework.Assert;

import org.csstudio.nams.common.testutils.AbstractValue_TestCase;
import org.junit.Test;

public class Millisekunden_Test extends AbstractValue_TestCase<Millisekunden> {
	@Test
	public void testDifferenz() {
		final Millisekunden millisekunden = Millisekunden.valueOf(2000);
		final Millisekunden millisekunden2 = Millisekunden.valueOf(4000);

		Assert.assertEquals(Millisekunden.valueOf(2000), millisekunden
				.differenz(millisekunden2));
		Assert.assertEquals(Millisekunden.valueOf(2000), millisekunden2
				.differenz(millisekunden));
	}

	@Test
	public void testEquals() {
		final Millisekunden millisekunden = Millisekunden.valueOf(2000);
		final Millisekunden millisekunden2 = Millisekunden.valueOf(4000);

		// Gleichheit
		Assert.assertEquals(Millisekunden.valueOf(2000), millisekunden);
		Assert.assertEquals(Millisekunden.valueOf(4000), millisekunden2);
		Assert.assertFalse(millisekunden.equals(millisekunden2));
		Assert.assertFalse(millisekunden2.equals(millisekunden));
	}

	@Test
	public void testIstNull() {
		final Millisekunden millisekunden = Millisekunden.valueOf(2000);
		final Millisekunden millisekunden2 = Millisekunden.valueOf(4000);
		final Millisekunden millisekunden3 = Millisekunden.valueOf(0);

		Assert.assertFalse(millisekunden.istNull());
		Assert.assertFalse(millisekunden2.istNull());
		Assert.assertTrue(millisekunden3.istNull());
	}

	@Test
	public void testKleinerGroesser() {
		final Millisekunden millisekunden = Millisekunden.valueOf(2000);
		final Millisekunden millisekunden2 = Millisekunden.valueOf(4000);

		Assert.assertTrue(millisekunden.istKleiner(millisekunden2));
		Assert.assertFalse(millisekunden.istGroesser(millisekunden2));
		Assert.assertFalse(millisekunden2.istKleiner(millisekunden));
		Assert.assertTrue(millisekunden2.istGroesser(millisekunden));

		Assert.assertFalse(millisekunden.istKleiner(millisekunden));
		Assert.assertFalse(millisekunden.istGroesser(millisekunden));
	}

	@Test
	public void testValueOf() {
		// Anlegen
		final Millisekunden millisekunden = Millisekunden.valueOf(2000);
		Assert.assertNotNull(millisekunden);
		final Millisekunden millisekunden2 = Millisekunden.valueOf(4000);
		Assert.assertNotNull(millisekunden2);
		final Millisekunden millisekunden3 = Millisekunden.valueOf(0);
		Assert.assertNotNull(millisekunden3);

		try {
			Millisekunden.valueOf(-42);
			Assert.fail("Anfrage eines ungueltigen Wertes muss fehlschlagen!");
		} catch (final AssertionError ae) {
			// Ok, call have to fail!
		}
	}

	@Override
	protected Millisekunden doGetAValueOfTypeUnderTest() {
		return Millisekunden.valueOf(42);
	}

	@Override
	protected Millisekunden[] doGetDifferentInstancesOfTypeUnderTest() {
		return new Millisekunden[] { Millisekunden.valueOf(42),
				Millisekunden.valueOf(23), Millisekunden.valueOf(1024) };
	}
}
