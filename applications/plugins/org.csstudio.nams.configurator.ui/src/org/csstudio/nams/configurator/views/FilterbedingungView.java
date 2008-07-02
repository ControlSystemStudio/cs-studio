package org.csstudio.nams.configurator.views;

import org.csstudio.nams.configurator.beans.IConfigurationBean;

public class FilterbedingungView extends AbstractNamsView {

	public static final String ID = "org.csstudio.nams.configurator.filterbedingung";

	@Override
	protected IConfigurationBean[] getTableContent() {
		return configurationBeanService.getFilterConditionBeans();
	}
}
