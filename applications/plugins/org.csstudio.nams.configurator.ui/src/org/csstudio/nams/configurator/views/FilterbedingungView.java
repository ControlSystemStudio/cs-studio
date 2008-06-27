package org.csstudio.nams.configurator.views;

import org.csstudio.nams.configurator.composite.FilteredListVarianteA;
import org.csstudio.nams.configurator.controller.AbstractConfigurationChangeListener;
import org.csstudio.nams.configurator.controller.ConfigurationBeanController;
import org.csstudio.nams.configurator.controller.IConfigurationChangeListener;
import org.csstudio.nams.configurator.modelmapping.ModelFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class FilterbedingungView extends ViewPart {

	private static ModelFactory modelFactory;
	public static final String ID = "org.csstudio.nams.configurator.filterbedingung";
	private static ConfigurationBeanController controller;
	public FilterbedingungView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		new FilteredListVarianteA(parent, SWT.None, controller) {
			@Override
			protected Object[] getTableInput() {
				return modelFactory.getFilterConditionBeans();
			}

			@Override
			protected void registerControllerListener() {
				controller.addConfigurationChangedListener(new AbstractConfigurationChangeListener(){
					protected void updateFilterBedingung() {
						updateView();
					}
						});

				}
		};
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public static void staticInject(ModelFactory modelFactory, ConfigurationBeanController controller) {
		FilterbedingungView.modelFactory = modelFactory;
		FilterbedingungView.controller = controller;
	}

}
