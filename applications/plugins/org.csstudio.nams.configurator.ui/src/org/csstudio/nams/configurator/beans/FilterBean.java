package org.csstudio.nams.configurator.beans;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class FilterBean extends AbstractConfigurationBean<FilterBean> {

	public static enum PropertyNames {
		filterID, name, defaultMessage, conditions

	}

	private int filterID;// PRIMARY KEY
	private String name;
	private String defaultMessage;
	private List<FilterbedingungBean> conditions = new LinkedList<FilterbedingungBean>();
	
	public FilterBean() {
		filterID = -1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((conditions == null) ? 0 : conditions.hashCode());
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
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final FilterBean other = (FilterBean) obj;
		if (conditions == null) {
			if (other.conditions != null)
				return false;
		} else if (!conditions.equals(other.conditions))
			return false;
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

	@Override
	protected void doUpdateState(FilterBean bean) {
		setDefaultMessage(bean.getDefaultMessage());
		setName(bean.getName());
		setFilterID(bean.getFilterID());
		
		List<FilterbedingungBean> cloneList = new LinkedList<FilterbedingungBean>();
		List<FilterbedingungBean> list = bean.getConditions();
		for (FilterbedingungBean filterbedingungBean : list) {
			cloneList.add(filterbedingungBean.getClone());
		}
		
		setConditions(cloneList);
	}

	public int getID() {
		return this.getFilterID();
	}
	
	@Override
	public String toString() {
		return getDisplayName();
	}

	public void setID(int id) {
		setFilterID(id);
	}

	/**
	 * returns a list of an and combined {@link FilterbedingungBean} list.
	 *  this is done for backwards compatibility
	 * @return
	 */
	public List<FilterbedingungBean> getConditions() {
		return new LinkedList<FilterbedingungBean>(this.conditions);
	}

	public void setConditions(List<FilterbedingungBean> conditions) {
		List<FilterbedingungBean> oldValue = this.conditions;
		this.conditions = conditions;
		Collections.sort(this.conditions);
		pcs.firePropertyChange(PropertyNames.conditions.name(), oldValue, conditions);
	}
}
