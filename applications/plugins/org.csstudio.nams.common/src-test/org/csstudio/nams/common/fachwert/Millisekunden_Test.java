package org.csstudio.nams.common.fachwert;

import static org.junit.Assert.assertEquals;

import org.csstudio.nams.common.testutils.AbstractTestValue;
import org.junit.Test;

public class Millisekunden_Test extends AbstractTestValue<Millisekunden> {
	@Test
	public void testDifferenz() {
		final Millisekunden millisekunden = Millisekunden.valueOf(2000);
		final Millisekunden millisekunden2 = Millisekunden.valueOf(4000);

		assertEquals(Millisekunden.valueOf(2000), millisekunden
				.differenz(millisekunden2));
		assertEquals(Millisekunden.valueOf(2000), millisekunden2
				.differenz(millisekunden));
	}

	@Test
	public void testEquals() {
		final Millisekunden millisekunden = Millisekunden.valueOf(2000);
		final Millisekunden millisekunden2 = Millisekunden.valueOf(4000);

		// Gleichheit
		assertEquals(Millisekunden.valueOf(2000), millisekunden);
		assertEquals(Millisekunden.valueOf(4000), millisekunden2);
		assertFalse(millisekunden.equals(millisekunden2));
		assertFalse(millisekunden2.equals(millisekunden));
	}

	@Test
	public void testIstNull() {
		final Millisekunden millisekunden = Millisekunden.valueOf(2000);
		final Millisekunden millisekunden2 = Millisekunden.valueOf(4000);
		final Millisekunden millisekunden3 = Millisekunden.valueOf(0);

		assertFalse(millisekunden.istNull());
		assertFalse(millisekunden2.istNull());
		assertTrue(millisekunden3.istNull());
	}

	@Test
	public void testKleinerGroesser() {
		final Millisekunden millisekunden = Millisekunden.valueOf(2000);
		final Millisekunden millisekunden2 = Millisekunden.valueOf(4000);

		assertTrue(millisekunden.istKleiner(millisekunden2));
		assertFalse(millisekunden.istGroesser(millisekunden2));
		assertFalse(millisekunden2.istKleiner(millisekunden));
		assertTrue(millisekunden2.istGroesser(millisekunden));

		assertFalse(millisekunden.istKleiner(millisekunden));
		assertFalse(millisekunden.istGroesser(millisekunden));
	}

	@Test
	public void testValueOf() {
		// Anlegen
		final Millisekunden millisekunden = Millisekunden.valueOf(2000);
		assertNotNull(millisekunden);
		final Millisekunden millisekunden2 = Millisekunden.valueOf(4000);
		assertNotNull(millisekunden2);
		final Millisekunden millisekunden3 = Millisekunden.valueOf(0);
		assertNotNull(millisekunden3);

		try {
			Millisekunden.valueOf(-42);
			fail("Anfrage eines ungueltigen Wertes muss fehlschlagen!");
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
