/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.WorkQueue;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/** Alarm Server Application
 *  <p>
 *  plugin.xml registers an application ID for this, and the 'product' file
 *  lists it as the application to run.
 *  <p>
 *  When started from the command line, it will run until killed
 *  (Ctrl-C or send kill signal).
 *  <p>
 *  When started with command-line argument "-console 4812", you can telnet
 *  to localhost 4812 and influence the application like this:
 *  
 *  help - list commands
 *  activeApps - show running apps (should be one)
 *  stopApp org.csstudio.alarm.server.application.0 - Stop that one app
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Application implements IApplication
{
    final public static String APPLICATION_NAME = "AlarmServer";
    private boolean run = true;

    /** {@inheritDoc} */
    public Object start(final IApplicationContext context) throws Exception
    {
        final String version = (String)
            context.getBrandingBundle().getHeaders().get("Bundle-Version");
        final String app_info = context.getBrandingName() + " " + version;
        CentralLogger.getInstance().getLogger(this).info(app_info +
            " started for '" + Preferences.getAlarmTreeRoot() + "' configuration");
        System.out.println(app_info);
        System.out.println("Configuration Root: " + Preferences.getAlarmTreeRoot());
        System.out.println("JMS Server Topic:   " + Preferences.getJMS_AlarmServerTopic());
        System.out.println("JMS Client Topic:   " + Preferences.getJMS_AlarmClientTopic());
        System.out.println("JMS Talk Topic:     " + Preferences.getJMS_TalkTopic());
        
        final Talker talker = new Talker();
        final WorkQueue work_queue = new WorkQueue();
        try
        {
            final AlarmServer alarm_server = new AlarmServer(talker, work_queue);
            // At this point we read the initial alarm configuration
            talker.say(Messages.StartupMessage);
            alarm_server.start();
            while (run)
                work_queue.execute(500);
            alarm_server.stop();
        }
        catch (Throwable ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            talker.say("Alarm server exiting");
            talker.close();
        }
        return IApplication.EXIT_OK;
    }

    /** From the Equinox console, calling 'stopApp' will invoke this method
     *  {@inheritDoc}
     */
    public void stop()
    {
        run = false;
    }
}
