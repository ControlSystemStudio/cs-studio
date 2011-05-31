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

import junit.framework.TestCase;

import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.Request;
import org.epics.css.dal.ResponseEvent;
import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.commands.AsynchronousCommand;
import org.epics.css.dal.commands.Command;
import org.epics.css.dal.context.AbstractApplicationContext;
import org.epics.css.dal.context.ConnectionEvent;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.context.LinkAdapter;
import org.epics.css.dal.device.AbstractDevice;


public abstract class AbstractDeviceTest extends TestCase
{
	protected abstract AbstractDevice getDevice();

	protected abstract AbstractApplicationContext getContext();

	protected abstract DynamicValuePropertyTest getPropertyTest(
	    DynamicValueProperty property);

	private class CommandResponseListener implements ResponseListener
	{
		private int responsesReceived = 0;
		private int responseErrorsReceived = 0;
		private ResponseEvent lastResponseEvent;

		public void responseReceived(ResponseEvent event)
		{
			responsesReceived++;
			lastResponseEvent = event;
		}

		public void responseError(ResponseEvent event)
		{
			responseErrorsReceived++;
			lastResponseEvent = event;
		}
	}

	private void testAsynchronousCommand(Command command, Object[] params)
	{
		AsynchronousCommand asynCmd = (AsynchronousCommand)command;
		CommandResponseListener crListener = new CommandResponseListener();

		synchronized (crListener) {
			try {
				asynCmd.executeAsync(crListener, params);
				crListener.wait(2000);
				assertTrue(crListener.responsesReceived > 0);
				assertFalse(crListener.responseErrorsReceived > 0); // TODO: might be error and still ok?
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private Object[] createCommandParameters(Command com)
	{
		Class[] paramTypes = com.getParameterTypes();

		Object[] dummyParams = new Object[paramTypes.length];

		for (int j = 0; j < paramTypes.length; j++) {
			try {
				dummyParams[j] = paramTypes[j].newInstance();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
		}

		return dummyParams;
	}

	public void testCommands()
	{
		AbstractDevice device = getDevice();

		try {
			Command[] commands = device.getCommands();

			for (int i = 0; i < commands.length; i++) {
				Command com = commands[i];
				Object[] dummyParams = createCommandParameters(com);
				Object commandRv = com.execute(dummyParams);

				if (com.getReturnedType() == void.class) {
					assertNull(commandRv);
				} else {
					assertEquals(com.getReturnedType(), commandRv.getClass());
				}

				if (com.isAsynchronous()) {
					testAsynchronousCommand(com, dummyParams);
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private boolean comparePropertyNames(String[] propertyNames,
	    DynamicValueProperty[] properties)
	{
		assertEquals(properties.length, propertyNames.length);

		for (int i = 0; i < propertyNames.length; i++) {
			boolean match = false;

			for (int j = 0; j < properties.length; j++) {
				if (properties[j].getName().equals(propertyNames[i])) {
					match = true;
				}
			}

			if (match == false) {
				return false;
			}
		}

		return true;
	}

	public void testSuspendResume()
	{
		AbstractDevice device = getDevice();

		try {
			assertTrue(!device.isSuspended());

			for (int i = 0; i < 100; i++) {
				device.suspend();
				assertTrue(device.isSuspended());
			}

			for (int i = 0; i < 99; i++) {
				device.resume();
				assertTrue(device.isSuspended());
			}

			device.resume();
			assertTrue(!device.isSuspended());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	private class DevicePCL implements PropertyChangeListener
	{
		public int propChanged;

		public void propertyChange(PropertyChangeEvent evt)
		{
			propChanged++;
		}
	}

	//Test properties, p
	public void testProperties() throws InterruptedException
	{
		AbstractDevice device = getDevice();
		DynamicValueProperty[] properties = device.toPropertyArray();

		String[] propertyNames = device.getPropertyNames();

		assertTrue(comparePropertyNames(propertyNames, properties));

		DevicePCL deviceListener = new DevicePCL();

		synchronized (deviceListener) {
			int pcListeners = device.getPropertyChangeListeners().length;
			device.addPropertyChangeListener(deviceListener);
			assertSame(device.getPropertyChangeListeners().length,
			    pcListeners + 1);

			for (int i = 0; i < properties.length; i++) {
				assertTrue(device.containsProperty(properties[i]));
				assertTrue(device.containsProperty(properties[i].getName()));

				DynamicValuePropertyTest propertyTest = getPropertyTest(properties[i]);

				if (propertyTest != null) {
					deviceListener.propChanged = 0;
					propertyTest.testGetAsynchronous();
					deviceListener.wait(2000);
				}
			}

			device.removePropertyChangeListener(deviceListener);
			assertSame(pcListeners, device.getPropertyChangeListeners().length);
		}
	}

	public void testRequestResponse() throws InterruptedException
	{
		AbstractDevice device = getDevice();

		try {
			Command[] commands = device.getCommands();
			AsynchronousCommand asynCmd = null;

			for (Command cmd : commands) {
				if (cmd.isAsynchronous()) {
					asynCmd = (AsynchronousCommand)cmd;

					break;
				}
			}

			Object[] dummyParams = createCommandParameters(asynCmd);

			CommandResponseListener crListener = new CommandResponseListener();
			CommandResponseListener drListener = new CommandResponseListener();

			int nListeners = device.getResponseListeners().length;
			device.addResponseListener(drListener);
			assertEquals(nListeners + 1, device.getResponseListeners().length);

			synchronized (drListener) {
				synchronized (crListener) {
					Request req = asynCmd.executeAsync(crListener, dummyParams);
					crListener.wait(1000);
					assertEquals(crListener.responsesReceived,
					    drListener.responsesReceived);
					assertNotNull(device.getLatestResponse());
					assertNotNull(device.getLatestRequest());

					assertEquals(device.getLatestResponse(),
					    crListener.lastResponseEvent.getResponse());
					assertEquals(device.getLatestResponse(),
					    drListener.lastResponseEvent.getResponse());

					assertEquals(device.getLatestRequest(), req);
					assertEquals(crListener.lastResponseEvent.getRequest(), req);
					assertEquals(drListener.lastResponseEvent.getRequest(), req);
					assertTrue(device.getLatestSuccess());
					assertTrue(crListener.responseErrorsReceived == 0);

					req = asynCmd.executeAsync(null, dummyParams);
					drListener.wait(1000);
					assertNotNull(device.getLatestResponse());
					assertNotNull(device.getLatestRequest());
					assertEquals(device.getLatestRequest(), req);
					assertEquals(drListener.lastResponseEvent.getRequest(), req);
					assertEquals(drListener.lastResponseEvent.getResponse(),
					    device.getLatestResponse());

					device.removeResponseListener(drListener);
					assertEquals(nListeners,
					    device.getResponseListeners().length);

					req = asynCmd.executeAsync(crListener, dummyParams);
					crListener.wait(1000);

					assertNotNull(device.getLatestResponse());
					assertNotNull(device.getLatestRequest());

					assertEquals(device.getLatestResponse(),
					    crListener.lastResponseEvent.getResponse());
					assertEquals(device.getLatestRequest(), req);
					assertEquals(crListener.lastResponseEvent.getRequest(), req);
					assertEquals(crListener.lastResponseEvent.getResponse(),
					    device.getLatestResponse());
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private class LinkListenerImpl extends LinkAdapter
	{
		private ConnectionState expectedState;
		private boolean responseReceived;

		public void setExpectedState(ConnectionState st)
		{
			responseReceived = false;
			expectedState = st;
		}

		public void destroyed(ConnectionEvent e)
		{
			responseReceived = true;
			assertEquals(e.getState(), expectedState);
		}
	}

	public void testDestroy() throws InterruptedException
	{
		AbstractDevice dev = getDevice();
		LinkListenerImpl lli = new LinkListenerImpl();
		lli.setExpectedState(ConnectionState.DESTROYED);
		dev.addLinkListener(lli);
		getContext().destroy();

		synchronized (lli) {
			lli.wait(1000);
		}

		assertTrue(lli.responseReceived);
		assertTrue(dev.isDestroyed());
	}
}

/* __oOo__ */
