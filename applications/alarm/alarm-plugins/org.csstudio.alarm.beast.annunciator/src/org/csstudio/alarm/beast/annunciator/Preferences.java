/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.annunciator;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Read preferences
 *  <p>
 *  See preferences.ini for explanation of supported preferences.
 *  
 *  @author Kay Kasemir
 *  @author Delphy Armstrong
 *  reviewed by Delphy 1/29/09
 */
@SuppressWarnings("nls")
public class Preferences
{
	final public static String MESSAGE_BUFFER = "message_buffer";
    final public static String THRESHOLD = "threshold";
    final public static String SEVERITIES = "jms_severity_priorities";
    final public static String TRANSLATIONS_FILE = "translations_file";
    final public static String TOPICS = "jms_topic";
    final public static String URL = "jms_url";
    
    public static final String DEFAULT_SEVERITIES =
	    "FATAL,INVALID, ERROR, MAJOR, WARN, MINOR, INFO, OK, DEBUG" ;

    public static String getURL()
    {
        return getString(URL, "tcp://localhost:61616");
    }

    public static String[] getTopics()
    {
        final String topics = getString(TOPICS, "TEST");
        return topics.split("\\s*,\\s*");
    }

    public static String getTranslationsFile()
    {
        return getString(TRANSLATIONS_FILE, "");
    }
	
    public static String getJMSSeverities()
    {
        return getString(SEVERITIES, DEFAULT_SEVERITIES);
    }

    public static int getThreshold()
    {
        return getInt(THRESHOLD, 3);
    }

    public static int getRingBufferSize()
    {
        return getInt(MESSAGE_BUFFER, 50);
    }
    
    /** Read preferences from the scoped Eclipse preference service
     *  @param name
     *  @param default_value
     *  @return String preference
     */
    private static String getString(final String name, final String default_value)
    {
        final IPreferencesService service = Platform.getPreferencesService();
        if (service != null)
           return service.getString(Activator.PLUGIN_ID, name, default_value, null).trim();
        else
            return default_value;
    }

    /** Read preferences from the scoped Eclipse preference service
     *  @param name
     *  @param default_value
     *  @return Integer preference
     */
    public static int getInt(final String name, final int default_value)
    {
        final IPreferencesService service = Platform.getPreferencesService();
        if (service != null)
           return service.getInt(Activator.PLUGIN_ID, name, default_value, null);
        else
            return default_value;
    }
}
