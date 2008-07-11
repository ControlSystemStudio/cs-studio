package org.csstudio.nams.configurator.beans.filters;

import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;
import org.csstudio.nams.configurator.beans.FilterbedingungBean;

public class NotConditionForFilterTreeBean extends AbstractConfigurationBean<NotConditionForFilterTreeBean> {

	private int id;
	private FilterbedingungBean bean;

	public String getDisplayName() {
		return "NOT " + bean.getDisplayName();
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
	protected void doUpdateState(NotConditionForFilterTreeBean bean) {
		// TODO Auto-generated method stub
		
	}

	public void setFilterbedingungBean(FilterbedingungBean bean) {
		this.bean = bean;
	}

}
