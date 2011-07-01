
package org.csstudio.nams.configurator.beans.filters;

import org.csstudio.nams.common.material.regelwerk.Operator;
import org.csstudio.nams.common.material.regelwerk.SuggestedProcessVariableType;
import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;

public class PVFilterConditionBean extends
		AbstractConfigurationBean<PVFilterConditionBean> implements
		FilterConditionAddOnBean {

	public enum PropertyNames {
		suggestedType, channelName, operator, compareValue;
	}

	private SuggestedProcessVariableType suggestedType = SuggestedProcessVariableType.STRING;
	private String channelName = ""; //$NON-NLS-1$
	private Operator operator = Operator.EQUALS;

	private String compareValue = ""; //$NON-NLS-1$

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
		final PVFilterConditionBean other = (PVFilterConditionBean) obj;
		if (this.channelName == null) {
			if (other.channelName != null) {
				return false;
			}
		} else if (!this.channelName.equals(other.channelName)) {
			return false;
		}
		if (this.compareValue == null) {
			if (other.compareValue != null) {
				return false;
			}
		} else if (!this.compareValue.equals(other.compareValue)) {
			return false;
		}
		if (this.operator == null) {
			if (other.operator != null) {
				return false;
			}
		} else if (!this.operator.equals(other.operator)) {
			return false;
		}
		if (this.suggestedType == null) {
			if (other.suggestedType != null) {
				return false;
			}
		} else if (!this.suggestedType.equals(other.suggestedType)) {
			return false;
		}
		return true;
	}

	public String getChannelName() {
		return this.channelName;
	}

	public String getCompareValue() {
		return this.compareValue;
	}

	@Override
    public String getDisplayName() {
		return this.channelName + " " + this.operator.toString() + " " //$NON-NLS-1$ //$NON-NLS-2$
				+ this.compareValue + " " + this.suggestedType; //$NON-NLS-1$
	}

	@Override
    public int getID() {
		return 0;
	}

	public Operator getOperator() {
		return this.operator;
	}

	public SuggestedProcessVariableType getSuggestedType() {
		return this.suggestedType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((this.channelName == null) ? 0 : this.channelName.hashCode());
		result = prime
				* result
				+ ((this.compareValue == null) ? 0 : this.compareValue
						.hashCode());
		result = prime * result
				+ ((this.operator == null) ? 0 : this.operator.hashCode());
		result = prime
				* result
				+ ((this.suggestedType == null) ? 0 : this.suggestedType
						.hashCode());
		return result;
	}

	public void setChannelName(final String channelName) {
		final String oldValue = this.channelName;
		this.channelName = (channelName != null) ? channelName : ""; //$NON-NLS-1$
		this.pcs.firePropertyChange(PropertyNames.channelName.name(), oldValue,
				channelName);
	}

	public void setCompareValue(final String compareValue) {
		final String oldValue = this.compareValue;
		this.compareValue = (compareValue != null) ? compareValue : ""; //$NON-NLS-1$
		this.pcs.firePropertyChange(PropertyNames.compareValue.name(),
				oldValue, compareValue);
	}

	@Override
    public void setID(final int id) {
	}

	public void setOperator(final Operator operator) {
		final Operator oldValue = this.operator;
		this.operator = operator;
		this.pcs.firePropertyChange(PropertyNames.operator.name(), oldValue,
				operator);
	}

	public void setSuggestedType(
			final SuggestedProcessVariableType suggestedType) {
		final SuggestedProcessVariableType type = this.suggestedType;
		this.suggestedType = suggestedType;
		this.pcs.firePropertyChange(PropertyNames.suggestedType.name(), type,
				suggestedType);
	}

	@Override
	protected void doUpdateState(final PVFilterConditionBean bean) {
		this.setChannelName(bean.getChannelName());
		this.setCompareValue(bean.getCompareValue());
		this.setOperator(bean.getOperator());
		this.setSuggestedType(bean.getSuggestedType());
	}

	@Override
    public void setDisplayName(String name) {
		// nothing to do here
	}
}
