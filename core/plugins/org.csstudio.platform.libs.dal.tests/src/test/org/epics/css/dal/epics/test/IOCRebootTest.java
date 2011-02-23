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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.DynamicValueAdapter;
import org.epics.css.dal.DynamicValueCondition;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.context.ConnectionEvent;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.context.LinkListener;
import org.epics.css.dal.epics.EPICSApplicationContext;
import org.epics.css.dal.spi.DefaultPropertyFactoryService;
import org.epics.css.dal.spi.LinkPolicy;
import org.epics.css.dal.spi.PropertyFactory;

/**
 * @author ikriznar
 *
 */
public class IOCRebootTest extends TestCase {
	
	
	private static boolean printout=false;
	
	
	class MyListener extends DynamicValueAdapter implements PropertyChangeListener, LinkListener {
		public int updated=0;
		public int changed=0; 
		public int connected=0; 
		public int lost=0;
		public Set<String> characteristics = new HashSet<String>(32); 
		public DynamicValueCondition condition;
		/* (non-Javadoc)
		 * @see org.epics.css.dal.DynamicValueAdapter#valueChanged(org.epics.css.dal.DynamicValueEvent)
		 */
		@Override
		public synchronized void valueChanged(DynamicValueEvent event) {
			if (printout) System.out.println("CHANGE");
			assertNotNull(event.getValue());
			changed++;
			notifyAll();
		}
		/* (non-Javadoc)
		 * @see org.epics.css.dal.DynamicValueAdapter#valueUpdated(org.epics.css.dal.DynamicValueEvent)
		 */
		@Override
		public synchronized void valueUpdated(DynamicValueEvent event) {
			if (printout) System.out.println("UPDATE");
			assertNotNull(event.getValue());
			updated++;
			notifyAll();
		}
		public synchronized void propertyChange(PropertyChangeEvent evt) {
			if (printout) System.out.println(evt.getPropertyName()+" "+evt.getNewValue());
			characteristics.add(evt.getPropertyName());
			notifyAll();
		}
		
		@Override
		public synchronized void conditionChange(DynamicValueEvent event) {
			if (printout) System.out.println("CONDITION "+event.getCondition());
			condition= event.getCondition();
			notifyAll();
		}
		public synchronized void connected(ConnectionEvent arg0) {
			if (printout) System.out.println("CONNECTED");
			connected++;
			notifyAll();
		}
		public synchronized void operational(ConnectionEvent arg0) {
			if (printout) System.out.println("OPERATIONAL");
			notifyAll();
		}
		public synchronized void connectionLost(ConnectionEvent arg0) {
			if (printout) System.out.println("LOST");
			lost++;
			notifyAll();
		}
		public void suspended(ConnectionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		public void connectionFailed(ConnectionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		public void destroyed(ConnectionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		public void disconnected(ConnectionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		public void resumed(ConnectionEvent arg0) {
			// TODO Auto-generated method stub
			
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
	
	public void testIOCReboot() {
		
		System.out.println("!!! This is IOC reboot test, it requires your cooperation. Follow the instructions !!!");
		try {
			MyListener l= new MyListener();
			
			prop.addPropertyChangeListener(l);
			prop.addDynamicValueListener(l);
			prop.addLinkListener(l);
			
			assertEquals(0,l.changed);
			assertEquals(0,l.updated);
			assertEquals(0,l.connected);
			assertEquals(0,l.lost);
			
			Double d= prop.getValue();
			
			assertNotNull(d);
			
			prop.setValue(d+1.0);

			assertTrue(d+1.0-prop.getValue()<0.00001);

			synchronized(l)
			{
				l.wait(1000);
			}
			assertTrue(""+l.changed,l.changed>=1);
			
			synchronized(l)
			{
				l.wait(1000);
			}
			
			/*Object obj= prop.getCharacteristic("fieldType");
			assertNotNull(obj);*/
		
			System.out.println("!!! Stop the test IOC NOW !!!");

			synchronized(l)
			{
				l.wait(60000);
			}
			
			Thread.sleep(1000);
			
			assertEquals(1,l.lost);
			assertEquals(0,l.connected);
			assertNotNull(l.condition);
			assertTrue(l.condition.isLinkNotAvailable());
			assertEquals(ConnectionState.CONNECTION_LOST,prop.getConnectionState());
			
			l.condition=null;
			l.characteristics.clear();

			System.out.println("!!! Start the test IOC NOW !!!");

			synchronized(l)
			{
				l.wait(60000);
			}
			
			Thread.sleep(1000);
			
			assertEquals(1,l.lost);
			assertEquals(1,l.connected);
			assertNotNull(l.condition);
			assertTrue(l.condition.isNormal());
			assertEquals(ConnectionState.CONNECTED,prop.getConnectionState());
			assertTrue(l.characteristics.size()>0);

			System.out.println("!!! Thank you, test was passed. !!!");
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
}
