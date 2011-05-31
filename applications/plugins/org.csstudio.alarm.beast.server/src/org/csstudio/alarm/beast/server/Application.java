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
import org.csstudio.logging.LogConfigurator;
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
    @Override
    public Object start(final IApplicationContext context) throws Exception
    {
        // Initialize logging
        LogConfigurator.configureFromPreferences();

        // Display config info
        final String version = (String)
            context.getBrandingBundle().getHeaders().get("Bundle-Version");
        final String app_info = context.getBrandingName() + " " + version;
        final String config_name = Preferences.getAlarmTreeRoot();
        Activator.getLogger().info(app_info +
            " started for '" + config_name + "' configuration");
        System.out.println(app_info);
        System.out.println("Configuration Root: " + config_name);
        System.out.println("JMS Server Topic:   " + Preferences.getJMS_AlarmServerTopic(config_name));
        System.out.println("JMS Client Topic:   " + Preferences.getJMS_AlarmClientTopic(config_name));
        System.out.println("JMS Talk Topic:     " + Preferences.getJMS_TalkTopic(config_name));
        System.out.println("JMS Global Topic:   " + Preferences.getJMS_GlobalServerTopic());

        final WorkQueue work_queue = new WorkQueue();
        try
        {
            final AlarmServer alarm_server = new AlarmServer(work_queue);
            alarm_server.start();
            while (run)
                work_queue.perform_queued_commands(500);
            alarm_server.stop();
        }
        catch (Throwable ex)
        {
            ex.printStackTrace();
        }
        return IApplication.EXIT_OK;
    }

    /** From the Equinox console, calling 'stopApp' will invoke this method
     *  {@inheritDoc}
     */
    @Override
    public void stop()
    {
        run = false;
    }
}
