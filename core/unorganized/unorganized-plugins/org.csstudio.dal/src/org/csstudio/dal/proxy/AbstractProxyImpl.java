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

package org.csstudio.dal.proxy;

import org.apache.log4j.Logger;
import org.csstudio.dal.context.ConnectionState;
import org.csstudio.dal.context.Identifier;
import org.csstudio.dal.context.IdentifierUtilities;

import com.cosylab.util.ListenerList;


/**
 * Simulation implementation of Proxy interface.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public abstract class AbstractProxyImpl<P extends AbstractPlug> implements Proxy<P>
{
	protected boolean debug = false;
	protected String name;
	protected ListenerList proxyListeners;
	protected ConnectionStateMachine connectionStateMachine = new ConnectionStateMachine();
	protected Identifier identifier;
	protected P plug;
	private String connectionInfo;
	
	/* (non-Javadoc)
	 * @see org.csstudio.dal.context.Identifiable#getIdentifier()
	 */
	public Identifier getIdentifier()
	{
		if (identifier == null) {
			identifier = IdentifierUtilities.createIdentifier(this);
		}

		return identifier;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.context.Identifiable#isDebug()
	 */
	public boolean isDebug()
	{
		return debug;
	}

	/**
	     * Default construcor.
	     */
	public AbstractProxyImpl(String name, P plug)
	{
		super();
		this.name = name;
		this.plug = plug;
	}
	
	public P getPlug() {
		return plug;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.proxy.Proxy#getUniqueName()
	 */
	public String getUniqueName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.proxy.Proxy#destroy()
	 */
	public void destroy()
	{
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.proxy.Proxy#addProxyListener(org.csstudio.dal.proxy.ProxyListener)
	 */
	public void addProxyListener(ProxyListener<?> l)
	{
		if (proxyListeners == null) {
			proxyListeners = new ListenerList(ProxyListener.class);
		}

		proxyListeners.add(l);

		ProxyEvent<Proxy<?>> e = new ProxyEvent<Proxy<?>>(this, null,
			    connectionStateMachine.getConnectionState(), null);

		try {
			l.connectionStateChange(e);
		} catch (Exception ex) {
			Logger.getLogger(this.getClass()).error("Failed to forward listener.", ex);
		}
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.proxy.Proxy#removeProxyListener(org.csstudio.dal.proxy.ProxyListener)
	 */
	public void removeProxyListener(ProxyListener<?> l)
	{
		if (proxyListeners != null) {
			proxyListeners.remove(l);
		}
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.proxy.Proxy#getConnectionState()
	 */
	public ConnectionState getConnectionState()
	{
		return connectionStateMachine.getConnectionState();
	}

	/**
	 * Intended for only within plug.
	 *
	 * @param s new connection state.
	 */
	public void setConnectionState(final ConnectionState s, final Throwable error)
	{
		if (connectionStateMachine.requestNextConnectionState(s)) {
			if (connectionStateMachine.getConnectionState()==ConnectionState.CONNECTED) {
				connectionInfo=null;
				getConnectionInfo();
			}
			handleConnectionState(s);
			fireConnectionState(s,error);
		}
	}
	/**
	 * Intended for only within plug.
	 *
	 * @param s new connection state.
	 */
	public void setConnectionState(ConnectionState s)
	{
		setConnectionState(s, null);
	}
	/**
	 * This method is called after connection state was changed but change was not jet 
	 * fired to listeners. 
	 * Plug implementation may want to override this method to provide internal 
	 * synchronization of proxy with new state.
	 * @param s the new connection state
	 */
	protected void handleConnectionState(ConnectionState s) {
	}

	/**
	 * Fires new connection event.
	 */
	protected void fireConnectionState(ConnectionState c, Throwable error)
	{
		if (proxyListeners == null) {
			return;
		}

		ProxyListener<?>[] l = (ProxyListener<?>[])proxyListeners.toArray();
		ProxyEvent<Proxy<?>> e = new ProxyEvent<Proxy<?>>(this, null,
			    c, error);

		for (int i = 0; i < l.length; i++) {
			try {
				l[i].connectionStateChange(e);
			} catch (Exception ex) {
				Logger.getLogger(this.getClass()).error("Exception in event handler, continuing.", ex);
			}
		}
	}
	
	public String getConnectionInfo() {
		if (connectionInfo == null) {
			if (connectionStateMachine.isConnected()) {
				StringBuilder sb= new StringBuilder(128);
				sb.append(name);
				sb.append('@');
				if (plug!=null) {
					sb.append(plug.getPlugType());
				} else {
					sb.append("UNKNOWN_PLUG");
				}
				sb.append('/');
				sb.append(getRemoteHostInfo());
				connectionInfo= sb.toString();
			} else {
				StringBuilder sb= new StringBuilder(128);
				sb.append(name);
				sb.append('@');
				if (plug!=null) {
					sb.append(plug.getPlugType());
				} else {
					sb.append("UNKNOWN_PLUG");
				}
				sb.append('/');
				sb.append("(NOT_CONNECTED)");
				connectionInfo= sb.toString();
			}
		}
		return connectionInfo;
	}
	
	/**
	 * Plug implementation should override this to provide remote host information.
	 * Something like: HOST_NAME:PORT or HOST_IP:PORT.
	 * @return remote host information
	 */
	protected String getRemoteHostInfo() {
		return "UNKNOWN_HOST";
	}
}

/* __oOo__ */
