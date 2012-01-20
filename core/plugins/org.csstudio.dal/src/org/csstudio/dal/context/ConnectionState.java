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
 * Defines a set of states, which define connection state of local
 * representation of remote object as result of local connection manipulation.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public enum ConnectionState {
	/**
	 * A constant indicating that the <code>Connectable</code> has been created,
	 * but it is not yet ready because the conditions for the connection are not
	 * yet met (e.g. connection parameters have not yet been set).
	 * This event may be sent again even if the <code>Connectable</code> is in
	 * such state (i.e. there was no state change). In this case it means that the
	 * transition to another state was attempted, failed and as a side effect of
	 * the failure the <code>Connectable</code> again returned to the initial
	 * state.
	 */
	INITIAL, 
	/**
	 * A constant indicating that the <code>Connectable</code> is ready to
	 * connect. This state is entered when all properties that connectable object
	 * needs to connect have been set and the <code>connect</code> method can be
	 * called without throwing an exception.
	 */
	READY, 
	/**
	 * A constant indicating that connection is in progress. During this state
	 * most of the functionality of the <code>Connectable</code> is suspended.
	 * Note, however, that the <code>Connectable</code> can be destroyed while
	 * it is in this state.
	 */
	CONNECTING, 
	/**
	 * A constant indicating that the <code>Connectable</code> and the pluggable
	 * system have successfully completed the connection, that is, that the
	 * methods which delegate to remote object can be called withoug throwing an
	 * exception. The transition to this state will be acompanied by
	 * <code>connected</code> being invoked on all listeners.
	 */
	CONNECTED, 
	/**
	 * A constant indicating that the <code>Connectable</code> and the pluggable
	 * system have successfully completed the connection plus has retrieved basic 
	 * set of meta-data and has requested and is receiving live value updates 
	 * from remote connection.  
	 * The transition to this state will be accompanied by
	 * <code>operational</code> being invoked on all listeners.
	 */
	OPERATIONAL,
	/**
	 * A constant indicating that Connectable has lost already established
	 * connection to remote object. This may happend if remote object is not
	 * available any more (eg. remote server is restarted or ethernet link is broken).
	 * <code>Connectable</code> in this state can eather return to <code>CONNECTED</code>
	 * if link has been restored or to <code>DISCONNECTING</code> if connection is
	 * beeing cloded manually.
	 */
	CONNECTION_LOST,
	/**
	 * A constant indicating that the <code>Connectable</code> and the pluggable
	 * system have unsuccessfully tried to perform the connection to the remote
	 * object. The process has failed and this message is issued.
	 * <code>Connectable</code> will return to <code>REDY</code>
	 * state by cleanup of all temporary connection objects. This value is
	 * equivalent to the <code>Linkable</code> super of the
	 * <code>Connectable</code> returning <code>true</code> on
	 * <code>isConnected()</code> call.
	 */
	CONNECTION_FAILED, 
	/**
	 * A constant indicating that disconnection is in progress.
	 */
	DISCONNECTING, 
	/**
	 * A constant indicating that a successfully established connection has been
	 * broken either on purpose or because of network failure.
	 * <code>Connectable</code> will return to <code>REDY</code>
	 * state by cleanup of all connection objects. The transition to this state
	 * will be acompanied by <code>disconnected</code> being invoked on all
	 * listeners.
	 */
	DISCONNECTED, 
	/**
	 * A constant indicating that the connectable object has been destroyed,
	 * probably because its enclosing context is being destroyed. Destroyed
	 * connectables cannot be used again (i.e. they must be recreated and
	 * reconnected). This value is equivalent to the <code>Linkable</code> super
	 * of the <code>Connectable</code> returning <code>false</code> on
	 * <code>isConnected()</code> call.
	 */
	DESTROYED;
	
	
	/**
	 * Return true if connection was successfully established. Actual connection 
	 * state might be CONNECTED, CONNECTION_LOST or OPERATIONAL.
	 * @return if connection process has been successfully completed.
	 */
	public boolean isConnected() {
		if (this == ConnectionState.CONNECTED
			    || this == ConnectionState.CONNECTION_LOST
			    || this == ConnectionState.OPERATIONAL) {
				return true;
			}
		return false;
	}

	public boolean isConnectionAlive() {
		if (this == ConnectionState.CONNECTED
			    || this == ConnectionState.OPERATIONAL) {
				return true;
			}
		return false;
	}

}
/* __oOo__ */
