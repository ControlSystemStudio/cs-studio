package org.csstudio.nams.configurator.views;

import org.csstudio.nams.configurator.composite.FilteredListVarianteA;
import org.csstudio.nams.configurator.modelmapping.ModelFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class AlarmbearbeitergruppenView extends ViewPart {

	public static final String ID = "org.csstudio.nams.configurator.alarmbearbeitergruppen";
	private static ModelFactory modelFactory;
	private FilteredListVarianteA filteredListVarianteA;

	public AlarmbearbeitergruppenView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {

		filteredListVarianteA = new FilteredListVarianteA(parent, SWT.None) {

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
