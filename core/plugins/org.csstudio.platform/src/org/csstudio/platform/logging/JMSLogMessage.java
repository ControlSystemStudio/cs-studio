package org.csstudio.platform.logging;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jms.JMSException;
import javax.jms.MapMessage;

/** Description of a JMS 'LOG' message
 *  and routines to convert to/from a JMS MapMessage.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class JMSLogMessage
{
    /** Date format for CREATETIME and EVENTTIME. */
    final public static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    /** Date format for CREATETIME and EVENTTIME.
     *  <p>
     *  <b>NOTE:</b> The SimpleDateFormat is not thread safe.
     *  Since there is a chance for a 'sender' thread to
     *  call toMapMessage while a 'receiver' thread calls
     *  fromMapMessage or toString, we need to synchronize
     *  on date_format, and cannot provide access to this
     *  instance of the date format.
     *  (before 2008/08/20, this was public)
     */
    final private static SimpleDateFormat date_format =
        new SimpleDateFormat(DATE_FORMAT);

    /** Default name of the JMS Queue used for log messages.
     *  @see #TYPE_LOG
     */
    final public static String DEFAULT_TOPIC = "LOG";
    
    /** Mandatory MapMessage element: type */
    final public static String TYPE = "TYPE";
    
    /** Value of the TYPE element.
     *  @see #TYPE
     *  @see #DEFAULT_TOPIC
     */
    final public static String TYPE_LOG = "log";
    
    /** Mandatory MapMessage element: content */
    final public static String TEXT = "TEXT";

    /** Mandatory MapMessage element: Severity of the message */
    final public static String SEVERITY = "SEVERITY";

    /** Mandatory MapMessage element: time of message creation */
    final public static String CREATETIME = "CREATETIME";

    /** Mandatory MapMessage element: time of original event */
    final public static String EVENTTIME = "EVENTTIME";
    
    /** Optional MapMessage element: Java class that generated the event */
    final public static String CLASS = "CLASS";

    /** Optional MapMessage element: Java method that generated the event */
    final public static String NAME = "NAME";
    
    /** Optional MapMessage element: Java source file that generated the event */
    final public static String FILENAME = "FILENAME";
    
    /** Optional MapMessage element: ID of application that generated the event */
    final public static String APPLICATION_ID = "APPLICATION-ID";
    
    /** Optional MapMessage element: host that generated the event */
    final public static String HOST = "HOST";
    
    /** Optional MapMessage element: user that generated the event */
    final public static String USER = "USER";
    
    // Components of the Log Message
    final private String text;
    final private String severity;
    final private Calendar create_time;
    final private Calendar event_time;
    final private String class_name;
    final private String method_name;
    final private String file_name;
    final private String application_id;
    final private String host;
    final private String user;

    /** Construct a new log message
     *  @param text Message text
     *  @param severity Severity of the message
     *  @param create_time Time of message creation
     *  @param event_time Time of original event
     *  @param class_name Generating class or <code>null</code>
     *  @param method_name Generating method or <code>null</code>
     *  @param file_name Generating source file name or <code>null</code>
     *  @param application_id Application ID or <code>null</code>
     *  @param host Host name or <code>null</code>
     *  @param user User name or <code>null</code>
     */
    public JMSLogMessage(final String text,
    		final String severity,
            final Calendar create_time, final Calendar event_time,
            final String class_name, final String method_name,
            final String file_name,
            final String application_id, final String host, final String user)
    {
        this.text = text;
        this.severity = severity;
        this.create_time = create_time;
        this.event_time = event_time;
        this.class_name = class_name;
        this.method_name = method_name;
        this.file_name = file_name;
        this.application_id = application_id;
        this.host = host;
        this.user = user;
    }

    /** Create JMSLogMessage from JMS MapMessage
     *  @param map MapMessage to parse/convert
     *  @return JMSLogMessage
     *  @throws Exception on error
     */
    public static JMSLogMessage fromMapMessage(final MapMessage map)
        throws Exception
    {
        final String type = map.getString(TYPE);
        if (!TYPE_LOG.equals(type))
            throw new Exception("Got " + type
                    + " instead of " + TYPE_LOG);
        
        String time_text = map.getString(CREATETIME);
        final Calendar create_time = Calendar.getInstance();
        create_time.clear();
        synchronized (date_format)
        {
            create_time.setTime(date_format.parse(time_text));
        }
        
        time_text = map.getString(EVENTTIME);
        final Calendar event_time = Calendar.getInstance();
        event_time.clear();
        synchronized (date_format)
        {
            event_time.setTime(date_format.parse(time_text));
        }

        final String text = map.getString(TEXT);
        final String severity = map.getString(SEVERITY);
        final String class_name = map.getString(CLASS);
        final String method_name = map.getString(NAME);
        final String file_name = map.getString(FILENAME);
        final String application_id = map.getString(APPLICATION_ID);
        final String host = map.getString(HOST);
        final String user = map.getString(USER);
        return new JMSLogMessage(text, severity, create_time, event_time,
                class_name, method_name, file_name, application_id, host, user);
    }
    
    /** Fill MapMessage with info from this JMSLogMessage
     *  @param map (empty) MapMessage to fill
     *  @return MapMessage
     *  @throws JMSException on error
     */
    public MapMessage toMapMessage(final MapMessage map) throws JMSException
    {
        map.setString(TYPE, TYPE_LOG);
        map.setString(TEXT, text);
        map.setString(SEVERITY, severity);
        String time_text;
        synchronized (date_format)
        {
            time_text = date_format.format(create_time.getTime());
        }
        map.setString(CREATETIME, time_text);
        synchronized (date_format)
        {
            time_text = date_format.format(event_time.getTime());
        }
        map.setString(EVENTTIME, time_text);
        setMapValue(map, CLASS, class_name);
        setMapValue(map, NAME, method_name);
        setMapValue(map, FILENAME, file_name);
        setMapValue(map, APPLICATION_ID, application_id);
        setMapValue(map, HOST, host);
        setMapValue(map, USER, user);
        return map;
    }

    /** Set element of map to value UNLESS value is <code>null</code>
     *  @param map
     *  @param element
     *  @param value
     *  @throws JMSException 
     */
    private void setMapValue(final MapMessage map,
            final String element, final String value) throws JMSException
    {
        if (value != null)
            map.setString(element, value);
    }
    
    /** @return Message text */
    public String getText()
    {
        return text;
    }

    /** @return Message severity */
    public String getSeverity()
    {
        return severity;
    }

    /** @return Time of message creation */
    public Calendar getCreateTime()
    {
        return create_time;
    }

    /** @return Time of original event */
    public Calendar getEventTime()
    {
        return event_time;
    }

    /** @return Generating class or <code>null</code> */
    public String getClassName()
    {
        return class_name;
    }

    /** @return Generating method or <code>null</code> */
    public String getMethodName()
    {
        return method_name;
    }

    /** @return Generating source file name or <code>null</code> */
    public String getFileName()
    {
        return file_name;
    }

    /** @return Application ID or <code>null</code> */
    public String getApplicationID()
    {
        return application_id;
    }

    /** @return Host name or <code>null</code> */
    public String getHost()
    {
        return host;
    }

    /** @return User name or <code>null</code> */
    public String getUser()
    {
        return user;
    }

    /** @return One-line representation of the message
     *          (except for TEXT that spans multiple lines)
     */
    @Override
    public String toString()
    {
        final StringBuffer buf = new StringBuffer();
        buf.append("LOG ");
        synchronized (date_format)
        {
            buf.append(date_format.format(create_time.getTime()));
        }
        if (! event_time.equals(create_time))
        {
            buf.append(" [");
            synchronized (date_format)
            {
                buf.append(date_format.format(event_time.getTime()));
            }
            buf.append("]");
        }
        buf.append(": '");
        buf.append(text);
        buf.append("'");
        if (class_name != null)
        {
            buf.append(" (" + class_name);
            buf.append("." + method_name);
            buf.append(" in " + file_name + ")");
        }
        if (application_id != null)
        {
            buf.append(" (" + application_id);
            buf.append(" on " + host);
            buf.append(", user " + user + ")");
        }
        return buf.toString();
    }
}
