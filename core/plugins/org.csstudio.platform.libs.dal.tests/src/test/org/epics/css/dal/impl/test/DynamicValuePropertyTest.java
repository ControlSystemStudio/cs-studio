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

package org.epics.css.dal.impl.test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.epics.css.dal.CharacteristicInfo;
import org.epics.css.dal.DataAccess;
import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DynamicValueCondition;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.DynamicValueMonitor;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.DynamicValueState;
import org.epics.css.dal.IllegalViewException;
import org.epics.css.dal.PropertyCharacteristics;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.Request;
import org.epics.css.dal.Response;
import org.epics.css.dal.ResponseEvent;
import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.Timestamp;
import org.epics.css.dal.context.AbstractApplicationContext;
import org.epics.css.dal.context.ConnectionEvent;
import org.epics.css.dal.context.ConnectionListener;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.context.Identifier;
import org.epics.css.dal.context.LinkListener;
import org.epics.css.dal.context.PropertyFamily;
import org.epics.css.dal.proxy.ConnectionStateMachine;
import org.epics.css.dal.simple.AnyDataChannel;
import org.epics.css.dal.simple.ChannelListener;
import org.epics.css.dal.simulation.PropertyProxyImpl;
import org.epics.css.dal.simulation.SimulatorPlug;


public abstract class DynamicValuePropertyTest extends TestCase
{
	public class LifecycleListener implements ConnectionListener, ChannelListener, DynamicValueListener {

		List events= new ArrayList();
		private DynamicValueCondition lastCondition;
		private ConnectionState lastConnectionState;
		
		
		public void checkConsistancy(ConnectionEvent e) {
			
			assertSame(e.getConnectable().getConnectionState(), e.getState());
			
			if (lastConnectionState!=null) {
				assertNotSame(lastConnectionState, e.getState());
				assertTrue(lastConnectionState+" -> "+e.getState()+" not allowed",ConnectionStateMachine.isTransitionAllowed(lastConnectionState, e.getState()));
			}
			
			lastConnectionState= e.getState();
			
			if (e.getState()==ConnectionState.OPERATIONAL) {
				DynamicValueProperty<?> p= (DynamicValueProperty<?>)e.getConnectable();
				assertTrue(p.getCondition().containsAllStates(DynamicValueState.HAS_LIVE_DATA, DynamicValueState.HAS_METADATA));
			}
			
		}
		
		public void resumed(ConnectionEvent e) {
			events.add(e);
			System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName()+" "+e);
			
			assertFalse(e.getConnectable().isSuspended());
		}

		public void suspended(ConnectionEvent e) {
			events.add(e);
			System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName()+" "+e);
			
			assertTrue(e.getConnectable().isSuspended());

		}

		public void connected(ConnectionEvent e) {
			events.add(e);
			System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName()+" "+e);
			
			assertEquals(ConnectionState.CONNECTED, e.getState());
			checkConsistancy(e);
		}

		public void operational(ConnectionEvent e) {
			events.add(e);
			System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName()+" "+e);
			
			assertEquals(ConnectionState.OPERATIONAL, e.getState());
			checkConsistancy(e);

		}

		public void disconnected(ConnectionEvent e) {
			events.add(e);
			System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName()+" "+e);
			
			assertEquals(ConnectionState.DISCONNECTED, e.getState());

			if (lastConnectionState!=null && lastConnectionState.isConnected()) {
				lastConnectionState=ConnectionState.DISCONNECTING;
			}

			checkConsistancy(e);

		}

		public void connectionLost(ConnectionEvent e) {
			events.add(e);
			System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName()+" "+e);
			
			assertEquals(ConnectionState.CONNECTION_LOST, e.getState());
			checkConsistancy(e);

		}

		public void destroyed(ConnectionEvent e) {
			events.add(e);
			System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName()+" "+e);
			
			assertEquals(ConnectionState.DESTROYED, e.getState());
			checkConsistancy(e);
			
		}

		public void connectionFailed(ConnectionEvent e) {
			events.add(e);
			System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName()+" "+e);
			
			assertEquals(ConnectionState.CONNECTION_FAILED, e.getState());
			checkConsistancy(e);

		}

		public void valueUpdated(DynamicValueEvent event) {
			events.add(event);
			System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName()+" "+event);
		}

		public void valueChanged(DynamicValueEvent event) {
			events.add(event);
			System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName()+" "+event);
		}

		public void timeoutStarts(DynamicValueEvent event) {
			events.add(event);
			System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName()+" "+event);
		}

		public void timeoutStops(DynamicValueEvent event) {
			events.add(event);
			System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName()+" "+event);
		}

		public void timelagStarts(DynamicValueEvent event) {
			events.add(event);
			System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName()+" "+event);
		}

		public void timelagStops(DynamicValueEvent event) {
			events.add(event);
			System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName()+" "+event);
		}

		public void errorResponse(DynamicValueEvent event) {
			events.add(event);
			System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName()+" "+event);
		}

		public void conditionChange(DynamicValueEvent event) {
			events.add(event);
			System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName()+" "+event);
			
			DynamicValueCondition cond= event.getProperty().getCondition();
			assertTrue(cond.getTimestamp().compareTo(event.getCondition().getTimestamp())>=0);
			
			if (cond.getTimestamp().compareTo(event.getCondition().getTimestamp())==0) {
				assertSame(event.getProperty().getCondition(), event.getCondition());
			}
			
			if (lastCondition!=null) {
				assertNotSame(lastCondition, event.getCondition());
				assertFalse(DynamicValueState.areSetsEqual(lastCondition.getStates(), event.getCondition().getStates()));
			}
			
			lastCondition= event.getCondition();
		}

		public void channelDataUpdate(AnyDataChannel channel) {
			events.add(channel);
			System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName()+" "+channel.getData().stringValue());
		}

		public void channelStateUpdate(AnyDataChannel channel) {
			events.add(channel);
			System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName()+" "+channel.getStateInfo());
		}

		public void connecting(ConnectionEvent e) {
			events.add(e);
			System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName()+" "+e);

			assertEquals(ConnectionState.CONNECTING, e.getState());
			checkConsistancy(e);

		}

		public void disconnecting(ConnectionEvent e) {
			events.add(e);
			System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName()+" "+e);
			
			assertEquals(ConnectionState.DISCONNECTING, e.getState());
			checkConsistancy(e);

		}

		public void initialState(ConnectionEvent e) {
			events.add(e);
			System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName()+" "+e);
			
			assertEquals(ConnectionState.INITIAL, e.getState());
			checkConsistancy(e);

		}

		public void ready(ConnectionEvent e) {
			events.add(e);
			System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName()+" "+e);
			
			assertEquals(ConnectionState.READY, e.getState());
			checkConsistancy(e);

		}

	}
	
	public class ResponseListenerImpl implements ResponseListener
	{
		/// Note that reponses is not synchronized
		ArrayList<ResponseEvent> responses = new ArrayList<ResponseEvent>();
		int nextCount=0;
		private DynamicValueProperty<?> property;
		private Response<?> lastPropertyResponse;
		
		public ResponseListenerImpl() {
			
		}
		
		public ResponseListenerImpl(DynamicValueProperty<?> prop) {
			this.property=prop;
		}
		

		public int getNextCount() {
			return nextCount;
		}
		
		public synchronized void responseError(ResponseEvent event)
		{
			if (property!=null) {
				lastPropertyResponse= property.getLatestResponse();
			}
			responses.add(event);
			notifyAll();
		}

		public synchronized void responseReceived(ResponseEvent event)
		{
			if (property!=null) {
				lastPropertyResponse= property.getLatestResponse();
			}
			responses.add(event);
			notifyAll();
		}
		
		public Response<?> getLastPropertyResponse() {
			return lastPropertyResponse;
		}

		public synchronized int getResponseCount()
		{
			return responses.size();
		}

		/// Note that reponses is not synchronized
		public synchronized ArrayList<ResponseEvent> getResponses()
		{
			return responses;
		}
		
		public synchronized ResponseEvent getLastResponse()
		{
			return responses.get(responses.size()-1);
		}
		
		
		public synchronized void waitForFirst(long timeout) {
			if (nextCount>0) {
				return;
			}
			try {
				wait(timeout);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		public synchronized void reset() {
			nextCount=0;
		}
		public synchronized boolean isDone() {
			if (responses.size()==0) {
				return false;
			}
			return responses.get(responses.size()-1).isLast();
		}
		public synchronized void waitForLast(long timeout) {
			long time= System.currentTimeMillis();
			while (!isDone() && (System.currentTimeMillis()-time)<timeout+10) {
				try {
					wait(timeout);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Class<? extends DynamicValueProperty<?>> propertyType;
	
	
	public DynamicValuePropertyTest(Class<? extends DynamicValueProperty<?>> propertyType) {
		this.propertyType= propertyType;
	}

	protected abstract DynamicValueProperty getProperty(LinkListener l);
	protected abstract DynamicValueProperty getProperty();

	protected abstract String getPropertyUniqueName();

	protected abstract Object getRandomValue();

	protected abstract AbstractApplicationContext getContext();

	protected abstract Object getExpectedCharacteristicValue(
	    String characteristicName) throws Exception;

	public abstract boolean matchValue(Object expected, Object got);
	
	protected int getPropertyChangeUpdatesNumber() {
		// By default the value is 0 and it can be overriden for plug specific needs.
		return 0;
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.DataAccessImpl.getDataType()'
	 */
	public abstract void testGetDataType();

	/*
	 * Test the test.
	 */
	public void testPropertyTestImpl() {

		String un= getPropertyUniqueName();
		DynamicValueProperty p= getProperty();
		AbstractApplicationContext ctx= getContext();
		
		assertNotNull(ctx);
		assertNotNull(p);
		assertNotNull(un);
		
		assertEquals(ctx, getContext());
		assertEquals(p, getProperty());
		assertEquals(un, getPropertyUniqueName());

		assertEquals(ctx, getContext());
		assertEquals(un, getPropertyUniqueName());
		assertEquals(p, getProperty());
		
		assertTrue(propertyType.isAssignableFrom(p.getClass()));

		PropertyFamily pf= (PropertyFamily)p.getParentContext();
		
		assertNotNull(pf);
		assertEquals(pf.getApplicationContext(), ctx);
		
	}

	
	/*
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.getIdentifier()'
	 */
	public void testGetIdentifier()
	{
		Identifier i = getProperty().getIdentifier();
		assertNotNull(i);
		assertEquals(getPropertyUniqueName(), i.getName());
		assertEquals(getPropertyUniqueName(), i.getUniqueName());
		assertEquals(getPropertyUniqueName(), i.getLongQualifiedName());
		assertEquals(Identifier.Type.PROPERTY, i.getType());
		
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.isDebug()'
	 */
	public void testIsDebug()
	{
		assertTrue(!getProperty().isDebug());

		// TODO wrong test
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.getParentContext()'
	 */
	public void testGetParentContext()
	{
		DynamicValueProperty prop = getProperty();
		assertNotNull(prop.getParentContext());
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.addResponseListener(ResponseListener)'
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.removeResponseListener(ResponseListener)'
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.getResponseListeners()'
	 */
	public void testResponseListeners()
		throws DataExchangeException, InterruptedException
	{
		DynamicValueProperty prop = getProperty();
		ResponseListenerImpl listener = new ResponseListenerImpl();
		ResponseListenerImpl listener1 = new ResponseListenerImpl();
		int nListeners = prop.getResponseListeners().length;

		synchronized (listener) {
			prop.addResponseListener(listener);
			prop.getAsynchronous(listener1);
			listener.waitForFirst(1000);
			
			assertEquals(listener.getResponseCount(),listener1.getResponseCount());
			assertTrue(listener.getResponseCount()>0);
			int lastResponseIdx =listener.getResponseCount()-1; 
			assertEquals(listener.getResponses().get(lastResponseIdx),listener1.getResponses().get(lastResponseIdx));
			ResponseListener[] list = prop.getResponseListeners();
			assertEquals(list.length, nListeners + 1);
			assertEquals(list[nListeners], listener);

			listener.reset();
			prop.setAsynchronous(getRandomValue(), listener1);
			listener.waitForFirst(1000);
			assertEquals(listener.getResponseCount(),listener1.getResponseCount());
			assertTrue(listener.getResponseCount()>(lastResponseIdx+1));
			lastResponseIdx =listener.getResponseCount()-1; 
			assertEquals(listener.getResponses().get(lastResponseIdx),listener1.getResponses().get(lastResponseIdx));
			
			listener.reset();
			Request request = prop.getCharacteristicAsynchronously(PropertyCharacteristics.C_DESCRIPTION);
			listener.waitForFirst(1000);
			assertTrue(listener.getResponseCount() > 0);

			ResponseEvent rEvent = listener.getResponses()
				.get(listener.getResponseCount() - 1);
			assertEquals(request, rEvent.getRequest());

			prop.removeResponseListener(listener);

			int update = listener.getResponseCount();

			listener.reset();
			prop.getAsynchronous();
			listener.waitForFirst(1000);

			list = prop.getResponseListeners();
			assertEquals(list.length, nListeners);
			assertEquals(listener.getResponseCount(), update);
		}
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.getLatestRequest()'
	 */
	public void testGetLatestRequest() throws DataExchangeException
	{
		DynamicValueProperty prop = getProperty();
		Request request = prop.getAsynchronous();
		assertEquals(request, prop.getLatestRequest());
		assertEquals(request, prop.getLatestValueRequest());
		prop.getCharacteristicAsynchronously(PropertyCharacteristics.C_DISPLAY_NAME);
		assertNotSame(request, prop.getLatestRequest());
		assertEquals(request, prop.getLatestValueRequest());
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.getLatestValueResponse()'
	 */
	public void testGetLatestValueResponse()
		throws DataExchangeException, InterruptedException
	{
		DynamicValueProperty prop = getProperty();
		prop.getDefaultMonitor().setHeartbeat(true);

		Object value = prop.getValue();
		ResponseListenerImpl rpListener = new ResponseListenerImpl();
		prop.setValue(value);

		//synchronized (rpListener) {
			Request request = prop.getAsynchronous(rpListener);
			rpListener.waitForFirst(2000); // wait for getAsyn response
			Thread.sleep(2000); // wait for default monitor for latestValueResponse
			
			Response valueResp = prop.getLatestValueResponse();
			assertNotNull(valueResp);
			assertNotNull(valueResp.getValue());
			assertTrue(matchValue(value, valueResp.getValue()));
			assertEquals(valueResp.getValue().getClass(), prop.getDataType());

			assertTrue(matchValue(valueResp.getValue(),
			        rpListener.getResponses().get(0).getResponse().getValue()));

			Response resp = prop.getLatestResponse();
			assertNotSame(resp, valueResp);
		//}
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.getLatestResponse()'
	 */
	public void testGetLatestResponse()
		throws DataExchangeException, InterruptedException
	{
		DynamicValueProperty prop = getProperty();
		Object value = prop.getValue();
		ResponseListenerImpl rpListener = new ResponseListenerImpl(prop);

		prop.setValue(value);
		
		prop.addResponseListener(rpListener);

		synchronized (rpListener) {
			//Request request = prop.getAsynchronous(rpListener);
			Request request = prop.getAsynchronous();
			rpListener.wait(1000);

			Response resp = rpListener.getLastPropertyResponse();
			assertNotSame(resp, rpListener.getResponses().get(0));
			assertEquals(resp, rpListener.getLastResponse().getResponse());
			
			prop.getCharacteristicAsynchronously(PropertyCharacteristics.C_DESCRIPTION);
			rpListener.wait(1000);
			resp = rpListener.getLastResponse().getResponse();
			assertNotNull(resp.getValue());
			//assertEquals(resp.getIdTag(), PropertyCharacteristics.C_DISPLAY_NAME);
			assertEquals(resp.getIdTag(), PropertyCharacteristics.C_DESCRIPTION);

			Response valueResp = prop.getLatestValueResponse();
			assertNotSame(resp, valueResp);
		}
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.getLatestSuccess()'
	 */
	public void testGetLatestSuccess()
		throws DataExchangeException, InterruptedException
	{
		DynamicValueProperty prop = getProperty();
		ResponseListenerImpl rpListener = new ResponseListenerImpl();
		String[] names = prop.getCharacteristicNames();

		synchronized (rpListener) {
			prop.addResponseListener(rpListener);
			prop.getCharacteristicAsynchronously(PropertyCharacteristics.C_DESCRIPTION);
			rpListener.wait(500);
			assertTrue(prop.getLatestSuccess());
			prop.getCharacteristicAsynchronously("faulty name");
			rpListener.wait(2000);
			assertFalse(prop.getLatestSuccess());
		}
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.getAsynchronous()'
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.getAsynchronous(ResponseListener)'
	 */
	public void testGetAsynchronous()
	{
		DynamicValueProperty<Object> prop = getProperty();
		try {
			prop.getDefaultMonitor().setHeartbeat(true);

			Object initialValue = prop.getValue();
			internalTestAsyncGetAllCases(initialValue, prop);

			final int LOOPS = 3;

			for (int i = 0; i < LOOPS; i++) {
				internalTestAsyncGetAllCases(getRandomValue(), prop);
			}

			// set back 
			prop.setValue(initialValue);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	class DynamicValueListenerImpl implements DynamicValueListener
	{
		public int valueUpdated = 0;
		public int valueChanged = 0;
		public int responseCount = 0;
		public Object lastValue;

		public synchronized void valueUpdated(DynamicValueEvent event)
		{
			valueUpdated++;
			responseCount++;
			lastValue = event.getValue();
			notifyAll();
		}

		public synchronized void valueChanged(DynamicValueEvent event)
		{
			valueChanged++;
			responseCount++;
			lastValue = event.getValue();
			notifyAll();
		}

		public void timeoutStarts(DynamicValueEvent event)
		{
		}

		public void timeoutStops(DynamicValueEvent event)
		{
		}

		public void timelagStarts(DynamicValueEvent event)
		{
		}

		public void timelagStops(DynamicValueEvent event)
		{
		}

		public void errorResponse(DynamicValueEvent event)
		{
		}

		public void conditionChange(DynamicValueEvent event)
		{
		}
		
		public void reset() {
			valueUpdated = 0;
			valueChanged = 0;
			responseCount = 0;
		}
		
		public synchronized void waitForFirstValue(long timeout) {
			if (responseCount>0) {
				return;
			}
			try {
				wait(timeout);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	protected void internalTestAsyncGetAllCases(Object value,
	    DynamicValueProperty dp)
		throws DataExchangeException, InterruptedException
	{
		dp.setValue(value);
		assertTrue(matchValue(value, dp.getValue()));
		internalTestAsyncGet(value, dp, false, true);
		internalTestAsyncGet(value, dp, true, false);
		internalTestAsyncGet(value, dp, true, true);
	}

	private void internalTestAsyncGet(Object value, DynamicValueProperty dp,
	    boolean listenToProperty, boolean provideListenerToRequest)
		throws DataExchangeException, InterruptedException
	{
		if (!listenToProperty && !provideListenerToRequest) {
			return;
		}
		
		ResponseListenerImpl listener = new ResponseListenerImpl();
		DynamicValueListenerImpl dvListener = new DynamicValueListenerImpl();

		if (listenToProperty) {
			dp.addDynamicValueListener(dvListener);
		}

		Request request;

		synchronized (dvListener) {
			synchronized (listener) {
				if (provideListenerToRequest) {
					request = dp.getAsynchronous(listener);
				} else {
					request = dp.getAsynchronous();
				}
				
				assertEquals(request, dp.getLatestRequest());
				assertEquals(request, dp.getLatestValueRequest());

				// proxy is actually retuned, but property hides it from us
				//assertEquals(dp.getProxy(), request.getSource());
				if (provideListenerToRequest) {
					listener.waitForFirst(5000);
				}
				if (listenToProperty) {
					dvListener.waitForFirstValue(5000);
				}
			
				if (provideListenerToRequest) {
					assertTrue(listener.getResponseCount() > 0);

					int nResponses = listener.getResponseCount();
					ResponseEvent responseEvent = listener.getResponses()
						.get(nResponses - 1);
					assertEquals(request, responseEvent.getRequest());
					assertTrue(matchValue(responseEvent.getResponse().getValue(),
					        value));
					
					assertEquals(request, dp.getLatestRequest());
					assertEquals(request, dp.getLatestValueRequest());
					// monitor is filling value responses faster than async geet
					//assertEquals(request.getLastResponse(), dp.getLatestResponse());
					//assertEquals(request.getLastResponse(), dp.getLatestValueResponse());
					

					if (listenToProperty) {
						assertTrue(matchValue(responseEvent.getResponse()
						        .getValue(), dvListener.lastValue));
					}
				}

				if (listenToProperty) {
					assertTrue(dvListener.responseCount > 0);
					assertTrue(matchValue(value, dvListener.lastValue));
				}
			}
		}

		if (listenToProperty) {
			dp.removeDynamicValueListener(dvListener);
		}
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.setAsynchronous(T)'
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.setAsynchronous(T, ResponseListener)'
	 */
	public void testSetAsynchronousT()
	{
		try {
			DynamicValueProperty prop = getProperty();
			prop.getDefaultMonitor().setHeartbeat(true);
			Object initialValue = prop.getValue();

			final int LOOPS = 4;

			for (int i = 0; i < LOOPS; i++) {
				internalTestAsyncSetAllCases(getRandomValue(), prop);
			}

			// set back 
			prop.setValue(initialValue);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	protected void internalTestAsyncSetAllCases(Object value,
	    DynamicValueProperty dp)
		throws DataExchangeException, InterruptedException
	{
		internalTestAsyncSet(value, dp, false, true);
		assertTrue(matchValue(value, dp.getValue()));
		internalTestAsyncSet(value, dp, true, false);
		assertTrue(matchValue(value, dp.getValue()));
		internalTestAsyncSet(value, dp, true, true);
		assertTrue(matchValue(value, dp.getValue()));
	}

	private void internalTestAsyncSet(Object value, DynamicValueProperty dp,
	    boolean listenToProperty, boolean provideListenerToRequest)
		throws DataExchangeException, InterruptedException
	{
		if (!listenToProperty && !provideListenerToRequest) {
			return;
		}

		ResponseListenerImpl listener = new ResponseListenerImpl();
		DynamicValueListenerImpl dvListener = new DynamicValueListenerImpl();

		if (listenToProperty) {
			dp.addDynamicValueListener(dvListener);
		}

		Request request;

		synchronized (dvListener) {
			synchronized (listener) {
				if (provideListenerToRequest) {
					request = dp.setAsynchronous(value, listener);
				} else {
					request = dp.setAsynchronous(value);
				}
				
				if (provideListenerToRequest) {
					listener.waitForFirst(5000);
				} 
				if (listenToProperty) {
						dvListener.waitForFirstValue(5000);

				}
				if (provideListenerToRequest) {
					assertTrue(listener.getResponseCount() > 0);

					int nResponses = listener.getResponseCount();
					ResponseEvent responseEvent = listener.getResponses()
						.get(nResponses - 1);
					assertEquals(request, responseEvent.getRequest());

					
					assertTrue(matchValue(responseEvent.getResponse().getValue(),
					        value));
					
					//assertNull(responseEvent.getResponse().getValue());
					
					if (listenToProperty) {
						
						assertTrue(matchValue(responseEvent.getResponse()
						        .getValue(), dvListener.lastValue));
						
						//assertNull(responseEvent.getResponse().getValue());
					}
				}

				if (listenToProperty) {
					assertTrue(dvListener.responseCount > 0);
					assertTrue(matchValue(value, dvListener.lastValue));
				}
			}
		}

		if (listenToProperty) {
			dp.removeDynamicValueListener(dvListener);
		}
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.getCharacteristicsAsynchronously(String[])'
	 */
	public void testGetCharacteristics()
		throws Exception, InterruptedException
	{
		//TODO Current implementation does not match API
		//fix this test accordingly when RT#26639 is resolved
		DynamicValueProperty property = getProperty();
		ResponseListenerImpl listener = new ResponseListenerImpl() {
			@Override
			public synchronized void responseReceived(ResponseEvent event) {
				//System.out.println(event.getResponse().getIdTag()+" "+event.getRequest());
				super.responseReceived(event);
			}
		};

		String[] names = property.getCharacteristicNames();

		for (int i = 0; i < names.length; i++) {
			assertNotNull(names[i]);

			Object characteristicValue = property.getCharacteristic(names[i]);
			Object expectedCharacteristicValue = getExpectedCharacteristicValue(names[i]);

			if (expectedCharacteristicValue != null) {
				assertEquals(names[i],expectedCharacteristicValue, characteristicValue);
			}
		}

		synchronized (listener) {
			property.addResponseListener(listener);

			Request req = property.getCharacteristicsAsynchronously(names);
			//System.out.println(req);
			listener.waitForLast(60000);

			ArrayList<ResponseEvent> responses = listener.getResponses();

			for (ResponseEvent e : responses) {
				assertEquals(e.getRequest(), req);
				assertNotNull(e.getResponse().getValue());
			}

			req = property.getCharacteristicAsynchronously(names[0]);
			//System.out.println(req);
			listener.wait(10000);

			ResponseEvent e = listener.getResponses()
				.get(listener.getResponseCount() - 1);
			assertEquals(req, e.getRequest());

			Object firstChar = e.getResponse().getValue();
			assertNotNull(firstChar);

			property.removeResponseListener(listener);

			assertEquals(firstChar, property.getCharacteristic(names[0]));
			assertNull(property.getCharacteristic(null));

			Map ch = property.getCharacteristics(names);

			for (String name : names) {
				assertNotNull(ch.get(name));
			}
		}
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.addLinkListener(LinkListener)'
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.removeLinkListener(LinkListener)'
	 */
	public void testAddLinkListener() throws RemoteException
	{
		DynamicValueProperty prop = getProperty();
		LinkListenerImpl ll = new LinkListenerImpl(this);

		PropertyProxyImpl proxy = SimulatorPlug.getInstance()
			.getSimulatedPropertyProxy(prop.getUniqueName());
		
		// this is not a simulator plug
		if (proxy == null)
			return;
		
		ConnectionState state;
		prop.addLinkListener(ll);
		try
		{
			state = ConnectionState.CONNECTION_LOST;
			ll.setExpectedConnectionState(state);
			proxy.simulateConnectionState(state);
			assertTrue(ll.isResponseReceived());

			state = ConnectionState.CONNECTED;
			ll.setExpectedConnectionState(state);
			proxy.simulateConnectionState(state);
			assertTrue(ll.isResponseReceived());

			state = ConnectionState.OPERATIONAL;
			ll.setExpectedConnectionState(state);
			proxy.simulateConnectionState(state);
			assertTrue(ll.isResponseReceived());

			state = ConnectionState.CONNECTION_LOST;
			ll.setExpectedConnectionState(state);
			proxy.simulateConnectionState(state);
			assertTrue(ll.isResponseReceived());

			state = ConnectionState.OPERATIONAL;
			ll.setExpectedConnectionState(state);
			proxy.simulateConnectionState(state);
			assertTrue(ll.isResponseReceived());

			state = ConnectionState.DISCONNECTING;
			proxy.simulateConnectionState(state);

			state = ConnectionState.DISCONNECTED;
			ll.setExpectedConnectionState(state);
			proxy.simulateConnectionState(state);
			assertTrue(ll.isResponseReceived());
			
			state = ConnectionState.INITIAL;
			proxy.simulateConnectionState(state);

			state = ConnectionState.READY;
			proxy.simulateConnectionState(state);
			
			state = ConnectionState.CONNECTING;
			proxy.simulateConnectionState(state);

			state = ConnectionState.CONNECTION_FAILED;
			ll.setExpectedConnectionState(state);
			proxy.simulateConnectionState(state);
			assertTrue(ll.isResponseReceived());

			state = ConnectionState.DESTROYED;
			ll.setExpectedConnectionState(state);
			proxy.simulateConnectionState(state);
			assertTrue(ll.isResponseReceived());

			ll.setSuspended(true);
			prop.suspend();
	
			ll.setSuspended(false);
			prop.resume();
	
			ll.setListenerActive(false); // deactivate listener (test will fail if it receives an event)
		}
		finally
		{
			prop.removeLinkListener(ll); // remove listener
		}
		
		// listener shouldn't get an event
		state = ConnectionState.DESTROYED;
		ll.setExpectedConnectionState(state);
		proxy.simulateConnectionState(state);
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.isConnected()'
	 */
	public void testIsConnected()
	{
		DynamicValueProperty prop = getProperty();

		PropertyProxyImpl proxy = (PropertyProxyImpl)SimulatorPlug.getInstance()
		.getSimulatedPropertyProxy(prop.getUniqueName());
	
		// this is not a simulator plug
		if (proxy == null)
		{
			assertTrue(prop.isConnected());
			assertFalse(prop.isConnectionFailed());
			return;
		}
		
		proxy.simulateConnectionState(ConnectionState.CONNECTED);
		assertTrue(prop.isConnected());
		proxy.simulateConnectionState(ConnectionState.CONNECTION_LOST);
		assertTrue(prop.isConnected());
		proxy.simulateConnectionState(ConnectionState.OPERATIONAL);
		assertTrue(prop.isConnected());
		proxy.simulateConnectionState(ConnectionState.DISCONNECTING);
		assertFalse(prop.isConnected());
		proxy.simulateConnectionState(ConnectionState.DISCONNECTED);
		assertFalse(prop.isConnected());
		proxy.simulateConnectionState(ConnectionState.DESTROYED);
		assertFalse(prop.isConnected());
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.isConnectionAlive()'
	 */
	public void testIsConnectionAlive()
	{
		DynamicValueProperty prop = getProperty();

		PropertyProxyImpl proxy = (PropertyProxyImpl)SimulatorPlug.getInstance()
		.getSimulatedPropertyProxy(prop.getUniqueName());
	
		// this is not a simulator plug
		if (proxy == null)
		{
			assertTrue(prop.isConnectionAlive());
			return;
		}
		
		proxy.simulateConnectionState(ConnectionState.CONNECTED);
		assertTrue(prop.isConnectionAlive());
		proxy.simulateConnectionState(ConnectionState.CONNECTION_LOST);
		assertFalse(prop.isConnectionAlive());
		proxy.simulateConnectionState(ConnectionState.OPERATIONAL);
		assertTrue(prop.isConnectionAlive());
		proxy.simulateConnectionState(ConnectionState.DISCONNECTING);
		assertFalse(prop.isConnectionAlive());
		proxy.simulateConnectionState(ConnectionState.DISCONNECTED);
		assertFalse(prop.isConnectionAlive());
		proxy.simulateConnectionState(ConnectionState.DESTROYED);
		assertFalse(prop.isConnectionAlive());
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.refresh()'
	 */
	public void testRefresh()
	{
		// TODO
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.suspend()'
	 */
	public void testSuspendResume()
	{
		DynamicValueProperty prop = getProperty();

		try {
			assertTrue(!prop.isSuspended());

			for (int i = 0; i < 100; i++) {
				prop.suspend();
				assertTrue(prop.isSuspended());
			}

			for (int i = 0; i < 99; i++) {
				prop.resume();
				assertTrue(prop.isSuspended());
			}

			prop.resume();
			assertTrue(!prop.isSuspended());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.SimplePropertyImpl.getAccessTypes()'
	 */
	public void testGetAccessTypes()
	{
		DynamicValueProperty prop = getProperty();
		try {
			prop.getValue();
		} catch (DataExchangeException e1) {
			e1.printStackTrace();
			fail(e1.toString());
		}
		try {
			prop.getDefaultMonitor().setHeartbeat(false);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail(e1.toString());
		}
		
		Class[] types= prop.getAccessTypes();
		assertNotNull(types);
		assertTrue(types.length > 0);

		for (int i = 0; i < types.length; i++) {
			try {
				DataAccess da = prop.getDataAccess(types[i]);
				
				assertNotNull(types[i].getName(),da);
				assertTrue(types[i].getName()+" from "+da.getClass().getName(),types[i].isAssignableFrom(da.getClass()));

				Object a= prop.getLatestReceivedValue();
				assertNotNull(a);
				Object b= da.getLatestReceivedValue();
				assertNotNull(types[i].getName()+" from "+da.getClass().getName(),b);
				
				// TODO: make better testing if conversions are OK, this fails for some cases
				/*if (a instanceof Number && b instanceof Number ) {
					assertEquals(types[i].getName()+" from "+da.getClass().getName(),((Number)a).longValue(), ((Number)b).longValue(), 0.00001);
				} else {
					assertEquals(types[i].getName()+" from "+da.getClass().getName(),a.toString(), b.toString());
				}*/	
			} catch (IllegalViewException e) {
				e.printStackTrace();
				fail(e.toString());
			}
		}
		
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.SimplePropertyImpl.getCondition()'
	 */
	public void testGetCondition()
	{
		DynamicValueProperty prop = getProperty();
		
		assertNotNull(prop.getConnectionState().isConnected());
		assertNotNull(prop.getCondition());

		DynamicValueCondition condition = new DynamicValueCondition(EnumSet.of(
			        DynamicValueState.NORMAL), null, null);
		//condition should be normal 
		assertTrue("States: "+prop.getCondition().getStates()+" does not contain NORMAL",prop.getCondition().containsAllStates(condition.getStates()));

		//TODO additional test?
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.SimplePropertyImpl.getDataAccess(Class<D>) <D>'
	 */
	public void testGetDataAccess() throws IllegalViewException
	{
		//DoubleProperty prop = getDoubleProperty();
		//assertNotNull(prop.getDataAccess(DoubleAccess.class));
		// TODO
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.SimplePropertyImpl.getDefaultDataAccess()'
	 */
	public void testGetDefaultDataAccess()
	{
		assertNotNull(getProperty().getDefaultDataAccess());

		//TODO
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.SimplePropertyImpl.getUniqueName()'
	 */
	public void testGetUniqueName()
	{
		DynamicValueProperty prop = getProperty();
		assertEquals(prop.getUniqueName(), getPropertyUniqueName());
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.SimplePropertyImpl.getDescription()'
	 */
	public void testGetDescription() throws DataExchangeException
	{
		DynamicValueProperty prop = getProperty();
		assertNotNull(prop.getDescription());
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.SimplePropertyImpl.isTimelag()'
	 */
	public void testIsTimelag()
	{
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.SimplePropertyImpl.isTimeout()'
	 */
	public void testIsTimeout()
	{
	}

	private class PropertyChangedInterceptor implements PropertyChangeListener
	{
		public int valueUpdated = 0;

		public void propertyChange(PropertyChangeEvent evt)
		{
			valueUpdated++;
		}
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.SimplePropertyImpl.addPropertyChangeListener(PropertyChangeListener)'
	 * Test method for 'org.epics.css.dal.impl.SimplePropertyImpl.removePropertyChangeListener(PropertyChangeListener)'
	 * Test method for 'org.epics.css.dal.impl.SimplePropertyImpl.getPropertyChangeListeners()'
	 */
	public void testPropertyChangeListeners()
		throws DataExchangeException, InterruptedException
	{
		DynamicValueProperty prop = getProperty();
		PropertyChangedInterceptor listener = new PropertyChangedInterceptor();

		synchronized (listener) {
			int nListeners = prop.getPropertyChangeListeners().length;

			prop.addPropertyChangeListener(listener);
			//			prop.getAsynchronous();
			prop.setValue(getRandomValue());
			listener.wait(2000);

			//assertEquals(getPropertyChangeUpdatesNumber(), listener.valueUpdated);

			PropertyChangeListener[] list = prop.getPropertyChangeListeners();
			assertEquals(list.length, nListeners + 1);
			assertEquals(list[list.length - 1], listener);

			prop.removePropertyChangeListener(listener);

			int update = listener.valueUpdated;

			prop.getAsynchronous();
			listener.wait(1000);

			list = prop.getPropertyChangeListeners();
			assertEquals(list.length, nListeners);
			assertEquals(listener.valueUpdated, update);
		}
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.SimplePropertyImpl.createNewMonitor(DynamicValueListener)'
	 */
	public void testCreateNewMonitor()
	{
		DynamicValueProperty prop = getProperty();

		try {
			PropertyListener pl = new PropertyListener();

			synchronized (pl) {
				DynamicValueMonitor monitor = prop.createNewMonitor(pl);
				monitor.setHeartbeat(true);
				int loopCount = 0;

				while (pl.valueUpdated <= 4 && loopCount++ <= 30) {
					pl.wait(500);
				}

				if (loopCount >= 30) {
					fail("Property was not updated fast enough.");
				}

				int updates = pl.valueUpdated;

				assertTrue(updates > 4);

				monitor.destroy();
				//wait for the monitor to disconnect.
				Thread.yield();
				Thread.sleep(2000);
				updates = pl.valueUpdated;
				assertEquals(1, pl.valueChanged);
				assertEquals(updates, pl.valueUpdated);
				//wait some time, no updates should be received
				pl.wait(2000);

				// assertEquals(1, pl.valueChanged);
				assertEquals(updates, pl.valueUpdated);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.SimplePropertyImpl.getDefaultMonitor()'
	 */
	public void testGetDefaultMonitor()
	{
		try {
			PropertyListener pl = new PropertyListener();
			DynamicValueProperty prop = getProperty();

			synchronized (pl) {
				DynamicValueMonitor monitor = prop.getDefaultMonitor();
				monitor.setHeartbeat(true);
				assertNotNull(monitor);
				prop.addDynamicValueListener(pl);

				int loopCount = 0;

				while (pl.valueUpdated <= 4 && loopCount++ <= 30) {
					pl.wait(500);
				}

				if (loopCount >= 30) {
					fail("Property was not updated fast enough.");
				}

				int updates = pl.valueUpdated;

				assertTrue(updates > 4);
				prop.removeDynamicValueListener(pl);

				int update = pl.valueUpdated;
				//wait some time - no values should be received
				pl.wait(2000);
				assertEquals(update, pl.valueUpdated);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.SimplePropertyImpl.getLatestValueChangeTimestamp()'
	 */
	public void testGetLatestValueChangeTimestamp()
		throws DataExchangeException, InterruptedException
	{
		DynamicValueProperty prop = getProperty();
		prop.getDefaultMonitor().setHeartbeat(true);

		PropertyListener listener = new PropertyListener();

		synchronized (listener) {
			prop.addDynamicValueListener(listener);
			Object val= getRandomValue();
			prop.setValue(val);
			listener.wait(3000);

			Timestamp timestamp1 = prop.getLatestValueChangeTimestamp();
			System.out.println(">> 1 "+val+" "+timestamp1);
			
			val= getRandomValue();
			prop.setValue(val);
			listener.wait(3000);

			Timestamp timestamp2 = prop.getLatestValueChangeTimestamp();
			System.out.println(">> 1 "+val+" "+timestamp2);

			assertTrue(timestamp1.compareTo(timestamp2) < 0);
		}

		prop.removeDynamicValueListener(listener);
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.SimplePropertyImpl.getName()'
	 */
	public void testGetName()
	{
		DynamicValueProperty prop = getProperty();
		assertTrue(getPropertyUniqueName().endsWith(prop.getName()));
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.DataAccessImpl.addDynamicValueListener(DynamicValueListener)'
	 * Test method for 'org.epics.css.dal.impl.DataAccessImpl.removeDynamicValueListener(DynamicValueListener)'
	 */
	public void testDynamicValueListeners()
		throws DataExchangeException, InterruptedException
	{
		DynamicValueProperty prop = getProperty();
		prop.getDefaultMonitor().setHeartbeat(true);
		PropertyListener listener = new PropertyListener();
		Object value = getRandomValue();
		prop.setValue(value);

		synchronized (listener) {
			int nListeners = prop.getDynamicValueListeners().length;
			prop.addDynamicValueListener(listener);
			
			listener.wait(2000);

			// check if listener had received initial event
			assertTrue( listener.valueChanged > 0);
			
			
			prop.getAsynchronous();
			listener.wait(5000);

			assertTrue(listener.valueUpdated > 0);

			assertTrue(prop.getLatestValueSuccess());

			DynamicValueListener[] list = prop.getDynamicValueListeners();
			assertEquals(list.length, nListeners + 1);
			assertEquals(list[list.length - 1], listener);

			prop.removeDynamicValueListener(listener);

			int update = listener.valueUpdated;

			prop.getAsynchronous();
			listener.wait(1000);

			list = prop.getDynamicValueListeners();
			assertEquals(list.length, nListeners);
			assertEquals(listener.valueUpdated, update);
		}
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.DataAccessImpl.getDynamicValueListeners()'
	 */
	public void testGetDynamicValueListeners()
	{
		DynamicValueProperty prop = getProperty();

		try {
			PropertyListener pl = new PropertyListener();
			prop.addDynamicValueListener(pl);
			prop.getDefaultMonitor().setHeartbeat(true);
			prop.getDefaultMonitor().setTimerTrigger(1000);

			synchronized (pl) {
				int loopCount = 0;

				while (pl.valueUpdated <= 4 && loopCount++ <= 30) {
					pl.wait(1000);
				}

				if (loopCount >= 30) {
					fail("Property was not updated fast enough. ");
				}

				// the first one can be missed
				// TODO well actually must be always fired as changed immediately
				// assertEquals(1, pl.valueChanged);
				int updates = pl.valueUpdated;
				assertTrue(updates > 4);

				prop.removeDynamicValueListener(pl);
				Thread.yield();
				Thread.sleep(1500);

				// assertEquals(1, pl.valueChanged);
				assertEquals(updates, pl.valueUpdated);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.DataAccessImpl.isSettable()'
	 */

	/*
	 * Test method for 'org.epics.css.dal.impl.DataAccessImpl.setValue(T)'
	 */
	public void testSetGetValue()
	{
		DynamicValueProperty prop = getProperty();

		try {
			Object startValue = prop.getValue();
			Object value = getRandomValue();
			prop.setValue(value);
			assertTrue(matchValue(value, prop.getValue()));

			prop.setValue(startValue);
			assertTrue(matchValue(startValue, prop.getValue()));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.DataAccessImpl.getLatestReceivedValue()'
	 * Test method for 'org.epics.css.dal.impl.DataAccessImpl.getLatestReceivedValueAsObject()'
	 * Test method for 'org.epics.css.dal.impl.DataAccessImpl.getLatestValueUpdateTimestamp()'
	 */
	public void testGetLatestReceivedValue()
		throws InterruptedException, RemoteException
	{
		ResponseListenerImpl listener = new ResponseListenerImpl();
		DynamicValueProperty prop = getProperty();

		synchronized (listener) {
			//			prop.addResponseListener(listener);
			prop.getDefaultMonitor();
			Thread.yield();
			Thread.sleep(1000);

			/*prop.getAsynchronous(listener);
			listener.waitForFirst(5000);

			ResponseEvent responseEvent = listener.getResponses()
				.get(listener.getResponseCount() - 1);
			Object value = responseEvent.getResponse().getValue();

			Timestamp time = null;

			if (prop.getLatestReceivedValue() != null) {
				assertTrue(matchValue(value, prop.getLatestReceivedValue()));
				assertNotNull(prop.getLatestReceivedValueAsObject());
				assertNotNull(time = prop.getLatestValueUpdateTimestamp());
			}*/


			listener.reset();
			Object randomValue = getRandomValue();
			prop.setAsynchronous(randomValue, listener);
			listener.waitForFirst(5000);
			//Thread.yield();
			//Thread.sleep(8000);

			listener.reset();
			prop.getAsynchronous(listener);
			listener.waitForFirst(7000);
			
			Thread.yield();
			Thread.sleep(3000);

			assertTrue(matchValue(randomValue, prop.getLatestReceivedValue()));

			//listener.wait(5000);

			ResponseEvent responseEvent = listener.getResponses()
				.get(listener.getResponseCount() - 1);
			Object value = responseEvent.getResponse().getValue();

			randomValue = getRandomValue();
			prop.setAsynchronous(randomValue);
			prop.getAsynchronous(listener);
			assertNotSame(randomValue, prop.getLatestReceivedValue());

			/*if (time != null) {
				assertTrue(prop.getLatestValueUpdateTimestamp().compareTo(time) > 0);
			}*/
		}
	}

	public void testMandatoryCharacteristics() {
		
		try {
			
			DynamicValueProperty property= getProperty();
			
			CharacteristicInfo[] infos= CharacteristicInfo.getDefaultCharacteristics(property.getClass(), property.getParentContext().getPlugType());
			
			assertNotNull(infos);
			
			for (int i = 0; i < infos.length; i++) {
				CharacteristicInfo info= infos[i];
				assertNotNull(info);
				
				Object value= property.getCharacteristic(info.getName());
				
				//System.out.println(info.getName()+" "+value);
				
				assertNotNull("'"+info.getName()+"' is null",value);
				assertTrue("'"+info.getName()+"' is "+value.getClass().getName(), info.getType().isAssignableFrom(value.getClass()));
				
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
	
	
	public void testPropertyLifecycleEvents() {
		
		LifecycleListener l= new LifecycleListener();
		
		DynamicValueProperty prop= getProperty(l);
		AnyDataChannel chan= (AnyDataChannel)prop;
		
		prop.addDynamicValueListener(l);
		chan.addListener(l);
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertTrue(l.events.size()>0);
		
	}


	/**
	 * NOTE: this test should be run as last since destroys plug instance!!!
	 * @throws InterruptedException
	 */
	public void testDestroy() throws InterruptedException
	{
		DynamicValueProperty prop = getProperty();
		assertTrue(prop.isConnected());
		LinkListenerImpl lli = new LinkListenerImpl(this);
		lli.setExpectedConnectionState(ConnectionState.DISCONNECTED,ConnectionState.DESTROYED);
		prop.addLinkListener(lli);
		
		DynamicValueProperty[] p= prop.getParentContext().toPropertyArray();
		
		//System.out.println(Arrays.toString(p));
		
		
		getContext().destroy();

		lli.waitForResponse(5000);

		assertTrue(prop.isDestroyed());
		assertTrue(lli.isResponseReceived());

		/*synchronized (lli) {
			lli.wait(1500);
		}*/
	}
	
}

/* __oOo__ */
