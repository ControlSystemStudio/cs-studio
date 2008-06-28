package org.csstudio.nams.configurator.service;

import org.csstudio.nams.configurator.beans.AlarmbearbeiterBean;

public interface ConfigurationBeanServiceListener {

	void onAlarmbearbeiterBeanUpdate(AlarmbearbeiterBean bean);

	void onAlarmbearbeiterBeanInsert(AlarmbearbeiterBean bean);

}
