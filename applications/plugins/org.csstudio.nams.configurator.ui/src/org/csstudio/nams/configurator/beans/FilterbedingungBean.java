package org.csstudio.nams.configurator.beans;

import org.csstudio.nams.configurator.beans.filters.AddOnBean;

public class FilterbedingungBean extends
		AbstractConfigurationBean<FilterbedingungBean> {

	private int filterbedinungID;
	private String description;
	private String name;
	
	private AddOnBean filterSpecificBean;

	public static enum PropertyNames {
		filterbedingungID, description, name;
	}
	
	public int getFilterbedinungID() {
		return filterbedinungID;
	}

	public void setFilterbedinungID(int filterbedinungID) {
		this.filterbedinungID = filterbedinungID;
	}

	public FilterbedingungBean() {
	}

	public String getDisplayName() {
		return name == null ? "(ohne Namen)" : name;
	}

	public void copyStateOf(FilterbedingungBean otherBean) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not implemented yet.");
	}

	@Override
	public FilterbedingungBean getClone() {
		FilterbedingungBean bean = new FilterbedingungBean();
		bean.setDescription(description);
		bean.setName(name);
		bean.setFilterbedinungID(filterbedinungID);
		return bean;
	}

	@Override
	public void updateState(FilterbedingungBean bean) {
		// TODO Auto-generated method stub

	}

	public int getID() {
		return this.getFilterbedinungID();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return getDisplayName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime
				* result
				+ ((filterSpecificBean == null) ? 0 : filterSpecificBean
						.hashCode());
		result = prime * result + filterbedinungID;
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
		final FilterbedingungBean other = (FilterbedingungBean) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (filterSpecificBean == null) {
			if (other.filterSpecificBean != null)
				return false;
		} else if (!filterSpecificBean.equals(other.filterSpecificBean))
			return false;
		if (filterbedinungID != other.filterbedinungID)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public AddOnBean getFilterSpecificBean() {
		return filterSpecificBean;
	}

	public void setFilterSpecificBean(AddOnBean filterSpecificBean) {
		this.filterSpecificBean = filterSpecificBean;
	}
}
