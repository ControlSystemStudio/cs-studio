package org.csstudio.nams.configurator.beans.filters;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.JunctorConditionType;

public class JunctorConditionForFilterTreeBean extends
		FilterbedingungBean {

	private JunctorConditionType junctorConditionType;
	private Set<FilterbedingungBean> filterbedingungBeans = new TreeSet<FilterbedingungBean>();


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
		return this == obj;
	}
}
