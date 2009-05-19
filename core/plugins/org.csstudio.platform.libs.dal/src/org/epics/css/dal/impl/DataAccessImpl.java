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

import org.epics.css.dal.DataAccess;
import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.SimpleProperty;
import org.epics.css.dal.Timestamp;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.proxy.PropertyProxy;
import org.epics.css.dal.proxy.PropertyProxyWrapper;
import org.epics.css.dal.proxy.SyncPropertyProxy;


/**
 * Default implementation of <code>DataAccess</code> interface.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 *
 */
public abstract class DataAccessImpl<T> implements DataAccess<T>
{
	protected PropertyProxy<T> proxy;
	protected SyncPropertyProxy<T> sproxy;
	protected Class<T> valClass;
	protected ListenerList dvListeners = new ListenerList(DynamicValueListener.class);
	protected T lastValue;

	/**
	     * Constructor.
	     * @param valClass datatype class
	     */
	protected DataAccessImpl(Class<T> valClass)
	{
		this.valClass = valClass;
	}

	/**
	 * Initializes this instance. Before data access is initialized, it
	 * will throw exceptions if used.
	 *
	 * @param proxy the proxy which presents remote connection
	 *
	 * @throws NullPointerException is thrown if supplied proxy is null
	 */
	protected void initialize(PropertyProxy<T> proxy)
	{
		if (proxy == null) {
			throw new NullPointerException("proxy");
		}

		this.proxy = proxy;

		if (proxy instanceof SyncPropertyProxy) {
			sproxy = (SyncPropertyProxy<T>)proxy;
		} else {
			sproxy = new PropertyProxyWrapper<T>(proxy);
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DataAccess#addDynamicValueListener(org.epics.css.dal.DynamicValueListener)
	 */
	public <P extends SimpleProperty<T>> void addDynamicValueListener(DynamicValueListener<T, P> l)
	{
		dvListeners.add(l);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DataAccess#removeDynamicValueListener(org.epics.css.dal.DynamicValueListener)
	 */
	public <P extends SimpleProperty<T>> void removeDynamicValueListener(DynamicValueListener<T, P> l)
	{
		dvListeners.remove(l);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DataAccess#getDynamicValueListeners()
	 */
	public DynamicValueListener<T, SimpleProperty<T>>[] getDynamicValueListeners()
	{
		return (DynamicValueListener<T, SimpleProperty<T>>[])dvListeners.toArray(new DynamicValueListener[dvListeners
		    .size()]);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DataAccess#getDataType()
	 */
	public Class<T> getDataType()
	{
		//by now, Java does not support geting class object out of type parameter
		//this is the only solution
		return valClass;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DataAccess#isSettable()
	 */
	public boolean isSettable()
	{
		return proxy.isSettable();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DataAccess#setValue(T)
	 */
	public void setValue(T value) throws DataExchangeException
	{
		if (sproxy.getConnectionState() != ConnectionState.CONNECTED) {
			throw new DataExchangeException(this, "Proxy not connected");
		}
		sproxy.setValueSync(value);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DataAccess#getValue()
	 */
	public T getValue() throws DataExchangeException
	{
		if (sproxy.getConnectionState() != ConnectionState.CONNECTED) {
			throw new DataExchangeException(this, "Proxy not connected");
		}

		return lastValue=sproxy.getValueSync();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DataAccess#getLatestReceivedValue()
	 */
	public T getLatestReceivedValue()
	{
		return lastValue;
	}

	/**
	 * Returns the PropertyProxy which represents the remote connection of this 
	 * DataAccess.
	 * 
	 * @return the property proxy
	 */
	public PropertyProxy<T> getProxy()
	{
		return proxy;
	}
}

/* __oOo__ */
