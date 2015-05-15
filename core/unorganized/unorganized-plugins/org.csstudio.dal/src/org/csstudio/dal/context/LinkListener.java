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

import java.util.EventListener;


/**
 * Listener for link events.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public interface LinkListener<C extends Linkable> extends EventListener
{
    /**
     * Linkable was resumed after suspend operation.
     *
     * @param e dispatched event object
     */
    public void resumed(ConnectionEvent<C> e);

    /**
     * Linkable was suspended.
     *
     * @param e dispatched event object
     */
    public void suspended(ConnectionEvent<C> e);

    /**
     * Linkable was successfully connected to remote object.
     *
     * @param e dispatched event object
     */
    public void connected(ConnectionEvent<C> e);

    /**
     * Linkable was successfully connected to remote object and got all initial data.
     *
     * @param e dispatched event object
     */
    public void operational(ConnectionEvent<C> e);

    /**
     * Linkable was disconnected from remote object.
     *
     * @param e dispatched event object
     */
    public void disconnected(ConnectionEvent<C> e);

    /**
     * Linkable has lost connection to remote object, while internal state
     * of linkable signals, that has not been disconnected locally.
     *
     * @param e dispatched event object
     */
    public void connectionLost(ConnectionEvent<C> e);

    /**
     * Invoked after the connectable has been destroyed. Destroying
     * releases remote resources and resources allocated by connectable.
     * Destroyed objects cannot be reused.
     *
     * @param e event object
     */
    void destroyed(ConnectionEvent<C> e);

    /**
     * Invoked when, during the connection, a plug or connectable
     * detected an error and aborted the connection. The connectable will
     * attempt to switch to initial state as soon as possible.
     *
     * @param e event object
     */
    void connectionFailed(ConnectionEvent<C> e);
}

/* __oOo__ */
