package org.csstudio.nams.configurator.views;

import org.csstudio.nams.configurator.ModelFactory;
import org.csstudio.nams.configurator.actions.OpenConfigurationEditor;
import org.csstudio.nams.configurator.composite.FilteredListVarianteA;
import org.csstudio.nams.configurator.treeviewer.model.AlarmbearbeiterGruppenBean;
import org.csstudio.nams.configurator.treeviewer.model.ConfigurationModel;
import org.csstudio.nams.configurator.treeviewer.model.IConfigurationModel;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class AlarmbearbeitergruppenView extends ViewPart {

	public static final String ID = "org.csstudio.nams.configurator.alarmbearbeitergruppen";
	private static ModelFactory modelFactory;
	
	public AlarmbearbeitergruppenView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {

		new FilteredListVarianteA(parent, SWT.None) {

			@Override
			protected Object[] getTableInput() {
				return modelFactory.getAlarmBearbeiterGruppenBeans();
			}
		};

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public static void staticInject(ModelFactory modelFactory) {
		AlarmbearbeitergruppenView.modelFactory = modelFactory;
	}

}
