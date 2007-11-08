package org.csstudio.sds.components.ui.internal.figures;

import static org.junit.Assert.*;

import org.csstudio.sds.components.ui.internal.figures.TickCalculator;
import org.junit.Before;
import org.junit.Test;


public class TickCalculatorTest {
	
	private TickCalculator t;
	
	@Before
	public void setUp() {
		t = new TickCalculator();
	}

	@Test
	public void testSimple() throws Exception {
		t.setMinimumValue(1.0);
		t.setMaximumValue(10.0);
		t.setMaximumTickCount(10);
		// range 1..10, ten tickmarks, expected: 1.0, 2.0, ..., 10.0
		assertEquals(1.0, t.getSmallestTick(), 0);
		assertEquals(1.0, t.getTickDistance(), 0);
	}
	
	@Test
	public void testSimple2() throws Exception {
		t.setMinimumValue(0.0);
		t.setMaximumValue(0.9);
		t.setMaximumTickCount(10);
		// range 0.0..0.9, expected: 0.0, 0.1, 0.2, ..., 0.9
		assertEquals(0.0, t.getSmallestTick(), 0);
		assertEquals(0.1, t.getTickDistance(), 0);
	}
	
}
