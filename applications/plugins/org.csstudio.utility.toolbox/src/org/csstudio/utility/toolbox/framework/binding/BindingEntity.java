package org.csstudio.utility.toolbox.framework.binding;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Transient;


public abstract class BindingEntity implements Serializable {

	protected static final long serialVersionUID = 1L;
	
	private Boolean newRecord = true;
		
	@Transient
	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public void removeAllPropertyChangeListeners() {
		for (PropertyChangeListener pcl: pcs.getPropertyChangeListeners()) {
			removePropertyChangeListener(pcl);
		}
	}

	public void childsHaveChanged() {
		Long now = new Date().getTime();
		pcs.firePropertyChange("childs", now, now + 1);	
	}

	public boolean isNew() {
		return newRecord;
	}

	public PropertyChangeSupport getPcs() {
		return pcs;
	}

	public void setNewRecord(boolean value) {
		newRecord = value;
	}
	
	public boolean hasId() {
		return false;
	}

}
