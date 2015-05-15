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

import org.csstudio.dal.RemoteException;


/**
 * <code>Linkable</code> interface represents the object that can report state of its
 * associated remote object, but is incapable of establishing the connection
 * by itself. Thus, <code>Linkable</code> must be initialized by another
 * object that establishes a connection (in final instance, this is always the
 * <code>Connectable</code>).
 *
 * <p>
 * <code>Linkable</code> defines no state except
 * the distinction between the state in which there is a link to remote and a
 * state in which there isn't one. There is no distinction as for the reason
 * why the link was lost or established. If the <code>Linkable</code> is
 * unable to execute the request because it is in invalid state (for example
 * if it is not yet initialized but <code>resume</code> is called), it should
 * quietly ignore the request (it can generate a notification, but must not
 * throw an exception). A failed request should be signaled by an exception. A
 * state distinction between suspended / resumed state can exist only if the
 * <code>Linkable</code> is in link established state.
 *
 * @see org.csstudio.dal.context.Connectable
 */
public interface Linkable extends Identifiable
{
    /**
     * Adds a link listener which will be notified about the changes in
     * the state of <code>this</code>.
     *
     * @param l a listener object
     */
    void addLinkListener(LinkListener<? extends Linkable> l);

    /**
     * Returns <code>true</code> if this <code>Linkable</code> has been
     * successfully connected with remote object. This state can be only
     * changed by  Connectable responsible for this linkable. If remote
     * connection is temporary or permanently (in any case linkable or
     * connectable  this can not know),  than this linkable is regarded as
     * connected until connectable is disconnected.
     *
     * @return boolean linked flag
     */
    boolean isConnected();

    /**
     * Returns <code>true</code> if this <code>Linkable</code> has been
     * successfully connected with remote object and has received meta-data and
     * is receiving live updates. This state can be only
     * changed by  Connectable responsible for this linkable. If remote
     * connection is temporary or permanently (in any case linkable or
     * connectable  this can not know),  than this linkable is regarded as
     * connected until connectable is disconnected.
     *
     * @return boolean linked flag
     */
    boolean isOperational();

    /**
     * Returns <code>true</code> if this <code>Linkable</code> has been
     * destroyed and connection with remote object lost.
     *
     * @return boolean destroyed flag
     */
    boolean isDestroyed();

    /**
     * Returns <code>true</code> iff this <code>Linkable</code> is
     * suspended and in link established state.
     *
     * @return boolean suspended flag
     */
    boolean isSuspended();

    /**
     * Returns <code>true</code> if connection to remote object is
     * alive and  functioning.
     *
     * @return <code>true</code> if connection is alive
     */
    boolean isConnectionAlive();

    /**
     * Returns <code>true</code> if connection process failed.
     *
     * @return <code>true</code> if connection process failed
     */
    boolean isConnectionFailed();

    /**
     * If this <code>Linkable</code> has access to data items of the
     * remote object for which it knows that they are of the static nature
     * (for instance read from the database and knwon not to change during the
     * normal operation of the remote) and such data items are buffered by
     * this <code>Linkable</code>, this method forces <code>this</code> to
     * flag this data items in such a manner, that the next query will read
     * them from the remote source and not from this <code>Linkable's</code>
     * buffer.
     *
     * @throws RemoteException if remote operation fails
     */
    void refresh() throws RemoteException;

    /**
     * Removes a link listener.
     *
     * @param l a listener object
     */
    void removeLinkListener(LinkListener<? extends Linkable> l);

    /**
     * If this <code>Linkable</code> is in suspended state, switch it
     * into the normal (resumed) state. Else do nothing. As general rule
     * if suspend has been called X times, than also resume must be called x times, before
     * linkable is moved from suspended mode to resumed.
     *
     * @throws RemoteException if remote operation fails
     */
    void resume() throws RemoteException;

    /**
     * Switch this <code>Linkable</code> in such a state, that it
     * conserves as much network resources as possible without breaking the
     * connection. Do nothing if this is not possible.
     *
     * @throws RemoteException if remote operation fails
     */
    void suspend() throws RemoteException;

    /**
     * Returns the current life cycle state of this
     * <code>Linkable</code>.
     *
     * @return connection status
     */
    ConnectionState getConnectionState();

} /* __oOo__ */


/* __oOo__ */
