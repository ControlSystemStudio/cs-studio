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

import org.csstudio.dal.simple.RemoteInfo;


/**
 * An interface for the object that is able to control its own life-cycle of the
 * connection to remote object. This includes the transition between initial state
 * (not-connected, not-ready to connect) to ready state (not-connected, ready to connect)
 * to connected state and back to initial state, allowing for all error states that
 * can occur in the process (connection-failed, disconnected). Connectables can be
 * members of <code>Family</code> instances. Each connectable can be managed separately
 * and independently (this includes creation, connection and destruction). All connectable
 * methods should be synchronized.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 *
 */
public interface Connectable extends Linkable, ContextBean
{
    /**
     * Adds a listener object that will receive notifications about the
     * life cycle state of this <code>Connectable</code>.
     *
     * @param l a listener object
     */
    void addConnectionListener(ConnectionListener l);

    /**
     * Synchronously connects this <code>Connectable</code> to remote
     * data source.  Method does not return untill connection is established
     * or failed. If this call fails,  should restore the
     * <code>Connectable</code> to <code>CONNECTABLE_INITIAL</code>.
     *
     * @exception ConnectionException when the connection has begun but an
     *            error has occured before the proxy implementation could be
     *            passed to <code>initialize</code>.
     * @exception IllegalStateException when the connection was attempted but
     *            the connectable was not in <code>CONNECTABLE_READY</code>
     *            state.
     */
    void connect() throws ConnectionException, IllegalStateException;

    /**
     * Forces asynchronous connect, does not wait (block) for
     * comlpetion. Listen to <code>ConnectionListener</code> or
     * <code>ResponseListener</code> events to check resutl of operation.
     *
     * @exception ConnectionException when the connection has begun but an
     *            error has occured before the proxy implementation could be
     *            passed to <code>initialize</code>.
     * @exception IllegalStateException when the connection was attempted but
     *            the connectable was not in <code>CONNECTABLE_READY</code>
     *            state.
     *
     * @see #connect()
     */
    void asyncConnect() throws ConnectionException, IllegalStateException;

    /**
     * Disconnects the connectable. The connectable will switch to
     * state <code>CONNECTABLE_DISCONNECTED</code>.
     */
    void disconnect();

    /**
     * Irrevocably destroys the connectable. The connectable will
     * switch to state <code>CONNECTABLE_DESTROYED</code> and will be unusable
     * (every remote call will throw an exception, as well as any attempt to
     * connect).
     */
    void destroy();

    /**
     * Returns the current life cycle state of this
     * <code>Connectable</code>. Possible values are constants defined in
     * <code>ConnectableConstants</code> starting with
     * <code>CONNECTABLE_</code>.
     *
     * @return connection status
     */
    ConnectionState getConnectionState();

    /**
     * Returns the <code>RemoteInfo</code> for <code>this</code>.
     *
     * @return connection parameters
     */
    RemoteInfo getRemoteInfo();

    /**
     * Removes connection listener.
     *
     * @param l a listener object
     */
    void removeConnectionListener(ConnectionListener l);

    /**
     * Sets the <code>RemoteInfo</code> of <code>this</code>. Throws an
     * <code>IllegalArgumentException</code> if the parameters are invalid
     * (plug type does not exist or some other inconsistency). In response to
     * this call, the <code>Connectable</code> can issue
     * <code>CONNECTABLE_READY</code> message.
     *
     * @param rinfo parameters to set
     *
     * @exception IllegalArgumentException if, in response
     *            to<code>CONNECTABLE_READY</code> a connection was attemped
     *            by calling <code>connect()</code> and failed
     */
    void setRemoteInfo(RemoteInfo rinfo) throws IllegalArgumentException;

    /**
     * @see {@link #setAutoConnect(boolean)}
     */
    public boolean isAutoConnect();

    /**
     * If autoConnect is true, Device is automatically connected when all requirements
     * are provided. Default value is <code>true</code>.
     */
    public void setAutoConnect(boolean autoConnect);

    /**
     * Returns true, if {@link #getConnectionState()} is {@link ConnectionState#CONNECTING}.
     */
    public boolean isConnecting();

}

/* __oOo__ */
