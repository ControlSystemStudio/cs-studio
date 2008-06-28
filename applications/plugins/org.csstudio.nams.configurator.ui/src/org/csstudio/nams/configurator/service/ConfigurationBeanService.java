package org.csstudio.nams.configurator.service;

import org.csstudio.nams.configurator.beans.AlarmbearbeiterBean;
import org.csstudio.nams.configurator.beans.AlarmbearbeiterGruppenBean;
import org.csstudio.nams.configurator.beans.AlarmtopicBean;
import org.csstudio.nams.configurator.beans.FilterBean;
import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.configurator.beans.IConfigurationBean;

public interface ConfigurationBeanService {

	public abstract AlarmbearbeiterBean[] getAlarmBearbeiterBeans();

	public abstract AlarmbearbeiterGruppenBean[] getAlarmBearbeiterGruppenBeans();

	public abstract AlarmtopicBean[] getAlarmTopicBeans();

	public abstract FilterBean[] getFilterBeans();

	public abstract FilterbedingungBean[] getFilterConditionBeans();

	public <T extends IConfigurationBean> T save(T bean);

	public void addConfigurationBeanServiceListener(ConfigurationBeanServiceListener listener);
	public void removeConfigurationBeanServiceListener(ConfigurationBeanServiceListener listener);
}
