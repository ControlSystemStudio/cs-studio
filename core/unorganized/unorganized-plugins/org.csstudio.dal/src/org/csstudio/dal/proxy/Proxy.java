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

import org.csstudio.dal.context.ConnectionState;
import org.csstudio.dal.context.Identifiable;


/**
 * Common interface for all proxies.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 *
 */
public interface Proxy<P extends AbstractPlug> extends Identifiable
{
	/**
	 * Returns the unique name. This name is used to initiate
	 * connection to remote object and can be regardes as remote name.
	 *
	 * @return String unique remote name for this property
	 */
	public String getUniqueName();

	/**
	 * Destroys object and releases all remote and local allocated resources.
	 * <p><b>NOTE</b></br>
	 * Only plug which created this proxy can call this method since lifecycle is controled by the plug.
	 * </p>
	 */
	public void destroy();

	/**
	 * Registers new listener of proxy events.
	 * @param l new listener
	 */
	public void addProxyListener(ProxyListener<?> l);

	/**
	 * Deregisters listener from proxy events.
	 * @param l new listener
	 */
	public void removeProxyListener(ProxyListener<?> l);

	/**
	 * Return connection state enum of the remote object.
	 * @return state of connection to remote object
	 */
	public ConnectionState getConnectionState();
	
	/**
	 * Returns plug instance which governs this proxy object.
	 * Plug implementation decided how plug reference is provided to the 
	 * proxy implementation. Preferred way is trough constructor.
	 * @return
	 */
	public P getPlug();
	
	/**
	 * Returns short description of connection to remote host.
	 * For example: CHANNEL_A@PROTOCOL/HOST:PORT  
	 * @return
	 */
	public String getConnectionInfo();
}

/* __oOo__ */
