
package org.csstudio.nams.configurator.beans.filters;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.TimeBasedType;

public class TimeBasedFilterConditionBean extends
		AbstractConfigurationBean<TimeBasedFilterConditionBean> implements
		FilterConditionAddOnBean {

	public enum PropertyNames {
		startKeyValue, startOperator, startCompValue, confirmKeyValue, confirmOperator, confirmCompValue, timePeriod, timeBehavior;
	}

	private MessageKeyEnum startKeyValue = MessageKeyEnum.NAME;
	private StringRegelOperator sStartOperator = StringRegelOperator.OPERATOR_TEXT_EQUAL;

	private String startCompValue = ""; //$NON-NLS-1$
	private MessageKeyEnum confirmKeyValue = MessageKeyEnum.NAME;
	private StringRegelOperator confirmOperator = StringRegelOperator.OPERATOR_TEXT_EQUAL;

	private String confirmCompValue = ""; //$NON-NLS-1$
	private Millisekunden timePeriodDomainValue = Millisekunden.valueOf(0);

	private TimeBasedType timeBehavior = TimeBasedType.TIMEBEHAVIOR_TIMEOUT_THEN_ALARM;

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
		final TimeBasedFilterConditionBean other = (TimeBasedFilterConditionBean) obj;
		if (this.confirmCompValue == null) {
			if (other.confirmCompValue != null) {
				return false;
			}
		} else if (!this.confirmCompValue.equals(other.confirmCompValue)) {
			return false;
		}
		if (this.confirmKeyValue == null) {
			if (other.confirmKeyValue != null) {
				return false;
			}
		} else if (!this.confirmKeyValue.equals(other.confirmKeyValue)) {
			return false;
		}
		if (this.startCompValue == null) {
			if (other.startCompValue != null) {
				return false;
			}
		} else if (!this.startCompValue.equals(other.startCompValue)) {
			return false;
		}
		if (this.startKeyValue == null) {
			if (other.startKeyValue != null) {
				return false;
			}
		} else if (!this.startKeyValue.equals(other.startKeyValue)) {
			return false;
		}
		if (this.confirmOperator == null) {
			if (other.confirmOperator != null) {
				return false;
			}
		} else if (!this.confirmOperator.equals(other.confirmOperator)) {
			return false;
		}
		if (this.sStartOperator == null) {
			if (other.sStartOperator != null) {
				return false;
			}
		} else if (!this.sStartOperator.equals(other.sStartOperator)) {
			return false;
		}
		if (this.timeBehavior == null) {
			if (other.timeBehavior != null) {
				return false;
			}
		} else if (!this.timeBehavior.equals(other.timeBehavior)) {
			return false;
		}
		if (this.timePeriodDomainValue == null) {
			if (other.timePeriodDomainValue != null) {
				return false;
			}
		} else if (!this.timePeriodDomainValue
				.equals(other.timePeriodDomainValue)) {
			return false;
		}
		return true;
	}

	public String getConfirmCompValue() {
		return this.confirmCompValue;
	}

	public MessageKeyEnum getConfirmKeyValue() {
		return this.confirmKeyValue;
	}

	public StringRegelOperator getConfirmOperator() {
		return this.confirmOperator;
	}

	@Override
    public String getDisplayName() {
		return this.startCompValue + " " + this.sStartOperator + " " //$NON-NLS-1$ //$NON-NLS-2$
				+ this.startCompValue + " " + this.confirmKeyValue + " " //$NON-NLS-1$ //$NON-NLS-2$
				+ this.confirmOperator + " " + this.confirmCompValue + " " //$NON-NLS-1$ //$NON-NLS-2$
				+ this.timePeriodDomainValue + " " + this.timeBehavior; //$NON-NLS-1$
	}

	@Override
    public int getID() {
		return 0;
	}

	public String getStartCompValue() {
		return this.startCompValue;
	}

	public MessageKeyEnum getStartKeyValue() {
		return this.startKeyValue;
	}

	public StringRegelOperator getStartOperator() {
		return this.sStartOperator;
	}

	public TimeBasedType getTimeBehavior() {
		return this.timeBehavior;
	}

	public Millisekunden getTimePeriod() {
		return this.timePeriodDomainValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((this.confirmCompValue == null) ? 0 : this.confirmCompValue
						.hashCode());
		result = prime
				* result
				+ ((this.confirmKeyValue == null) ? 0 : this.confirmKeyValue
						.hashCode());
		result = prime
				* result
				+ ((this.startCompValue == null) ? 0 : this.startCompValue
						.hashCode());
		result = prime
				* result
				+ ((this.startKeyValue == null) ? 0 : this.startKeyValue
						.hashCode());
		result = prime
				* result
				+ ((this.confirmOperator == null) ? 0 : this.confirmOperator
						.hashCode());
		result = prime
				* result
				+ ((this.sStartOperator == null) ? 0 : this.sStartOperator
						.hashCode());
		result = prime
				* result
				+ ((this.timeBehavior == null) ? 0 : this.timeBehavior
						.hashCode());
		result = prime
				* result
				+ ((this.timePeriodDomainValue == null) ? 0
						: this.timePeriodDomainValue.hashCode());
		return result;
	}

	public void setConfirmCompValue(final String confirmCompValue) {
		final String oldValue = this.confirmCompValue;
		this.confirmCompValue = (confirmCompValue != null) ? confirmCompValue
				: ""; //$NON-NLS-1$
		this.pcs.firePropertyChange(PropertyNames.confirmCompValue.name(),
				oldValue, confirmCompValue);
	}

	public void setConfirmKeyValue(final MessageKeyEnum messageKeyEnum) {
		final MessageKeyEnum oldValue = this.confirmKeyValue;
		this.confirmKeyValue = messageKeyEnum;
		this.pcs.firePropertyChange(PropertyNames.confirmKeyValue.name(),
				oldValue, messageKeyEnum);
	}

	public void setConfirmOperator(final StringRegelOperator stringRegelOperator) {
		final StringRegelOperator oldValue = this.confirmOperator;
		this.confirmOperator = stringRegelOperator;
		this.pcs.firePropertyChange(PropertyNames.confirmOperator.name(),
				oldValue, stringRegelOperator);
	}

	@Override
    public void setID(final int id) {
	    // Nothing to do
	}

	public void setStartCompValue(final String startCompValue) {
		final String oldValue = this.startCompValue;
		this.startCompValue = (startCompValue != null) ? startCompValue : ""; //$NON-NLS-1$
		this.pcs.firePropertyChange(PropertyNames.startCompValue.name(),
				oldValue, startCompValue);
	}

	public void setStartKeyValue(final MessageKeyEnum startKeyValue) {
		final MessageKeyEnum oldValue = this.startKeyValue;
		this.startKeyValue = startKeyValue;
		this.pcs.firePropertyChange(PropertyNames.startKeyValue.name(),
				oldValue, startKeyValue);
	}

	public void setStartOperator(final StringRegelOperator stringRegelOperator) {
		final StringRegelOperator oldValue = stringRegelOperator;
		this.sStartOperator = stringRegelOperator;
		this.pcs.firePropertyChange(PropertyNames.startOperator.name(),
				oldValue, stringRegelOperator);
	}

	public void setTimeBehavior(final TimeBasedType timeBasedType) {
		final TimeBasedType oldValue = this.timeBehavior;
		this.timeBehavior = timeBasedType;
		this.pcs.firePropertyChange(PropertyNames.timeBehavior.name(),
				oldValue, timeBasedType);
	}

	public void setTimePeriod(final Millisekunden millisekunden) {
		Millisekunden oldValue = this.timePeriodDomainValue;
		if (oldValue == null) {
			oldValue = Millisekunden.valueOf(0);
		}
		this.timePeriodDomainValue = millisekunden;
		this.pcs.firePropertyChange(PropertyNames.timePeriod.name(), oldValue
				.alsLongVonMillisekunden(), millisekunden
				.alsLongVonMillisekunden());
	}

	@Override
	protected void doUpdateState(final TimeBasedFilterConditionBean bean) {
		this.setStartKeyValue(bean.getStartKeyValue());
		this.setStartOperator(bean.getStartOperator());
		this.setStartCompValue(bean.getStartCompValue());

		this.setConfirmKeyValue(bean.getConfirmKeyValue());
		this.setConfirmOperator(bean.getConfirmOperator());
		this.setConfirmCompValue(bean.getConfirmCompValue());

		this.setTimePeriod(bean.getTimePeriod());
		this.setTimeBehavior(bean.getTimeBehavior());
	}

	@Override
    public void setDisplayName(String name) {
		// Nothing to do here		
	}
}
