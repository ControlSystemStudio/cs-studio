/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.debugging.jmsmonitor;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.csstudio.java.time.TimestampFormats;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/** A message received from JMS: Type and content.
 *  <p>
 *  Also provides data for Eclipse Property View
 *  (ID "org.eclipse.ui.views.PropertySheet")
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ReceivedMessage implements IPropertySource
{
    final private static DateTimeFormatter date_format = TimestampFormats.MILLI_FORMAT;


    final private Instant date;
    final private String type;
    final private ArrayList<MessageProperty> content;

    /** Initialize
     *  @param type Type string
     *  @param content Content as array of MessageProperties
     */
    public ReceivedMessage(final String type,
            final ArrayList<MessageProperty> content)
    {
        date = Instant.now();
        this.type = type;
        this.content = content;
    }

    /** Create pseudo-message to indicate error condition
     *  @param error Error description
     *  @return ReceivedMessage
     */
    public static ReceivedMessage createErrorMessage(final String error)
    {
        final ArrayList<MessageProperty> content =
            new ArrayList<MessageProperty>();
        content.add(new MessageProperty(Messages.ErrorMessage, error));
        return new ReceivedMessage(Messages.ErrorType, content);
    }

    /** @return Time when message was received */
    public Instant getDate()
    {
        return date;
    }

    /** @return Time when message was received as string */
    public String getDateString()
    {
        return date_format.format(date);
    }

    /** @return Message type */
    public String getType()
    {
        return type;
    }

    /** @return Number of properties in message content */
    public int getPropertyCount()
    {
        return content.size();
    }

    /** Get one message property.
     *  @param index 0 .. <code>getPropertyCount()-1</code>
     *  @return MessageProperty
     */
    public MessageProperty getProperty(final int index)
    {
        return content.get(index);
    }

    /** @return Message content */
    public String getContent()
    {
        final StringBuilder buf = new StringBuilder();
        boolean first = true;
        for (MessageProperty item : content)
        {
            if (first)
                first = false;
            else
                buf.append(", ");
            buf.append(item.getName());
            buf.append("=");
            buf.append(item.getValue());
        }
        return buf.toString();
    }

    /** IPropertySource */
    @Override
    public Object getEditableValue()
    {
        return this;
    }

    /** IPropertySource */
    @Override
    public IPropertyDescriptor[] getPropertyDescriptors()
    {
        final int msg_props = content.size();
        // Create read-only properties in the property view.
        // (Would use TextPropertyDescriptor for edit-able)
        final IPropertyDescriptor props[] =
            new IPropertyDescriptor[2 + msg_props];
        props[0] = new PropertyDescriptor(Messages.DateColumn, Messages.DateColumn);
        props[1] = new PropertyDescriptor(Messages.TypeColumn, Messages.TypeColumn);
        for (int i=0; i<msg_props; ++i)
        {
            final String name = content.get(i).getName();
            props[2+i] = new PropertyDescriptor(name, name);
        }
        return props;
    }

    /** IPropertySource */
    @Override
    public Object getPropertyValue(final Object id)
    {
        if (Messages.DateColumn.equals(id))
            return getDateString();
        if (Messages.TypeColumn.equals(id))
            return type;
        for (MessageProperty prop : content)
            if (prop.getName().equals(id))
                return prop.getValue();
        return null;
    }

    /** IPropertySource */
    @Override
    public boolean isPropertySet(final Object id)
    {
        return getPropertyValue(id) != null;
    }

    /** IPropertySource */
    @Override
    public void resetPropertyValue(final Object id)
    {
        // NOP; read-only
    }

    /** IPropertySource */
    @Override
    public void setPropertyValue(final Object id, final Object value)
    {
        // NOP; read-only
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return type + ": " + getContent();
    }
}
