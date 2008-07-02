package org.csstudio.nams.configurator.views;

import org.csstudio.nams.configurator.beans.IConfigurationBean;

public class AlarmbearbeiterView extends AbstractNamsView {

	public static final String ID = "org.csstudio.nams.configurator.alarmbearbeiter";

	@Override
	protected IConfigurationBean[] getTableContent() {
		return configurationBeanService.getAlarmBearbeiterBeans();
	}

}
