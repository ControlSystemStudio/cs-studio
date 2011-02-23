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

import gov.aps.jca.jni.ThreadSafeContext;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.BitSet;
import java.util.Vector;

import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.DoubleSeqProperty;
import org.epics.css.dal.DynamicValueAdapter;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.DynamicValueMonitor;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.EnumProperty;
import org.epics.css.dal.LongProperty;
import org.epics.css.dal.LongSeqProperty;
import org.epics.css.dal.PatternProperty;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.Request;
import org.epics.css.dal.Response;
import org.epics.css.dal.ResponseEvent;
import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.StringProperty;
import org.epics.css.dal.StringSeqProperty;
import org.epics.css.dal.Timestamp;
import org.epics.css.dal.epics.EPICSApplicationContext;
import org.epics.css.dal.epics.EPICSPlug;
import org.epics.css.dal.epics.PlugUtilities;
import org.epics.css.dal.epics.PropertyProxyImpl;
import org.epics.css.dal.impl.DataAccessImpl;
import org.epics.css.dal.impl.DoublePropertyImpl;
import org.epics.css.dal.impl.DoubleSeqPropertyImpl;
import org.epics.css.dal.spi.AbstractFactory;
import org.epics.css.dal.spi.DefaultPropertyFactoryService;
import org.epics.css.dal.spi.LinkPolicy;
import org.epics.css.dal.spi.PropertyFactory;

import com.cosylab.epics.caj.CAJContext;

import junit.framework.TestCase;

/**
 * @author ikriznar
 *
 */
public class EPICSPlugTest extends TestCase {

	class ResponseListenerImpl implements ResponseListener
	{
		ResponseEvent responseEvent = null;
		
		/* (non-Javadoc)
		 * @see org.epics.css.dal.ResponseListener#responseError(org.epics.css.dal.ResponseEvent)
		 */
		public synchronized void responseError(ResponseEvent event) {
			responseEvent = event;
			notifyAll();
		}

		/* (non-Javadoc)
		 * @see org.epics.css.dal.ResponseListener#responseReceived(org.epics.css.dal.ResponseEvent)
		 */
		public synchronized void responseReceived(ResponseEvent event) {
			responseEvent = event;
			notifyAll();
		}
		
	}
	
	class DynamicValueAdapterImpl extends DynamicValueAdapter {

		public Vector<DynamicValueEvent> changeEvents = new Vector<DynamicValueEvent>();
		public Vector<DynamicValueEvent> updateEvents = new Vector<DynamicValueEvent>();
		
		/* (non-Javadoc)
		 * @see org.epics.css.dal.DynamicValueAdapter#valueChanged(org.epics.css.dal.DynamicValueEvent)
		 */
		@Override
		public synchronized void valueChanged(DynamicValueEvent event) {
			changeEvents.add(event);
			notifyAll();
		}

		/* (non-Javadoc)
		 * @see org.epics.css.dal.DynamicValueAdapter#valueUpdated(org.epics.css.dal.DynamicValueEvent)
		 */
		@Override
		public synchronized void valueUpdated(DynamicValueEvent event) {
			updateEvents.add(event);
			notifyAll();
		}
		
	}
	
	
	final String name = "PV_AI_01";
	final String nameSeq = "PV_ARR_TEST_01";

	EPICSApplicationContext ctx;
	PropertyFactory factory;

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() {
		try {
			ctx = new EPICSApplicationContext(this.getClass().getName());
			
			factory = DefaultPropertyFactoryService.getPropertyFactoryService().getPropertyFactory(ctx, LinkPolicy.SYNC_LINK_POLICY);

			assertNotNull(factory);
			assertEquals(LinkPolicy.SYNC_LINK_POLICY, factory.getLinkPolicy());
			assertEquals(ctx, factory.getApplicationContext());
			
		} catch (Throwable th) {
			th.printStackTrace();
			fail(th.toString());
		}
	}

	private <P extends DynamicValueProperty> P createProperty(Class<P> type) throws InstantiationException, RemoteException {
		String name;
		if (type.getName().indexOf("Seq") == -1)
			name = this.name;
		else
			name = this.nameSeq;
		P prop = factory.getProperty(name, type, null);
		
		assertNotNull(prop);
		assertEquals(name, prop.getName());
		assertEquals(name, prop.getUniqueName());
		assertTrue(prop.isConnected());
		
		return prop;
	}


	
	private void checkValue(Object expectedValue, Object value) {
		assertEquals(expectedValue.getClass().isArray(), value.getClass().isArray());
		if (expectedValue.getClass().isArray()) {
			final int len = Array.getLength(expectedValue);
			final int len2 = Array.getLength(value);
			assertEquals(len, len2);
			for (int i = 0; i < len; i++)
				assertEquals(Array.get(expectedValue, i), Array.get(value, i));
		}
		else
			assertEquals(expectedValue, value);
	}
	
	private <P extends DynamicValueProperty<T>, T> void setGet(P prop, T newValue) throws DataExchangeException {
		// set sync
		prop.setValue(newValue);

		// get sync
		T value = prop.getValue();
		assertNotNull(value);
		checkValue(newValue, value);
	}

	private <P extends DynamicValueProperty<T>, T> void numberSetGet(P prop, T initialValue, T newValue) throws Throwable {
		setGet(prop, initialValue);
		setGet(prop, newValue);
	}

	private <P extends DynamicValueProperty<T>, T> void asyncSetGet(P prop, T newValue) throws Throwable {
		ResponseListenerImpl listener = new ResponseListenerImpl();
		
		Timestamp before = new Timestamp();
		// set async
		Request request;
		synchronized (listener) {
			listener.responseEvent = null;
			request = prop.setAsynchronous(newValue, listener);
			listener.wait(3000);
		}
		
		ResponseEvent responseEvent = listener.responseEvent; 
		assertNotNull(responseEvent);
		assertEquals(request, responseEvent.getRequest());
		//assertEquals(prop, responseEvent.getSource());
		Response response = responseEvent.getResponse();
		assertNotNull(response);
		assertNull(response.getError());
		assertEquals(request, response.getRequest());
	//	assertEquals(null, response.getValue());
		
		Timestamp after = new Timestamp();
		assertTrue(before.getMilliseconds() <= response.getTimestamp().getMilliseconds());
		assertTrue(after.getMilliseconds() >= response.getTimestamp().getMilliseconds());

		// TODO these fail
		//assertEquals(response.getValue(), prop.getLatestReceivedValue());
		//assertEquals(response.getValue(), prop.getLatestReceivedValueAsObject());

		// TODO these fail
		//assertEquals(response.getTimestamp(), prop.getLatestValueChangeTimestamp());
		//assertEquals(response.getTimestamp(), prop.getLatestValueUpdateTimestamp());
		
		assertEquals(true, prop.getLatestSuccess());
		assertEquals(true, prop.getLatestValueSuccess());

		assertEquals(request, prop.getLatestValueRequest());
		assertEquals(request, prop.getLatestRequest());
		
		// TODO these fail 
		//assertEquals(response, prop.getLatestValueResponse());
		//assertEquals(response, prop.getLatestResponse());

		
		// get sync
		before = new Timestamp();
		synchronized (listener) {
			listener.responseEvent = null;
			request = prop.getAsynchronous(listener);
			listener.wait(3000);
		}

		responseEvent = listener.responseEvent; 
		assertNotNull(responseEvent);
		assertEquals(request, responseEvent.getRequest());
		//assertEquals(prop, responseEvent.getSource());
		response = responseEvent.getResponse();
		assertNotNull(response);
		assertNull(response.getError());
		assertEquals(request, response.getRequest());

		assertNotNull(response.getValue());
		checkValue(newValue, response.getValue());
		
		after = new Timestamp();
		assertTrue(before.getMilliseconds() <= response.getTimestamp().getMilliseconds());
		assertTrue(after.getMilliseconds() >= response.getTimestamp().getMilliseconds());

		// TODO these fail
		//assertEquals(response.getValue(), prop.getLatestReceivedValue());
		//assertEquals(response.getValue(), prop.getLatestReceivedValueAsObject());

		// TODO these fail
		//assertEquals(response.getTimestamp(), prop.getLatestValueChangeTimestamp());
		//assertEquals(response.getTimestamp(), prop.getLatestValueUpdateTimestamp());
		
		assertEquals(true, prop.getLatestSuccess());
		assertEquals(true, prop.getLatestValueSuccess());

		assertEquals(request, prop.getLatestValueRequest());
		assertEquals(request, prop.getLatestRequest());
		
		// TODO these fail 
		//assertEquals(response, prop.getLatestValueResponse());
		//assertEquals(response, prop.getLatestResponse());
	}
	
	private <P extends DynamicValueProperty<T>, T> void numberAsyncGetSet(P prop, T initialValue, T newValue) throws Throwable {
		asyncSetGet(prop, initialValue);
		asyncSetGet(prop, newValue);
	}

	private <P extends DynamicValueProperty<T>, T>  void numberTestAll(P prop, T initialValue, T newValue) throws Throwable
	{
		assertEquals(true, prop.isSettable());
		assertEquals(prop.getDataType(), initialValue.getClass());
		
		numberSetGet(prop, initialValue, newValue);
		numberAsyncGetSet(prop, initialValue, newValue);

		numberMonitorTest(prop, initialValue, newValue);
	}
	
	public void testGetSetDouble() throws Throwable {
		DoubleProperty p = createProperty(DoubleProperty.class);
		numberTestAll(p, new Double(12.32), new Double(8.12));
	}

	public void testGetSetLong() throws Throwable {
		LongProperty p = createProperty(LongProperty.class);
		numberTestAll(p, new Long(232), new Long(43));
	}
	
	public void testGetSetString() throws Throwable {
		StringProperty p = createProperty(StringProperty.class);
		// PREC is set to 0
		numberTestAll(p, "73", "187");
	}

	public void testGetSetEnum() throws Throwable {
		EnumProperty p = createProperty(EnumProperty.class);
		numberTestAll(p, new Long(1), new Long(2));
	}

	public void testGetSetPattern() throws Throwable {
		PatternProperty p = createProperty(PatternProperty.class);
		BitSet v1 = new BitSet(16);
		v1.set(0, 9, true);
		BitSet v2 = new BitSet(16);
		v2.set(5, 15, true);
		numberTestAll(p, v1, v2);
	}

	public void testGetSetDoubleSeq() throws Throwable {
		DoubleSeqProperty p = createProperty(DoubleSeqProperty.class);
		numberTestAll(p, new double[] {1.2, 2.3}, new double[] {1.2, 2.3});
	}

	public void testGetSetLongSeq() throws Throwable {
		LongSeqProperty p = createProperty(LongSeqProperty.class);
		numberTestAll(p, new long[] {32, -17}, new long[] {-56, 98});
	}
	
	public void testGetSetStringSeq() throws Throwable {
		StringSeqProperty p = createProperty(StringSeqProperty.class);
		// PREC is set to 0
		numberTestAll(p, new String[] {"15", "-172"}, new String[] {"261", "-17"});
	}

	private <P extends DynamicValueProperty<T>, T> void numberMonitorTest(P prop, T initialValue, T newValue) throws Throwable {

		DynamicValueAdapterImpl listener = new DynamicValueAdapterImpl();
	
		DynamicValueMonitor monitor;
		
		// first value should be fire immediately
		synchronized (listener)
		{
			monitor = prop.createNewMonitor(listener);
			listener.wait(3000);
		}
		
		assertEquals(1, listener.changeEvents.size());


		//Timestamp before = new Timestamp();
		// change value
		synchronized (listener)
		{
			prop.setValue(initialValue);
			listener.wait(30000);
		}
		assertEquals(2, listener.changeEvents.size());
		DynamicValueEvent event = listener.changeEvents.lastElement();
		assertNotNull(event.getValue());
		checkValue(initialValue, event.getValue());
		
		/*
		Timestamp after = new Timestamp();
		assertTrue(before.getMilliseconds() <= event.getTimestamp().getMilliseconds());
		assertTrue(after.getMilliseconds() >= event.getTimestamp().getMilliseconds());
		*/
		

		// change value
		synchronized (listener)
		{
			prop.setValue(newValue);
			listener.wait(30000);
		}
		
		assertEquals(3, listener.changeEvents.size());
		event = listener.changeEvents.lastElement();
		assertNotNull(event.getValue());
		checkValue(newValue, event.getValue());
		
		/*
		Timestamp after = new Timestamp();
		assertTrue(before.getMilliseconds() <= event.getTimestamp().getMilliseconds());
		assertTrue(after.getMilliseconds() >= event.getTimestamp().getMilliseconds());
		*/


		// test heartbeat, every 500ms
		final int TRIGGER_TIME = 500;
		monitor.setTimerTrigger(TRIGGER_TIME);
		
		// only updates are expected, one is fired immediately
		final int COUNT = 4;
		long start = System.currentTimeMillis();
		synchronized (listener)
		{
			monitor.setHeartbeat(true);
			for (int i = 0; i < COUNT; i++)
				listener.wait(1000);
			monitor.setHeartbeat(false);
		}

		final int ALLOWED_DIFF = COUNT * 25;
		long stop = System.currentTimeMillis();
		long diff = stop - start;
		assertTrue(diff >= ((COUNT-1)*TRIGGER_TIME));
		assertTrue(diff < ((COUNT-1)*TRIGGER_TIME+ALLOWED_DIFF));
		assertEquals(3, listener.changeEvents.size());
		assertEquals(COUNT, listener.updateEvents.size());

		// wait for a second
		synchronized (listener)
		{
			listener.wait(1000);
		}
		
		assertEquals(3, listener.changeEvents.size());
		assertEquals(COUNT, listener.updateEvents.size());
		
		monitor.destroy();
		
		// no events here...
		assertEquals(3, listener.changeEvents.size());
		assertEquals(COUNT, listener.updateEvents.size());
	}
	
	/*
	public void testDoubleCharacteristics() throws Throwable {
		DoubleProperty p = createProperty(DoubleProperty.class);
		String[] names = p.getCharacteristicNames();
		for (int i = 0; i < names.length; i++)
			System.out.println(names[i] + " = " + p.getCharacteristic(names[i]));
	}

	public void testEnumCharacteristics() throws Throwable {
		name = "enum";
		EnumProperty p = createProperty(EnumProperty.class);
		String[] names = p.getCharacteristicNames();
		for (int i = 0; i < names.length; i++) {
			Object val = p.getCharacteristic(names[i]);
			if (val.getClass().isArray()) {
				final int len = Array.getLength(val);
				for (int j = 0; j < len; j++)
					System.out.println(names[i] + "[" + j + "] = " + Array.get(val, j));
			}
			else
				System.out.println(names[i] + " = " + val);
		}
	}
	*/

	/*
	manual test
	public void testContextEvents() throws Throwable
	{
		createProperty(DoubleProperty.class);
		EPICSPlug.getInstance(null).addEventSystemListener(
				new EventSystemListener() {

					public void errorArrived(SystemEvent e) {
						System.out.println(e.getValue());
					}

					public void eventArrived(SystemEvent e) {
						System.out.println(e.getValue());
					}
					
				});
		Thread.sleep(1000000);
	}
	
	public void testConnectionEvents() throws Throwable
	{
		DoubleProperty dp = createProperty(DoubleProperty.class);
		dp.addLinkListener(new LinkListener() {
			public void resumed(ConnectionEvent e) {
			}

			public void suspended(ConnectionEvent e) {
				
			}

			public void connected(ConnectionEvent e) {
				System.out.println("Connected" + e);
			}

			public void disconnected(ConnectionEvent e) {
				System.out.println("Disconnected" + e);
			}

			public void connectionLost(ConnectionEvent e) {
				System.out.println("ConnectionLost" + e);
			}

			public void destroyed(ConnectionEvent e) {
			}

			public void connectionFailed(ConnectionEvent e) {
			}
		});
		Thread.sleep(100000);
	}*/
	
	private EPICSPlug getPlugForEPICSProperty(DataAccessImpl property) throws Throwable
	{
		Method method = PropertyProxyImpl.class.getMethod("getPlug", new Class[0]);
		return (EPICSPlug)method.invoke(property.getProxy(), new Object[0]);
	}
	
	public void testLifetime() throws Throwable
	{
		DoubleProperty dp = createProperty(DoubleProperty.class);
		DoubleSeqProperty dsp = createProperty(DoubleSeqProperty.class);
		EPICSPlug ep1 = getPlugForEPICSProperty((DoublePropertyImpl)dp);
		EPICSPlug ep2 = getPlugForEPICSProperty((DoubleSeqPropertyImpl)dsp);
		// should be the same since, since one factory is used
		assertTrue(ep1 == ep2);

		// test destruction
		ctx.destroy();
		ctx = null;

		// FIXME: this will open new context. Design does not allow for Plug alone to know, 
		// if it is safe to destroy CAJ context if single application context is destroyed.
		//assertTrue(((CAJContext)ep1.getContext()).isDestroyed());
	}
	
	/*
	manual test
	public void testConditions() throws Throwable
	{
		DoubleProperty prop = factory.getProperty("COUNTER", DoubleProperty.class, null);
		System.out.println("Connected");
		prop.getDefaultMonitor();
		while (true) {
			System.out.println(prop.getCondition());
			Thread.sleep(1000);
		}
	}
	*/
	
	public void testJCAContext() {
		
		System.out.println(System.getProperty("java.library.path"));
		
			EPICSApplicationContext eac= new EPICSApplicationContext("CtxTest");
			eac.getConfiguration().setProperty(AbstractFactory.SHARE_PLUG, "false");
			
			System.getProperties().remove(EPICSPlug.USE_JNI);
			
			PropertyFactory pf= DefaultPropertyFactoryService.getPropertyFactoryService().getPropertyFactory(eac, LinkPolicy.SYNC_LINK_POLICY);
	
			assertNotSame(pf.getPlug(), factory.getPlug());
			
			assertEquals(EPICSPlug.class, pf.getPlug().getClass());
			
			EPICSPlug p= (EPICSPlug)pf.getPlug();
		
			assertEquals(CAJContext.class, p.getContext().getClass());
		
		
			System.getProperties().setProperty(EPICSPlug.USE_JNI,"false");
			
			PropertyFactory pf1= DefaultPropertyFactoryService.getPropertyFactoryService().getPropertyFactory(eac, LinkPolicy.SYNC_LINK_POLICY);
	
			assertNotSame(pf1.getPlug(), pf.getPlug());
			assertNotSame(pf1.getPlug(), factory.getPlug());
			
			assertEquals(EPICSPlug.class, pf1.getPlug().getClass());
			
			p= (EPICSPlug)pf1.getPlug();
		
			assertEquals(CAJContext.class, p.getContext().getClass());

			
			System.getProperties().setProperty(EPICSPlug.USE_JNI,"true");
			
			PropertyFactory pf2= DefaultPropertyFactoryService.getPropertyFactoryService().getPropertyFactory(eac, LinkPolicy.SYNC_LINK_POLICY);
	
			assertNotSame(pf2.getPlug(), pf.getPlug());
			assertNotSame(pf2.getPlug(), pf1.getPlug());
			assertNotSame(pf2.getPlug(), factory.getPlug());
			
			assertEquals(EPICSPlug.class, pf2.getPlug().getClass());
			
			p= (EPICSPlug)pf2.getPlug();
		
			assertEquals(ThreadSafeContext.class, p.getContext().getClass());

	}
	
}
