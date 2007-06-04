package org.csstudio.util.formula;

import static org.junit.Assert.*;

import org.junit.Test;

public class DoublerTest {

	@Test
	public void testTwice()
	{
		Doubler dbl = new Doubler();
		assertEquals(8.0, dbl.twice(4.0), 0.0001);
	}

}
