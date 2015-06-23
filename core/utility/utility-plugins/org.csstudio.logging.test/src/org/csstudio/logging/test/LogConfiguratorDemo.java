/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.logging.LogFormatDetail;
import org.csstudio.logging.LogConfigurator;
import org.csstudio.logging.LogFormatter;
import org.junit.Test;

/** JUnit demo of the LogConfigurator
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LogConfiguratorDemo
{
    /** Base of log file names */
    final private static String BASENAME = "logtest";

    /** Directory where log files are generated */
    final private String dirname = System.getProperty("java.io.tmpdir");

    @Test
    public void testLogging() throws Exception
    {
        final LogFormatter format_high = new LogFormatter(LogFormatDetail.HIGH);
        final LogFormatter format_low = new LogFormatter(LogFormatDetail.LOW);

        LogConfigurator.configureConsoleLogger(Level.ALL, format_high);

        LogConfigurator.configureFileLogging(Level.INFO, dirname + File.separator + BASENAME,
                4*1024, 3,
                format_low);

        LogConfigurator.configureJMSLogging(Level.ALL, DemoSetup.url, DemoSetup.topic,
                format_high);

        final Logger log = Logger.getLogger(this.getClass().getName());

        log.severe("Severe problem");
        log.warning("This is a warning");
        log.info("FYI");
        log.config("You are using Java");
        log.fine("Some detail");
        log.finer("More detail");
        log.finest("Most detail possible");

        log.log(Level.INFO, "Argument was {0}", "something");
        log.log(Level.WARNING, "Caught exception", new Exception("Exception message"));


        // Allow logging threads to finish their work
        Thread.sleep(2000);

        // Check log files
        // Hard to predict how many  lines will be in the log file:
        // Not all might be 'flushed' out, while JMS also adds log messages
        final int lines = dump_files();
        assertTrue(lines > 4);

        // Remove log files when exiting JVM
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                deleteLogFiles();
            }
        });
    }

    /** Delete log files */
    private void deleteLogFiles()
    {
        final String files[] = new File(dirname).list(new FilenameFilter()
        {
            @Override
            public boolean accept(final File dir, final String name)
            {
                return name.contains(BASENAME);
            }
        });
        for (String file : files)
        {
            final File file_obj = new File(dirname, file);
            System.out.println("Deleting " + file_obj);
            file_obj.delete();
        }
    }

    /** Dump log files
     *  @return Lines found in log files
     */
    private int dump_files() throws Exception
    {
        final String files[] = new File(dirname).list(new FilenameFilter()
        {
            @Override
            public boolean accept(final File dir, final String name)
            {
                return name.contains(BASENAME);
            }
        });
        int lines = 0;
        for (String file : files)
            lines += dump_file(file);
        return lines;
    }

    /** Dump one log file
     *  @return Lines found in log file
     */
    private int dump_file(final String filename) throws Exception
    {
        final File file = new File(dirname, filename);
        System.out.println("Messages in " + file);
        final BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        int lines = 0;
        while ((line = reader.readLine()) != null)
        {
            System.out.println(line);
            ++lines;
        }
        reader.close();
        return lines;
    }
}
