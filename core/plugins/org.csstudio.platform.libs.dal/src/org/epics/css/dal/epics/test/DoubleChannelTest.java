/**
 * 
 */
package org.epics.css.dal.epics.test;

import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.DynamicValueAdapter;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.DynamicValueMonitor;
import org.epics.css.dal.epics.EPICSApplicationContext;
import org.epics.css.dal.spi.DefaultPropertyFactoryService;
import org.epics.css.dal.spi.LinkPolicy;
import org.epics.css.dal.spi.PropertyFactory;

import junit.framework.TestCase;

/**
 * @author ikriznar
 *
 */
public class DoubleChannelTest extends TestCase {
	
	class MyListener extends DynamicValueAdapter {
		public int updated=0;
		public int changed=0; 
		/* (non-Javadoc)
		 * @see org.epics.css.dal.DynamicValueAdapter#valueChanged(org.epics.css.dal.DynamicValueEvent)
		 */
		@Override
		public void valueChanged(DynamicValueEvent event) {
			assertNotNull(event.getValue());
			changed++;
		}
		/* (non-Javadoc)
		 * @see org.epics.css.dal.DynamicValueAdapter#valueUpdated(org.epics.css.dal.DynamicValueEvent)
		 */
		@Override
		public void valueUpdated(DynamicValueEvent event) {
			assertNotNull(event.getValue());
			updated++;
		}
	}
	
	
	String name="PV_AI_01";
	EPICSApplicationContext ctx;
	PropertyFactory factory;
	DoubleProperty prop;
	

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		try {
			
			ctx= new EPICSApplicationContext("DoubleChannelTest");
			
			factory= DefaultPropertyFactoryService.getPropertyFactoryService().getPropertyFactory(ctx,LinkPolicy.SYNC_LINK_POLICY);

			assertNotNull(factory);
			assertEquals(LinkPolicy.SYNC_LINK_POLICY,factory.getLinkPolicy());
			assertEquals(ctx,factory.getApplicationContext());
			
			prop= factory.getProperty(name,DoubleProperty.class,null);
			
			Thread.sleep(500);
			
			assertNotNull(prop);
			assertEquals(name,prop.getName());
			assertEquals(name,prop.getUniqueName());
			assertTrue(prop.isConnected());
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		try {
			
			ctx.destroy();
						
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	public void testGetSet() {
		try {
			
			Double d= prop.getValue();
			
			assertNotNull(d);
			
			prop.setValue(d+1.0);
			
			assertTrue(d+1.0-prop.getValue()<0.00001);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	public void testMonitor() {
		try {
			MyListener l= new MyListener();
			DynamicValueMonitor m= prop.createNewMonitor(l);
			//prop.addDynamicValueListener(l);
			
			assertEquals(0,l.changed);
			assertEquals(0,l.updated);
			
			Double d= prop.getValue();
			
			assertNotNull(d);
			
			prop.setValue(d+1.0);

			assertTrue(d+1.0-prop.getValue()<0.00001);

			synchronized(l)
			{
				l.wait(30000);
			}
			assertTrue(""+l.changed,l.changed>1);
//			assertTrue(""+l.updated,l.updated>1);
			
			//prop.removeDynamicValueListener(l);
			
			prop.setValue(d+2.0);

			assertTrue(d+2.0-prop.getValue()<0.00001);

			synchronized(l)
			{
				l.wait(30000);
			}

			assertTrue(""+l.changed,l.changed>1);
//			assertTrue(""+l.updated,l.updated>1);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
}
