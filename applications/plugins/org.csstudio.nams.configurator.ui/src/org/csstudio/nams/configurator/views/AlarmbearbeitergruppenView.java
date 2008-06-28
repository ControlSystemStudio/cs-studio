package org.csstudio.nams.configurator.views;

import org.csstudio.nams.configurator.composite.FilteredListVarianteA;
import org.csstudio.nams.configurator.service.AbstractConfigurationBeanServiceListener;
import org.csstudio.nams.configurator.service.ConfigurationBeanService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class AlarmbearbeitergruppenView extends ViewPart {

	public static final String ID = "org.csstudio.nams.configurator.alarmbearbeitergruppen";
	private static ConfigurationBeanService configurationBeanService;
	private FilteredListVarianteA filteredListVarianteA;

	public AlarmbearbeitergruppenView() {
		configurationBeanService.addConfigurationBeanServiceListener(new AbstractConfigurationBeanServiceListener() {
			
		});
	}

	@Override
	public void createPartControl(Composite parent) {

		filteredListVarianteA = new FilteredListVarianteA(parent, SWT.None) {

			protected Object[] getTableInput() {
				return configurationBeanService.getAlarmBearbeiterGruppenBeans();
			}
		};

	}

	@Override
	public void setFocus() {
		
	}

	public static void staticInject(ConfigurationBeanService beanService) {
		configurationBeanService = beanService;
	}

}
