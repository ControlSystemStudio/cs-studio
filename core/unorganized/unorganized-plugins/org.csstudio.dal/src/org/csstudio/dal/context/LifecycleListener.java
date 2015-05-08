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
 * Listens to lifecycle events. The sources of lifecycle events
 * are those interfaces, that either:
 * <ul>
 * <li>Have to do their initialization out of constructor, and the
 *    time of such initialization depends asnychronously on some
 *    other action being completed. In this case the implementor
 *    of this interface will not be ready for service when its
 *    constructor has finished, but later. The exact time is signaled
 *    by firing an event into this method. </li>
 * <li>Have to perform explicit destruction. Therefore there exists a time
 *    frame - from the time when the explicit destruction occured to the
 *    time that the object (if ever) is released and garbage collected -
 *    during which the references to this object are accessible, but the
 *    object itself is useless. An event fired into this listener interface
 *    signals that the object should not be used.</li>
 * </ul>
 * <p>
 * Note the exact names of the listener methods: <code>destroying()</code>
 * means that the notification is dispatched at least (barring phased notification,
 * see <code>LifecycleEvent</code> documentation) when the destruction
 * <b>begins</b>. <code>initialized()</code> is dispatched when the
 * initialization ends. A lifecycle reporter, it must first return <code>true</code> on
 * <code>isDestroying()</code> and <code>false</code> on <code>isDestroyed()</code>,
 * and the reverse when the destruction finishes.
 * </p>
 *
 * @author        Gasper Tkacik
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public interface LifecycleListener extends EventListener
{
    /**
     * Notification that the object is dying. "Dying" means that the
     * object has begun its destruction process. At the begining of the
     * process, the <code>isDestroying()</code> invoked on the
     * <code>LifecycleReporter</code> instance will return <code>true</code>.
     *
     * @param event event object, non-<code>null</code>
     */
    void destroying(LifecycleEvent event);

    /**
     * Notification that the object completed its destruction process.
     * The <code>isDestroyed()</code> invoked on the
     * <code>LifecycleReporter</code> instance will return <code>true</code>.
     *
     * @param event event object, non-<code>null</code>
     */
    void destroyed(LifecycleEvent event);

    /**
     * Notifies that the initialization has been completed. The object
     * has deterined that it is in the initialized state and is ready to
     * process requests. Notice that that may happen sometime after the
     * constructor has already completed, especially if the object listened to
     * depends on the completion of some asynchronous operation. Before
     * distributing such events, <code>LifecycleReporter</code> will return
     * <code>true</code> on a call to <code>isInitialized()</code>.
     *
     * @param event event object, non-<code>null</code>.
     */
    void initialized(LifecycleEvent event);

    /**
     * Notifies that the initialization process has begun.  Before
     * distributing such events, <code>LifecycleReporter</code> will return
     * <code>true</code> on a call to <code>isInitializing()</code>.
     *
     * @param event event object, non-<code>null</code>.
     */
    void initializing(LifecycleEvent event);
}

/* __oOo__ */
