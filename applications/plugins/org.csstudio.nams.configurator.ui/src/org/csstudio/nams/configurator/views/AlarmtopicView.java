package org.csstudio.nams.configurator.views;

import org.csstudio.nams.configurator.composite.FilteredListVarianteA;
import org.csstudio.nams.configurator.modelmapping.ModelFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class AlarmtopicView extends ViewPart {

	private static ModelFactory modelFactory;
	public static final String ID = "org.csstudio.nams.configurator.alarmtopic";

	public AlarmtopicView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		new FilteredListVarianteA(parent, SWT.None) {
			protected Object[] getTableInput() {
				return modelFactory.getAlarmTopicBeans();
			}
		};
	}

	public static void staticInject(ModelFactory modelFactory) {
		AlarmtopicView.modelFactory = modelFactory;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}
}
