package org.csstudio.debugging.jmsmonitor;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.debugging.jmsmonitor.messages"; //$NON-NLS-1$
    public static String Clear;
    public static String ClearTT;
    public static String ContentColumn;
    public static String DateColumn;
    public static String DetailDialogTitle;
    public static String ErrorMessage;
    public static String ErrorNoTopic;
    public static String ErrorNoURL;
    public static String ErrorType;
    public static String JMSMonitorPrefs;
    public static String Preferences_JMS_URL;
    public static String Property;
    public static String SeparatorValue;
    public static String SeperatorType;
    public static String Topic_TT;
    public static String TopicLabel;
    public static String TypeColumn;
    public static String UnknownType;
    public static String Value;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
        // Prevent instantiation
    }
}
