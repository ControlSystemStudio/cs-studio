package org.csstudio.nams.configurator.beans.filters;

import org.csstudio.nams.configurator.beans.FilterbedingungBean;

public class NotConditionForFilterTreeBean extends FilterbedingungBean {

	private FilterbedingungBean bean;

	@Override
	public int compareTo(final FilterbedingungBean o) {
		if (o instanceof NotConditionForFilterTreeBean) {
			return this.compareTo((NotConditionForFilterTreeBean) o);
		}
		return -1;
	}

	public int compareTo(final NotConditionForFilterTreeBean o) {
		if (this == o) {
			return 0;
		}
		return this.bean.compareTo(o.bean);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof NotConditionForFilterTreeBean) {
			return this.bean.equals(((NotConditionForFilterTreeBean) obj).bean);
		}
		return false;
	}

	@Override
	public String getDisplayName() {
		String result = "NOT ";
		if (this.bean != null) {
			if (this.bean instanceof JunctorConditionForFilterTreeBean) {
				result = "N";
			}
			result = result + this.bean.getDisplayName();
		}
		return result;
	}

	public FilterbedingungBean getFilterbedingungBean() {
		return this.bean;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((this.bean == null) ? 0 : this.bean.hashCode());
		return result;
	}

	public void setFilterbedingungBean(final FilterbedingungBean bean) {
		this.bean = bean;
	}

	@Override
	protected void doUpdateState(final FilterbedingungBean bean) {
		if (bean instanceof NotConditionForFilterTreeBean) {
			super.doUpdateState(bean);
			final NotConditionForFilterTreeBean ncffBean = (NotConditionForFilterTreeBean) bean;
			this.bean = ncffBean.getFilterbedingungBean();
		}
	}
}
