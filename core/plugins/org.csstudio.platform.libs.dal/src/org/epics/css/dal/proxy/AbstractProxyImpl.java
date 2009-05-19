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

import com.cosylab.util.ListenerList;

import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.context.Identifier;
import org.epics.css.dal.context.IdentifierUtilities;


/**
 * Simulation implementation of Proxy interface.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public abstract class AbstractProxyImpl implements Proxy
{
	protected boolean debug = false;
	protected String name;
	protected ListenerList proxyListeners;
	protected ConnectionState connectionState = ConnectionState.INITIAL;
	protected Identifier identifier;

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
		return debug;
	}

	/**
	     * Default construcor.
	     */
	public AbstractProxyImpl(String name)
	{
		super();
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.Proxy#getUniqueName()
	 */
	public String getUniqueName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.Proxy#destroy()
	 */
	public void destroy()
	{
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.Proxy#addProxyListener(org.epics.css.dal.proxy.ProxyListener)
	 */
	public void addProxyListener(ProxyListener<?> l)
	{
		if (proxyListeners == null) {
			proxyListeners = new ListenerList(ProxyListener.class);
		}

		proxyListeners.add(l);

		ProxyEvent<Proxy> e = new ProxyEvent<Proxy>(this, null,
			    connectionState, null);

		try {
			l.connectionStateChange(e);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.Proxy#removeProxyListener(org.epics.css.dal.proxy.ProxyListener)
	 */
	public void removeProxyListener(ProxyListener<?> l)
	{
		if (proxyListeners != null) {
			proxyListeners.remove(l);
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.Proxy#getConnectionState()
	 */
	public ConnectionState getConnectionState()
	{
		return connectionState;
	}

	/**
	 * Intended for only within plug.
	 *
	 * @param s new connection state.
	 */
	protected void setConnectionState(ConnectionState s)
	{
		connectionState = s;
		fireConnectionState();
	}

	/**
	 * Fires new connection event.
	 */
	protected void fireConnectionState()
	{
		if (proxyListeners == null) {
			return;
		}

		ProxyListener<?>[] l = (ProxyListener<?>[])proxyListeners.toArray();
		ProxyEvent<Proxy> e = new ProxyEvent<Proxy>(this, null,
			    connectionState, null);

		for (int i = 0; i < l.length; i++) {
			try {
				l[i].connectionStateChange(e);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}

/* __oOo__ */
