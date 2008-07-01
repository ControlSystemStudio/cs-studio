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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((compareValues == null) ? 0 : compareValues.hashCode());
		result = prime * result
				+ ((keyValue == null) ? 0 : keyValue.hashCode());
		result = prime * result
				+ ((operator == null) ? 0 : operator.hashCode());
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
		final StringArrayFilterConditionBean other = (StringArrayFilterConditionBean) obj;
		if (compareValues == null) {
			if (other.compareValues != null)
				return false;
		} else if (!compareValues.equals(other.compareValues))
			return false;
		if (keyValue == null) {
			if (other.keyValue != null)
				return false;
		} else if (!keyValue.equals(other.keyValue))
			return false;
		if (operator == null) {
			if (other.operator != null)
				return false;
		} else if (!operator.equals(other.operator))
			return false;
		return true;
	}


}
