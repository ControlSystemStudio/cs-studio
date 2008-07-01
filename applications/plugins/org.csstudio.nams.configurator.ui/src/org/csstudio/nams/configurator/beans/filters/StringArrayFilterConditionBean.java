package org.csstudio.nams.configurator.beans.filters;

import java.util.LinkedList;
import java.util.List;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;

public class StringArrayFilterConditionBean extends AbstractConfigurationBean<StringArrayFilterConditionBean>
		implements AddOnBean {

	private List<String> compareValues = new LinkedList<String>();
	private MessageKeyEnum keyValue;
	private StringRegelOperator operator;
	
	@Override
	public StringArrayFilterConditionBean getClone() {
		StringArrayFilterConditionBean bean = new StringArrayFilterConditionBean();
		bean.setCompareValues(compareValues);
		bean.setKeyValue(keyValue);
		bean.setOperator(operator);
		return bean;
	}

	@Override
	public void updateState(StringArrayFilterConditionBean bean) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Unimplemented method.");		
	}

	public String getDisplayName() {
		return compareValues.toString() + " " + keyValue + " " + operator;
	}

	public int getID() {
		return 0;
	}

	public List<String> getCompareValues() {
		return compareValues;
	}

	public void setCompareValues(List<String> compareValues) {
		this.compareValues = compareValues;
	}

	public MessageKeyEnum getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(MessageKeyEnum keyValue) {
		this.keyValue = keyValue;
	}

	public StringRegelOperator getOperator() {
		return operator;
	}

	public void setOperator(StringRegelOperator operator) {
		this.operator = operator;
	}


}
