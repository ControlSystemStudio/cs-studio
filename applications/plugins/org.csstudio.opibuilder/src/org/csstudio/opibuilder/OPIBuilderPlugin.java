package org.csstudio.opibuilder;

import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.script.ScriptService;
import org.csstudio.opibuilder.util.GUIRefreshThread;
import org.csstudio.opibuilder.util.MediaService;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Xihui Chen
 *
 */
public class OPIBuilderPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.opibuilder"; //$NON-NLS-1$
	
	/**
	 * The ID of the widget extension point.
	 */
	public static final String EXTPOINT_WIDGET = PLUGIN_ID + ".widget"; //$NON-NLS-1$
	
	
	/**
	 * The ID of the widget extension point.
	 */
	public static final String EXTPOINT_FEEDBACK_FACTORY = PLUGIN_ID + ".graphicalFeedbackFactory"; //$NON-NLS-1$
	
	
	public static final String OPI_FILE_EXTENSION = "opi";

	// The shared instance
	private static OPIBuilderPlugin plugin;
	
	private IPropertyChangeListener preferenceLisener;
	
	
	/**
	 * The constructor
	 */
	public OPIBuilderPlugin() {
		plugin = this;
	}



	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static OPIBuilderPlugin getDefault() {
		return plugin;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		ScriptService.getInstance();
		
		//ConsoleService.getInstance().writeInfo("Welcome to Best OPI, Yet (BOY)!");
		preferenceLisener = new IPropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent event) {
				if(event.getProperty().equals(PreferencesHelper.COLOR_FILE) ||
						event.getProperty().equals(PreferencesHelper.FONT_FILE)){
					MediaService.getInstance().reload();
				}
				if(event.getProperty().equals(PreferencesHelper.OPI_GUI_REFRESH_CYCLE))
					GUIRefreshThread.getInstance().reSchedule();
				if(event.getProperty().equals(PreferencesHelper.DISABLE_ADVANCED_GRAPHICS)){
					System.setProperty("prohibit_advanced_graphics", //$NON-NLS-1$
							PreferencesHelper.isAdvancedGraphicsDisabled() ? "true": "false"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			
		};
		
		getPluginPreferences().addPropertyChangeListener(preferenceLisener);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		ScriptService.getInstance().exit();
		getPluginPreferences().removePropertyChangeListener(preferenceLisener);
	}


}
