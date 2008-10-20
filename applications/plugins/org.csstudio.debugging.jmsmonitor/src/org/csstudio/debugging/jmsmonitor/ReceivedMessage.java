package org.csstudio.debugging.jmsmonitor;

import java.util.ArrayList;
import java.util.Date;

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
    final private Date date;
    final private String type;
    final private ArrayList<MessageProperty> content;

    /** Initialize
     *  @param type Type string
     *  @param content Content as array of MessageProperties
     */
    public ReceivedMessage(final String type,
            final ArrayList<MessageProperty> content)
    {
        date = new Date();
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
    public Date getDate()
    {
        return date;
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
    public Object getEditableValue()
    {
		return this;
	}

    /** IPropertySource */
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		// Create read-only properties in the property view.
		// (Would use TextPropertyDescriptor for edit-able)
		final IPropertyDescriptor props[] =
			new IPropertyDescriptor[content.size()];
		for (int i=0; i<props.length; ++i)
		{
			final String name = content.get(i).getName();
			props[i] = new PropertyDescriptor(name, name);
		}
		return props;
	}

    /** IPropertySource */
	public Object getPropertyValue(final Object id)
	{
		for (MessageProperty prop : content)
			if (prop.getName().equals(id))
				return prop.getValue();
		return null;
	}

    /** IPropertySource */
	public boolean isPropertySet(final Object id)
	{
		return getPropertyValue(id) != null;
	}

    /** IPropertySource */
	public void resetPropertyValue(final Object id)
	{
		// NOP; read-only
	}

    /** IPropertySource */
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
