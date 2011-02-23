/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

/**
 * 
 */
package org.epics.css.dal.epics.test;

import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.DynamicValueAdapter;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.context.ConnectionEvent;
import org.epics.css.dal.context.LinkListener;
import org.epics.css.dal.context.Linkable;
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
			
//			System.setProperty("EPICSPlug.use_jni", Boolean.toString(false));
//			System.setProperty("EPICSPlug.property.use_common_executor", Boolean.toString(true));
//			System.setProperty("EPICSPlug.property.core_threads", Integer.toString(0));
//			System.setProperty("EPICSPlug.property.max_threads", Integer.toString(2));
			
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
			/*DynamicValueMonitor m=*/ prop.createNewMonitor(l);
			//prop.addDynamicValueListener(l);
			
			assertEquals(0,l.changed);
			assertEquals(0,l.updated);
			
			Double d= prop.getValue();
			
			System.out.println("< "+d);
			
			assertNotNull(d);
			
			System.out.println("> "+(d+1.0));
			prop.setValue(d+1.0);

			assertTrue(d+1.0-prop.getValue()<0.00001);

			synchronized(l)
			{
				l.wait(3000);
			}
			assertTrue(""+l.changed,l.changed>1);
//			assertTrue(""+l.updated,l.updated>1);
			
			//prop.removeDynamicValueListener(l);
			
			System.out.println("> "+(d+2.0));
			prop.setValue(d+2.0);

			double dd= prop.getValue();
			System.out.println("< "+dd);

			assertTrue(d+2.0-dd<0.00001);

			synchronized(l)
			{
				l.wait(3000);
			}

			assertTrue(""+l.changed,l.changed>1);
//			assertTrue(""+l.updated,l.updated>1);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
}
