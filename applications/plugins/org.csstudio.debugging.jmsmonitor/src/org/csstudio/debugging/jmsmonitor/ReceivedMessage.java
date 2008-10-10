package org.csstudio.debugging.jmsmonitor;

import java.util.ArrayList;
import java.util.Date;

/** A message received from JMS: Type and content.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ReceivedMessage
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

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return type + ": " + getContent();
    }
}
