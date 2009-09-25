package org.csstudio.diag.diles.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

abstract public class AbstractChartElement implements Cloneable {
	public static final String UNIQUE_ID = "unique_id", SIZE = "size",
			LOC = "location", NAME = "name", CHILD = "children",
			TARGETS = "targets", SOURCES = "sources",
			ACTIVITY_STATUS = "activity_status", COLUMN = "column",
			BENDPOINT = "bendpoint", ACTIVE_COLUMN = "bg_color",
			NUMBER_ID = "number_id", DELAY = "delay";

	protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		listeners.addPropertyChangeListener(pcl);
	}

	protected void firePropertyChange(String propName, Object old,
			Object newValue) {
		listeners.firePropertyChange(propName, old, newValue);
	}

	public PropertyChangeSupport getListeners() {
		return listeners;
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		listeners.removePropertyChangeListener(pcl);
	}
}