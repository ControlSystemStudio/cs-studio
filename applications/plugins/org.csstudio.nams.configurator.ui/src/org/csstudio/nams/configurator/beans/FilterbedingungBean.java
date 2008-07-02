package org.csstudio.nams.configurator.beans;

import org.csstudio.nams.configurator.beans.filters.AddOnBean;
import org.csstudio.nams.configurator.beans.filters.JunctorConditionBean;

public class FilterbedingungBean extends
		AbstractConfigurationBean<FilterbedingungBean> {

	private int filterbedinungID;
	private String description;
	private String name;
	
	private AddOnBean filterSpecificBean;

	public static enum PropertyNames {
		filterbedingungID, description, name, filterSpecificBean;
	}
	
	public int getFilterbedinungID() {
		return filterbedinungID;
	}

	public void setFilterbedinungID(int filterbedinungID) {
		int oldValue = this.filterbedinungID;
		this.filterbedinungID = filterbedinungID;
		pcs.firePropertyChange(PropertyNames.filterbedingungID.name(), oldValue, filterbedinungID);
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
		if (filterSpecificBean != null) {
			bean.setFilterSpecificBean((AddOnBean) ((IConfigurationBean)filterSpecificBean).getClone());
		} else {
			// TODO mw: default is always ODER, i'm not sure about this here
			JunctorConditionBean junctorConditionBean = new JunctorConditionBean();
			filterSpecificBean = junctorConditionBean;
			bean.setFilterSpecificBean(junctorConditionBean);
		}
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
		String oldValue = this.description;
		this.description = description;
		pcs.firePropertyChange(PropertyNames.description.name(), oldValue, description);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldValue = this.name;
		this.name = name;
		pcs.firePropertyChange(PropertyNames.name.name(), oldValue, name);
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

	public AbstractConfigurationBean getFilterSpecificBean() {
		return (AbstractConfigurationBean) filterSpecificBean;
	}

	public void setFilterSpecificBean(AddOnBean filterSpecificBean) {
		AddOnBean oldValue = this.filterSpecificBean;
		this.filterSpecificBean = filterSpecificBean;
		pcs.firePropertyChange(PropertyNames.filterSpecificBean.name(), oldValue, filterSpecificBean);
	}

	public void setID(int id) {
		setFilterbedinungID(id);
	}
}
