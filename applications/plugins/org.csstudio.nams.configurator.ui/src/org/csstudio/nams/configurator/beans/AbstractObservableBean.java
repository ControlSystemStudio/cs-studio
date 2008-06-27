package org.csstudio.nams.configurator.beans;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.csstudio.nams.configurator.modelmapping.IConfigurationBean;

/**
 * Diese Bean kapselt den grundlegenden PropertyChangeSupport. Beans, die
 * lediglich den PropertyChangeSupport benötigen, können hiervon erben
 * 
 * @author Eugen Reiswich
 * 
 */
public abstract class AbstractObservableBean implements IConfigurationBean {

	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	/**
	 * Propertychange suppoert for JFace Databinding
	 */
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(propertyName, listener);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	protected PropertyChangeSupport getPropertyChangeSupport() {
		return pcs;
	}
	
	public void clearPropertyChangeListeners() {
		pcs = new PropertyChangeSupport(this);
	}
}
