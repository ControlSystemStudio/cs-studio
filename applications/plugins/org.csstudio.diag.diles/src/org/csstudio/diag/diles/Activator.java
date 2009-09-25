package org.csstudio.diag.diles;

import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.diag.diles";

	private static Activator singleton;

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return singleton;
	}

	/**
	 * The constructor
	 */
	public Activator() {
		if (singleton == null) {
			singleton = this;
		}
	}

}
