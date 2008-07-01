package org.csstudio.nams.configurator.beans.filters;

import org.csstudio.nams.common.material.regelwerk.Operator;
import org.csstudio.nams.common.material.regelwerk.SuggestedProcessVariableType;
import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;

public class PVFilterConditionBean extends
		AbstractConfigurationBean<PVFilterConditionBean> implements AddOnBean {

	private SuggestedProcessVariableType suggestedType;
	private String channelName;
	private Operator operator;
	private String compareValue;

	public SuggestedProcessVariableType getSuggestedType() {
		return suggestedType;
	}

	public void setSuggestedType(SuggestedProcessVariableType suggestedType) {
		this.suggestedType = suggestedType;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public String getCompareValue() {
		return compareValue;
	}

	public void setCompareValue(String compareValue) {
		this.compareValue = compareValue;
	}

	@Override
	public PVFilterConditionBean getClone() {
		PVFilterConditionBean bean = new PVFilterConditionBean();
		bean.setChannelName(channelName);
		bean.setCompareValue(compareValue);
		bean.setOperator(operator);
		bean.setSuggestedType(suggestedType);
		return bean;
	}

	@Override
	public void updateState(PVFilterConditionBean bean) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Unimplemented method");
	}

	public String getDisplayName() {
		return channelName + " " + operator.toString() + " " + compareValue
				+ " " + suggestedType;
	}

	public int getID() {
		return 0;
	}

}
