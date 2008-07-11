package org.csstudio.nams.configurator.beans.filters;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;
import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.JunctorConditionType;

public class JunctorConditionForFilterTreeBean extends
		AbstractConfigurationBean<JunctorConditionForFilterTreeBean> {

	private JunctorConditionType junctorConditionType;
	private int id;
	private Set<FilterbedingungBean> filterbedingungBeans = new TreeSet<FilterbedingungBean>();

	public String getDisplayName() {
		return junctorConditionType.name();
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public int compareTo(JunctorConditionForFilterTreeBean o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void doUpdateState(JunctorConditionForFilterTreeBean bean) {
		// TODO Auto-generated method stub
		
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

}
