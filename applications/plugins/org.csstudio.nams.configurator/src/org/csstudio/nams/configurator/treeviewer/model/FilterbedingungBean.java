package org.csstudio.nams.configurator.treeviewer.model;

public class FilterbedingungBean extends
		AbstractConfigurationBean<FilterbedingungBean> {

	private int filterbedinungID;

	public int getFilterbedinungID() {
		return filterbedinungID;
	}

	public void setFilterbedinungID(int filterbedinungID) {
		this.filterbedinungID = filterbedinungID;
	}

	public FilterbedingungBean() {
	}

	public String getDisplayName() {
		return "(ohne Namen)";
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

	@Override
	public int getID() {
		return this.getFilterbedinungID();
	}
}
