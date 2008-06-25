package org.csstudio.nams.configurator.modelmapping;

import java.beans.PropertyChangeListener;

public interface IConfigurationBean {
	public String getDisplayName();
	public IConfigurationBean getClone();
	public void addPropertyChangeListener(PropertyChangeListener listener);
}
