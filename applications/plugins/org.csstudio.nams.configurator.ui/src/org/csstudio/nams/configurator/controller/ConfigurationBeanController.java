package org.csstudio.nams.configurator.controller;

import java.util.HashSet;
import java.util.Set;

import org.csstudio.nams.configurator.modelmapping.IConfigurationBean;

public class ConfigurationBeanController {
	
	Set<IConfigurationChangeListener> listeners = new HashSet<IConfigurationChangeListener>();
	
	public void addConfigurationChangedListener(IConfigurationChangeListener listener){
		listeners.add(listener);
	}
	/**
	 * calls all update methods on all registered listeners
	 */
	public void fireChange(){
		for (IConfigurationChangeListener listener : listeners) {
			listener.updateAll();
		}
	};
	/**
	 * calls the bean specific update methods on all registered listeners
	 * @param cls
	 */
	public void fireChangeOn(Class<IConfigurationBean> cls){
		for (IConfigurationChangeListener listener : listeners) {
			listener.update(cls);
		}
	}
	
}
