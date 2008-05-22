package org.csstudio.nams.configurator.treeviewer.model;

import java.beans.PropertyChangeListener;

public interface ObservableBean {

	/**
	 * Propertychange suppoert for JFace Databinding
	 */
	public abstract void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener);

	public abstract void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener);

	public abstract void addPropertyChangeListener(
			PropertyChangeListener listener);

	public abstract void removePropertyChangeListener(
			PropertyChangeListener listener);

}