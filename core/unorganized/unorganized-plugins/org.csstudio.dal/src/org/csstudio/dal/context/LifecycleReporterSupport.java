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

import com.cosylab.util.ListenerList;


/**
 * <code>LifecycleReporterSupport</code> is convenience implementation of
 * <code>LifecycleReporter</code>. Supports fireing
 * <code>LifecycleEvent</code> objects with default <code>fire()</code> event
 * and with <code>fire(LifecycleEvent)</code> custom event. Each time fire
 * method is invoked this class first checkes if it is in appropriate
 * lifecycle phase. If it's not, than <code>IllegalStateException</code> is
 * thrown. If this class is in correct phase, the phase is advanced to the
 * next phase (see LifecycleReporter for details) and event is propagated to
 * listeners.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public class LifecycleReporterSupport implements LifecycleReporter
{
    private ListenerList listeners = new ListenerList(LifecycleListener.class);
    private LifecycleState state = LifecycleState.BEFORE_INITIALIZATION;
    private LifecycleReporter source;

    /**
         * Default constructor for <code>LifecycleReporterSupport</code>.
         * This constructor calls <code>LifecycleReporterSupport(null,LifecyclePhase.BEFORE_INITIALIZATION)</code>.
         */
    public LifecycleReporterSupport()
    {
        this(null, LifecycleState.BEFORE_INITIALIZATION);
    }

    /**
         * Constructor for <code>LifecycleReporterSupport</code> which takes
         * parameter <code>source</code> for default source in generated
         * <code>LifecycleEvent</code> events.
         * This constructor calls <code>LifecycleReporterSupport(<b>source</b>,LifecyclePhase.BEFORE_INITIALIZATION)</code>.
         * @param source a default source in generated
         * <code>LifecycleEvent</code> events
         */
    public LifecycleReporterSupport(LifecycleReporter source)
    {
        this(source, LifecycleState.BEFORE_INITIALIZATION);
    }

    /**
         * Constructor for <code>LifecycleReporterSupport</code> with initial
         * state to be set from provided parameters.
         * @param source a default source in generated
         * <code>LifecycleEvent</code> events, if <code>null</code> this object will be
         * event's source
         * @param phase sets the initial phase to this object
         */
    public LifecycleReporterSupport(LifecycleReporter source,
        LifecycleState phase)
    {
        super();

        if (source == null) {
            source = this;
        }

        this.source = source;
        this.state = phase;
    }

    /**
     *
     * @see LifecycleReporter#addLifecycleListener(LifecycleListener)
     */
    public void addLifecycleListener(LifecycleListener l)
    {
        listeners.add(l);
    }

    /**
     *
     * @see LifecycleReporter#removeLifecycleListener(LifecycleListener)
     */
    public void removeLifecycleListener(LifecycleListener l)
    {
        listeners.remove(l);
    }

    /**
     *
     * @see LifecycleReporter#getLifecycleState()
     */
    public LifecycleState getLifecycleState()
    {
        return state;
    }

    /**
     * Moves the state of this support object from
     * <code>BEFORE_INITIALIZATION</code> to <code>INITIALIZING</code> phase
     * and notifies listeners that source of this support object has begun with
     * initialization process. All exception caught during event dispatching
     * are collected and rethrown as single <code>CombinedException</code>.
     */
    public void fireInitializing()
    {
        if (!(state == LifecycleState.BEFORE_INITIALIZATION)) {
            throwIllegalStateException("fireInitializing");
        }

        state = LifecycleState.INITIALIZING;

        LifecycleEvent event = new LifecycleEvent(source, state);

        LifecycleListener[] l = (LifecycleListener[])listeners.toArray();

        for (int i = 0; i < l.length; i++) {
            l[i].initializing(event);
        }
    }

    /**
     * Moves the state of this support object from
     * <code>INITIALIZING</code> to <code>INITIALIZED</code> phase and notifies
     * listeners that source of this support object is ready to be used. All
     * exception caught during event dispatching are collected and rethrown as
     * single <code>CombinedException</code>.
     */
    public void fireInitialized()
    {
        if (state != LifecycleState.INITIALIZING) {
            throwIllegalStateException("fireInitializing");
        }

        state = LifecycleState.INITIALIZED;

        LifecycleEvent event = new LifecycleEvent(source, state);

        LifecycleListener[] l = (LifecycleListener[])listeners.toArray();

        for (int i = 0; i < l.length; i++) {
            l[i].initializing(event);
        }
    }

    /**
     * Moves the state of this support object from
     * <code>INITIALIZED</code> or <code>INITIALIZING</code> to
     * <code>DESTROYING</code> phase and notifies listeners that source of this
     * support object is about to be destroyed. All exception caught during
     * event dispatching are collected and rethrown as single
     * <code>CombinedException</code>.
     */
    public void fireDestroying()
    {
        if (state != LifecycleState.INITIALIZED
            && state != LifecycleState.INITIALIZING) {
            throwIllegalStateException("fireInitializing");
        }

        state = LifecycleState.DESTROYING;

        LifecycleEvent event = new LifecycleEvent(source, state);

        LifecycleListener[] l = (LifecycleListener[])listeners.toArray();

        for (int i = 0; i < l.length; i++) {
            l[i].destroying(event);
        }
    }

    /**
     * Moves the state of this support object from
     * <code>DESTROYING</code> to  <code>DESTROYED</code> phase and notifies
     * listeners that source of  this support object is destroyed and should
     * not be used any more if it is  not recyclable.  All exception caught
     * during event dispatching are collected and rethrown as single
     * <code>CombinedException</code>.
     */
    public void fireDestroyed()
    {
        if (state != LifecycleState.DESTROYING) {
            throwIllegalStateException("fireInitializing");
        }

        state = LifecycleState.DESTROYED;

        LifecycleEvent event = new LifecycleEvent(source, state);

        LifecycleListener[] l = (LifecycleListener[])listeners.toArray();

        for (int i = 0; i < l.length; i++) {
            l[i].destroyed(event);
        }
    }

    private void throwIllegalStateException(String method)
        throws IllegalStateException
    {
        throw new IllegalStateException("The '" + source
            + "' can not go out of '" + state + "' phase with '" + method
            + "'!");
    }

    /**
     * Returns all registered lifecycle listeners.
     *
     * @return all registered lifecycle listeners
     */
    public LifecycleListener[] getLifecycleListeners()
    {
        return (LifecycleListener[])listeners.toArray();
    }
} /* __oOo__ */


/* __oOo__ */
