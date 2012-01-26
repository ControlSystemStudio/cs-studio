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

import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.DynamicValueState;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.EventObject;


/**
 * This event objects holds values and timestamps for goup of properties
 * and is fired by <code>DynamicValuesListener</code>.
 *
 * @param <T> exact type of values
 */
public class DynamicValuesEvent<T> extends EventObject
{
	private static final long serialVersionUID = 1L;
	private long[] timestamps;
	private EnumSet<DynamicValueState> conditions;
	private String message = null;
	private T values;
	private Exception[] errors;

	/**
	     * Creates a new instance of this event. All arrays should be orderd in same order as properties in group.
	     *
	     * @param source the source that generated the event
	     * @param values the event value
	     * @param conditions is array of conditions of the properties in group and may be <code>null</code>
	     * @param timestamps are exact timestamps of the even values. If <code>null</code>, current time will be used for <code>timestamp</code>.
	     * @param message event message, may be <code>null</code>
	     */
	public DynamicValuesEvent(GroupDataAccess<T, DynamicValueProperty<T>> source, T values,
	    EnumSet<DynamicValueState> conditions, long[] timestamps, String message)
	{
		super(source);

		if (conditions == null) {
			throw new NullPointerException("conditions");
		}

		if (values == null) {
			throw new NullPointerException("values");
		}

		if (timestamps == null) {
			this.timestamps = new long[conditions.size()];
			Arrays.fill(this.timestamps, System.currentTimeMillis());
		} else {
			this.timestamps = timestamps;
		}

		this.message = message;
		this.conditions = conditions;
	}

	/**
	 * Returns event message.
	 *
	 * @return Event message string
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Returns the conditions of the properties.
	 *
	 * @return The condition of event's property
	 */
	public EnumSet<DynamicValueState> getConditions()
	{
		return conditions;
	}

	/**
	 * Returns event timestamps, each timestamp for each value.
	 *
	 * @return Event timestamps
	 */
	public long[] getTimestamps()
	{
		return timestamps;
	}

	/**
	 * Returns the event values.
	 *
	 * @return event values
	 */
	public T getValues()
	{
		return values;
	}

	/**
	 * Returns event exceptions.
	 *
	 * @return event exceptions
	 */
	public Exception[] getErrors()
	{
		return errors;
	}
} /* __oOo__ */


/* __oOo__ */
