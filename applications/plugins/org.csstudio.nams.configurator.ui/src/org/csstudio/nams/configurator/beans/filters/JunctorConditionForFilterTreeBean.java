package org.csstudio.nams.configurator.beans.filters;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.JunctorConditionType;

public class JunctorConditionForFilterTreeBean extends
		FilterbedingungBean {

	private JunctorConditionType junctorConditionType;
	private Set<FilterbedingungBean> filterbedingungBeans = new TreeSet<FilterbedingungBean>();

	public JunctorConditionForFilterTreeBean() {
		setFilterSpecificBean(null);
	}

	public String getDisplayName() {
		return junctorConditionType.name();
	}
	
	public int compareTo(FilterbedingungBean o) {
		if (o instanceof JunctorConditionForFilterTreeBean) {
			return compareTo((JunctorConditionForFilterTreeBean) o);
		} 
		return -1;
	}

	public int compareTo(JunctorConditionForFilterTreeBean o) {
		int result = -1;
		if (o != null) {
			result = this.junctorConditionType.compareTo(o.junctorConditionType);
			if (result == 0){
				result = filterbedingungBeans.hashCode() - o.filterbedingungBeans.hashCode();
			}
		}
		return result;
	}

	public boolean addOperand(FilterbedingungBean bean) {
		Contract.require(this != bean, "this != bean");
		return filterbedingungBeans.add(bean);
	}

	public JunctorConditionType getJunctorConditionType() {
		return junctorConditionType;
	}

	public void setJunctorConditionType(JunctorConditionType junctorConditionType) {
		this.junctorConditionType = junctorConditionType;
	}

	public boolean hasOperands() {
		return !filterbedingungBeans.isEmpty();
	}

	public Set<FilterbedingungBean> getOperands() {
		return Collections.unmodifiableSet(filterbedingungBeans);
	}

	public void removeOperand(FilterbedingungBean bean) {
		filterbedingungBeans.remove(bean);
	}

	@Override
	protected void doUpdateState(FilterbedingungBean bean) {
		if (bean instanceof JunctorConditionForFilterTreeBean) {
			super.doUpdateState(bean);
			this.filterbedingungBeans.clear();
			JunctorConditionForFilterTreeBean junctorBean = (JunctorConditionForFilterTreeBean) bean;
			Set<FilterbedingungBean> operands = junctorBean.getOperands();
			for (FilterbedingungBean filterbedingungBean : operands) {
				if (filterbedingungBean instanceof JunctorConditionForFilterTreeBean) {
					this.filterbedingungBeans.add(filterbedingungBean.getClone());
				} else {
					this.filterbedingungBeans.add(filterbedingungBean);
				}
			}
			this.junctorConditionType = junctorBean.junctorConditionType;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((filterbedingungBeans == null) ? 0 : filterbedingungBeans
						.hashCode());
		result = prime
				* result
				+ ((junctorConditionType == null) ? 0 : junctorConditionType
						.hashCode());
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
		final JunctorConditionForFilterTreeBean other = (JunctorConditionForFilterTreeBean) obj;
		if (filterbedingungBeans == null) {
			if (other.filterbedingungBeans != null)
				return false;
		} else if (!filterbedingungBeans.equals(other.filterbedingungBeans))
			return false;
		if (junctorConditionType == null) {
			if (other.junctorConditionType != null)
				return false;
		} else if (!junctorConditionType.equals(other.junctorConditionType))
			return false;
		return true;
	}
	
}
