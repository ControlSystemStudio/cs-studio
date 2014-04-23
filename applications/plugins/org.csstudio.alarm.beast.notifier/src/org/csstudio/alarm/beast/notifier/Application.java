/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier;

import java.util.logging.Level;

import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.notifier.actions.AutomatedActionFactory;
import org.csstudio.alarm.beast.notifier.rdb.AlarmRDBHandler;
import org.csstudio.alarm.beast.notifier.rdb.IAlarmRDBHandler;
import org.csstudio.alarm.beast.notifier.util.NotifierUtils;
import org.csstudio.apputil.args.ArgParser;
import org.csstudio.apputil.args.BooleanOption;
import org.csstudio.apputil.args.StringOption;
import org.csstudio.logging.LogConfigurator;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class Application implements IApplication {

	final public static String APPLICATION_NAME = "AlarmNotifier";
    private boolean run = true;

    /** {@inheritDoc} */
    @Override
    public Object start(final IApplicationContext context) throws Exception
    {
    	// Display configuration info
        final String version = (String) context.getBrandingBundle().getHeaders().get("Bundle-Version");
        final String app_info = context.getBrandingName() + " " + version;
        
    	// Create parser for arguments and run it.
        final String args[] = (String []) context.getArguments().get("application.args");

        final ArgParser parser = new ArgParser();
        final BooleanOption help_opt = new BooleanOption(parser, "-help", "Display help");
        final BooleanOption version_opt = new BooleanOption(parser, "-version", "Display version info");
        final StringOption config_name = new StringOption(parser,
    		"-root", "Alarm Configuration root", Preferences.getAlarmTreeRoot());
		parser.addEclipseParameters();
		try {
			parser.parse(args);
		} catch (final Exception ex) {
			System.out.println(ex.getMessage() + "\n" + parser.getHelp());
			return IApplication.EXIT_OK;
		}
		if (help_opt.get()) {
			System.out.println(app_info + "\n\n" + parser.getHelp());
			return IApplication.EXIT_OK;
		}
		if (version_opt.get()) {
			System.out.println(app_info);
			return IApplication.EXIT_OK;
		}

        // Initialize logging
        LogConfigurator.configureFromPreferences();
        Activator.getLogger().info(app_info + " started for '" + config_name.get() + "' configuration");
        System.out.println(app_info);
        System.out.println("Configuration Root: " + config_name.get());
        System.out.println("JMS Server Topic:   " + Preferences.getJMS_AlarmServerTopic(config_name.get()));
        System.out.println("JMS Client Topic:   " + Preferences.getJMS_AlarmClientTopic(config_name.get()));
        System.out.println("JMS Global Topic:   " + Preferences.getJMS_GlobalServerTopic());
        System.out.println("Notifier timer threshold: " + org.csstudio.alarm.beast.notifier.Preferences.getTimerThreshold());
        
		try {
			AutomatedActionFactory factory = AutomatedActionFactory.getInstance();
			factory.init(NotifierUtils.getActions());
			final IAlarmRDBHandler rdbHandler = new AlarmRDBHandler(config_name.get());
			final int timer_threshold = org.csstudio.alarm.beast.notifier.Preferences.getTimerThreshold();
			final AlarmNotifier alarm_notifer = new AlarmNotifier(
					config_name.get(), rdbHandler, factory, timer_threshold);
			rdbHandler.init(alarm_notifer);
			alarm_notifer.start();
			while (run) {
				Thread.sleep(500);
			}
			alarm_notifer.stop();
		} catch (Throwable ex) {
			Activator.getLogger().log(Level.SEVERE,
					"Exception during Alarm Notifier starting: {0}",
					ex.getMessage());
			return Integer.valueOf(-1);
		}
		return IApplication.EXIT_OK;
    }

	/**
	 * From the Equinox console, calling 'stopApp' will invoke this method
	 * {@inheritDoc}
	 */
	@Override
	public void stop() {
		run = false;
	}

}
