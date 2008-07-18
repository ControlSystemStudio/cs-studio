package org.csstudio.nams.configurator.beans.filters;

import org.csstudio.nams.common.material.regelwerk.Operator;
import org.csstudio.nams.common.material.regelwerk.SuggestedProcessVariableType;
import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;

public class PVFilterConditionBean extends
		AbstractConfigurationBean<PVFilterConditionBean> implements FilterConditionAddOnBean {

	private SuggestedProcessVariableType suggestedType;
	private String channelName;
	private Operator operator;
	private String compareValue;
	
	public enum PropertyNames{
		suggestedType, channelName, operator, compareValue;
	}

	public SuggestedProcessVariableType getSuggestedType() {
		return suggestedType;
	}

	public void setSuggestedType(SuggestedProcessVariableType suggestedType) {
		SuggestedProcessVariableType type = this.suggestedType;
		this.suggestedType = suggestedType;
		pcs.firePropertyChange(PropertyNames.suggestedType.name(), type, suggestedType);
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		String oldValue = this.channelName;
		this.channelName = channelName;
		pcs.firePropertyChange(PropertyNames.channelName.name(), oldValue, channelName);
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		Operator oldValue = this.operator;
		this.operator = operator;
		pcs.firePropertyChange(PropertyNames.operator.name(), oldValue, operator);
	}

	public String getCompareValue() {
		return compareValue;
	}

	public void setCompareValue(String compareValue) {
		String oldValue = this.compareValue;
		this.compareValue = compareValue;
		pcs.firePropertyChange(PropertyNames.compareValue.name(), oldValue, compareValue);
	}

	@Override
	protected void doUpdateState(PVFilterConditionBean bean) {
		setChannelName(bean.getChannelName());
		setCompareValue(bean.getCompareValue());
		setOperator(bean.getOperator());
		setSuggestedType(bean.getSuggestedType());
	}

	public String getDisplayName() {
		return channelName + " " + operator.toString() + " " + compareValue
				+ " " + suggestedType;
	}

	public int getID() {
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((channelName == null) ? 0 : channelName.hashCode());
		result = prime * result
				+ ((compareValue == null) ? 0 : compareValue.hashCode());
		result = prime * result
				+ ((operator == null) ? 0 : operator.hashCode());
		result = prime * result
				+ ((suggestedType == null) ? 0 : suggestedType.hashCode());
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
		final PVFilterConditionBean other = (PVFilterConditionBean) obj;
		if (channelName == null) {
			if (other.channelName != null)
				return false;
		} else if (!channelName.equals(other.channelName))
			return false;
		if (compareValue == null) {
			if (other.compareValue != null)
				return false;
		} else if (!compareValue.equals(other.compareValue))
			return false;
		if (operator == null) {
			if (other.operator != null)
				return false;
		} else if (!operator.equals(other.operator))
			return false;
		if (suggestedType == null) {
			if (other.suggestedType != null)
				return false;
		} else if (!suggestedType.equals(other.suggestedType))
			return false;
		return true;
	}

	public void setID(int id) {
	}

}
