package org.csstudio.nams.configurator.controller;

import org.csstudio.nams.configurator.modelmapping.IConfigurationBean;

public interface IConfigurationChangeListener {
	public void update(Class<IConfigurationBean> cls);
	public void updateAll();
}
