package org.csstudio.nams.configurator.beans;

import org.csstudio.nams.configurator.beans.filters.FilterConditionAddOnBean;
import org.csstudio.nams.configurator.beans.filters.JunctorConditionBean;
import org.csstudio.nams.configurator.beans.filters.StringFilterConditionBean;

public class FilterbedingungBean extends
		AbstractConfigurationBean<FilterbedingungBean> {

	private int filterbedinungID;
	private String description;
	private String name;
	
	private FilterConditionAddOnBean filterSpecificBean;

	public static enum PropertyNames {
		filterbedingungID, description, name, filterSpecificBean
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
		filterSpecificBean = new JunctorConditionBean();
	}

	public String getDisplayName() {
		return name == null ? "(ohne Namen)" : name;
	}

	@Override
	protected void doUpdateState(FilterbedingungBean bean) {
		setDescription(bean.getDescription());
		setName(bean.getName());
		setFilterbedinungID(bean.getFilterbedinungID());
		setFilterSpecificBean((FilterConditionAddOnBean) bean.getFilterSpecificBean());

		if (filterSpecificBean != null) {
			bean.setFilterSpecificBean((FilterConditionAddOnBean) filterSpecificBean.getClone());
		} else {
			// TODO mw: default is always ODER, i'm not sure about this here
			StringFilterConditionBean junctorConditionBean = new StringFilterConditionBean();
			filterSpecificBean = junctorConditionBean;
			bean.setFilterSpecificBean(junctorConditionBean);
		}
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
		int result = super.hashCode();
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
		if (!super.equals(obj))
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

	public AbstractConfigurationBean<?> getFilterSpecificBean() {
		return (AbstractConfigurationBean<?>) filterSpecificBean;
	}

	public void setFilterSpecificBean(FilterConditionAddOnBean filterSpecificBean) {
		FilterConditionAddOnBean oldValue = this.filterSpecificBean;
		this.filterSpecificBean = filterSpecificBean;
		pcs.firePropertyChange(PropertyNames.filterSpecificBean.name(), oldValue, filterSpecificBean);
	}

	public void setID(int id) {
		setFilterbedinungID(id);
	}
}
