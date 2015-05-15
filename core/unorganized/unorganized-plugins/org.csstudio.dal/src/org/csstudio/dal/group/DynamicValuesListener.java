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

package org.csstudio.dal.group;

import java.util.EventListener;


/**
 * A listner interface which should be used to listen for changes in
 * properties and dynamic values data quality.
 *
 * @see org.csstudio.dal.group.PropertyCollection
 */
public interface DynamicValuesListener<T> extends EventListener
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
    public void valuesUpdated(DynamicValuesEvent<T> event);

    /**
     * Notifies the listener that the change in dynamic value or the
     * quality of the  dynamic value has occured. In this case, both the
     * <code>latestValueUpdateTimestamp</code> and the
     * <code>latestValueChangeTimestamp</code> will be modified to reflect the
     * new time. Most listeners will usually implement at least this method.
     *
     * @param event the event carrying the new dynamic value and value quality
     */
    public void valuesChanged(DynamicValuesEvent<T> event);

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
    public void timeoutStarts(DynamicValuesEvent<T> event);

    /**
     * Notifies the listener that the value update has been received
     * and is being sent with this event. This event cancells the timeout
     * state.
     *
     * @param event event carrying the new value update details
     *
     * @see #timeoutStarts
     */
    public void timeoutStops(DynamicValuesEvent<T> event);

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
    public void timelagStarts(DynamicValuesEvent<T> event);

    /**
     * Notifies the listener that the value update has been received
     * which has timestamps of the data synchronized with local timestamps to
     * an amount smaller than the timelag delta.
     *
     * @param event event carrying the timelag details
     *
     * @see #timelagStarts
     */
    public void timelagStops(DynamicValuesEvent<T> event);

    /**
     * Notifies the listener that the underlying implementation has
     * determined that the new dynamic value response indicates some error
     * condition (which is not a timing error condition, i.e. a timelag or a
     * timeout). Error conditions are determined by  the underlying
     * implementation and can be described only by that implementation.
     *
     * @param event event describing the error condition
     */
    public void errorsResponse(DynamicValuesEvent<T> event);

    /**
     * Notifies the listener that the condition of the properties has
     * changed.
     *
     * @param event event describing the change of the condition
     */
    public void conditionsChange(DynamicValuesEvent<T> event);
}

/* __oOo__ */
