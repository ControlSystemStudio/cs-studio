package org.csstudio.sds.components.common;

import java.util.HashMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

/**
 * A shared class used to get information about the loaded switch type plugins.
 * 
 * @author jbercic
 *
 */
public class SwitchPlugins {
	/**
	 * The switch names and their IConfigurationElements.
	 */
	public static HashMap<String,IConfigurationElement> classes_map;
	public static String [] names;
	public static String [] ids;
	
	/**
	 * enumerate plugins
	 */
	static {
		HashMap<String,String> names_map=new HashMap<String,String>();
		classes_map=new HashMap<String,IConfigurationElement>();
		IExtension [] extensions=Platform.getExtensionRegistry().getExtensionPoint("org.csstudio.sds.org.csstudio.sds.components.Switch").getExtensions();
		IConfigurationElement [] configs;
		for (IExtension i:extensions) {
			configs=i.getConfigurationElements();
			for (IConfigurationElement j:configs) {
				names_map.put(j.getAttribute("SwitchID"),j.getAttribute("Name"));
				classes_map.put(j.getAttribute("SwitchID"),j);
			}
		}
		names=new String[names_map.size()];
		ids=names_map.keySet().toArray(new String[names_map.size()]);
		for (int i=0;i<names_map.size();i++) {
			names[i]=names_map.get(ids[i]);
		}
	}
}
