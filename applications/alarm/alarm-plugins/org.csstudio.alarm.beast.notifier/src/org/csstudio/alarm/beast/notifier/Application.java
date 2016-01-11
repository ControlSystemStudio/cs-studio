/*******************************************************************************
* Copyright (c) 2010-2016 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.notifier.actions.AutomatedActionFactory;
import org.csstudio.alarm.beast.notifier.model.IApplicationListener;
import org.csstudio.alarm.beast.notifier.rdb.AlarmRDBHandler;
import org.csstudio.alarm.beast.notifier.rdb.IAlarmRDBHandler;
import org.csstudio.alarm.beast.notifier.util.NotifierUtils;
import org.csstudio.apputil.args.ArgParser;
import org.csstudio.apputil.args.BooleanOption;
import org.csstudio.apputil.args.StringOption;
import org.csstudio.logging.LogConfigurator;
import org.csstudio.security.PasswordInput;
import org.csstudio.security.preferences.SecurePreferences;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 *  @author Fred Arnaud (Sopra Group)
 *  @author Xinyu Wu, Kay Kasemir - notify only on escalating alarms
 */
@SuppressWarnings("nls")
public class Application implements IApplication {

    private static final String[] VERBOSE_PACKAGES = new String[] {
            "com.sun.jersey.core.spi.component",
            "com.sun.jersey.core.spi.component.ProviderServices",
            "com.sun.jersey.spi.service.ServiceFinder",
            "org.apache.activemq",
            // "com.sun" disables com.sun.mail.smtp.SMTPTransport.
            // Should also disable "com.sun.activation.registries.LogSupport",
            // but doesn't ?!
            "com.sun",
            "javax.mail"
    };
    private final List<Logger> strongRefLoggers = new ArrayList<>();

    final public static String APPLICATION_NAME = "AlarmNotifier";
    private boolean run = true;

    /** {@inheritDoc} */
    @Override
    public Object start(final IApplicationContext context) throws Exception
    {
        // Initialize logging
        LogConfigurator.configureFromPreferences();

        // Adjust log level of verbose packages
        Level verboseLogLevel = org.csstudio.alarm.beast.notifier.Preferences.getVerboseLogLevel();
        for (String verbosePackage : VERBOSE_PACKAGES) {
            Logger logger = Logger.getLogger(verbosePackage);
            logger.setLevel(verboseLogLevel);
            for (Handler handler : logger.getHandlers()) {
                handler.setLevel(verboseLogLevel);
            }
            //keep strong references so log manager doesn't release and recreate the loggers with default level
            strongRefLoggers.add(logger);
        }

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
        final StringOption set_password = new StringOption(parser,
                "-set_password", "plugin/key=value", "Set secure preferences", null);
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
        final String option = set_password.get();
        if (option != null) { // Split "plugin/key=value"
            final String pref, value;
            final int sep = option.indexOf("=");
            if (sep >= 0) {
                pref = option.substring(0, sep);
                value = option.substring(sep + 1);
            } else {
                pref = option;
                value = PasswordInput.readPassword("Value for " + pref + ":");
            }
            SecurePreferences.set(pref, value);
            return IApplication.EXIT_OK;
        }

        Activator.getLogger().info(app_info + " started for '" + config_name.get() + "' configuration");
        System.out.println(app_info);

        final int timer_threshold = org.csstudio.alarm.beast.notifier.Preferences.getTimerThreshold();
        System.out.println("Configuration Root: " + config_name.get());
        System.out.println("JMS Server Topic:   " + Preferences.getJMS_AlarmServerTopic(config_name.get()));
        System.out.println("JMS Client Topic:   " + Preferences.getJMS_AlarmClientTopic(config_name.get()));
        System.out.println("JMS Global Topic:   " + Preferences.getJMS_GlobalServerTopic());
        System.out.println("Notifier timer threshold: " + timer_threshold);
        System.out.println("Notifie only escalating alarms: " + PVAlarmHandler.notify_escalating_alarms_only);

        try {
            List<IApplicationListener> listeners = NotifierUtils.getListeners();
            if (listeners != null)
                for (IApplicationListener l : listeners)
                    l.applicationStarted(context);
            AutomatedActionFactory factory = AutomatedActionFactory.getInstance();
            factory.init(NotifierUtils.getActions());
            final IAlarmRDBHandler rdbHandler = new AlarmRDBHandler(config_name.get());
            final AlarmNotifier alarm_notifier = new AlarmNotifier(
                    config_name.get(), rdbHandler, factory, timer_threshold);
            rdbHandler.init(alarm_notifier);
            alarm_notifier.start();
            while (run) {
                Thread.sleep(500);
            }
            alarm_notifier.stop();
        } catch (Throwable ex) {
            Activator.getLogger().log(Level.SEVERE,
                    "Exception during Alarm Notifier starting", ex);
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
