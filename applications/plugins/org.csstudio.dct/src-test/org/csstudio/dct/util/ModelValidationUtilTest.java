package org.csstudio.dct.util;

import static org.junit.Assert.*;

import java.util.UUID;

import org.csstudio.dct.model.internal.Instance;
import org.csstudio.dct.model.internal.Prototype;
import org.junit.Before;
import org.junit.Test;

public class ModelValidationUtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public final void testCausesTransitiveLoop() {
		Prototype a = new Prototype("A", UUID.randomUUID());
		Instance ia = new Instance("IA", a, UUID.randomUUID());
		
		Prototype b = new Prototype("B", UUID.randomUUID());
		Instance ib = new Instance("IB", b, UUID.randomUUID());
		
		Prototype c = new Prototype("C", UUID.randomUUID());
		Instance ic = new Instance("IC", c, UUID.randomUUID());

		a.addInstance(ib);
		b.addInstance(ic);
		
		assertTrue(ModelValidationUtil.causesTransitiveLoop(ia, a));
		assertTrue(ModelValidationUtil.causesTransitiveLoop(ib, b));
		assertTrue(ModelValidationUtil.causesTransitiveLoop(ic, c));
		assertTrue(ModelValidationUtil.causesTransitiveLoop(ib, a));
		assertTrue(ModelValidationUtil.causesTransitiveLoop(ic, b));
	}

}
