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
 * This interface contains methods that access the set of transient objects generated
 * by the underlying implementation when the dynamic value is queried. What these
 * transient objects contain depends on the implementation. The implementation - in
 * case the query does not require any transient objects, such as completion codes,
 * requests, responses, error codes etc - may return <code>null</code>. The access
 * to these objects <b>must be thread local</b>. For details, see the documentation
 * of <code>java.lang.ThreadLocal</code>. In short, this requirement means that the
 * returned values are different for different threads that invoke these methods. The
 * "last request" is defined as being the last that has been invoked from the caller's
 * thread.
 * <p>
 * A dynamic value can be accessed in different ways, either by subscriptions (observer
 * pattern) or by direct invocation of <code>get</code> methods. An instance of
 * <code>ValueUpdateable</code> must store the value, when accessed by any means, with the
 * latest timestamp as provided by the underlying implementation (or, if the underlying
 * implementation does not provide timestamped data, with the latest local timestamp), in
 * the <code>latestReceivedValueAsObject</code>. The <code>latestValueChangeTimestamp</code>
 * and <code>latestValueUpdateTimestamp</code> reflect the times, when the value last
 * changed and when the underlying implementation last confirmed that the value is still the
 * same, respectivelly.
 * </p>
 * <p>
 * This interface is implemented by all objects that allow access to the dynamic value.
 * </p>
 */
public interface ValueUpdateable<T>
{

    /**
     * Returns the latest value change timestamp. The change is defined
     * by the implementation, but should normally follow the criterion that
     * <code>!newvalue.equals(oldvalue)</code>.
     *
     * @return the timestamp in Java <code>System.currentTimeMillis</code>
     *         format of the latest change in dynamic value
     */
    public Timestamp getLatestValueChangeTimestamp();

    /**
     * This is a convenience method implemented in the underlying layer
     * that examines the latest response and determines if all data quality
     * parameters in the response indicate that the dynamic value update is
     * error-free, alarm-free and in general without any warning condition.
     * The users of Datatypes may conclude that, if this method returns
     * <code>true</code>, no further examination of
     * <code>latestValueResponse</code> or <code>latestValueRequest</code> is
     * necessary.<p>Note: this method returns <code>true</code> if no
     * request has been submitted or no response has arrived.</p>
     *
     * @return boolean <code>true</code> iff the latetst dynamic value update
     *         is error-free
     */
    public boolean getLatestValueSuccess();

    /**
     * Returns the time of the latest dynamic value update. An update
     * is either a change in dynamic value or the confirmation from the
     * primary data source that the value and its quality are still the same.
     * The timestamp must be that provided by the underlying implementation
     * or, if that is unavailable, it must be the local timestamp. This
     * definition also implies that the
     * <code>latestValueUpdateTimestamp</code> must be necessarily equal to or
     * later than the <code>latestValueChangeTimestamp</code>, since the value
     * may be updated without being changed, but not vice versa.
     *
     * @return a Java <code>System.currentTimeMillis</code> style
     *         timestamp of the latest update
     */
    public Timestamp getLatestValueUpdateTimestamp();

    /**
     * Returns common monitor which controls all dynamic value
     * listeners, which has been registered at property with
     * <code>addDynamicValueListener</code> method.
     *
     * @return common monitor for all listeners without special monitor
     */
    public DynamicValueMonitor getDefaultMonitor();

    /**
     * Returns all by this property created monitors.
     *
     * @return common all created or active monitors
     */
    public DynamicValueMonitor[] getMonitors();

    /**
     * Creates new monitor which controls updates for single value
     * listener independantly from default monitor.
     *
     * @param listener
     *
     * @return particular monitor with single listener
     *
     * @throws RemoteException DOCUMENT ME!
     */
    public <E extends SimpleProperty<T>> DynamicValueMonitor createNewMonitor(DynamicValueListener<T, E> listener)
        throws RemoteException;
}

/* __oOo__ */
