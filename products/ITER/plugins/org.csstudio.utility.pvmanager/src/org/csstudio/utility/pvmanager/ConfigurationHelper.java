package org.csstudio.utility.pvmanager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.epics.pvmanager.DataSource;

/**
 * Helper class to configure PVManager data sources based on extension points.
 * 
 * @author carcassi
 */
public class ConfigurationHelper {
	
	private static final Logger log = Logger.getLogger(ConfigurationHelper.class.getName());
	
	private ConfigurationHelper() {
		// Do not instantiate
	}
	
    // Note: "org.csstudio.utility.pv" and "default_type"
	//       are common setting between pv, pv.ui, pvmanager and pvmanager.ui
	//       They need to be kept synchronized.
	/** Preference ID of the default PV type */
    final public static String DEFAULT_TYPE_KEY = "default_type";
    final public static String DEFAULT_TYPE_QUALIFIER = "org.csstudio.utility.pv";
	
	public static String defaultDataSourceName() {
        final IPreferencesService prefs = Platform.getPreferencesService();
        return prefs.getString(DEFAULT_TYPE_QUALIFIER, DEFAULT_TYPE_KEY, "ca", null);
	}
	
	/**
	 * Retrieves the data sources that have been registered through the extension point.
	 * 
	 * @return the registered data sources
	 */
	public static Map<String, DataSource> configuredDataSources() {
		try {
			Map<String, DataSource> dataSources = new HashMap<String, DataSource>();
			
			IConfigurationElement[] config = Platform.getExtensionRegistry()
			.getConfigurationElementsFor("org.csstudio.utility.pvmanager.datasource");
			
			for (IConfigurationElement iConfigurationElement : config) {
				final Object o = iConfigurationElement
						.createExecutableExtension("datasource");
				final String prefix = iConfigurationElement.getAttribute("prefix");
				if (o instanceof DataSource) {					
					DataSource ds = (DataSource) o;
					dataSources.put(prefix, ds);
				}
			}
			
			return dataSources;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Could not retrieve configured DataSources for PVManager", e);
			return Collections.emptyMap();
		}

	}

}
