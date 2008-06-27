package org.csstudio.nams.configurator.views;

import org.csstudio.nams.configurator.composite.FilteredListVarianteA;
import org.csstudio.nams.configurator.controller.AbstractConfigurationChangeListener;
import org.csstudio.nams.configurator.controller.ConfigurationBeanController;
import org.csstudio.nams.configurator.modelmapping.ModelFactory;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;

public class AlarmbearbeiterView extends ViewPart {

	private static ModelFactory modelFactory;
	public static final String ID = "org.csstudio.nams.configurator.alarmbearbeiter";
	private static ConfigurationBeanController controller;

	public AlarmbearbeiterView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		FilteredListVarianteA composite = new FilteredListVarianteA(parent, SWT.None, controller){
			protected Object[] getTableInput() {
				return modelFactory.getAlarmBearbeiterBeans();
			}

			@Override
			protected void registerControllerListener() {
				controller.addConfigurationChangedListener(new AbstractConfigurationChangeListener(){
					@Override
					protected void updateAlarmBearbeiter() {
						updateView();
					}
				});
				
			}
		};
		MenuManager menuManager = new MenuManager();
		TableViewer table = composite.getTable();
		Menu menu = menuManager.createContextMenu(table.getTable());
		table.getTable().setMenu(menu);
		getSite().registerContextMenu(menuManager, table);
		getSite().setSelectionProvider(table);

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	public static void staticInject(ModelFactory modelFactory, ConfigurationBeanController controller) {
		AlarmbearbeiterView.modelFactory = modelFactory;
		AlarmbearbeiterView.controller = controller;
	}

}
