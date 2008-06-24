package org.csstudio.nams.configurator.views;

import org.csstudio.nams.configurator.composite.FilteredListVarianteA;
import org.csstudio.nams.configurator.modelmapping.ModelFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class AlarmbearbeiterView extends ViewPart {

	private static ModelFactory modelFactory;
	public static final String ID = "org.csstudio.nams.configurator.alarmbearbeiter";

	public AlarmbearbeiterView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		new FilteredListVarianteA(parent, SWT.None){
			protected Object[] getTableInput() {
				return modelFactory.getAlarmBearbeiterBeans();
			}
		};

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public static void staticInject(ModelFactory modelFactory) {
		AlarmbearbeiterView.modelFactory = modelFactory;
	}

}
