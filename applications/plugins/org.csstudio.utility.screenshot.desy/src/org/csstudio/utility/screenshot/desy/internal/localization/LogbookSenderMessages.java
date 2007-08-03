package org.csstudio.utility.screenshot.desy.internal.localization;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class LogbookSenderMessages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.utility.screenshot.desy.internal.localization.messages";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, LogbookSenderMessages.class);
    }

    private LogbookSenderMessages() { }
    
    public static String getString(String key)
    {
        try
        {
            return RESOURCE_BUNDLE.getString(key);
        }
        catch(MissingResourceException _ex)
        {
            return '!' + key + '!';
        }
    }

    public static String getString(String key, Object params[])
    {
        if(params == null)
        {
            return getString(key);
        }
        
        try
        {
            return MessageFormat.format(getString(key), params);
        }
        catch(Exception _ex)
        {
            return "!" + key + "!";
        }
    }
}
