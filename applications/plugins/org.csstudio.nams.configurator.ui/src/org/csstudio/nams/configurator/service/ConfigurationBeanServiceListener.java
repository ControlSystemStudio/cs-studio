package org.csstudio.nams.configurator.service;

import org.csstudio.nams.configurator.beans.IConfigurationBean;

public interface ConfigurationBeanServiceListener {

	void onBeanUpdate(IConfigurationBean bean);

	void onBeanInsert(IConfigurationBean bean);
	
	void onBeanDeleted(IConfigurationBean bean);

	void onConfigurationReload();

}
