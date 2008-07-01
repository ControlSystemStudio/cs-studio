package org.csstudio.nams.configurator.beans.filters;

import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;
import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.JunctorConditionType;

public class JunctorConditionBean extends AbstractConfigurationBean<JunctorConditionBean> implements AddOnBean{

	FilterbedingungBean firstCondition;
	FilterbedingungBean secondCondition;
	JunctorConditionType junctor;
	
	@Override
	public JunctorConditionBean getClone() {
		JunctorConditionBean bean = new JunctorConditionBean();
		bean.setFirstCondition(firstCondition);
		bean.setSecondCondition(secondCondition);
		bean.setJunctor(junctor);
		return bean;
	}

	@Override
	public void updateState(JunctorConditionBean bean) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Unimplemented method");
	}

	public String getDisplayName() {
		return firstCondition + " " + junctor + " " + secondCondition;
	}

	public int getID() {
		return 0;
	}

	public FilterbedingungBean getFirstCondition() {
		return firstCondition;
	}

	public void setFirstCondition(FilterbedingungBean firstCondition) {
		this.firstCondition = firstCondition;
	}

	public FilterbedingungBean getSecondCondition() {
		return secondCondition;
	}

	public void setSecondCondition(FilterbedingungBean secondCondition) {
		this.secondCondition = secondCondition;
	}

	public JunctorConditionType getJunctor() {
		return junctor;
	}

	public void setJunctor(JunctorConditionType junctor) {
		this.junctor = junctor;
	}


}
