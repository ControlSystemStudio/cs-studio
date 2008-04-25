package org.csstudio.nams.application.department.decision;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.BundleContext;

/**
 * The activator and application class controls the plug-in and application life
 * cycle.
 */
public class DecisionDepartmentActivator extends Plugin implements IApplication {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.csstudio.nams.application.department.decision";

	/**
	 * TODO Beachten: Eclipse wird 2 Exemplare dieser Klasse anlegen! Dies
	 * bedeutet, alle felder die zwischen PlugIn Activator und Application
	 * geteilt werden, müssen static sein! Also alle Servicde-Felder. Die
	 * Tracker werden nur von Activator benutzt und müssen nicht static sein.
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
		// TODO sammle benötigte services aus dem Bundle context.

		super.start(context);
	}

	/**
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);

		// TODO scliesse serviceTracker.
	}

	/**
	 * Start the applciation.
	 */
	public Object start(IApplicationContext context) throws Exception {
		// TODO Lade configuration, konvertiere diese und ewrzeuge die büros und
		// starte deren Arbeit
		// lock until application ready to quit.
		return IApplication.EXIT_OK;
	}

	/**
	 * Stop the application.
	 */
	public void stop() {
		// TODO stoppe büros
	}
}
