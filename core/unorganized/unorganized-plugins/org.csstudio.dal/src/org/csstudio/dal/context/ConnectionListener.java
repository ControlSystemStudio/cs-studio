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

package org.csstudio.dal.context;


/**
 * A listener interface for the <code>Connectable</code> connection state changes.
 *
 */
public interface ConnectionListener<C extends Connectable> extends LinkListener<C>
{
	/**
	 * Invoked when the connection process begins. The connection
	 * begins when  <code>connect()</code> is manually invoked. The exact
	 * timing is specified by the <code>Connector</code>: if synchronous, it
	 * is called just prior to <code>Connector.internalConnect</code>, if
	 * asynchronous, it is called just prior to the <code>Connectable</code>
	 * being added to the connection queue.
	 *
	 * @param e event object
	 */
	void connecting(ConnectionEvent<C> e);

	/**
	 * Invoked when the disconnection process begins. The connection
	 * begins either when the <code>Connectable</code> has switched to
	 * <code>CONNECTABLE_DISCONNECTING</code> state.
	 *
	 * @param e event object
	 */
	void disconnecting(ConnectionEvent<C> e);

	/**
	 * The initial state of the connectable when it is created and also
	 * the state entered after the connectable has cycled through error
	 * condition states (disconnected, connection failed). The connectable
	 * should pass through this state to be reused (for example, before
	 * reconnect).
	 *
	 * @param e event object
	 */
	void initialState(ConnectionEvent<C> e);

	/**
	 * The connectable has entered a state where it awaits the
	 * permission to connect.
	 *
	 * @param e event object
	 */
	void ready(ConnectionEvent<C> e);
}

/* __oOo__ */
