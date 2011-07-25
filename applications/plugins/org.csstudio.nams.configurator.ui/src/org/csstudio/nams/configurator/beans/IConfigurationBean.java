
package org.csstudio.nams.configurator.beans;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public interface IConfigurationBean {
	public void addPropertyChangeListener(PropertyChangeListener listener);

	public IConfigurationBean getClone();

	public String getDisplayName();
	
	public void setDisplayName(String name);

	public int getID();

	public PropertyChangeSupport getPropertyChangeSupport();

	public String getRubrikName();

	public void removePropertyChangeListener(PropertyChangeListener listener);

	public void setID(int id);
}
