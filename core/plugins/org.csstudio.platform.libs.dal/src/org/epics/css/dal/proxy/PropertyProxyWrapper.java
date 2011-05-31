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

package org.epics.css.dal.proxy;

import java.util.Map;

import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DynamicValueCondition;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.Request;
import org.epics.css.dal.Response;
import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.context.Identifier;


/**
 * Wrapper for property proxy, which adds missing synchronous functionality for asynchronous only
 * property proxy.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 * @version $id$
  *
 * @param <T> exact data type
 */
public class PropertyProxyWrapper<T,P extends AbstractPlug> implements SyncPropertyProxy<T,P>
{
	private PropertyProxy<T,P> proxy;

	public P getPlug() {
		return proxy.getPlug();
	}
	
	/**
	 * Creates a new PropertyProxyWrapper object.
	 *
	 * @param proxy Proxy to wrap
	 */
	public PropertyProxyWrapper(PropertyProxy<T,P> proxy)
	{
		this.proxy = proxy;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.SyncPropertyProxy#getValueSync()
	 */
	public T getValueSync() throws DataExchangeException
	{
		return new GetValueInterceptor<T>().executeAndWait(proxy);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.SyncPropertyProxy#setValueSync(T)
	 */
	public void setValueSync(T value) throws DataExchangeException
	{
		new SetValueInterceptor<T>().executeAndWait(proxy, value);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.PropertyProxy#destroy()
	 */
	public void destroy()
	{
		proxy.destroy();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.PropertyProxy#getValueAsync(java.lang.String, org.epics.css.dal.ResponseListener)
	 */
	public Request<T> getValueAsync(ResponseListener<T> callback)
		throws DataExchangeException
	{
		return proxy.getValueAsync(callback);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.PropertyProxy#setValueAsync(java.lang.String, T, org.epics.css.dal.ResponseListener)
	 */
	public Request<T> setValueAsync(T value, ResponseListener<T> callback)
		throws DataExchangeException
	{
		return proxy.setValueAsync(value, callback);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.PropertyProxy#getUniqueName()
	 */
	public String getUniqueName()
	{
		return proxy.getUniqueName();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.PropertyProxy#isSettable()
	 */
	public boolean isSettable()
	{
		return proxy.isSettable();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.PropertyProxy#createMonitor(org.epics.css.dal.ResponseListener)
	 */
	public MonitorProxy createMonitor(ResponseListener<T> callback,
			Map<String, Object> parameters) throws RemoteException {
		return proxy.createMonitor(callback,parameters);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.Proxy#addProxyListener(org.epics.css.dal.proxy.ProxyListener)
	 */
	public void addProxyListener(ProxyListener<?> l)
	{
		proxy.addProxyListener(l);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.Proxy#removeProxyListener(org.epics.css.dal.proxy.ProxyListener)
	 */
	public void removeProxyListener(ProxyListener<?> l)
	{
		proxy.removeProxyListener(l);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.PropertyProxy#getCondition()
	 */
	public DynamicValueCondition getCondition()
	{
		return proxy.getCondition();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.Proxy#getConnectionState()
	 */
	public ConnectionState getConnectionState()
	{
		return proxy.getConnectionState();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Identifiable#getIdentifier()
	 */
	public Identifier getIdentifier()
	{
		return proxy.getIdentifier();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Identifiable#isDebug()
	 */
	public boolean isDebug()
	{
		return proxy.isDebug();
	}
	
	public Response<T> getLatestValueResponse() {
		return proxy.getLatestValueResponse();
	}
	
	public String getConnectionInfo() {
		return proxy.getConnectionInfo();
	}
}

/* __oOo__ */
