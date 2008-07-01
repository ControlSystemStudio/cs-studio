package org.csstudio.nams.configurator.beans;


public class FilterBean extends AbstractConfigurationBean<FilterBean> {

	public static enum PropertyNames {
		filterID, name, defaultMessage

	}

	private int filterID;// PRIMARY KEY
	private String name;
	private String defaultMessage;
	// TODO hier fehlt noch einiges (Beans für FilterConditions) tr: regenerate hashCode/equals !!!

	public FilterBean() {
		filterID = -1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((defaultMessage == null) ? 0 : defaultMessage.hashCode());
		result = prime * result + filterID;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final FilterBean other = (FilterBean) obj;
		if (defaultMessage == null) {
			if (other.defaultMessage != null)
				return false;
		} else if (!defaultMessage.equals(other.defaultMessage))
			return false;
		if (filterID != other.filterID)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public int getFilterID() {
		return filterID;
	}

	public void setFilterID(int filterID) {
		int oldValue = getFilterID();
		this.filterID = filterID;
		pcs.firePropertyChange(
				PropertyNames.filterID.name(), oldValue,
				getFilterID());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldValue = getName();
		this.name = name;
		pcs.firePropertyChange(PropertyNames.name
				.name(), oldValue, getName());
	}

	public String getDefaultMessage() {
		return defaultMessage;
	}

	public void setDefaultMessage(String defaultMessage) {
		String oldValue = getDefaultMessage();
		this.defaultMessage = defaultMessage;
		pcs.firePropertyChange(
				PropertyNames.defaultMessage.name(), oldValue,
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
		FilterBean bean = new FilterBean();
		bean.setDefaultMessage(getDefaultMessage());
		bean.setName(getName());
		return bean;
	}

	@Override
	public void updateState(FilterBean bean) {
		// TODO Auto-generated method stub

	}

	public int getID() {
		return this.getFilterID();
	}
	
	@Override
	public String toString() {
		return getDisplayName();
	}
}
