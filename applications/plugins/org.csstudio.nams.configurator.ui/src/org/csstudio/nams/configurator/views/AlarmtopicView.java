package org.csstudio.nams.configurator.views;

import org.csstudio.nams.configurator.ModelFactory;
import org.csstudio.nams.configurator.composite.FilteredListVarianteA;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class AlarmtopicView extends ViewPart {

	private static ModelFactory modelFactory;

	public AlarmtopicView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		new FilteredListVarianteA(parent, SWT.None)  {
			@Override
			protected Object[] getTableInput() {
				return modelFactory.getAlarmTopicBeans();
			}
		};

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public static void staticInject(ModelFactory modelFactory) {
		AlarmtopicView.modelFactory = modelFactory;
	}

}
