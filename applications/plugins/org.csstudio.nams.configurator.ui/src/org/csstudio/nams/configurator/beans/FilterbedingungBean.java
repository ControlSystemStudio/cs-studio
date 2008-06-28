package org.csstudio.nams.configurator.beans;

public class FilterbedingungBean extends
		AbstractConfigurationBean<FilterbedingungBean> {

	private int filterbedinungID;
	private String description;
	private String name;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FilterbedingungBean) {
			// TODO compare IDs
			// FilterbedingungBean bean = (FilterbedingungBean) obj;
		}
		return super.equals(obj);
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
}
