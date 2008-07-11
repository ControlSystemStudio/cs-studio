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

	public int compareTo(JunctorConditionForFilterTreeBean o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void addOperand(FilterbedingungBean bean) {
		filterbedingungBeans.add(bean);
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

}
