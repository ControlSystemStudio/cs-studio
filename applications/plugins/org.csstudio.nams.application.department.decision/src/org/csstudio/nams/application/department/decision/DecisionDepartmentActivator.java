package org.csstudio.nams.application.department.decision;

import org.csstudio.nams.service.logging.declaration.Logger;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator and application class controls the plug-in and application life
 * cycle.
 */
public class DecisionDepartmentActivator extends Plugin implements IApplication {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.csstudio.nams.application.department.decision";

	/**
	 * Feld des Activators: ServiceTracker des Loggers.
	 */
	private ServiceTracker _serviceTrackerLogger;

	/**
	 * Gemeinsames Feld des Activators und der Application: Der Logger.
	 */
	private static Logger logger;

	/**
	 * Feld der Application: Feld das angibt, ob die Application weiter arbeiten
	 * soll.
	 */
	private volatile boolean _arbeitFortsetzen = false;

	/**
	 * TODO Beachten: Eclipse wird 2 Exemplare dieser Klasse anlegen! Dies
	 * bedeutet, alle felder die zwischen PlugIn Activator und Application
	 * geteilt werden, mŸssen static sein! Also alle Servicde-Felder. Die
	 * Tracker werden nur von Activator benutzt und mŸssen nicht static sein.
	 */

	/**
	 * The constructor
	 */
	public DecisionDepartmentActivator() {
	}

	/**
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);

		_serviceTrackerLogger = new ServiceTracker(context, Logger.class
				.getName(), null);
		_serviceTrackerLogger.open();
		logger = (Logger) _serviceTrackerLogger.getService();
	}

	/**
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);

		_serviceTrackerLogger.close();
	}

	/**
	 * Start the applciation.
	 */
	public Object start(IApplicationContext context) throws Exception {
		// TODO Lade configuration, konvertiere diese und ewrzeuge die bŸros und
		// starte deren Arbeit
		// lock until application ready to quit.
		logger.logInfoMessage(this, "Decision department application is going to be initialized...");
		_arbeitFortsetzen = true;

		logger.logInfoMessage(this, "Decision department application successfully initialized, begining work...");
		while (_arbeitFortsetzen) {
			/*-
			 * try jms.receive if result != null (tue was)
			 */
			
			Thread.yield();
		}
		// TODO stoppe bŸros

		logger.logInfoMessage(this, "Decision department application successfully shuted down.");
		return IApplication.EXIT_OK;
	}

	/**
	 * Stop the application.
	 */
	public void stop() {
		logger.logInfoMessage(this, "Shuting down decision department application...");
		_arbeitFortsetzen = false;
		// TODO JMS Close -> recieve abgebrochen mit null return
	}
}
