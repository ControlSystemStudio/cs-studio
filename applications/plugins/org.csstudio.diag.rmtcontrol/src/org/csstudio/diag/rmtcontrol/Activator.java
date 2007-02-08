package org.csstudio.diag.rmtcontrol;

import java.io.File;

import org.csstudio.diag.rmtcontrol.Preference.SampleService;
import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractCssUiPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.diag.RMTControl"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}



	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	@Override
	protected void doStart(BundleContext context) throws Exception {
//		super.start(context);
		File defaultFile = new File(getPluginPreferences().getString(SampleService.RMT_XML_FILE_PATH));
		if(!defaultFile.isFile()){
			if(defaultFile.isDirectory()){
				defaultFile = new File(defaultFile,"rmt.xml"); //$NON-NLS-1$
			}
			else{
				defaultFile = new File(getPluginPreferences().getDefaultString(SampleService.RMT_XML_FILE_PATH));
			}
			getPluginPreferences().setValue(SampleService.RMT_XML_FILE_PATH, defaultFile.toString());
			if(defaultFile.createNewFile()){
				WriteDefaultXML.writeDefault(defaultFile);
			}
		}
	}

	@Override
	protected void doStop(BundleContext context) throws Exception {
		plugin = null;
	}


	@Override
	public String getPluginId() {
		return PLUGIN_ID;
	}

	/** Add informational message to the plugin log. */
    public static void logInfo(String message)
    {
        getDefault().log(IStatus.INFO, message, null);
    }

    /** Add error message to the plugin log. */
    public static void logError(String message)
    {
        getDefault().log(IStatus.ERROR, message, null);
    }

    /** Add an exception to the plugin log. */
    public static void logException(String message, Exception e)
    {
        getDefault().log(IStatus.ERROR, message, e);
    }

    /** Add a message to the log.
     * @param type
     * @param message
     */
    private void log(int type, String message, Exception e)
    {
        getLog().log(new Status(type, PLUGIN_ID, IStatus.OK, message, e));
    }
}
