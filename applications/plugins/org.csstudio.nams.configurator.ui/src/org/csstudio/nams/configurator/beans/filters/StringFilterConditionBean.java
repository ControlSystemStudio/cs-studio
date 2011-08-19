
package org.csstudio.nams.configurator.beans.filters;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;

public class StringFilterConditionBean extends
		AbstractConfigurationBean<StringFilterConditionBean> implements
		FilterConditionAddOnBean {

	public static enum PropertyNames {
		keyValue, operator, compValue;
	}

	private MessageKeyEnum keyValue = MessageKeyEnum.NAME;
	private StringRegelOperator operator = StringRegelOperator.OPERATOR_TEXT_EQUAL;

	private String compValue = ""; //$NON-NLS-1$

	public StringFilterConditionBean() {
	    // Nothing to do
	}

	public StringFilterConditionBean(final MessageKeyEnum keyValue,
			final StringRegelOperator operator, final String compValue) {
		super();
		this.keyValue = keyValue;
		this.operator = operator;
		this.compValue = compValue;
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
		final StringFilterConditionBean other = (StringFilterConditionBean) obj;
		if (this.compValue == null) {
			if (other.compValue != null) {
				return false;
			}
		} else if (!this.compValue.equals(other.compValue)) {
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

	public String getCompValue() {
		return this.compValue;
	}

	@Override
    public String getDisplayName() {
		return this.keyValue + " " + this.operator.toString() + " " //$NON-NLS-1$ //$NON-NLS-2$
				+ this.compValue;
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
		result = prime * result
				+ ((this.compValue == null) ? 0 : this.compValue.hashCode());
		result = prime * result
				+ ((this.keyValue == null) ? 0 : this.keyValue.hashCode());
		result = prime * result
				+ ((this.operator == null) ? 0 : this.operator.hashCode());
		return result;
	}

	public void setCompValue(final String compValue) {
		final String oldValue = this.compValue;
		this.compValue = (compValue != null) ? compValue : ""; //$NON-NLS-1$
		this.pcs.firePropertyChange(PropertyNames.compValue.name(), oldValue,
				this.compValue);
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
	protected void doUpdateState(final StringFilterConditionBean bean) {
		this.setCompValue(bean.getCompValue());
		this.setKeyValue(bean.getKeyValue());
		this.setOperator(bean.getOperator());
	}

	@Override
    public void setDisplayName(String name) {
		// Nothing to do here		
	}
}
