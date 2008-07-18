package org.csstudio.nams.common.fachwert;

import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.testutils.AbstractValue_TestCase;
import org.junit.Test;

public class Millisekunden_Test extends AbstractValue_TestCase<Millisekunden> {
	@Test
	public void testValueOf() {
		// Anlegen
		Millisekunden millisekunden = Millisekunden.valueOf(2000);
		assertNotNull(millisekunden);
		Millisekunden millisekunden2 = Millisekunden.valueOf(4000);
		assertNotNull(millisekunden2);
		Millisekunden millisekunden3 = Millisekunden.valueOf(0);
		assertNotNull(millisekunden3);
		
		try {
			Millisekunden.valueOf(-42);
			fail("Anfrage eines ungueltigen Wertes muss fehlschlagen!");
		} catch(AssertionError ae) {
			// Ok, call have to fail!
		}
	}

	@Test
	public void testEquals() {
		Millisekunden millisekunden = Millisekunden.valueOf(2000);
		Millisekunden millisekunden2 = Millisekunden.valueOf(4000);

		// Gleichheit
		assertEquals(Millisekunden.valueOf(2000), millisekunden);
		assertEquals(Millisekunden.valueOf(4000), millisekunden2);
		assertFalse(millisekunden.equals(millisekunden2));
		assertFalse(millisekunden2.equals(millisekunden));
	}

	@Test
	public void testDifferenz() {
		Millisekunden millisekunden = Millisekunden.valueOf(2000);
		Millisekunden millisekunden2 = Millisekunden.valueOf(4000);

		assertEquals(Millisekunden.valueOf(2000), millisekunden
				.differenz(millisekunden2));
		assertEquals(Millisekunden.valueOf(2000), millisekunden2
				.differenz(millisekunden));
	}

	@Test
	public void testIstNull() {
		Millisekunden millisekunden = Millisekunden.valueOf(2000);
		Millisekunden millisekunden2 = Millisekunden.valueOf(4000);
		Millisekunden millisekunden3 = Millisekunden.valueOf(0);

		assertFalse(millisekunden.istNull());
		assertFalse(millisekunden2.istNull());
		assertTrue(millisekunden3.istNull());
	}

	@Test
	public void testKleinerGroesser() {
		Millisekunden millisekunden = Millisekunden.valueOf(2000);
		Millisekunden millisekunden2 = Millisekunden.valueOf(4000);

		assertTrue(millisekunden.istKleiner(millisekunden2));
		assertFalse(millisekunden.istGroesser(millisekunden2));
		assertFalse(millisekunden2.istKleiner(millisekunden));
		assertTrue(millisekunden2.istGroesser(millisekunden));

		assertFalse(millisekunden.istKleiner(millisekunden));
		assertFalse(millisekunden.istGroesser(millisekunden));
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
