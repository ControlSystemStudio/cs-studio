package org.csstudio.nams.configurator.views;

import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.composite.FilteredListVarianteA;
import org.csstudio.nams.configurator.service.AbstractConfigurationBeanServiceListener;
import org.csstudio.nams.configurator.service.ConfigurationBeanService;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
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

			protected IConfigurationBean[] getTableInput() {
				return configurationBeanService.getAlarmBearbeiterGruppenBeans();
			}
		};
		MenuManager menuManager = new MenuManager();
		TableViewer table = filteredListVarianteA.getTable();
		Menu menu = menuManager.createContextMenu(table.getTable());
		table.getTable().setMenu(menu);
		getSite().registerContextMenu(menuManager, table);
		getSite().setSelectionProvider(table);
	}

	@Override
	public void setFocus() {
		
	}

	public static void staticInject(ConfigurationBeanService beanService) {
		configurationBeanService = beanService;
	}

}
