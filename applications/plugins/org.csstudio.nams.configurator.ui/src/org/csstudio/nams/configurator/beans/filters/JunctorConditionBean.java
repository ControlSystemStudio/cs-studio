package org.csstudio.nams.configurator.beans.filters;

import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;
import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.JunctorConditionType;

public class JunctorConditionBean extends AbstractConfigurationBean<JunctorConditionBean> implements FilterConditionAddOnBean{

	FilterbedingungBean firstCondition;
	FilterbedingungBean secondCondition;
	JunctorConditionType junctor;
	
	public static enum PropertyNames {
		firstCondition, secondCondition, junctor;
	}
	
	@Override
	public void doUpdateState(JunctorConditionBean bean) {
		setFirstCondition(bean.getFirstCondition());
		setSecondCondition(bean.getSecondCondition());
		setJunctor(bean.getJunctor());
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
		FilterbedingungBean oldValue = this.firstCondition;
		this.firstCondition = firstCondition;
		pcs.firePropertyChange(PropertyNames.firstCondition.name(), oldValue, this.firstCondition);
		
	}

	public FilterbedingungBean getSecondCondition() {
		return secondCondition;
	}

	public void setSecondCondition(FilterbedingungBean secondCondition) {
		FilterbedingungBean oldValue = this.secondCondition;
		this.secondCondition = secondCondition;
		pcs.firePropertyChange(PropertyNames.secondCondition.name(), oldValue, this.secondCondition);
	}

	public JunctorConditionType getJunctor() {
		return junctor;
	}

	public void setJunctor(JunctorConditionType junctor) {
		JunctorConditionType oldValue = this.junctor;
		this.junctor = junctor;
		pcs.firePropertyChange(PropertyNames.junctor.name(), oldValue, this.junctor);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((firstCondition == null) ? 0 : firstCondition.hashCode());
		result = prime * result + ((junctor == null) ? 0 : junctor.hashCode());
		result = prime * result
				+ ((secondCondition == null) ? 0 : secondCondition.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final JunctorConditionBean other = (JunctorConditionBean) obj;
		if (firstCondition == null) {
			if (other.firstCondition != null)
				return false;
		} else if (!firstCondition.equals(other.firstCondition))
			return false;
		if (junctor == null) {
			if (other.junctor != null)
				return false;
		} else if (!junctor.equals(other.junctor))
			return false;
		if (secondCondition == null) {
			if (other.secondCondition != null)
				return false;
		} else if (!secondCondition.equals(other.secondCondition))
			return false;
		return true;
	}

	public void setID(int id) {
	}


}
