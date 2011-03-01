/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging;

import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Platform;

/** Configurator for java.util.logging based on Eclipse preferences
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LogConfigurator
{
    /** Root of the logger hierarchy */
    final private static Logger root = Logger.getLogger("");

    private static FileHandler file_handler = null;

    private static JMSLogHandler jms_handler = null;

    private static PluginLogListener ilog_listener = null;

    /** Allow only static access */
    private LogConfigurator()
    {
        // NOP
    }

    /** (Re-)Initialize logger from Eclipse preferences
     *  @throws Exception on error: Invalid preference values, errors during logger setup
     */
    public static void configureFromPreferences() throws Exception
    {
        // Basic configuration
        final LogFormatDetail detail = Preferences.getDetail();
        Level level = Preferences.getConsoleLevel();
        final Formatter formatter = new LogFormatter(detail);
        configureConsoleLogger(level, formatter);

        // Configure file logging
        level = Preferences.getFileLevel();

        final String file_pattern = Preferences.getFilePattern();
        final int max_bytes = Preferences.getFileBytes();
        final int max_files = Preferences.getFileCount();
        configureFileLogging(level, file_pattern, max_bytes, max_files, formatter);

        // Configure JMS logging
        level = Preferences.getJMSLevel();
        final String jms_url = Preferences.getJMSURL();
        final String topic = Preferences.getJMSTopic();
        configureJMSLogging(level, jms_url, topic, formatter);

        // Forward Eclipse ILog messages to Logger
        if (ilog_listener == null)
        {   // .. but only once
            ilog_listener = new PluginLogListener();
            Platform.addLogListener(ilog_listener);
        }
    }

    /** Configure all currently active loggers.
     *  Ordinarily, that should be the root logger writing to the console,
     *  but additional handlers could already be in place via
     *  "lib/logging.properties" in the JRE directory or other means.
     * @param level
     * @param formatter
     */
    public static void configureConsoleLogger(final Level level, final Formatter formatter)
    {
        root.setLevel(level);
        for (Handler handler : root.getHandlers())
        {
            handler.setLevel(level);
            handler.setFormatter(formatter);
        }
    }

    /** Configure a file logger
     *  When logging to multiple files, the first one will use the given filename.
     *  When that file is full, it's renamed to filename.1, and a new filename.0 is
     *  written.
     *  @param level Log level to use with file logging. <code>Level.OFF</code> to disable file logging
     *  @param file_pattern Base name of the file, ".0", ".1", ".2" will be added for multiple files
     *  @param max_bytes When file exceeds this size, a new file will be created
     *  @param max_files Number of files to use
     *  @throws Exception on error: Cannot create new file, ...
     */
    public static void configureFileLogging(final Level level, final String file_pattern,
            final int max_bytes, final int max_files,
            final Formatter formatter) throws Exception
    {
        if (file_handler != null)
        {
            root.removeHandler(file_handler);
            file_handler.close();
            file_handler = null;
        }
        if (file_pattern.isEmpty()  ||  level.intValue() >= Level.OFF.intValue())
            return;

        file_handler = new FileHandler(file_pattern, max_bytes, max_files, true);
        file_handler.setLevel(level);
        file_handler.setFormatter(formatter);
        root.addHandler(file_handler);
    }

    /** Configure a JMS logger
     *  @param level Log level. <code>Level.OFF</code> to disable JMS logging
     *  @param jms_url JMS server URL
     *  @param topic JMS topic
     *  @param formatter Formatter
     *  @throws Exception on error
     */
    public static void configureJMSLogging(final Level level, final String jms_url,
            final String topic,
            final Formatter formatter) throws Exception
    {
        if (jms_handler != null)
        {
            root.removeHandler(jms_handler);
            jms_handler.close();
            jms_handler = null;
        }
        if (jms_url.isEmpty()  ||  level.intValue() >= Level.OFF.intValue())
            return;

        jms_handler = new JMSLogHandler(jms_url, topic);
        jms_handler.setLevel(level);
        jms_handler.setFormatter(formatter);
        jms_handler.start();
        root.addHandler(jms_handler);
    }
}
