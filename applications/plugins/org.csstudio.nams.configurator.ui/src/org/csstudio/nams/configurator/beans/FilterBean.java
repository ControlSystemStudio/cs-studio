package org.csstudio.nams.configurator.beans;

import java.beans.PropertyChangeSupport;

public class FilterBean extends AbstractConfigurationBean<FilterBean> {

	public static enum FilterBeanPropertyNames {
		filterID, name, defaultMessage

	}

	private int filterID;// PRIMARY KEY
	private String name;
	private String defaultMessage;
	// TODO hier fehlt noch einiges (Beans für FilterConditions)

	public FilterBean() {
		filterID = -1;
	}

	public int getFilterID() {
		return filterID;
	}

	public void setFilterID(int filterID) {
		int oldValue = getFilterID();
		this.filterID = filterID;
		pcs.firePropertyChange(
				FilterBeanPropertyNames.filterID.name(), oldValue,
				getFilterID());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldValue = getName();
		this.name = name;
		pcs.firePropertyChange(FilterBeanPropertyNames.name
				.name(), oldValue, getName());
	}

	public String getDefaultMessage() {
		return defaultMessage;
	}

	public void setDefaultMessage(String defaultMessage) {
		String oldValue = getDefaultMessage();
		this.defaultMessage = defaultMessage;
		pcs.firePropertyChange(
				FilterBeanPropertyNames.defaultMessage.name(), oldValue,
				getDefaultMessage());
	}

	public String getDisplayName() {
		return getName() != null ? getName() : "(ohne Namen)";
	}

	public void copyStateOf(FilterBean otherBean) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not implemented yet.");
	}

	@Override
	public FilterBean getClone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FilterBean) {
			FilterBean bean = (FilterBean) obj;
			return this.getFilterID() == bean.getFilterID();
		}
		return super.equals(obj);
	}

	@Override
	public void updateState(FilterBean bean) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getID() {
		return this.getFilterID();
	}
	
	@Override
	public String toString() {
		return getDisplayName();
	}
}
