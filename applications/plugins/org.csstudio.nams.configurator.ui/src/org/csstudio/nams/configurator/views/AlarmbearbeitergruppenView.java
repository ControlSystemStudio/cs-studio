package org.csstudio.nams.configurator.views;

import org.csstudio.nams.configurator.beans.AlarmbearbeiterGruppenBean;
import org.csstudio.nams.configurator.beans.IConfigurationBean;

public class AlarmbearbeitergruppenView extends AbstractNamsView {

	public static final String ID = "org.csstudio.nams.configurator.alarmbearbeitergruppen";

	@Override
	protected IConfigurationBean[] getTableContent() {
		return configurationBeanService.getAlarmBearbeiterGruppenBeans();
	}
	
	@Override
	protected Class<? extends IConfigurationBean> getBeanClass() {
		return AlarmbearbeiterGruppenBean.class;
	}
}
