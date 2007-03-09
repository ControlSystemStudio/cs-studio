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

import junit.framework.TestCase;

import org.epics.css.dal.CharacteristicContext;
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
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.context.Identifier;
import org.epics.css.dal.context.LinkListener;
import org.epics.css.dal.simulation.PropertyProxyImpl;
import org.epics.css.dal.simulation.SimulatorPlug;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Map;


public abstract class DynamicValuePropertyTest extends TestCase
{
	public class ResponseListenerImpl implements ResponseListener
	{
		/// Note that reponses is not synchronized
		ArrayList<ResponseEvent> responses = new ArrayList<ResponseEvent>();

		public synchronized void responseError(ResponseEvent event)
		{
			responses.add(event);
			notifyAll();
		}

		public synchronized void responseReceived(ResponseEvent event)
		{
			responses.add(event);
			notifyAll();
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
	}

	protected abstract DynamicValueProperty getProperty();

	protected abstract String getPropertyUniqueName();

	protected abstract Object getRandomValue();

	protected abstract AbstractApplicationContext getContext();

	protected abstract Object getExpectedCharacteristicValue(
	    String characteristicName);

	public abstract boolean matchValue(Object expected, Object got);

	/*
	 * Test method for 'org.epics.css.dal.impl.DataAccessImpl.getDataType()'
	 */
	public abstract void testGetDataType();

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

		int nListeners = prop.getResponseListeners().length;

		synchronized (listener) {
			prop.addResponseListener(listener);
			prop.getAsynchronous();
			listener.wait(1000);

			assertTrue(listener.getResponseCount() == 0);

			ResponseListener[] list = prop.getResponseListeners();
			assertEquals(list.length, nListeners + 1);
			assertEquals(list[nListeners], listener);

			Request request = prop.getCharacteristicAsynchronously(PropertyCharacteristics.C_DESCRIPTION);
			listener.wait(1000);
			assertTrue(listener.getResponseCount() > 0);

			ResponseEvent rEvent = listener.getResponses()
				.get(listener.getResponseCount() - 1);
			assertEquals(request, rEvent.getRequest());

			prop.removeResponseListener(listener);

			int update = listener.getResponseCount();

			prop.getAsynchronous();
			listener.wait(1000);

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
		prop.getDefaultMonitor();

		Object value = prop.getValue();
		ResponseListenerImpl rpListener = new ResponseListenerImpl();
		prop.setValue(value);

		synchronized (rpListener) {
			Request request = prop.getAsynchronous(rpListener);
			rpListener.wait(1000);

			Response valueResp = prop.getLatestValueResponse();
			assertNotNull(valueResp);
			assertNotNull(valueResp.getValue());
			assertTrue(matchValue(value, valueResp.getValue()));
			assertEquals(valueResp.getValue().getClass(), prop.getDataType());

			assertTrue(matchValue(valueResp.getValue(),
			        rpListener.getResponses().get(0).getResponse().getValue()));

			Response resp = prop.getLatestResponse();
			assertNotSame(resp, valueResp);
		}
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.getLatestResponse()'
	 */
	public void testGetLatestResponse()
		throws DataExchangeException, InterruptedException
	{
		DynamicValueProperty prop = getProperty();
		Object value = prop.getValue();
		ResponseListenerImpl rpListener = new ResponseListenerImpl();

		prop.setValue(value);

		synchronized (rpListener) {
			Request request = prop.getAsynchronous(rpListener);
			rpListener.wait(1000);

			Response resp = prop.getLatestResponse();
			assertNotSame(resp, rpListener.getResponses().get(0));
			prop.getCharacteristicAsynchronously(PropertyCharacteristics.C_DISPLAY_NAME);
			rpListener.wait(1000);
			resp = prop.getLatestResponse();
			assertNull(resp.getValue());
			assertEquals(resp.getIdTag(), PropertyCharacteristics.C_DISPLAY_NAME);

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
			rpListener.wait(1500);
			assertFalse(prop.getLatestSuccess());
		}
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.getAsynchronous()'
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.getAsynchronous(ResponseListener)'
	 */
	public void testGetAsynchronous()
	{
		try {
			DynamicValueProperty prop = getProperty();
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

		public void valueUpdated(DynamicValueEvent event)
		{
			valueUpdated++;
			responseCount++;
			lastValue = event.getValue();
		}

		public void valueChanged(DynamicValueEvent event)
		{
			valueChanged++;
			responseCount++;
			lastValue = event.getValue();
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

				// proxy is actually retuned, but property hides it from us
				//assertEquals(dp.getProxy(), request.getSource());
				if (provideListenerToRequest
				    && listener.getResponseCount() == 0) {
					listener.wait(1000);
				} else if (listenToProperty && dvListener.valueUpdated == 0) {
					dvListener.wait(1000);
				}

				if (provideListenerToRequest) {
					assertTrue(listener.getResponseCount() > 0);

					int nResponses = listener.getResponseCount();
					ResponseEvent responseEvent = listener.getResponses()
						.get(nResponses - 1);
					assertEquals(request, responseEvent.getRequest());
					assertTrue(matchValue(responseEvent.getResponse().getValue(),
					        value));

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

				if (provideListenerToRequest
				    && listener.getResponseCount() == 0) {
					listener.wait(1000);
				} else if (listenToProperty && dvListener.valueUpdated == 0) {
					dvListener.wait(1000);
				}

				if (provideListenerToRequest) {
					assertTrue(listener.getResponseCount() > 0);

					int nResponses = listener.getResponseCount();
					ResponseEvent responseEvent = listener.getResponses()
						.get(nResponses - 1);
					assertEquals(request, responseEvent.getRequest());

					assertTrue(matchValue(responseEvent.getResponse().getValue(),
					        value));

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
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.getCharacteristicsAsynchronously(String[])'
	 */
	public void testGetCharacteristics()
		throws DataExchangeException, InterruptedException
	{
		//TODO Current implementation does not match API
		//fix this test accordingly when RT#26639 is resolved
		DynamicValueProperty property = getProperty();
		ResponseListenerImpl listener = new ResponseListenerImpl();

		String[] names = property.getCharacteristicNames();

		for (int i = 0; i < names.length; i++) {
			assertNotNull(names[i]);

			Object characteristicValue = property.getCharacteristic(names[i]);
			Object expectedCharacteristicValue = getExpectedCharacteristicValue(names[i]);

			if (expectedCharacteristicValue != null) {
				assertEquals(expectedCharacteristicValue, characteristicValue);
			}
		}

		synchronized (listener) {
			property.addResponseListener(listener);

			Request req = property.getCharacteristicsAsynchronously(names);
			listener.wait(5000);

			ArrayList<ResponseEvent> responses = listener.getResponses();

			for (ResponseEvent e : responses) {
				assertEquals(e.getRequest(), req);
				assertNotNull(e.getResponse().getValue());
			}

			req = property.getCharacteristicAsynchronously(names[0]);
			listener.wait(5000);

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

	private class LinkListenerImpl implements LinkListener
	{
		private ConnectionState expConnState = ConnectionState.INITIAL;
		private boolean listenerActive = true;
		private boolean suspended = false;
		private boolean responseReceived = false;

		public void setListenerActive(boolean active)
		{
			listenerActive = active;
		}

		public void setExpectedConnectionState(ConnectionState state)
		{
			expConnState = state;
			responseReceived = false;
		}

		public void setSuspended(boolean suspended)
		{
			this.suspended = suspended;
		}

		private void evaluateConnectionState(ConnectionEvent e)
		{
			responseReceived = true;

			if (listenerActive) {
				assertEquals(expConnState, e.getState());
			} else {
				assertTrue(false);
			}
		}

		private void evaluateSuspended(ConnectionEvent e)
		{
			responseReceived = true;

			if (listenerActive) {
				assertEquals(e.getConnectable().isSuspended(), suspended);
			} else {
				assertTrue(false);
			}
		}

		public void resumed(ConnectionEvent e)
		{
			evaluateSuspended(e);
		}

		public void suspended(ConnectionEvent e)
		{
			evaluateSuspended(e);
		}

		public void connected(ConnectionEvent e)
		{
			evaluateConnectionState(e);
		}

		public void disconnected(ConnectionEvent e)
		{
			evaluateConnectionState(e);
		}

		public void connectionLost(ConnectionEvent e)
		{
			evaluateConnectionState(e);
		}

		public void destroyed(ConnectionEvent e)
		{
			evaluateConnectionState(e);
		}

		public void connectionFailed(ConnectionEvent e)
		{
			evaluateConnectionState(e);
		}
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.addLinkListener(LinkListener)'
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.removeLinkListener(LinkListener)'
	 */
	public void testAddLinkListener() throws RemoteException
	{
		DynamicValueProperty prop = getProperty();
		LinkListenerImpl ll = new LinkListenerImpl();

		PropertyProxyImpl proxy = SimulatorPlug.getInstance()
			.getSimulatedPropertyProxy(prop.getUniqueName());
		ConnectionState state;
		prop.addLinkListener(ll);

		state = ConnectionState.CONNECTED;
		ll.setExpectedConnectionState(state);
		proxy.simulateConnectionState(state);
		state = ConnectionState.DISCONNECTED;
		ll.setExpectedConnectionState(state);
		proxy.simulateConnectionState(state);
		state = ConnectionState.CONNECTION_LOST;
		ll.setExpectedConnectionState(state);
		proxy.simulateConnectionState(state);

		ll.setSuspended(true);
		prop.suspend();

		ll.setSuspended(false);
		prop.resume();

		ll.setListenerActive(false); // deactivate listener (test will fail if it receives an event)
		prop.removeLinkListener(ll); // remove listener

		// listener shouldn't get an event
		state = ConnectionState.CONNECTED;
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
		proxy.simulateConnectionState(ConnectionState.DISCONNECTED);
		assertFalse(prop.isConnected());
		proxy.simulateConnectionState(ConnectionState.CONNECTED);
		assertTrue(prop.isConnected());
		proxy.simulateConnectionState(ConnectionState.CONNECTION_LOST);
		assertTrue(prop.isConnected());
		proxy.simulateConnectionState(ConnectionState.CONNECTED);
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.DynamicValuePropertyImpl.isConnectionAlive()'
	 */
	public void testIsConnectionAlive()
	{
		DynamicValueProperty prop = getProperty();
		PropertyProxyImpl proxy = (PropertyProxyImpl)SimulatorPlug.getInstance()
			.getSimulatedPropertyProxy(prop.getUniqueName());
		proxy.simulateConnectionState(ConnectionState.DISCONNECTED);
		assertFalse(prop.isConnectionAlive());
		proxy.simulateConnectionState(ConnectionState.CONNECTED);
		assertTrue(prop.isConnectionAlive());
		proxy.simulateConnectionState(ConnectionState.CONNECTION_LOST);
		assertFalse(prop.isConnectionAlive());
		proxy.simulateConnectionState(ConnectionState.CONNECTED);
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
		assertNotNull(prop.getAccessTypes());
		assertTrue(prop.getAccessTypes().length > 0);

		//TODO
	}

	/*
	 * Test method for 'org.epics.css.dal.impl.SimplePropertyImpl.getCondition()'
	 */
	public void testGetCondition()
	{
		DynamicValueProperty prop = getProperty();
		assertNotNull(prop.getCondition());

		DynamicValueCondition condition = new DynamicValueCondition(EnumSet.of(
			        DynamicValueState.NORMAL), System.currentTimeMillis(), null);
		//condition should be normal 
		assertTrue(condition.areStatesEqual(prop.getCondition()));

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
			listener.wait(5000);

			assertTrue(listener.valueUpdated <= 0);

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

				int loopCount = 0;

				while (pl.valueUpdated <= 4 && loopCount++ <= 30) {
					pl.wait(1000);
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
				assertNotNull(monitor);
				prop.addDynamicValueListener(pl);

				int loopCount = 0;

				while (pl.valueUpdated <= 4 && loopCount++ <= 30) {
					pl.wait(1000);
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
		prop.getDefaultMonitor();

		PropertyListener listener = new PropertyListener();

		synchronized (listener) {
			prop.addDynamicValueListener(listener);
			prop.setValue(getRandomValue());
			listener.wait(5000);

			Timestamp timestamp1 = prop.getLatestValueChangeTimestamp();
			prop.setValue(getRandomValue());
			listener.wait(5000);

			Timestamp timestamp2 = prop.getLatestValueChangeTimestamp();

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
		PropertyListener listener = new PropertyListener();
		Object value = getRandomValue();
		prop.setValue(value);

		synchronized (listener) {
			int nListeners = prop.getDynamicValueListeners().length;
			prop.addDynamicValueListener(listener);
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
	public void testIsSettable()
	{
		//TODO
	}

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

			prop.getAsynchronous(listener);

			listener.wait(5000);

			ResponseEvent responseEvent = listener.getResponses()
				.get(listener.getResponseCount() - 1);
			Object value = responseEvent.getResponse().getValue();

			Timestamp time = null;

			if (prop.getLatestReceivedValue() != null) {
				assertEquals(value, prop.getLatestReceivedValue());
				assertNotNull(prop.getLatestReceivedValueAsObject());
				assertNotNull(time = prop.getLatestValueUpdateTimestamp());
			}

			Object randomValue = getRandomValue();
			prop.setAsynchronous(randomValue, listener);
			Thread.yield();
			Thread.sleep(1000);

			prop.getAsynchronous(listener);

			Thread.yield();
			Thread.sleep(500);

			assertEquals(randomValue, prop.getLatestReceivedValue());
			assertEquals(randomValue, prop.getLatestReceivedValueAsObject());

			listener.wait(5000);

			responseEvent = listener.getResponses()
				.get(listener.getResponseCount() - 1);
			value = responseEvent.getResponse().getValue();

			Object lastValueObj = prop.getLatestReceivedValueAsObject();

			assertEquals(lastValueObj, value);

			randomValue = getRandomValue();
			prop.setAsynchronous(randomValue);
			prop.getAsynchronous(listener);
			assertNotSame(randomValue, prop.getLatestReceivedValue());

			if (time != null) {
				assertTrue(prop.getLatestValueUpdateTimestamp().compareTo(time) > 0);
			}
		}
	}

	public void testDestroy() throws InterruptedException
	{
		DynamicValueProperty prop = getProperty();
		LinkListenerImpl lli = new LinkListenerImpl();
		lli.setExpectedConnectionState(ConnectionState.DESTROYED);
		prop.addLinkListener(lli);
		getContext().destroy();

		synchronized (lli) {
			lli.wait(1500);
		}

		assertTrue(lli.responseReceived);
		assertTrue(prop.isDestroyed());

		synchronized (lli) {
			lli.wait(1500);
		}
	}
}

/* __oOo__ */
