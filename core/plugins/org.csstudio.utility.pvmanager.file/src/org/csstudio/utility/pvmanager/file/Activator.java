package org.csstudio.utility.pvmanager.file;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.epics.pvmanager.file.FileFormat;
import org.epics.pvmanager.file.FileFormatRegistry;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.utility.pvmanager.file";

	// The shared instance
	private static Activator plugin;
	private static final Logger log = Logger.getLogger(Activator.class.getName());
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		registerFileFormats();
		
	}

	private void registerFileFormats() {
	    	IConfigurationElement[] config = Platform.getExtensionRegistry()
		    .getConfigurationElementsFor("org.csstudio.utility.pvmanager.file.format");
	    	for (IConfigurationElement iConfigurationElement : config) {
			try {
			    final Object o = iConfigurationElement.createExecutableExtension("fileFormat");
			    final String extension = iConfigurationElement.getAttribute("extension");
			    if (extension!= null && !extension.isEmpty() && o instanceof FileFormat) {
			        FileFormatRegistry.getDefault().registerFileFormat(extension, (FileFormat) o);			    
			    }
			} catch (Exception e) {
			    log.log(Level.INFO, "Failed to registed FileFormat : Cause " + e.getMessage());
			}
		    }
	    
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
