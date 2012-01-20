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

import org.csstudio.dal.AsynchronousContext;
import org.csstudio.dal.DynamicValueMonitor;


/**
 * This interface is implemented by all objects that allow access to the dynamic value.
 * Provides convenience acces to asyncrhonously received values.
 */
public interface GroupUpdateable extends AsynchronousContext
{
	/**
	 * Returns the last known dynamic value in object rendering. This
	 * method does not cause the implementation to query the primary data
	 * source and so the data that this method returns <b>must</b> be local. A
	 * call to this method does not cause the latest timestamps to change. In
	 * a way, this method provides a single (latest) value buffer for the
	 * dynamic value. The "latest" is defined with respect to the timestamp
	 * provided by the underlying implementation, or, if that is unavailable,
	 * to the local timestamp of the data.
	 *
	 * @return Object the latest dynamic value in object rendering
	 */
	public Object[] getLatestReceivedValuesAsObjects();

	/**
	 * Returns the latest value change timestamp. The change is defined
	 * by the implementation, but should normally follow the criterion that
	 * <code>!newvalue.equals(oldvalue)</code>.
	 *
	 * @return long the timestamp in Java <code>System.currentTimeMillis</code>
	 *         format of the latest change in dynamic value
	 */
	public long[] getLatestValuesChangeTimestamps();

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
	 * @return long a Java <code>System.currentTimeMillis</code> style
	 *         timestamp of the latest update
	 */
	public long[] getLatestValuesUpdateTimestamps();

	/**
	 * Returns monitor which controls remote value subscriptions.
	 *
	 * @return monitor which controls remote value subscriptions
	 */
	public DynamicValueMonitor getDefaultMonitor();
}

/* __oOo__ */
