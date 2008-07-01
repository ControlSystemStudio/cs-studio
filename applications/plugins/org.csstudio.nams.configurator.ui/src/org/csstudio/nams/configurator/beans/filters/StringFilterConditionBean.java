package org.csstudio.nams.configurator.beans.filters;

import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;

public class StringFilterConditionBean extends AbstractConfigurationBean<StringFilterConditionBean> implements AddOnBean{

	private String keyValue;
	private StringRegelOperator operator;
	private String compValue;
	
	@Override
	public StringFilterConditionBean getClone() {
		StringFilterConditionBean bean = new StringFilterConditionBean();
		bean.setCompValue(compValue);
		bean.setKeyValue(keyValue);
		bean.setOperator(operator);
		return bean;
	}

	public String getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}

	public String getCompValue() {
		return compValue;
	}

	public void setCompValue(String compValue) {
		this.compValue = compValue;
	}

	public StringRegelOperator getOperator() {
		return operator;
	}

	public void setOperator(StringRegelOperator operator) {
		this.operator = operator;
	}

	@Override
	public void updateState(StringFilterConditionBean bean) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Unimplemented method.");
	}

	public String getDisplayName() {
		return keyValue + " " + operator.toString() + " " + compValue;
	}

	public int getID() {
		return 0;
	}

}
