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
 * Object, which is becomes fully initialized and functional outside of scope of
 * constructor, reports the change in its lifecycle's internal state through
 * <code>LifecycleEvent</code> instances. Lifecycle phase of reporter is
 * defined by <code>LifecyclePhase</code> enumeration objects. Lifecycle
 * state shifts from one phase to next one in following order:
 * <ul>
 * <li><code>BEFORE_INITIALIZATION</code> - After construction, object
 * should not be used.</li>
 * <li><code>INITIALIZING</code> - Object is signalaing that is in
 * process of initialization, which could be of loner time period.</li>
 * <li><code>INITIALIZED</code> - Object is initialized and ready to be used.
 * It is safe to use the object.</li>
 * <li><code>DESTROYING</code> - Object is in process of destruction, should
 * not be used any more.</li>
 * <li><code>DESTROYED</code> - Object is destroyed and no invocation should
 * be made on object.</li>
 * </ul>
 * <p>
 * If initialization process failes by any mean, reporter is allowed to skip
 * the <code>INITIALIZED</code> phase and go directly to the
 * <code>DESTROYING</code> phase.
 * <p/>
 * <p>
 * The <code>is...()</code> methods serve to check the state of the lifecycle
 * reporter at any time (and especially before the object registers as the lifecycle
 * listener, so that it knows the initial state of the reporter).
 * <p/>
 * <p>
 * The <code>LifecycleListener</code> prescribes when the value of the state changes (i.e.
 * in general the change-of-state happens just before the events are distributed).
 * See <code>LifecycleEvent</code> documentation for info about phased destruction
 * or initialization.
 * </p>
 *
 * @author        Gasper Tkacik
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 *
 * @see org.csstudio.dal.context.LifecycleListener
 * @see org.csstudio.dal.context.LifecycleState
 */
public interface LifecycleReporter
{
	/**
	 * Adds a lifecycle listener.
	 *
	 * @param l a listener object
	 */
	void addLifecycleListener(LifecycleListener l);

	/**
	 * Remove a lifecycle listener.
	 *
	 * @param l a listener object
	 */
	void removeLifecycleListener(LifecycleListener l);

	LifecycleListener[] getLifecycleListeners();

	/**
	 * Returns class which defines reporter's phase.
	 *
	 * @return LifecyclePhase lifecycle phase of this reporter
	 */
	LifecycleState getLifecycleState();
}

/* __oOo__ */
