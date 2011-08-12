
package org.csstudio.nams.configurator.beans.filters;

import java.util.LinkedList;
import java.util.List;
import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;

public class StringArrayFilterConditionBean extends
		AbstractConfigurationBean<StringArrayFilterConditionBean> implements
		FilterConditionAddOnBean {

	public static enum PropertyNames {
		compareValues, keyValue, operator;
	}

	private List<String> compareValues = new LinkedList<String>();
	private MessageKeyEnum keyValue = MessageKeyEnum.NAME;

	private StringRegelOperator operator = StringRegelOperator.OPERATOR_TEXT_EQUAL;

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
		final StringArrayFilterConditionBean other = (StringArrayFilterConditionBean) obj;
		if (this.compareValues == null) {
			if (other.compareValues != null) {
				return false;
			}
		} else if (!this.compareValues.equals(other.compareValues)) {
			return false;
		}
		if (this.keyValue == null) {
			if (other.keyValue != null) {
				return false;
			}
		} else if (!this.keyValue.equals(other.keyValue)) {
			return false;
		}
		if (this.operator == null) {
			if (other.operator != null) {
				return false;
			}
		} else if (!this.operator.equals(other.operator)) {
			return false;
		}
		return true;
	}

	public List<String> getCompareValues() {
		return new LinkedList<String>(this.compareValues);
	}

	@Override
    public String getDisplayName() {
		return this.compareValues.toString() + " " + this.keyValue + " " //$NON-NLS-1$ //$NON-NLS-2$
				+ this.operator;
	}

	@Override
    public int getID() {
		return 0;
	}

	public MessageKeyEnum getKeyValue() {
		return this.keyValue;
	}

	public StringRegelOperator getOperator() {
		return this.operator;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((this.compareValues == null) ? 0 : this.compareValues
						.hashCode());
		result = prime * result
				+ ((this.keyValue == null) ? 0 : this.keyValue.hashCode());
		result = prime * result
				+ ((this.operator == null) ? 0 : this.operator.hashCode());
		return result;
	}

	public void setCompareValues(final List<String> compareValues) {
		final List<String> oldValue = this.compareValues;
		this.compareValues = compareValues;
		this.pcs.firePropertyChange(PropertyNames.compareValues.name(),
				oldValue, this.compareValues);
	}

	@Override
    public void setID(final int id) {
	    // Nothing to do
	}

	public void setKeyValue(final MessageKeyEnum keyValue) {
		final MessageKeyEnum oldValue = this.keyValue;
		this.keyValue = keyValue;
		this.pcs.firePropertyChange(PropertyNames.keyValue.name(), oldValue,
				this.keyValue);
	}

	public void setOperator(final StringRegelOperator operator) {
		final StringRegelOperator oldValue = this.operator;
		this.operator = operator;
		this.pcs.firePropertyChange(PropertyNames.operator.name(), oldValue,
				this.operator);
	}

	@Override
	protected void doUpdateState(final StringArrayFilterConditionBean bean) {
		this.setCompareValues(bean.getCompareValues());
		this.setKeyValue(bean.getKeyValue());
		this.setOperator(bean.getOperator());
	}

	@Override
    public void setDisplayName(String name) {
		// nothing to do here		
	}
}
