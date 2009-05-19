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
package org.epics.css.dal.simulation;

import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DynamicValueCondition;
import org.epics.css.dal.DynamicValueState;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.Request;
import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.SimpleProperty;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.impl.PropertyUtilities;
import org.epics.css.dal.impl.RequestImpl;
import org.epics.css.dal.impl.ResponseImpl;
import org.epics.css.dal.proxy.AbstractProxyImpl;
import org.epics.css.dal.proxy.DirectoryProxy;
import org.epics.css.dal.proxy.MonitorProxy;
import org.epics.css.dal.proxy.PropertyProxy;
import org.epics.css.dal.proxy.ProxyEvent;
import org.epics.css.dal.proxy.ProxyListener;
import org.epics.css.dal.proxy.SyncPropertyProxy;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Simulations implementations of proxy.
 *
 * @author ikriznar
 *
 */
public class PropertyProxyImpl<T> extends AbstractProxyImpl
	implements PropertyProxy<T>, SyncPropertyProxy<T>, DirectoryProxy
{
	protected ValueProvider<T> valueProvider = new MemoryValueProvider<T>();
	protected DynamicValueCondition condition = new DynamicValueCondition(EnumSet
		    .of(DynamicValueState.NORMAL), System.currentTimeMillis(), null);
	protected List<MonitorProxyImpl> monitors = new ArrayList<MonitorProxyImpl>(1);
	protected boolean isSettable = true;

	/**
	 * Creates new instance.
	 * @param name
	 */
	public PropertyProxyImpl(String name)
	{
		this(name, (Long)SimulatorUtilities.getConfiguration(SimulatorUtilities.CONNECTION_DELAY));
	}
	
	/**
	 * Creates new instance.
	 * @param name
	 * @param connectDelay
	 */
	public PropertyProxyImpl(String name, long connectDelay)
	{
		super(name);
		delayedConnect(connectDelay);
	}

	public void delayedConnect(long timeout)
	{
		if (timeout > 0) {
			Timer t = new Timer();
			t.schedule(new TimerTask() {
					@Override
					public void run()
					{
						setConnectionState(ConnectionState.CONNECTED);
					}
				}, timeout);
		} else {
			setConnectionState(ConnectionState.CONNECTED);
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.PropertyProxy#getValueAsync(org.epics.css.dal.ResponseListener)
	 */
	public Request<T> getValueAsync(ResponseListener<T> callback)
		throws DataExchangeException
	{
		if (getConnectionState() != ConnectionState.CONNECTED) {
			throw new DataExchangeException(this, "Proxy not connected");
		}

		RequestImpl<T> r = new RequestImpl<T>(this, callback);
		r.addResponse(new ResponseImpl<T>(this, r, valueProvider.get(), "value",
		        true, null, condition, null, true));

		return r;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.PropertyProxy#setValueAsync(T, org.epics.css.dal.ResponseListener)
	 */
	public Request<T> setValueAsync(T value, ResponseListener<T> callback)
		throws DataExchangeException
	{
		if (getConnectionState() != ConnectionState.CONNECTED) {
			throw new DataExchangeException(this, "Proxy not connected");
		}

		setValueSync(value);

		RequestImpl<T> r = new RequestImpl<T>(this, callback);
		r.addResponse(new ResponseImpl<T>(this, r, value, "", true, null,
		        condition, null, true));

		return r;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.PropertyProxy#isSettable()
	 */
	public boolean isSettable()
	{
		return isSettable;
	}

	public void setSettable(boolean settable)
	{
		this.isSettable = settable;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.PropertyProxy#createMonitor(org.epics.css.dal.ResponseListener)
	 */
	public MonitorProxy createMonitor(ResponseListener<T> callback)
		throws RemoteException
	{
		MonitorProxyImpl<T> m = new MonitorProxyImpl<T>(this, callback);
		monitors.add(m);

		return m;
	}

	public void destroy()
	{
		super.destroy();

		MonitorProxyImpl[] m = monitors.toArray(new MonitorProxyImpl[monitors
			    .size()]);

		for (int i = 0; i < m.length; i++) {
			m[i].destroy();
		}
		setConnectionState(ConnectionState.DESTROYED);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.PropertyProxy#getCondition()
	 */
	public DynamicValueCondition getCondition()
	{
		return condition;
	}

	/**
	 * Intended for only within plug.
	 * @param s new condition state.
	 */
	public void setCondition(DynamicValueCondition s)
	{
		if (condition.areStatesEqual(s)) {
			return;
		}

		condition = s;
		fireCondition();
	}

	
	/**
	 * Fires new characteristics changed event
	 */
	
	protected void fireCharacteristicsChanged(PropertyChangeEvent ev)
	{
		if (proxyListeners == null) 
			return;
		
		ProxyListener[] l = (ProxyListener[])proxyListeners.toArray();

		for (int i = 0; i < l.length; i++) {
			try {
				l[i].characteristicsChange(ev);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	/**
	 * Fires new condition event.
	 */
	protected void fireCondition()
	{
		if (proxyListeners == null) 
			return;
		
		ProxyListener<T>[] l = (ProxyListener<T>[])proxyListeners.toArray();
		ProxyEvent<PropertyProxy<T>> ev = new ProxyEvent<PropertyProxy<T>>(this,
			    condition, connectionState, null);

		for (int i = 0; i < l.length; i++) {
			try {
				l[i].dynamicValueConditionChange(ev);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see DirectoryProxy#getCharacteristicNames()
	 */
	public String[] getCharacteristicNames() throws DataExchangeException
	{
		return SimulatorUtilities.getCharacteristicNames(this);
	}

	/**
	 * @see DirectoryProxy#getCommandNames()
	 */
	public String[] getCommandNames() throws DataExchangeException
	{
		// property does not support commands
		throw new UnsupportedOperationException();
	}

	/**
	 * @see DirectoryProxy#getCommandParameterTypes(String)
	 */
	public Class[] getCommandParameterTypes(String commandName)
		throws DataExchangeException
	{
		// property does not support commands
		throw new UnsupportedOperationException();
	}

	/**
	 * @see DirectoryProxy#getCharacteristics(String[], ResponseListener)
	 */
	public Request<? extends Object> getCharacteristics(String[] characteristics,
	    ResponseListener<? extends Object> callback) throws DataExchangeException
	{
		RequestImpl<Object> r = new RequestImpl<Object>(this, (ResponseListener<Object>) callback);

		for (int i = 0; i < characteristics.length; i++) {
			Object value = PropertyUtilities.verifyCharacteristic(this, characteristics[i], getCharacteristic(characteristics[i]));
			r.addResponse(new ResponseImpl<Object>(this, r, value, characteristics[i],
			        value != null, null, condition, null, true));
		}

		return r;
	}

	/**
	 * @throws
	 * @see DirectoryProxy#getCharacteristic(String)
	 */
	public Object getCharacteristic(String characteristicName)
		throws DataExchangeException
	{
		return PropertyUtilities.verifyCharacteristic(this, characteristicName, SimulatorUtilities.getCharacteristic(characteristicName, this));
	}

	public T getValueSync() throws DataExchangeException
	{
		if (getConnectionState() != ConnectionState.CONNECTED) {
			throw new DataExchangeException(this, "Proxy not connected");
		}

		return valueProvider.get();
	}

	public void setValueSync(T value) throws DataExchangeException
	{
		if (getConnectionState() != ConnectionState.CONNECTED) {
			throw new DataExchangeException(this, "Proxy not connected");
		}

		valueProvider.set(value);

		MonitorProxyImpl[] m = monitors.toArray(new MonitorProxyImpl[monitors
			    .size()]);

		for (int i = 0; i < m.length; i++) {
			m[i].fireValueChange();
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getPropertyNames()
	 */
	public String[] getPropertyNames()
	{
		throw new UnsupportedOperationException("This is not device proxy.");
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getPropertyType(java.lang.String)
	 */
	public Class<?extends SimpleProperty<T>> getPropertyType(String propertyName)
	{
		throw new UnsupportedOperationException("This is not device proxy.");
	}

	/**
	 * @return Returns the valueProvder.
	 */
	public ValueProvider<T> getValueProvider()
	{
		return valueProvider;
	}

	/**
	 * @param valueProvder The valueProvder to set.
	 */
	public void setValueProvider(ValueProvider<T> valueProvder)
	{
		try {
			valueProvder.set(this.valueProvider.get());
		} catch (Exception e) {
			// noop
		}

		this.valueProvider = valueProvder;
	}

	public void refresh()
	{
		// Override in order to clean up cached values.
	}

	/*
	 *  (non-Javadoc)
	 * @see org.epics.css.dal.proxy.AbstractProxyImpl#setConnectionState(org.epics.css.dal.context.ConnectionState)
	 */
	@Override
	protected void setConnectionState(ConnectionState s)
	{
		super.setConnectionState(s);

		EnumSet<DynamicValueState> set = null;

		if (s == ConnectionState.CONNECTED) {
			set = EnumSet.of(DynamicValueState.NORMAL);
		} else if (s == ConnectionState.DISCONNECTED) {
			set = EnumSet.of(DynamicValueState.LINK_NOT_AVAILABLE);
		} else if (s == ConnectionState.DESTROYED) {
			set = EnumSet.of(DynamicValueState.LINK_NOT_AVAILABLE);
		} else if (s == ConnectionState.CONNECTION_FAILED) {
			set = EnumSet.of(DynamicValueState.ERROR);
		} else if (s == ConnectionState.CONNECTION_LOST) {
			set = EnumSet.of(DynamicValueState.ERROR);
		} else {
			set = EnumSet.of(DynamicValueState.NORMAL);
		}

		setCondition(new DynamicValueCondition(set, 0,
		        "Connection state changed"));
	}

	/**
	 * Simulate changes of connection state
	 * @param state Connection State
	 */
	public void simulateConnectionState(ConnectionState state)
	{
		setConnectionState(state);
	}
	
	public void simulateCharacteristicChange(String characteristicName, Object value) {
		Object old= SimulatorUtilities.putCharacteristic(characteristicName, getUniqueName(), value);
		fireCharacteristicsChanged(new PropertyChangeEvent(this,characteristicName,old,value));
	}
}

/* __oOo__ */
