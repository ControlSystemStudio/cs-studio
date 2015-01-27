/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.apputil.time.SecondsParser;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/** One message in the message history.
 *  <p>
 *  Also provides the message detail to Eclipse Property View
 *  (ID "org.eclipse.ui.views.PropertySheet")
 *
 *  @author Kay Kasemir
 */
public class Message implements IPropertySource
{
    /** Suggested time stamp format */
    final private static SimpleDateFormat date_format =
        new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");         //$NON-NLS-1$

    /** Property for sequential message number */
    public static final String SEQ = "SEQ"; //$NON-NLS-1$

    /** Property for message ID in RDB */
    public static final String ID = "ID"; //$NON-NLS-1$

    /** Property for message type */
    public static final String TYPE = "TYPE"; //$NON-NLS-1$

    /** Property for Time when message was added to log */
    final public static String DATUM = "TIME"; //$NON-NLS-1$

    /** Property for PV or method name */
    final public static String NAME = "NAME"; //$NON-NLS-1$

    /** Property for message severity */
    final public static String SEVERITY = "SEVERITY"; //$NON-NLS-1$

    /** Property for Time in seconds from previous message to this message */
    final public static String DELTA = "DELTA"; //$NON-NLS-1$

    final private int sequence, id;

    /** Map of property names and values */
    final private Map<String, String> properties;

    /** Constructor
     *  @param sequence Sequence number
     *  @param id ID from RDB
     *  @param properties Map of message properties
     */
    public Message(final int sequence,
            final int id, final Map<String, String> properties)
    {
        this.sequence = sequence;
        this.id = id;
        this.properties = properties;
    }

    public static String format(final Date date)
    {
    	synchronized (date_format)
        {
        	return date_format.format(date);
        }
    }

    /** Set 'delta'.
     *  Public, but really only meant to be called by code that
     *  constructs the message to overcome the problem that
     *  we can only configure the 'delta' after constructing
     *  the _next_ message.
     *  @param last_datum Date of previous entry
     *  @param datum Date of this entry
     */
    public void setDelta(final Date last_datum, final Date datum)
    {
        final double delta_secs = (last_datum.getTime() - datum.getTime()) / 1000.0;
        properties.put(Message.DELTA, SecondsParser.formatSeconds(delta_secs));
    }

    /** @return Sequential Message sequence number */
    public int getSequence()
    {
        return sequence;
    }

    /** @return Message id (internal to RDB) */
    public int getId()
    {
        return id;
    }

    /** @return Iterator over all properties in this message */
    public Iterator<String> getProperties()
    {
        return properties.keySet().iterator();
    }

    /** Get a property.
     *  @param property Which property to get
     *  @return Value of requested property or <code>null</code>
     */
    public String getProperty(final String property)
    {
        return properties.get(property);
    }

    /** @see IPropertySource */
	@Override
    public IPropertyDescriptor[] getPropertyDescriptors()
	{
		final Set<String> key_set = properties.keySet();
		final List<IPropertyDescriptor> props = new ArrayList<IPropertyDescriptor>();
		// Create read-only properties in the property view.
		// (Would use TextPropertyDescriptor for edit-able)
		props.add(new PropertyDescriptor(ID, ID));
		for (String key : key_set)
		    if (key != null)
		        props.add(new PropertyDescriptor(key, key));
		return props.toArray(new IPropertyDescriptor[props.size()]);
	}

	/** @see IPropertySource */
	@Override
    public Object getEditableValue()
	{
	    return this;
	}

	 /** @see IPropertySource */
	@Override
    public Object getPropertyValue(final Object id)
	{
	    if (id == ID)
	        return Integer.toString(this.id);
		return properties.get(id);
	}

	/** @see IPropertySource */
	@Override
    public boolean isPropertySet(final Object id)
	{
		return getPropertyValue(id) != null;
	}

	/** @see IPropertySource */
	@Override
    public void resetPropertyValue(final Object id)
	{
		// NOP, properties are read-only
	}

	/** @see IPropertySource */
	@Override
    public void setPropertyValue(final Object id, final Object value)
	{
		// NOP, properties are read-only
	}

    /** {@inhericDoc} */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        final StringBuffer buf = new StringBuffer();

        buf.append("Message ").append(id).append(" ");
        final Iterator<String> props = getProperties();
        while (props.hasNext())
        {
            final String prop = props.next();
            buf.append("\n").append(prop).append(": ")
                .append(getProperty(prop));
        }
        return buf.toString();
    }
}
