package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

public class FilterbedingungBean extends
		AbstractObservableBean<FilterbedingungBean> {

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
}
