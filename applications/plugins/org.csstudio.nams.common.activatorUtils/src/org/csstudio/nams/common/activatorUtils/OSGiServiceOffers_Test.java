package org.csstudio.nams.common.activatorUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OSGiServiceOffers_Test {

	static class MyService {};
	static class MySecondService {};
	
	@Test
	public void testNormalBehaviour() {
		OSGiServiceOffers offers = new OSGiServiceOffers();
		
		// at beginning
		assertTrue(offers.isEmpty());
		
		assertTrue(offers.keySet().isEmpty());
		assertFalse(offers.keySet().iterator().hasNext());
		
		assertTrue(offers.values().isEmpty());
		assertFalse(offers.values().iterator().hasNext());
		
		// add a offer:
		offers.put(MyService.class, new MyService());
		
		// contents...
		assertFalse(offers.isEmpty());
		
		assertFalse(offers.keySet().isEmpty());
		assertTrue(offers.keySet().iterator().hasNext());
		
		assertFalse(offers.values().isEmpty());
		assertTrue(offers.values().iterator().hasNext());
		
		assertEquals(1, offers.size());
		
		// add a offer:
		offers.put(MySecondService.class, new MySecondService());
		
		// contents...
		assertFalse(offers.isEmpty());
		
		assertFalse(offers.keySet().isEmpty());
		assertTrue(offers.keySet().iterator().hasNext());
		
		assertFalse(offers.values().isEmpty());
		assertTrue(offers.values().iterator().hasNext());
		
		assertEquals(2, offers.size());
	}

}
