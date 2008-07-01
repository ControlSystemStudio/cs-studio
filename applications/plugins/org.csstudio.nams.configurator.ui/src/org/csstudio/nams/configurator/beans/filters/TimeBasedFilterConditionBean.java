package org.csstudio.nams.configurator.beans.filters;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.TimeBasedType;

public class TimeBasedFilterConditionBean extends AbstractConfigurationBean<TimeBasedFilterConditionBean> implements
		AddOnBean {

	private String cStartKeyValue;
	private StringRegelOperator sStartOperator;
	private String cStartCompValue;
	
	private MessageKeyEnum cConfirmKeyValue;
	private StringRegelOperator sConfirmOperator;
	private String cConfirmCompValue;
	
	private Millisekunden sTimePeriod;
	private TimeBasedType sTimeBehavior;
	
	@Override
	public TimeBasedFilterConditionBean getClone() {
		TimeBasedFilterConditionBean bean = new TimeBasedFilterConditionBean();
		
		bean.setCStartKeyValue(cStartKeyValue);
		bean.setSStartOperator(sStartOperator);
		bean.setCStartCompValue(cStartCompValue);
		
		bean.setCConfirmKeyValue(cConfirmKeyValue);
		bean.setSConfirmOperator(sConfirmOperator);
		bean.setCConfirmCompValue(cConfirmCompValue);
		
		bean.setSTimePeriod(sTimePeriod);
		bean.setSTimeBehavior(sTimeBehavior);
		return bean;
	}

	@Override
	public void updateState(TimeBasedFilterConditionBean bean) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Unimplemented method.");	
	}

	public String getDisplayName() {
		return cStartCompValue + " " + sStartOperator + " " + cStartCompValue + " " 
		+ cConfirmKeyValue + " " + sConfirmOperator + " " + cConfirmCompValue+ " "
		+ sTimePeriod + " " + sTimeBehavior;
	}

	public int getID() {
		return 0;
	}

	public String getCStartKeyValue() {
		return cStartKeyValue;
	}

	public void setCStartKeyValue(String startKeyValue) {
		cStartKeyValue = startKeyValue;
	}

	public StringRegelOperator getSStartOperator() {
		return sStartOperator;
	}

	public void setSStartOperator(StringRegelOperator stringRegelOperator) {
		sStartOperator = stringRegelOperator;
	}

	public String getCStartCompValue() {
		return cStartCompValue;
	}

	public void setCStartCompValue(String startCompValue) {
		cStartCompValue = startCompValue;
	}

	public MessageKeyEnum getCConfirmKeyValue() {
		return cConfirmKeyValue;
	}

	public void setCConfirmKeyValue(MessageKeyEnum messageKeyEnum) {
		cConfirmKeyValue = messageKeyEnum;
	}

	public StringRegelOperator getSConfirmOperator() {
		return sConfirmOperator;
	}

	public void setSConfirmOperator(StringRegelOperator stringRegelOperator) {
		sConfirmOperator = stringRegelOperator;
	}

	public String getCConfirmCompValue() {
		return cConfirmCompValue;
	}

	public void setCConfirmCompValue(String confirmCompValue) {
		cConfirmCompValue = confirmCompValue;
	}

	public Millisekunden getSTimePeriod() {
		return sTimePeriod;
	}

	public void setSTimePeriod(Millisekunden millisekunden) {
		sTimePeriod = millisekunden;
	}

	public TimeBasedType getSTimeBehavior() {
		return sTimeBehavior;
	}

	public void setSTimeBehavior(TimeBasedType timeBasedType) {
		sTimeBehavior = timeBasedType;
	}


}
