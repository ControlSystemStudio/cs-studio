package org.epics.css.dal.epics.test;

import junit.framework.TestCase;

import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.DynamicValueAdapter;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.epics.EPICSApplicationContext;
import org.epics.css.dal.spi.DefaultPropertyFactoryService;
import org.epics.css.dal.spi.LinkPolicy;
import org.epics.css.dal.spi.PropertyFactory;

public class MultipleDoubleChannelTest extends TestCase {

	private static final int PROPERTY_COUNT = 150;

	private static final int EXPECTED_EVENTS_PER_SECOND = 10;

	class MyListener extends DynamicValueAdapter {
		public int updated = 0;

		public int changed = 0;

		@Override
		public void valueChanged(DynamicValueEvent event) {
			assertNotNull(event.getValue());
			changed++;
		}

		@Override
		public void valueUpdated(DynamicValueEvent event) {
			assertNotNull(event.getValue());
			updated++;
		}
	}

	public void testMonitor() {
		EPICSApplicationContext ctx = new EPICSApplicationContext(
				"DoubleChannelTest");

		PropertyFactory factory = DefaultPropertyFactoryService
				.getPropertyFactoryService().getPropertyFactory(ctx,
						LinkPolicy.SYNC_LINK_POLICY);

		assertNotNull(factory);
		assertEquals(LinkPolicy.SYNC_LINK_POLICY, factory.getLinkPolicy());
		assertEquals(ctx, factory.getApplicationContext());

		try {
			MyListener l = new MyListener();
			assertEquals(0, l.changed);
			assertEquals(0, l.updated);

			for (int i = 0; i < PROPERTY_COUNT; i++) {
				DoubleProperty prop = factory.getProperty("c1wps:RAMP_rnd" + i,
						DoubleProperty.class, null);

				prop.addDynamicValueListener(l);

				Double d = prop.getValue();

				assertNotNull(d);
			}

			synchronized (l) {
				l.wait(1000);
			}

			assertTrue(l.changed > (PROPERTY_COUNT * EXPECTED_EVENTS_PER_SECOND));

		} catch (Exception e) {
			fail(e.toString());
		}

		ctx.destroy();
	}

}
