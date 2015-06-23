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

import java.util.EventListener;


/**
 * This interface is implemented by the listeners that wish to be informed about the
 * changes in the dynamic value. The change in the dynamic value includes both the
 * change in the value itself or the change of data quality. The notifications are
 * dispatched in the usual JavaBeans way (i.e. ordering not important, all listeners
 * get the same events) only in the case where the implementation does not offer
 * subscription control through <code>DynamicValueMonitor</code> interface. Where such
 * control is available, the notifications will be sent to this listener interface
 * on a per-listener basis, when one of the conditions known as <i>triggers</i> is
 * fulfilled.
 * <p>
 * Data quality is determined by the underlying implementation. Errors connected with
 * event timing can be declared here statically, because the corresponding interface
 * <code>DynamicValueMonitor</code> statically declares the timer trigger. Other errors,
 * for example the value falling outside the alarm limits or some other error, can be
 * signalled through <code>valueErrorResponse</code> event. It is then up to the
 * listener to query the event and unpack the data from <code>ValueUpdateable</code>
 * interface to determine the exact nature of the error.
 * </p>
 * <p>
 * Timing errors generally fall into two categories. Timeout happens, when the value
 * is - for any reason - not delivered on time (promised by the timer trigger) to the
 * listener. In this case timeout should be signalled. More information will usually be
 * contained within the event. Timelag, on the other hand, can happen even if the events
 * are delivered on time. Timelag is a condition, where the timestamp on the data itself
 * (if such concept exists in the underlying layer) differs up to some amount from the
 * local time on the machine where Datatypes are running. This time difference is
 * set by the implementation or can be set through the <code>DynamicValueMonitor</code>
 * interface. It makes sense to check for timelags only if there are guarantees that the
 * clocks of the computers are synchronized within the interval smaller than the timelag
 * parameter.
 * </p>
 *
 * @see DynamicValueMonitor
 */
public interface DynamicValueListener<T, P extends SimpleProperty<T>>
    extends EventListener
{
    /**
     * Notifies the listener that the update in value has occured.
     * Update means that the existing dynamic value has <b>not</b> changed its
     * value, but only that  the underlying data source has actually confirmed
     * that the value is still the  same. In this case, the
     * <code>latestValueUpdateTimestamp</code> in the  <code>Updateable</code>
     * interface on the event will be modified to reflect the  new
     * confirmation time. Note that if the data quality changes, the
     * <code>valueChanged</code> or one of the other appropriate methods
     * (maybe error if the new quality signifies an error state) must be
     * invoked, even if the value  itself remains the same. Note that if the
     * monitor is configurable through the <code>DynamicValueMonitor</code>
     * interface and the <code>heartbeat</code> flag has been set to
     * <code>false</code> for this subscription, update notifications will not
     * be delivered; in that case only change notifications are delivered.
     *
     * @param event the event carrying the details about the update
     */
    public void valueUpdated(DynamicValueEvent<T, P> event);

    /**
     * Notifies the listener that the change in dynamic value or the
     * quality of the  dynamic value has occured. In this case, both the
     * <code>latestValueUpdateTimestamp</code> and the
     * <code>latestValueChangeTimestamp</code> will be modified to reflect the
     * new time. Most listeners will usually implement at least this method.
     *
     * @param event the event carrying the new dynamic value and value quality
     */
    public void valueChanged(DynamicValueEvent<T, P> event);

    /**
     * Notifies the listener that the underlying implementation has
     * determined that  the value update has not been received (or cannot be
     * sent to the listener) in the time interval prescribed by one of the
     * time triggers. This event will notify the listener that from the moment
     * of event reception, the value should be considered and perhaps visually
     * marked as stale. When the new update is received, the timeout condition
     * will be cancelled by <code>valueTimeoutStops</code> event.
     *
     * @param event event carrying the timeout details
     *
     * @see #timeoutStops
     */
    public void timeoutStarts(DynamicValueEvent<T, P> event);

    /**
     * Notifies the listener that the value update has been received
     * and is being sent with this event. This event cancells the timeout
     * state.
     *
     * @param event event carrying the new value update details
     *
     * @see #timeoutStarts
     */
    public void timeoutStops(DynamicValueEvent<T, P> event);

    /**
     * Notifies the listener that the underlying implementation has
     * deteced a time lag between the timestamps from the primary data source
     * and the local timestamps. Such condition may not be sensible for all
     * underlying data sources (for example, if their data is not tagged with
     * timestamps). In such case, these notifications must not be dispatched.
     * The timelag delta that must be exceeded is either  determined by the
     * underlying implementation or may be set through the
     * <code>DynamicValueMonitor</code> interface if the subscription is
     * controllable.
     *
     * @param event event carrying the timelag details
     *
     * @see #timelagStops
     */
    public void timelagStarts(DynamicValueEvent<T, P> event);

    /**
     * Notifies the listener that the value update has been received
     * which has timestamps of the data synchronized with local timestamps to
     * an amount smaller than the timelag delta.
     *
     * @param event event carrying the timelag details
     *
     * @see #timelagStarts
     */
    public void timelagStops(DynamicValueEvent<T, P> event);

    /**
     * Notifies the listener that the underlying implementation has
     * determined that the new dynamic value response indicates some error
     * condition (which is not a timing error condition, i.e. a timelag or a
     * timeout). Error conditions are determined by  the underlying
     * implementation and can be described only by that implementation.
     *
     * @param event event describing the error condition
     */
    public void errorResponse(DynamicValueEvent<T, P> event);

    /**
     * Notifies the listener that the condition of the property has
     * changed.
     *
     * @param event event describing the change of the condition
     */
    public void conditionChange(DynamicValueEvent<T, P> event);
}

/* __oOo__ */
