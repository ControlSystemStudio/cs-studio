package org.csstudio.logging.es.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.csstudio.apputil.time.SecondsParser;
import org.csstudio.logging.JMSLogMessage;
import org.csstudio.logging.es.archivedjmslog.LogMessage;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.json.JSONException;
import org.json.JSONObject;

public class EventLogMessage extends LogMessage
{
    /** Property for message ID in RDB */
    public static final String ID = "ID"; //$NON-NLS-1$

    // these are copied 1:1 from the incoming message by jms2es, so use the
    // JMSLogMessage constants.
    /** Property for Time when message was added to log */
    public static final String DATUM = JMSLogMessage.CREATETIME;

    /** Property for message severity */
    public static final String SEVERITY = JMSLogMessage.SEVERITY;

    /** Property for message text */
    public static final String TEXT = JMSLogMessage.TEXT;

    /** Property for Time in seconds from previous message to this message */
    public static final String DELTA = "DELTA"; //$NON-NLS-1$

    /** Map of property names and values */
    private final Map<String, String> properties = new HashMap<>();
    private String delta = null;

    static final SimpleDateFormat df = new SimpleDateFormat(
            JMSLogMessage.DATE_FORMAT);

    @SuppressWarnings("nls")
    static final String[] PROPERTY_NAMES = { "CREATETIME", "TEXT", "NAME",
            "CLASS", "USER", "HOST", "APPLICATION-ID", "SEVERITY" };

    @SuppressWarnings("nls")
    public static EventLogMessage fromElasticsearch(JSONObject json)
    {
        try
        {
            EventLogMessage msg = new EventLogMessage();
            JSONObject source = json.getJSONObject("_source");
            for (String name : EventLogMessage.PROPERTY_NAMES)
            {
                msg.properties.put(name, source.getString(name));
            }
            msg.properties.put("ID", json.getString("_id"));
            msg.verify();
            return msg;
        }
        catch (JSONException ex)
        {
            return null;
        }
    }

    public static EventLogMessage fromJMS(MapMessage message)
    {
        try
        {
            EventLogMessage msg = new EventLogMessage();
            for (String name : EventLogMessage.PROPERTY_NAMES)
            {
                msg.properties.put(name, message.getString(name));
            }
            msg.properties.put("ID", message.getJMSMessageID()); //$NON-NLS-1$
            msg.verify();
            return msg;
        }
        catch (JMSException ex)
        {
            return null;
        }
    }

    @Override
    public int compareTo(final LogMessage other)
    {
        if (!(other instanceof EventLogMessage))
        {
            return 0;
        }
        EventLogMessage o = (EventLogMessage) other;

        // first compare by date
        int r = this.getPropertyValue(EventLogMessage.DATUM)
                .compareTo(o.getPropertyValue(EventLogMessage.DATUM));
        if (0 != r)
        {
            return r;
        }

        // then by anything else that might be different
        for (String s : EventLogMessage.PROPERTY_NAMES)
        {
            r = this.getPropertyValue(s).compareTo(o.getPropertyValue(s));
            if (0 != r)
            {
                return r;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(final Object other)
    {
        if (other == this)
        {
            return true;
        }
        if (!(other instanceof EventLogMessage))
        {
            return false;
        }
        EventLogMessage msg = (EventLogMessage) other;
        for (String s : EventLogMessage.PROPERTY_NAMES)
        {
            if (!this.getPropertyValue(s).equals(msg.getPropertyValue(s)))
            {
                return false;
            }
        }
        return true;
    }

    /** @see IPropertySource */
    @Override
    public Object getEditableValue()
    {
        return this;
    }

    /** @return Iterator over all properties in this message */
    public Iterator<String> getProperties()
    {
        return this.properties.keySet().iterator();
    }

    /** @see IPropertySource */
    @Override
    public IPropertyDescriptor[] getPropertyDescriptors()
    {
        Set<String> key_set = this.properties.keySet();
        List<IPropertyDescriptor> props = new ArrayList<>();
        // Create read-only properties in the property view.
        // (Would use TextPropertyDescriptor for edit-able)
        props.add(new PropertyDescriptor(EventLogMessage.DELTA,
                EventLogMessage.DELTA));
        for (String key : key_set)
        {
            if (key != null)
            {
                props.add(new PropertyDescriptor(key, key));
            }
        }
        return props.toArray(new IPropertyDescriptor[props.size()]);
    }

    /** @see IPropertySource */
    @Override
    public String getPropertyValue(final Object id)
    {
        if (EventLogMessage.DELTA.equals(id))
        {
            return this.delta;
        }
        return this.properties.get(id);
    }

    @Override
    public Long getTime()
    {
        try
        {
            return EventLogMessage.df
                    .parse(getPropertyValue(EventLogMessage.DATUM)).getTime();
        }
        catch (NumberFormatException | ParseException e)
        {
            System.err.println(getPropertyValue(EventLogMessage.DATUM));
            e.printStackTrace();
            return 0L;
        }
    }

    @Override
    public int hashCode()
    {
        return this.properties.hashCode();
    }

    /**
     * Set 'delta'. Public, but really only meant to be called by code that
     * constructs the message to overcome the problem that we can only configure
     * the 'delta' after constructing the _next_ message.
     */
    public void setDelta(long delta_millis)
    {
        this.delta = SecondsParser.formatSeconds(delta_millis / 1000.0);
    }

    /** @see IPropertySource */
    @Override
    public void setPropertyValue(Object id, Object value)
    {
        // NOP, properties are read-only
    }

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append("Message ").append(this.getPropertyValue(EventLogMessage.ID))
                .append(":");
        Iterator<String> props = getProperties();
        while (props.hasNext())
        {
            String prop = props.next();
            if (EventLogMessage.ID.equals(prop))
            {
                continue;
            }
            buf.append("\n").append(prop).append(": ")
                    .append(getPropertyValue(prop));
        }
        return buf.toString();
    }

    /** Verify that all required fields are there. */
    public void verify() throws IllegalArgumentException
    {
        // this is the absolute minimum a message should have.
        if (!this.properties.containsKey(EventLogMessage.DATUM)
                || !this.properties.containsKey(EventLogMessage.SEVERITY)
                || !this.properties.containsKey(EventLogMessage.TEXT))
        {
            throw new IllegalArgumentException("Invalid log message."); //$NON-NLS-1$
        }
    }
}
