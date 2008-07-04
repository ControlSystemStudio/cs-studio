package org.csstudio.nams.configurator.beans.filters;

import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;

public class StringFilterConditionBean extends AbstractConfigurationBean<StringFilterConditionBean> implements FilterConditionAddOnBean{

	private String keyValue;
	private StringRegelOperator operator;
	private String compValue;
	
	public static enum PropertyNames {
		keyValue, operator, compValue;
	}
	
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
		String oldValue = this.keyValue;
		this.keyValue = keyValue;
		pcs.firePropertyChange(PropertyNames.keyValue.name(), oldValue, this.keyValue);
	}

	public String getCompValue() {
		return compValue;
	}

	public void setCompValue(String compValue) {
		String oldValue = this.compValue;
		this.compValue = compValue;
		pcs.firePropertyChange(PropertyNames.compValue.name(), oldValue, this.compValue);
	}

	public StringRegelOperator getOperator() {
		return operator;
	}

	public void setOperator(StringRegelOperator operator) {
		StringRegelOperator oldValue = this.operator;
		this.operator = operator;
		pcs.firePropertyChange(PropertyNames.operator.name(), oldValue, this.operator);
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((compValue == null) ? 0 : compValue.hashCode());
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
		final StringFilterConditionBean other = (StringFilterConditionBean) obj;
		if (compValue == null) {
			if (other.compValue != null)
				return false;
		} else if (!compValue.equals(other.compValue))
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

	public void setID(int id) {
	}

}
