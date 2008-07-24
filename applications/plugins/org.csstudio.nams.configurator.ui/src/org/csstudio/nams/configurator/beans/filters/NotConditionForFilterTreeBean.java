package org.csstudio.nams.configurator.beans.filters;

import org.csstudio.nams.configurator.beans.FilterbedingungBean;

public class NotConditionForFilterTreeBean extends FilterbedingungBean {

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

	@Override
	public int compareTo(FilterbedingungBean o) {
		if (o instanceof NotConditionForFilterTreeBean) {
			return compareTo((NotConditionForFilterTreeBean) o); 
		}
		return -1;
	}
	
	public int compareTo(NotConditionForFilterTreeBean o) {
		if (this == o){
			return 0;
		}
		return this.bean.compareTo(o.bean);
	}

	@Override
	protected void doUpdateState(FilterbedingungBean bean) {
		if (bean instanceof NotConditionForFilterTreeBean) {
			super.doUpdateState(bean);
			NotConditionForFilterTreeBean ncffBean = (NotConditionForFilterTreeBean) bean;
			this.bean = ncffBean.getFilterbedingungBean();
		}
	}

	public void setFilterbedingungBean(FilterbedingungBean bean) {
		this.bean = bean;
	}

	public FilterbedingungBean getFilterbedingungBean() {
		return bean;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((bean == null) ? 0 : bean.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NotConditionForFilterTreeBean) {
			return this.bean.equals(((NotConditionForFilterTreeBean)obj).bean);
		}
		return false;
	}
}
