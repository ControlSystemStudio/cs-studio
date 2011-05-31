/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging;

import java.util.logging.Level;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Helper to read preferences
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
    final public static String DETAIL = "detail";
    final public static String CONSOLE_LEVEL = "console_level";
    final public static String FILE_LEVEL = "file_level";
    final public static String FILE_PATTERN = "file_pattern";
    final public static String FILE_BYTES = "file_bytes";
    final public static String FILE_COUNT = "file_count";
    final public static String JMS_LEVEL = "jms_level";
    final public static String JMS_URL = "jms_url";
    final public static String JMS_TOPIC = "jms_topic";

    /** @return {@link LogFormatDetail} from preferences or a default
     *  @throws Exception when value cannot be parsed
     */
    public static LogFormatDetail getDetail() throws Exception
    {
        LogFormatDetail detail = LogFormatDetail.HIGH;
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs != null)
        {
            String txt = prefs.getString(Activator.ID, DETAIL, detail.name(), null);
            try
            {
                detail = LogFormatDetail.valueOf(txt);
            }
            catch (Throwable ex)
            {
                throw new Exception("Illegal log detail '" + txt + "'");
            }
        }
        return detail;
    }

    /** @return {@link Level} for root/console logger from preferences or a default
     *  @throws Exception when value cannot be parsed
     */
    public static Level getConsoleLevel() throws Exception
    {
        Level level = Level.INFO;
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs != null)
        {
            String txt = prefs.getString(Activator.ID, CONSOLE_LEVEL, level.getName(), null);
            try
            {
                level = Level.parse(txt);
            }
            catch (Throwable ex)
            {
                throw new Exception("Illegal console log level '" + txt + "'");
            }
        }
        return level;
    }

    /** @return {@link Level} for file logger from preferences or a default
     *  @throws Exception when value cannot be parsed
     */
    public static Level getFileLevel() throws Exception
    {
        Level level = Level.OFF;
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs != null)
        {
            String txt = prefs.getString(Activator.ID, FILE_LEVEL, level.getName(), null);
            try
            {
                level = Level.parse(txt);
            }
            catch (Throwable ex)
            {
                throw new Exception("Illegal file log level '" + txt + "'");
            }
        }
        return level;
    }

    /** @return Filename path and pattern for file logger from preferences or a default */
    public static String getFilePattern() throws Exception
    {
        String file_pattern = "";
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs != null)
            file_pattern = prefs.getString(Activator.ID, FILE_PATTERN, file_pattern, null);
        return file_pattern.trim();
    }

    /** @return Maximum number of bytes in file before rotation to next file */
    public static int getFileBytes() throws Exception
    {
        int file_bytes = 8000;
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs != null)
            file_bytes = prefs.getInt(Activator.ID, FILE_BYTES, file_bytes, null);
        return file_bytes;
    }

    /** @return Maximum number of files in rotation */
    public static int getFileCount() throws Exception
    {
        int file_count = 2;
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs != null)
            file_count = prefs.getInt(Activator.ID, FILE_COUNT, file_count, null);
        return file_count;
    }

    /** @return {@link Level} for JMS logger from preferences or a default
     *  @throws Exception when value cannot be parsed
     */
    public static Level getJMSLevel() throws Exception
    {
        Level level = Level.OFF;
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs != null)
        {
            String txt = prefs.getString(Activator.ID, JMS_LEVEL, level.getName(), null);
            try
            {
                level = Level.parse(txt);
            }
            catch (Throwable ex)
            {
                throw new Exception("Illegal JMS log level '" + txt + "'");
            }
        }
        return level;
    }

    /** @return JMS URL from preferences or a default */
    public static String getJMSURL() throws Exception
    {
        String jms_url = "";
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs != null)
            jms_url = prefs.getString(Activator.ID, JMS_URL, jms_url, null);
        return jms_url.trim();
    }

    /** @return JMS Topic from preferences or a default */
    public static String getJMSTopic() throws Exception
    {
        String jms_topic = "LOG";
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs != null)
            jms_topic = prefs.getString(Activator.ID, JMS_TOPIC, jms_topic, null);
        return jms_topic.trim();
    }
}
