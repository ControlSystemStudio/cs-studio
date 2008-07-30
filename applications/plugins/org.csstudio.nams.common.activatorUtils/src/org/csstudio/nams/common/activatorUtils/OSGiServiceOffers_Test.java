package org.csstudio.nams.common.activatorUtils;

import org.junit.Assert;
import org.junit.Test;

public class OSGiServiceOffers_Test {

	static class MySecondService {
	};

	static class MyService {
	};

	@Test
	public void testNormalBehaviour() {
		final OSGiServiceOffers offers = new OSGiServiceOffers();

		// at beginning
		Assert.assertTrue(offers.isEmpty());

		Assert.assertTrue(offers.keySet().isEmpty());
		Assert.assertFalse(offers.keySet().iterator().hasNext());

		Assert.assertTrue(offers.values().isEmpty());
		Assert.assertFalse(offers.values().iterator().hasNext());

		// add a offer:
		offers.put(MyService.class, new MyService());

		// contents...
		Assert.assertFalse(offers.isEmpty());

		Assert.assertFalse(offers.keySet().isEmpty());
		Assert.assertTrue(offers.keySet().iterator().hasNext());

		Assert.assertFalse(offers.values().isEmpty());
		Assert.assertTrue(offers.values().iterator().hasNext());

		Assert.assertEquals(1, offers.size());

		// add a offer:
		offers.put(MySecondService.class, new MySecondService());

		// contents...
		Assert.assertFalse(offers.isEmpty());

		Assert.assertFalse(offers.keySet().isEmpty());
		Assert.assertTrue(offers.keySet().iterator().hasNext());

		Assert.assertFalse(offers.values().isEmpty());
		Assert.assertTrue(offers.values().iterator().hasNext());

		Assert.assertEquals(2, offers.size());
	}

}
