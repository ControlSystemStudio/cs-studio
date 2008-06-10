package org.csstudio.platform.logging;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@SuppressWarnings("nls")
public class JMSLogMessage
{
    /** Date format for CREATETIME and EVENTTIME */
    final public static SimpleDateFormat date_format =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /** Mandatory MapMessage element: type */
    final public static String TYPE = "TYPE";
    
    final public static String TYPE_LOG = "log";
    
    /** Mandatory MapMessage element: content */
    final public static String TEXT = "TEXT";

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
    final private Calendar time;
    final private String class_name;
    final private String method_name;
    final private String file_name;
    final private String application_id;
    final private String host;
    final private String user;

    /** Construct a new log message
     *  @param text Message text
     *  @param time Time stamp
     *  @param class_name Generating class or <code>null</code>
     *  @param method_name Generating method or <code>null</code>
     *  @param file_name Generating source file name or <code>null</code>
     *  @param application_id Application ID or <code>null</code>
     *  @param host Host name or <code>null</code>
     *  @param user User name or <code>null</code>
     */
    public JMSLogMessage(final String text, final Calendar time,
            final String class_name, final String method_name,
            final String file_name,
            final String application_id, final String host, final String user)
    {
        this.text = text;
        this.time = time;
        this.class_name = class_name;
        this.method_name = method_name;
        this.file_name = file_name;
        this.application_id = application_id;
        this.host = host;
        this.user = user;
    }

    /** @return Message text */
    public String getText()
    {
        return text;
    }

    /** @return Time stamp */
    public Calendar getTime()
    {
        return time;
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

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        final StringBuffer buf = new StringBuffer();
        buf.append("LOG Message ");
        buf.append(date_format.format(time.getTime()));
        buf.append(": ");
        buf.append(text);
        // buf.append("\n");
        return buf.toString();
    }
}
