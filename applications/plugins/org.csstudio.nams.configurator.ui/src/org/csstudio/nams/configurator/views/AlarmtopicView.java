package org.csstudio.nams.configurator.views;

import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.composite.FilteredListVarianteA;
import org.csstudio.nams.configurator.service.ConfigurationBeanService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class AlarmtopicView extends ViewPart {

	private static ConfigurationBeanService configurationBeanService;
	public static final String ID = "org.csstudio.nams.configurator.alarmtopic";

	public AlarmtopicView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		new FilteredListVarianteA(parent, SWT.None) {
			protected IConfigurationBean[] getTableInput() {
				return configurationBeanService.getAlarmTopicBeans();
			}
		};
	}

	public static void staticInject(ConfigurationBeanService beanService) {
		configurationBeanService = beanService;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}
}
