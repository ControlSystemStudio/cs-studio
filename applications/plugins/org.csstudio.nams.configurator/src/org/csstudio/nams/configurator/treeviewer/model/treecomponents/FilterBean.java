package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import java.beans.PropertyChangeSupport;

import org.csstudio.nams.configurator.treeviewer.model.AbstractConfigurationBean;

public class FilterBean extends AbstractConfigurationBean<FilterBean> {

	public static enum FilterBeanPropertyNames {
		filterID, name, defaultMessage
			
	}
	
	private int 	filterID;// PRIMARY KEY
	private String 	name;
	private String 	defaultMessage;
	//TODO hier fehlt noch einiges (Beans für FilterConditions)
	
	private PropertyChangeSupport propertyChangeSupport;
	
	public FilterBean() {
		filterID = -1;
		propertyChangeSupport = getPropertyChangeSupport();
	}

	public int getFilterID() {
		return filterID;
	}

	public void setFilterID(int filterID) {
		int oldValue = getFilterID();
		this.filterID = filterID;
		propertyChangeSupport.firePropertyChange(FilterBeanPropertyNames.filterID.name(), oldValue, getFilterID());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldValue = getName();
		this.name = name;
		propertyChangeSupport.firePropertyChange(FilterBeanPropertyNames.name.name(), oldValue, getName());
	}

	public String getDefaultMessage() {
		return defaultMessage;
	}

	public void setDefaultMessage(String defaultMessage) {
		String oldValue = getDefaultMessage();
		this.defaultMessage = defaultMessage;
		propertyChangeSupport.firePropertyChange(FilterBeanPropertyNames.defaultMessage.name(), oldValue, getDefaultMessage());
	}
	
	@Override
	public String getDisplayName() {
		return getName() != null ? getName() : "(ohne Namen)";
	}

	@Override
	public void copyStateOf(FilterBean otherBean) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not implemented yet.");
	}
}
