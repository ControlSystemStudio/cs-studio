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

package org.csstudio.dal;


/**
 * Interface <code>SimpleMonitor</code> defines the way in which an observer
 * (realized as JavaBeans listener) can influence the flow of events from the
 * data access event source. Subscription can be changed by each observer separately
 * and it affects that observer only. Event delivery is changed by changing characteristics
 * of the monitor.
 * <p>
 * Timer trigger may operate at the prescribed default frequency, with all other (optional)
 * triggers set to their respective default values. In this case the method
 * <code>isDefault</code> must return <code>true</code>. The default subscription may be
 * handled separately (in a more efficient way) by the underlying implementation.
 * </p>
 * <p>
 * Methods of this interface which generate transient connection objects such as requests and
 * responses, store those objects directly into the latest values of the property to
 * which this monitor belongs.
 * </p>
 */
public interface SimpleMonitor
{
	/**
	 * Returns the value of the timer trigger for this subscription.
	 * The timer trigger is  the promise of the implementation of this
	 * interface to check for the new value in the underlying data source
	 * every <code>long</code> milliseconds. If an event is dispatched in
	 * response to the check depends on the <code>heartbeat</code> value. This
	 * is a writable characteristic of the subscription.
	 *
	 * @return long the amount in milliseconds between the checks for new value
	 *         status in the underlying data source layer
	 *
	 * @exception DataExchangeException if the query for the current trigger
	 *            value fails
	 *
	 * @see #setTimerTrigger
	 * @see #isHeartbeat
	 */
	public long getTimerTrigger() throws DataExchangeException;

	/**
	 * Sets the timer trigger for this subscription. The value supplied
	 * must be positive or zero. If it is zero, the timer trigger is disabled
	 * and no periodic checks will be performed. Of course, other (optional)
	 * triggers may still cause an event to be  dispatched in this case. This
	 * is a writable characteristic of the subscription.
	 *
	 * @param trigger a positive or zero value in milliseconds between the
	 *        checks in the  underlying data source layer
	 *
	 * @exception DataExchangeException if the set of the new trigger fails
	 * @throws UnsupportedOperationException DOCUMENT ME!
	 *
	 * @see #getTimerTrigger
	 */
	public void setTimerTrigger(long trigger)
		throws DataExchangeException, UnsupportedOperationException;

	/**
	 * Sets the heartbeat flag for this subscription. If the heartbeat
	 * is turned on, an event is dispatched to the listeners whenever any
	 * trigger triggers. If the heartbeat is turned off, the event is
	 * dispatched <b>only</b> if the dynamic value or its status has changed.
	 * In any case, the <code>latestUpdateTimestamp</code> in
	 * <code>Updateable</code> interface will be modified.
	 *
	 * @param heartbeat <code>true</code> iff the subscription should generate
	 *        heartbeat  events
	 *
	 * @throws DataExchangeException DOCUMENT ME!
	 * @throws UnsupportedOperationException DOCUMENT ME!
	 *
	 * @see #isHeartbeat
	 */
	public void setHeartbeat(boolean heartbeat)
		throws DataExchangeException, UnsupportedOperationException;

	/**
	 * Returns the value of the heartbeat flag for this subscription.
	 *
	 * @return boolean <code>true</code> iff the subscription generates
	 *         heartbeat events
	 *
	 * @see #setHeartbeat
	 */
	public boolean isHeartbeat();

	/**
	 * Returns the default timer trigger that is used when the
	 * subscription first becomes  active. This is a characteristic of the
	 * subscription. Its value is determined by the implementation.
	 *
	 * @return long the default value of the <code>timerTrigger</code>
	 *         characteristic in  milliseconds
	 *
	 * @exception DataExchangeException if the query for the characteristic
	 *            value fails
	 */
	public long getDefaultTimerTrigger() throws DataExchangeException;

	/**
	 * Returns <code>true</code> if the subscription has default
	 * triggers. The default values of all triggers (timer as well as
	 * optional) are determined by the implementation of this interface. If
	 * the subscription is default, it may be optimized by the underlying
	 * layer. It is expected that most of the subscriptions will, during their
	 * duration,  remain at default triggers. For example, the implementation
	 * may choose to  create only one link to the underlying (remote) layer
	 * for all default subscriptions.
	 *
	 * @return boolean <code>true</code> if this subscription uses default
	 *         triggers
	 */
	public boolean isDefault();

	/**
	 * Destroys this monitor and ends subscription to remote value
	 * changes.
	 */
	public void destroy();

	/**
	 * Returns <code>true</code> if destroy has been called.
	 *
	 * @return <code>true</code> if destroy has been called
	 */
	public boolean isDestroyed();
}

/* __oOo__ */
