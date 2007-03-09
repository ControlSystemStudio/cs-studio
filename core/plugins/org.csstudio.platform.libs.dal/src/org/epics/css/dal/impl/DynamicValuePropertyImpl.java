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

import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.EventSystemListener;
import org.epics.css.dal.ExpertMonitor;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.Request;
import org.epics.css.dal.Response;
import org.epics.css.dal.ResponseEvent;
import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.context.ConnectionEvent;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.context.LinkListener;
import org.epics.css.dal.context.PropertyContext;
import org.epics.css.dal.proxy.DirectoryProxy;
import org.epics.css.dal.proxy.PropertyProxy;
import org.epics.css.dal.proxy.Proxy;
import org.epics.css.dal.proxy.ProxyEvent;
import org.epics.css.dal.proxy.ProxyListener;

import java.beans.PropertyChangeListener;

import java.util.Iterator;
import java.util.Map;


/**
 * Glue code implementation of DynamicValueProperty.
 *
 * @author ikriznar
 *
 */
public class DynamicValuePropertyImpl<T> extends SimplePropertyImpl<T>
	implements DynamicValueProperty<T>
{
	protected PropertyContext propertyContext;
	protected ListenerList responseListeners = new ListenerList(ResponseListener.class);
	protected ResponseListener defaultResponseListener = new ResponseListener() {
			public void responseReceived(ResponseEvent event)
			{
				fireResponseReceived(event);
			}

			public void responseError(ResponseEvent event)
			{
				fireResponseError(event);
			}
		};

	protected ListenerList linkListeners = new ListenerList(LinkListener.class);
	protected ProxyListener proxyListener = new ProxyListener() {
			public void connectionStateChange(ProxyEvent<Proxy> e)
			{
				setConnectionState(e.getConnectionState());
			}

			public void dynamicValueConditionChange(ProxyEvent<PropertyProxy> e)
			{
				condition = e.getCondition();
				fireCondition();
			}
		};

	protected Request lastRequest = null;
	protected Request lastValueRequest = null;
	protected Response lastResponse = null;
	protected Response lastValueResponse = null;
	private int suspended = 0;
	protected ConnectionState connectionState = ConnectionState.INITIAL;

	private class ResponseForwarder implements ResponseListener
	{
		private ResponseListener listener;

		/**
		         * Creates a new ResponseForwarder object.
		         *
		         * @param listener Response listener
		         */
		public ResponseForwarder(ResponseListener listener)
		{
			this.listener = listener;
		}

		/**
		 * Accepts responsReceived notifications
		 *
		 * @param event Response event
		 */
		public void responseReceived(ResponseEvent event)
		{
			if (listener != null) {
				listener.responseReceived(event);
			}

			//fireResponseReceived(event);
		}

		/**
		 * Accepts responseError notifications
		 *
		 * @param event Response event
		 */
		public void responseError(ResponseEvent event)
		{
			if (listener != null) {
				listener.responseError(event);
			}

			//fireResponseError(event);
		}
	}

	/**
	     * @param valClass
	     */
	public DynamicValuePropertyImpl(Class<T> valClass, String name,
	    PropertyContext propertyContext)
	{
		super(valClass, name);
		this.propertyContext = propertyContext;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DynamicValueProperty#getParentContext()
	 */
	public PropertyContext getParentContext()
	{
		return propertyContext;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.AsynchronousAccess#getAsynchronous()
	 */
	public Request getAsynchronous() throws DataExchangeException
	{
		return getAsynchronous(null);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.AsynchronousAccess#getAsynchronous(org.epics.css.dal.ResponseListener)
	 */
	public Request getAsynchronous(ResponseListener listener)
		throws DataExchangeException
	{
		/*
		    lastValueRequest = proxy.getValueAsync(listener == null
		            ? defaultResponseListener : new ResponseForwarder(listener));
		*/
		lastValueRequest = proxy.getValueAsync(new ResponseForwarder(listener));
		lastRequest = lastValueRequest;

		return lastValueRequest;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.AsynchronousAccess#setAsynchronous(T)
	 */
	public Request setAsynchronous(T value) throws DataExchangeException
	{
		return setAsynchronous(value, null);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.AsynchronousAccess#setAsynchronous(T, org.epics.css.dal.ResponseListener)
	 */
	public Request setAsynchronous(T value, ResponseListener listener)
		throws DataExchangeException
	{
		lastValueRequest = proxy.setValueAsync(value,
			    listener == null ? defaultResponseListener
			    : new ResponseForwarder(listener));
		lastRequest = lastValueRequest;

		return lastValueRequest;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.AsynchronousContext#addResponseListener(org.epics.css.dal.ResponseListener)
	 */
	public void addResponseListener(ResponseListener l)
	{
		responseListeners.add(l);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.AsynchronousContext#removeResponseListener(org.epics.css.dal.ResponseListener)
	 */
	public void removeResponseListener(ResponseListener l)
	{
		responseListeners.remove(l);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.AsynchronousContext#getResponseListeners()
	 */
	public ResponseListener[] getResponseListeners()
	{
		return (ResponseListener[])responseListeners.toArray(new ResponseListener[responseListeners
		    .size()]);
	}

	protected void fireResponseReceived(ResponseEvent event)
	{
		lastResponse = event.getResponse();

		Iterator ite = responseListeners.iterator();

		while (ite.hasNext()) {
			((ResponseListener)ite.next()).responseReceived(event);
		}
	}

	protected void fireResponseError(ResponseEvent event)
	{
		lastResponse = event.getResponse();

		Iterator ite = responseListeners.iterator();

		while (ite.hasNext()) {
			((ResponseListener)ite.next()).responseError(event);
		}
	}

	void setLastValueResponse(Response r)
	{
		lastValueResponse = r;
		lastValue = (T)r.getValue();
		lastTimestamp = r.getTimestamp();
	}

	void setLastResponse(Response r)
	{
		lastResponse = r;
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
	 * @see org.epics.css.dal.AsynchronousCharacteristicContext#getCharacteristicsAsynchronously(java.lang.String[])
	 */
	public Request getCharacteristicsAsynchronously(String[] names)
		throws DataExchangeException
	{
		lastRequest = directoryProxy.getCharacteristics(names,
			    defaultResponseListener);

		return lastRequest;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.AsynchronousCharacteristicContext#getCharacteristicAsynchronously(java.lang.String)
	 */
	public Request getCharacteristicAsynchronously(String name)
		throws DataExchangeException
	{
		lastRequest = directoryProxy.getCharacteristics(new String[]{ name },
			    defaultResponseListener);

		return lastRequest;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.Updateable#getLatestValueRequest()
	 */
	public Request getLatestValueRequest()
	{
		return lastValueRequest;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.Updateable#getLatestValueResponse()
	 */
	public Response getLatestValueResponse()
	{
		return lastValueResponse;
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
		if (connectionState == ConnectionState.CONNECTED
		    || connectionState == ConnectionState.CONNECTION_LOST) {
			return true;
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Linkable#isDestroyed()
	 */
	public boolean isDestroyed()
	{
		return connectionState == ConnectionState.DESTROYED;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Linkable#isSuspended()
	 */
	public boolean isSuspended()
	{
		return suspended > 0;
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
	 * @see org.epics.css.dal.context.Linkable#refresh()
	 */
	public void refresh() throws RemoteException
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
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
	 * @see org.epics.css.dal.context.Identifiable#isDebug()
	 */
	public boolean isDebug()
	{
		return false;
	}

	/**
	 * @return Returns the connectionState.
	 */
	public ConnectionState getConnectionState()
	{
		return connectionState;
	}

	/**
	 * @param connectionState The connectionState to set.
	 */
	protected void setConnectionState(ConnectionState connectionState)
	{
		if (this.connectionState == connectionState) {
			return;
		}

		this.connectionState = connectionState;

		LinkListener[] l = (LinkListener[])linkListeners.toArray();

		ConnectionEvent e = new ConnectionEvent(this, connectionState);

		if (connectionState == ConnectionState.CONNECTED) {
			for (int i = 0; i < l.length; i++) {
				try {
					l[i].connected(e);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} else if (connectionState == ConnectionState.CONNECTION_LOST) {
			for (int i = 0; i < l.length; i++) {
				try {
					l[i].connectionLost(e);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} else if (connectionState == ConnectionState.DISCONNECTED) {
			for (int i = 0; i < l.length; i++) {
				try {
					l[i].disconnected(e);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} else if (connectionState == ConnectionState.DESTROYED) {
			for (int i = 0; i < l.length; i++) {
				try {
					l[i].destroyed(e);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.impl.SimplePropertyImpl#initialize(org.epics.css.dal.proxy.PropertyProxy, org.epics.css.dal.proxy.DirectoryProxy)
	 */
	@Override
	public void initialize(PropertyProxy<T> proxy, DirectoryProxy dirProxy)
	{
		if (this.proxy != null) {
			this.proxy.removeProxyListener(proxyListener);
		}

		super.initialize(proxy, dirProxy);

		if (proxy != null) {
			proxy.addProxyListener(proxyListener);
		}
	}

	protected void fireCondition()
	{
		DynamicValueEvent<T, DynamicValueProperty> dve = new DynamicValueEvent<T, DynamicValueProperty>(this,
			    this, lastValue, condition, null, "Condition changed");
		DynamicValueListener[] listen = new DynamicValueListener[dvListeners
			.size()];
		listen = (DynamicValueListener[])dvListeners.toArray(listen);

		for (int i = 0; i < listen.length; i++) {
			listen[i].conditionChange(dve);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.impl.SimplePropertyImpl#addDynamicValueListener(org.epics.css.dal.DynamicValueListener)
	 */
	@Override
	public void addDynamicValueListener(DynamicValueListener l)
	{
		super.addDynamicValueListener(l);

		DynamicValueEvent<T, DynamicValueProperty> dve = new DynamicValueEvent<T, DynamicValueProperty>(this,
			    this, lastValue, condition, null, "Condition changed");
		l.conditionChange(dve);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DynamicValueProperty#getSupportedExpertMonitorParameters()
	 */
	public Map<String, Object> getSupportedExpertMonitorParameters()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DynamicValueProperty#createNewExpertMonitor(org.epics.css.dal.DynamicValueListener, java.util.Map)
	 */
	public ExpertMonitor createNewExpertMonitor(DynamicValueListener listener,
	    Map<String, Object> parameters) throws RemoteException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.EventSystemContext#addEventSystemListener(org.epics.css.dal.EventSystemListener, java.util.Map)
	 */
	public void addEventSystemListener(
	    EventSystemListener<DynamicValueEvent> l, Map<String, Object> parameters)
		throws RemoteException
	{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.EventSystemContext#addEventSystemListener(org.epics.css.dal.EventSystemListener)
	 */
	public void addEventSystemListener(EventSystemListener<DynamicValueEvent> l)
		throws RemoteException
	{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.EventSystemContext#removeEventSystemListener(org.epics.css.dal.EventSystemListener, java.util.Map)
	 */
	public void removeEventSystemListener(
	    EventSystemListener<DynamicValueEvent> l, Map<String, Object> parameters)
	{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.EventSystemContext#removeEventSystemListener(org.epics.css.dal.EventSystemListener)
	 */
	public void removeEventSystemListener(
	    EventSystemListener<DynamicValueEvent> l)
	{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.EventSystemContext#getEventSystemListeners()
	 */
	public EventSystemListener<DynamicValueEvent>[] getEventSystemListeners()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.EventSystemContext#getSupportedEventSystemParameters()
	 */
	public Map<String, Object> getSupportedEventSystemParameters()
	{
		// TODO Auto-generated method stub
		return null;
	}
} /* __oOo__ */


/* __oOo__ */
