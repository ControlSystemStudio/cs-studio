package org.csstudio.alarm.beast.notifier;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.notifier.actions.NotificationActionFactory;
import org.csstudio.alarm.beast.notifier.model.IAutomatedAction;
import org.csstudio.alarm.beast.notifier.rdb.AlarmRDBHandler;
import org.csstudio.alarm.beast.notifier.rdb.IAlarmRDBHandler;
import org.csstudio.apputil.args.ArgParser;
import org.csstudio.apputil.args.BooleanOption;
import org.csstudio.apputil.args.StringOption;
import org.csstudio.logging.LogConfigurator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class Application implements IApplication {

	final public static String APPLICATION_NAME = "AlarmNotifier";
    private boolean run = true;

    /** {@inheritDoc} */
    @Override
    public Object start(final IApplicationContext context) throws Exception
    {
    	// Create parser for arguments and run it.
        final String args[] = (String []) context.getArguments().get("application.args");

        final ArgParser parser = new ArgParser();
        final BooleanOption help_opt = new BooleanOption(parser, "-help", "Display Help");
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
			System.out.println(parser.getHelp());
			return IApplication.EXIT_OK;
		}

        // Initialize logging
        LogConfigurator.configureFromPreferences();

        // Display configuration info
        final String version = (String) context.getBrandingBundle().getHeaders().get("Bundle-Version");
        final String app_info = context.getBrandingName() + " " + version;
        Activator.getLogger().info(app_info + " started for '" + config_name.get() + "' configuration");
        System.out.println(app_info);
        System.out.println("Configuration Root: " + config_name.get());
        System.out.println("JMS Server Topic:   " + Preferences.getJMS_AlarmServerTopic(config_name.get()));
        System.out.println("JMS Client Topic:   " + Preferences.getJMS_AlarmClientTopic(config_name.get()));
        System.out.println("JMS Global Topic:   " + Preferences.getJMS_GlobalServerTopic());
        System.out.println("JMS Notifier Exe Topic: " + Preferences.getJMS_AlarmNotifierExeTopic(config_name.get()));
        System.out.println("JMS Notifier Rtn Topic: " + Preferences.getJMS_AlarmNotifierRtnTopic(config_name.get()));
        System.out.println("Notifier threshold: " + org.csstudio.alarm.beast.notifier.Preferences.getThreshold());

		try {
			NotificationActionFactory factory = NotificationActionFactory.getInstance();
			factory.init(getActions());
			final IAlarmRDBHandler rdbHandler = new AlarmRDBHandler(config_name.get());
			// TODO: define threshold in preferences
			final int threshold = org.csstudio.alarm.beast.notifier.Preferences.getThreshold();
			final AlarmNotifier alarm_notifer = new AlarmNotifier(config_name.get(), rdbHandler, factory, threshold);
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

	/**
	 * Read automated action extension points from plugin.xml.
	 * @return Map<String, IAutomatedAction>, extension points referenced by their scheme.
	 * @throws CoreException if implementations don't provide the correct IAutomatedAction
	 */
	public static Map<String, IAutomatedAction> getActions() throws CoreException {
	    final Map<String, IAutomatedAction> map = new HashMap<String, IAutomatedAction>();
		final IExtensionRegistry reg = Platform.getExtensionRegistry();
		final IConfigurationElement[] extensions = reg
				.getConfigurationElementsFor(IAutomatedAction.EXTENSION_POINT);
		for (IConfigurationElement element : extensions)
		{
			final String scheme = element.getAttribute("scheme");
			final IAutomatedAction action = (IAutomatedAction) element.createExecutableExtension("action");
			map.put(scheme, action);
		}
		return map;
	}

}
