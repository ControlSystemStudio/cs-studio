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

package org.epics.css.dal.impl;

import com.cosylab.util.ListenerList;
import com.cosylab.util.ObjectList;

import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.Request;
import org.epics.css.dal.Response;
import org.epics.css.dal.ResponseEvent;
import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.commands.AsynchronousCommand;
import org.epics.css.dal.commands.Command;
import org.epics.css.dal.context.ConnectionEvent;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.context.Identifier;
import org.epics.css.dal.context.IdentifierUtilities;
import org.epics.css.dal.context.LifecycleReporterSupport;
import org.epics.css.dal.context.LinkListener;
import org.epics.css.dal.context.PropertyContext;
import org.epics.css.dal.device.AbstractDevice;
import org.epics.css.dal.group.GroupDataAccess;
import org.epics.css.dal.group.PropertyGroupConstrain;
import org.epics.css.dal.proxy.CommandProxy;
import org.epics.css.dal.proxy.DeviceProxy;
import org.epics.css.dal.proxy.DirectoryProxy;
import org.epics.css.dal.proxy.PropertyProxy;
import org.epics.css.dal.proxy.Proxy;
import org.epics.css.dal.proxy.ProxyEvent;
import org.epics.css.dal.proxy.ProxyListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Glue implementation of AbstractDevice around DeviceProxy and
 * DirectoryProxy.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public class AbstractDeviceImpl extends LifecycleReporterSupport
	implements AbstractDevice
{
	class ProxyInterceptor implements ProxyListener
	{
		/* (non-Javadoc)
		 * @see org.epics.css.dal.proxy.ProxyListener#connectionStateChange(org.epics.css.dal.proxy.Proxy, org.epics.css.dal.context.ConnectionState)
		 */
		public void connectionStateChange(ProxyEvent<Proxy> e)
		{
			if (e.getProxy() != deviceProxy) {
				return;
			}

			// fix if proxy did not fire connected to this listener
			if (e.getConnectionState() == ConnectionState.CONNECTION_LOST
			    && connectionState != ConnectionState.CONNECTED) {
				setConnectionState(ConnectionState.CONNECTED);
			}

			setConnectionState(e.getConnectionState());
		}

		/* (non-Javadoc)
		 * @see org.epics.css.dal.proxy.ProxyListener#dynamicValueConditionChange(org.epics.css.dal.proxy.PropertyProxy, java.util.EnumSet)
		 */
		public void dynamicValueConditionChange(ProxyEvent e)
		{
			// not needed for device
		}
	}

	private class PropertyInterceptor implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			firePropertyChangeEvent(evt);
		}
	}

	protected ResponseListener defaultResponseLitener = new ResponseListener() {
			public void responseReceived(ResponseEvent event)
			{
				fireResponseReceived(event);
			}

			public void responseError(ResponseEvent event)
			{
				fireResponseReceived(event);
			}
		};

	protected ListenerList propertyListeners = new ListenerList(PropertyChangeListener.class);
	protected ListenerList responseListeners = new ListenerList(ResponseListener.class);
	protected Identifier identifier;
	protected DeviceProxy deviceProxy;
	protected DirectoryProxy directoryProxy;
	protected String name;
	protected ConnectionState connectionState = ConnectionState.INITIAL;
	protected ProxyInterceptor proxyInterceptor = new ProxyInterceptor();
	protected PropertyChangeListener propertyInterceptor = new PropertyInterceptor();
	protected String[] propertyNames;
	protected ArrayList<LinkListener> linkListeners = new ArrayList<LinkListener>();
	protected Request lastRequest = null;
	protected Response lastResponse = null;
	private int suspended = 0;
	private boolean isdebug = false;

	/**
	 * Do not access this field diretly.
	 *
	 * @see #getProperties()
	 */
	protected Map<String, DynamicValueProperty> properties;
	protected String[] commandNames;
	protected Map<String, Command> commands;

	/**
	     *
	     */
	public AbstractDeviceImpl(String name)
	{
		super();
		this.name = name;
	}

	/**
	 * Initializes the device. Device and directory proxy must both be defined or both <code>null</code>
	 *
	 * @param devp Device proxy
	 * @param dirp Directory proxy
	 *
	 * @throws IllegalStateException is thrown device has already been initialized
	 * @throws IllegalArgumentException is thrown if both proxies aren't non-null or null.
	 */
	public void initialize(DeviceProxy devp, DirectoryProxy dirp)
		throws IllegalStateException
	{
		if ((devp != null && dirp == null) || (devp == null && dirp != null)) {
			throw new IllegalArgumentException(
			    "Both proxies must be null or non-null.");
		}

		if (devp == null && dirp == null) {
			if (connectionState != ConnectionState.DISCONNECTING) {
				setConnectionState(ConnectionState.DISCONNECTING);

				//throw new IllegalStateException("Only device in DISCONNECTING can be diconnected, not in "+connectionState+".");
			}

			if (deviceProxy != null) {
				deviceProxy.removeProxyListener(proxyInterceptor);
			}

			deviceProxy = null;
			directoryProxy = null;
			setConnectionState(ConnectionState.DISCONNECTED);

			return;
		}

		if (this.deviceProxy != null || this.directoryProxy != null) {
			throw new IllegalStateException("Device '" + name
			    + "' is already initialized.");
		}

		deviceProxy = devp;
		directoryProxy = dirp;
		deviceProxy.addProxyListener(proxyInterceptor);

		// additional check if there was race condition within the listner. Anyway reduntant events will be ignored
		if (deviceProxy.getConnectionState() == ConnectionState.CONNECTED
		    || deviceProxy.getConnectionState() == ConnectionState.CONNECTION_FAILED
		    || deviceProxy.getConnectionState() == ConnectionState.CONNECTION_LOST) {
			setConnectionState(deviceProxy.getConnectionState());
		}
	}

	/**
	 * This method implements lazy initialization of properties field.
	 * Do not access properties field  directly but use this method.
	 *
	 * @return ensures lazy creation of properties array
	 */
	protected Map<String, DynamicValueProperty> getProperties()
	{
		if (properties == null) {
			properties = new HashMap<String, DynamicValueProperty>(getPropertyNames().length);

			String[] names = getPropertyNames();

			for (int i = 0; i < names.length; i++) {
				properties.put(names[i], null);
			}
		}

		return properties;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.device.AbstractDevice#getUniqueName()
	 */
	public String getUniqueName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.commands.CommandContext#getCommand(java.lang.String)
	 */
	public Command getCommand(String name) throws RemoteException
	{
		if (commands == null) {
			commands = new HashMap<String, Command>(getCommandNames().length);
		}

		Command c = commands.get(name);

		if (c == null) {
			CommandProxy cp = deviceProxy.getCommand(name);

			if (cp.isAsynchronous()) {
				c = new AsynchronousCommandImpl(cp, this, defaultResponseLitener);
				commands.put(name, c);
			} else {
				c = new CommandImpl(cp, this);
				commands.put(name, c);
			}
		}

		return c;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.commands.CommandContext#getCommandNames()
	 */
	public String[] getCommandNames() throws RemoteException
	{
		if (commandNames == null) {
			commandNames = directoryProxy.getCommandNames();
		}

		String[] n = new String[commandNames.length];
		System.arraycopy(commandNames, 0, n, 0, commandNames.length);

		return n;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.commands.CommandContext#getCommands()
	 */
	public Command[] getCommands() throws RemoteException
	{
		String[] commNames = getCommandNames();
		Command[] comms = new Command[commNames.length];

		for (int i = 0; i < commNames.length; i++) {
			comms[i] = getCommand(commNames[i]);
		}

		return comms;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.PropertyContext#containsProperty(java.lang.Object)
	 */
	public boolean containsProperty(Object property)
	{
		return getProperties().containsValue(property);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.PropertyContext#containsProperty(java.lang.String)
	 */
	public boolean containsProperty(String name)
	{
		return getProperties().containsKey(name);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.PropertyContext#getProperty(java.lang.String)
	 */
	public synchronized DynamicValueProperty getProperty(String name)
	{
		if (!containsProperty(name)) {
			throw new IllegalArgumentException("Property '" + name
			    + "' not found in '" + getUniqueName() + "'.");
		}

		DynamicValueProperty p = getProperties().get(name);

		if (p == null) {
			try {
				p = createProperty(name);
				properties.put(name, p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return p;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.PropertyContext#getPropertyNames()
	 */
	public String[] getPropertyNames()
	{
		if (propertyNames == null) {
			propertyNames = directoryProxy.getPropertyNames();
		}

		String[] n = new String[propertyNames.length];
		System.arraycopy(propertyNames, 0, n, 0, propertyNames.length);

		return n;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.PropertyContext#toPropertyArray()
	 */
	public DynamicValueProperty[] toPropertyArray()
	{
		String[] n = getPropertyNames();
		DynamicValueProperty[] p = new DynamicValueProperty[n.length];

		for (int i = 0; i < n.length; i++) {
			p[i] = getProperty(n[i]);
		}

		return p;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Identifiable#getIdentifier()
	 */
	public Identifier getIdentifier()
	{
		if (identifier == null) {
			identifier = IdentifierUtilities.createIdentifier(this);
		}

		return identifier;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Identifiable#isDebug()
	 */
	public boolean isDebug()
	{
		return isdebug;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.AsynchronousContext#addResponseListener(org.epics.css.dal.ResponseListener)
	 */
	public void addResponseListener(ResponseListener l)
	{
		responseListeners.add(l);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.AsynchronousContext#getLatestRequest()
	 */
	public Request getLatestRequest()
	{
		return lastRequest;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.AsynchronousContext#getLatestResponse()
	 */
	public Response getLatestResponse()
	{
		return lastResponse;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.AsynchronousContext#getLatestSuccess()
	 */
	public boolean getLatestSuccess()
	{
		return lastResponse == null ? true : lastResponse.success();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.AsynchronousContext#getResponseListeners()
	 */
	public ResponseListener[] getResponseListeners()
	{
		return (ResponseListener[])responseListeners.toArray(new ResponseListener[responseListeners
		    .size()]);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.AsynchronousContext#removeResponseListener(org.epics.css.dal.ResponseListener)
	 */
	public void removeResponseListener(ResponseListener l)
	{
		responseListeners.remove(l);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Linkable#addLinkListener(org.epics.css.dal.context.LinkListener)
	 */
	public void addLinkListener(LinkListener l)
	{
		linkListeners.add(l);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Linkable#isConnected()
	 */
	public boolean isConnected()
	{
		return connectionState == ConnectionState.CONNECTED
		|| connectionState == ConnectionState.CONNECTION_LOST;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Linkable#isDestroyed()
	 */
	public boolean isDestroyed()
	{
		return connectionState == ConnectionState.DESTROYED;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Linkable#isConnectionAlive()
	 */
	public boolean isConnectionAlive()
	{
		return connectionState == ConnectionState.CONNECTED;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Linkable#isConnectionFailed()
	 */
	public boolean isConnectionFailed()
	{
		return connectionState == ConnectionState.CONNECTION_FAILED;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Linkable#isSuspended()
	 */
	public boolean isSuspended()
	{
		return suspended > 0;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Linkable#refresh()
	 */
	public void refresh() throws RemoteException
	{
		deviceProxy.refresh();
		directoryProxy.refresh();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Linkable#removeLinkListener(org.epics.css.dal.context.LinkListener)
	 */
	public void removeLinkListener(LinkListener l)
	{
		linkListeners.remove(l);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Linkable#resume()
	 */
	public void resume() throws RemoteException
	{
		if (suspended > 0) {
			suspended--;
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Linkable#suspend()
	 */
	public void suspend() throws RemoteException
	{
		suspended++;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.group.GroupDataAccessProvider#getGroupDataAccess(java.lang.Class, java.lang.Class, org.epics.css.dal.group.PropertyGroupConstrain)
	 */
	public <T, P extends DynamicValueProperty> GroupDataAccess<T, P> getGroupDataAccess(
	    Class<T> dataType, Class<P> propertyType,
	    PropertyGroupConstrain constrain)
	{
		// NOT TO BE DONE
		// Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.group.GroupDataAccessProvider#getGroupDataAccess(java.lang.Class, java.lang.Class)
	 */
	public <T, P extends DynamicValueProperty> GroupDataAccess<T, P> getGroupDataAccess(
	    Class<T> dataType, Class<P> propertyType)
	{
		// NOT TO BE DONE 
		// auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.CharacteristicContext#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener l)
	{
		propertyListeners.add(l);
	}

	protected void firePropertyChangeEvent(PropertyChangeEvent e)
	{
		for (int i = 0; i < propertyListeners.size(); i++) {
			((PropertyChangeListener)propertyListeners.get(i)).propertyChange(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.CharacteristicContext#getCharacteristic(java.lang.String)
	 */
	public Object getCharacteristic(String name) throws DataExchangeException
	{
		// NOT TO BE DONE
		// Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.CharacteristicContext#getCharacteristicNames()
	 */
	public String[] getCharacteristicNames() throws DataExchangeException
	{
		// NOT TO BE DONE
		// Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.CharacteristicContext#getCharacteristics(java.lang.String[])
	 */
	public Map getCharacteristics(String[] names) throws DataExchangeException
	{
		// NOT TO BE DONE
		// Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.CharacteristicContext#getPropertyChangeListeners()
	 */
	public PropertyChangeListener[] getPropertyChangeListeners()
	{
		return (PropertyChangeListener[])propertyListeners.toArray(new PropertyChangeListener[propertyListeners
		    .size()]);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.CharacteristicContext#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener l)
	{
		propertyListeners.remove(l);
	}

	/**
	 * Sets connection state and fires event.
	 *
	 * @param s new state
	 */
	protected void setConnectionState(ConnectionState s)
	{
		synchronized (this) {
			if (connectionState == s) {
				return;
			}

			connectionState = s;
		}

		ConnectionEvent<AbstractDevice> e = new ConnectionEvent<AbstractDevice>(this,
			    connectionState);

		LinkListener[] listenersArry = linkListeners.toArray(new LinkListener[linkListeners
			    .size()]);

		switch (connectionState) {
		case CONNECTED: {
			for (LinkListener l : listenersArry) {
				l.connected(e);
			}

			break;
		}

		case DISCONNECTED: {
			for (LinkListener l : listenersArry) {
				l.disconnected(e);
			}

			break;
		}

		case CONNECTION_LOST: {
			for (LinkListener l : listenersArry) {
				l.connectionLost(e);
			}

			break;
		}

		case DESTROYED: {
			for (LinkListener l : listenersArry) {
				l.destroyed(e);
			}

			break;
		}

		default:
			break;
		}
	}

	protected DynamicValueProperty createProperty(String name)
		throws RemoteException, IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException
	{
		PropertyProxy pp = deviceProxy.getPropertyProxy(name);
		DirectoryProxy dp = deviceProxy.getDirectoryProxy(name);

		Class type = directoryProxy.getPropertyType(name);

		// Creates property implementation
		Class impClass = PropertyUtilities.getImplementationClass(type);
		DynamicValuePropertyImpl property = (DynamicValuePropertyImpl)impClass.getConstructor(String.class,
			    PropertyContext.class).newInstance(name, this);

		property.initialize(pp, dp);
		property.addPropertyChangeListener(propertyInterceptor);

		return property;
	}

	protected void fireResponseReceived(ResponseEvent event)
	{
		lastResponse = event.getResponse();
		lastRequest = event.getRequest();

		Iterator ite = responseListeners.iterator();

		while (ite.hasNext()) {
			((ResponseListener)ite.next()).responseReceived(event);
		}
	}

	public DeviceProxy getProxy()
	{
		return deviceProxy;
	}
}

/* __oOo__ */
