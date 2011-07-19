
package org.csstudio.nams.configurator.beans.filters;

import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;
import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.JunctorConditionType;

public class JunctorConditionBean extends
		AbstractConfigurationBean<JunctorConditionBean> implements
		FilterConditionAddOnBean {

	public static enum PropertyNames {
		firstCondition, secondCondition, junctor;
	}

	FilterbedingungBean firstCondition;
	FilterbedingungBean secondCondition;

	JunctorConditionType junctor;

	@Override
	public void doUpdateState(final JunctorConditionBean bean) {
		this.setFirstCondition(bean.getFirstCondition());
		this.setSecondCondition(bean.getSecondCondition());
		this.setJunctor(bean.getJunctor());
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final JunctorConditionBean other = (JunctorConditionBean) obj;
		if (this.firstCondition == null) {
			if (other.firstCondition != null) {
				return false;
			}
		} else if (!this.firstCondition.equals(other.firstCondition)) {
			return false;
		}
		if (this.junctor == null) {
			if (other.junctor != null) {
				return false;
			}
		} else if (!this.junctor.equals(other.junctor)) {
			return false;
		}
		if (this.secondCondition == null) {
			if (other.secondCondition != null) {
				return false;
			}
		} else if (!this.secondCondition.equals(other.secondCondition)) {
			return false;
		}
		return true;
	}

	@Override
    public String getDisplayName() {
		return this.firstCondition + " " + this.junctor + " " //$NON-NLS-1$ //$NON-NLS-2$
				+ this.secondCondition;
	}

	public FilterbedingungBean getFirstCondition() {
		return this.firstCondition;
	}

	@Override
    public int getID() {
		return 0;
	}

	public JunctorConditionType getJunctor() {
		return this.junctor;
	}

	public FilterbedingungBean getSecondCondition() {
		return this.secondCondition;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((this.firstCondition == null) ? 0 : this.firstCondition
						.hashCode());
		result = prime * result
				+ ((this.junctor == null) ? 0 : this.junctor.hashCode());
		result = prime
				* result
				+ ((this.secondCondition == null) ? 0 : this.secondCondition
						.hashCode());
		return result;
	}

	public void setFirstCondition(final FilterbedingungBean firstCondition) {
		final FilterbedingungBean oldValue = this.firstCondition;
		this.firstCondition = firstCondition;
		this.pcs.firePropertyChange(PropertyNames.firstCondition.name(),
				oldValue, this.firstCondition);

	}

	@Override
    public void setID(final int id) {
	    // Nothing to do
	}

	public void setJunctor(final JunctorConditionType junctor) {
		final JunctorConditionType oldValue = this.junctor;
		this.junctor = junctor;
		this.pcs.firePropertyChange(PropertyNames.junctor.name(), oldValue,
				this.junctor);
	}

	public void setSecondCondition(final FilterbedingungBean secondCondition) {
		final FilterbedingungBean oldValue = this.secondCondition;
		this.secondCondition = secondCondition;
		this.pcs.firePropertyChange(PropertyNames.secondCondition.name(),
				oldValue, this.secondCondition);
	}

	@Override
    public void setDisplayName(String name) {
		// nothing to do here
	}
}
