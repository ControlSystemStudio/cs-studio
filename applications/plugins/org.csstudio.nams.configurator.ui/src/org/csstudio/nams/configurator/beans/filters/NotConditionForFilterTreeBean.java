package org.csstudio.nams.configurator.beans.filters;

import org.csstudio.nams.configurator.beans.FilterbedingungBean;

public class NotConditionForFilterTreeBean extends FilterbedingungBean {

	private int id;
	private FilterbedingungBean bean;

	public String getDisplayName() {
		String result = "NOT ";
		if (bean != null) {
			if (bean instanceof JunctorConditionForFilterTreeBean) {
				result = "N";
			}
			result = result + bean.getDisplayName();
		}
		return result;
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public int compareTo(NotConditionForFilterTreeBean o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void doUpdateState(FilterbedingungBean bean) {
		// TODO Auto-generated method stub
		
	}

	public void setFilterbedingungBean(FilterbedingungBean bean) {
		this.bean = bean;
	}

	public FilterbedingungBean getFilterbedingungBean() {
		return bean;
	}
}
