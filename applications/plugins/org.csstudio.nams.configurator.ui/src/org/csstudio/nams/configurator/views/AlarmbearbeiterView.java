package org.csstudio.nams.configurator.views;

import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.composite.FilterableBeanList;
import org.csstudio.nams.configurator.service.AbstractConfigurationBeanServiceListener;
import org.csstudio.nams.configurator.service.ConfigurationBeanService;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;

public class AlarmbearbeiterView extends ViewPart {

	private static ConfigurationBeanService configurationBeanService;
	public static final String ID = "org.csstudio.nams.configurator.alarmbearbeiter";
	private FilterableBeanList filteredListVarianteA;
	
	public AlarmbearbeiterView() {
		configurationBeanService.addConfigurationBeanServiceListener(new AbstractConfigurationBeanServiceListener() {
			//TODO updateView() is in most cases overkill
			@Override
			public void onBeanInsert(IConfigurationBean bean) {
				if (filteredListVarianteA != null) {
					filteredListVarianteA.updateView();
				}
			}
			@Override
			public void onBeanUpdate(IConfigurationBean bean) {
				if (filteredListVarianteA != null) {
					filteredListVarianteA.updateView();
				}
			}
			@Override
			public void onBeanDeleted(IConfigurationBean bean) {
				if (filteredListVarianteA != null) {
					filteredListVarianteA.updateView();
				}
			}
		});
	}

	@Override
	public void createPartControl(Composite parent) {
		filteredListVarianteA = new FilterableBeanList(parent, SWT.None){
			protected IConfigurationBean[] getTableInput() {
				return configurationBeanService.getAlarmBearbeiterBeans();
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
		// TODO Auto-generated method stub
	}

	public static void staticInject(ConfigurationBeanService beanService) {
		configurationBeanService = beanService;
	}

}
