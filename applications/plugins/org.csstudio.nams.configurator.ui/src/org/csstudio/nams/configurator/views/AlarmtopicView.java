package org.csstudio.nams.configurator.views;

import org.csstudio.nams.configurator.beans.IConfigurationBean;

public class AlarmtopicView extends AbstractNamsView {

	public static final String ID = "org.csstudio.nams.configurator.alarmtopic";

	@Override
	protected IConfigurationBean[] getTableContent() {
		return configurationBeanService.getAlarmTopicBeans();
	}
}
