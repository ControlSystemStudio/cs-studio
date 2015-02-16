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

import org.csstudio.dal.simple.AnyData;


/**
 * General EventSystem event.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 *
 */
public class DynamicValueEvent<T, P extends SimpleProperty<T>> extends SystemEvent<T, DataAccess<T>>
{
	private static final long serialVersionUID = 1L;

	protected P property = null;
	protected DynamicValueCondition condition;

	/**
	 * Creates a new instance of this event and initializes both the event
	 * source and the property that the event source belongs to. Parameter
	 * <code>souce</code> must be one of the data renderings issued by the
	 * parameter <code>property</code>. <code>condition</code> is the condition
	 * of the property and may be <code>null</code>. <code>Timestamp</code> is the timestamp
	 * of the event. If it's 0, current time will be used for <code>timestamp</code>. Parameter
	 * <code>message</code> is used for the event message. It may be <code>null</code>.
	 * Parameter <code>Exception</code> is the event exception and may be <code>null</code>.
	 * The event identification tag parameter <code>type</code> may be null.
	 *
	 * @param source the source that generated the event
	 * @param property the property that the source belongs to
	 * @param value the event value
	 * @param condition the condition of the property
	 * @param timestamp the timestamp of the event
	 * @param message event message
	 * @param error event exception
	 * @param type the event identification tag
	 */
	public DynamicValueEvent(final DataAccess<T> source, final P property, final T value,
	    final DynamicValueCondition condition, final Timestamp timestamp, final String message,
	    final Exception error, final Object type)
	{
		super(source, value, timestamp, message, error, type);

		if (property == null) {
			throw new NullPointerException("property");
		}

		// TODO for the time being we allow null conditions
		/*
		if (condition==null) {
		    throw new NullPointerException("condition");
		}
		*/
		if (timestamp == null) {
			this.timestamp = new Timestamp();
		} else {
			this.timestamp = timestamp;
		}

		this.condition = condition;
		this.property = property;
	}

	/**
	 * Convenience constructor.
	 *
	 * @see DynamicValueEvent#EventSystemEvent(DataAccess, P, Object, DynamicValueCondition, long, String, Exception, Object)
	 *
	 *
	 * @param source the source that generated the event
	 * @param property the property that the source belongs to
	 * @param condition the condition of the property
	 * @param timestamp the timestamp of the event
	 * @param message event message
	 */
	public DynamicValueEvent(final DataAccess<T> source, final P property, final T value,
	    final DynamicValueCondition condition, final Timestamp timestamp, final String message)
	{
		this(source, property, value, condition, timestamp, message, null, null);
	}

	/**
	 * Convenience constructor.
	 *
	 * @see DynamicValueEvent#EventSystemEvent(DataAccess, P, Object, DynamicValueCondition, long, String, Exception, Object)
	 *
	 *
	 * @param source the source that generated the event
	 * @param property the property that the source belongs to
	 * @param condition the condition of the property
	 * @param timestamp the timestamp of the event
	 * @param message event message
	 * @param error event exception
	 */
	public DynamicValueEvent(final DataAccess<T> source, final P property, final T value,
	    final DynamicValueCondition condition, final Timestamp timestamp, final String message,
	    final Exception error)
	{
		this(source, property, value, condition, timestamp, message, error, null);
	}

	/**
	 * Returns the property whose dynamic value has been updated.
	 *
	 * @return SimpleProperty the issuer of the data access specified as source
	 *         of this event
	 */
	public P getProperty()
	{
		return property;
	}

	/**
	 * Returns the condition of the property.
	 *
	 * @return The condition of event's property
	 */
	public DynamicValueCondition getCondition()
	{
		return condition;
	}

	/**
	 * Returns event <code>value</value> if it's a number otherwise return <code>null</code>
	 *
	 * @return Returns event value
	 */
	public Number getNumber()
	{
		if (value instanceof Number) {
			return (Number)value;
		}

		return null;
	}

	public AnyData getData() {
		return property.getData();
	}

	@Override
	public String toString() {
		final StringBuilder sb= new StringBuilder(256);
		sb.append(this.getClass().getName());
		sb.append('{');
		sb.append(property.getIdentifier().getUniqueName());
		sb.append(", ");
		sb.append(getValue());
		sb.append(", ");
		sb.append(getCondition());
		sb.append('}');
		return sb.toString();
	}
}

/* __oOo__ */
