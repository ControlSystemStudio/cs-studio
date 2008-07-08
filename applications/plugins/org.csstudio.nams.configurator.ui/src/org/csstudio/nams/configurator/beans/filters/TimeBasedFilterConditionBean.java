package org.csstudio.nams.configurator.beans.filters;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.TimeBasedType;

public class TimeBasedFilterConditionBean extends
		AbstractConfigurationBean<TimeBasedFilterConditionBean> implements
		FilterConditionAddOnBean {

	private MessageKeyEnum startKeyValue;
	private StringRegelOperator sStartOperator;
	private String startCompValue;

	private MessageKeyEnum confirmKeyValue;
	private StringRegelOperator confirmOperator;
	private String confirmCompValue;

	private Millisekunden timePeriodDomainValue;
	private TimeBasedType timeBehavior;
	
	public enum PropertyNames {
		startKeyValue, startOperator, startCompValue, 
		confirmKeyValue, confirmOperator, confirmCompValue, 
		timePeriod, timeBehavior;
	}

	@Override
	protected void doUpdateState(TimeBasedFilterConditionBean bean) {
		setStartKeyValue(bean.getStartKeyValue());
		setStartOperator(bean.getStartOperator());
		setStartCompValue(bean.getStartCompValue());
		
		setConfirmKeyValue(bean.getConfirmKeyValue());
		setConfirmOperator(bean.getConfirmOperator());
		setConfirmCompValue(bean.getConfirmCompValue());
		
		setTimePeriod(bean.getTimePeriod());
		setTimeBehavior(bean.getTimeBehavior());
	}

	public String getDisplayName() {
		return startCompValue + " " + sStartOperator + " " + startCompValue
				+ " " + confirmKeyValue + " " + confirmOperator + " "
				+ confirmCompValue + " " + timePeriodDomainValue + " " + timeBehavior;
	}

	public int getID() {
		return 0;
	}

	public MessageKeyEnum getStartKeyValue() {
		return startKeyValue;
	}

	public void setStartKeyValue(MessageKeyEnum startKeyValue) {
		MessageKeyEnum oldValue = this.startKeyValue;
		this.startKeyValue = startKeyValue;
		pcs.firePropertyChange(PropertyNames.startKeyValue.name(), oldValue, startKeyValue);
	}

	public StringRegelOperator getStartOperator() {
		return sStartOperator;
	}

	public void setStartOperator(StringRegelOperator stringRegelOperator) {
		StringRegelOperator oldValue = stringRegelOperator;
		sStartOperator = stringRegelOperator;
		pcs.firePropertyChange(PropertyNames.startOperator.name(), oldValue, stringRegelOperator);
	}

	public String getStartCompValue() {
		return startCompValue;
	}

	public void setStartCompValue(String startCompValue) {
		String oldValue = this.startCompValue;
		this.startCompValue = startCompValue;
		pcs.firePropertyChange(PropertyNames.startCompValue.name(), oldValue, startCompValue);
	}

	public MessageKeyEnum getConfirmKeyValue() {
		return confirmKeyValue;
	}

	public void setConfirmKeyValue(MessageKeyEnum messageKeyEnum) {
		MessageKeyEnum oldValue = confirmKeyValue;
		confirmKeyValue = messageKeyEnum;
		pcs.firePropertyChange(PropertyNames.confirmKeyValue.name(), oldValue, messageKeyEnum);
	}

	public StringRegelOperator getConfirmOperator() {
		return confirmOperator;
	}

	public void setConfirmOperator(StringRegelOperator stringRegelOperator) {
		StringRegelOperator oldValue = this.confirmOperator;
		this.confirmOperator = stringRegelOperator;
		pcs.firePropertyChange(PropertyNames.confirmOperator.name(), oldValue, stringRegelOperator);
		}

	public String getConfirmCompValue() {
		return confirmCompValue;
	}

	public void setConfirmCompValue(String confirmCompValue) {
		String oldValue = this.confirmCompValue;
		this.confirmCompValue = confirmCompValue;
		pcs.firePropertyChange(PropertyNames.confirmCompValue.name(), oldValue, confirmCompValue);
	}

	public Millisekunden getTimePeriod() {
		return timePeriodDomainValue;
	}

	public void setTimePeriod(Millisekunden millisekunden) {
		Millisekunden oldValue = timePeriodDomainValue;
		if (oldValue == null) oldValue = Millisekunden.valueOf(0);
		timePeriodDomainValue = millisekunden;
		pcs.firePropertyChange(PropertyNames.timePeriod.name(), oldValue.alsLongVonMillisekunden(), millisekunden.alsLongVonMillisekunden());
	}
	
	public TimeBasedType getTimeBehavior() {
		return timeBehavior;
	}

	public void setTimeBehavior(TimeBasedType timeBasedType) {
		TimeBasedType oldValue = timeBehavior;
		timeBehavior = timeBasedType;
		pcs.firePropertyChange(PropertyNames.timeBehavior.name(), oldValue, timeBasedType);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((confirmCompValue == null) ? 0 : confirmCompValue
						.hashCode());
		result = prime
				* result
				+ ((confirmKeyValue == null) ? 0 : confirmKeyValue.hashCode());
		result = prime * result
				+ ((startCompValue == null) ? 0 : startCompValue.hashCode());
		result = prime * result
				+ ((startKeyValue == null) ? 0 : startKeyValue.hashCode());
		result = prime
				* result
				+ ((confirmOperator == null) ? 0 : confirmOperator.hashCode());
		result = prime * result
				+ ((sStartOperator == null) ? 0 : sStartOperator.hashCode());
		result = prime * result
				+ ((timeBehavior == null) ? 0 : timeBehavior.hashCode());
		result = prime * result
				+ ((timePeriodDomainValue == null) ? 0 : timePeriodDomainValue.hashCode());
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
		final TimeBasedFilterConditionBean other = (TimeBasedFilterConditionBean) obj;
		if (confirmCompValue == null) {
			if (other.confirmCompValue != null)
				return false;
		} else if (!confirmCompValue.equals(other.confirmCompValue))
			return false;
		if (confirmKeyValue == null) {
			if (other.confirmKeyValue != null)
				return false;
		} else if (!confirmKeyValue.equals(other.confirmKeyValue))
			return false;
		if (startCompValue == null) {
			if (other.startCompValue != null)
				return false;
		} else if (!startCompValue.equals(other.startCompValue))
			return false;
		if (startKeyValue == null) {
			if (other.startKeyValue != null)
				return false;
		} else if (!startKeyValue.equals(other.startKeyValue))
			return false;
		if (confirmOperator == null) {
			if (other.confirmOperator != null)
				return false;
		} else if (!confirmOperator.equals(other.confirmOperator))
			return false;
		if (sStartOperator == null) {
			if (other.sStartOperator != null)
				return false;
		} else if (!sStartOperator.equals(other.sStartOperator))
			return false;
		if (timeBehavior == null) {
			if (other.timeBehavior != null)
				return false;
		} else if (!timeBehavior.equals(other.timeBehavior))
			return false;
		if (timePeriodDomainValue == null) {
			if (other.timePeriodDomainValue != null)
				return false;
		} else if (!timePeriodDomainValue.equals(other.timePeriodDomainValue))
			return false;
		return true;
	}

	public void setID(int id) {
	}

}
