package org.csstudio.nams.configurator.beans;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public interface IConfigurationBean {
	public String getDisplayName();
	public IConfigurationBean getClone();
	public void addPropertyChangeListener(PropertyChangeListener listener);
	public PropertyChangeSupport getPropertyChangeSupport();
	public int getID();
	public String getRubrikName();
}
