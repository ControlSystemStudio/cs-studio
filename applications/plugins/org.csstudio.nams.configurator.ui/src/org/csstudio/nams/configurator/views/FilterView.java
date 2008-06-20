package org.csstudio.nams.configurator.views;

import org.csstudio.nams.configurator.ModelFactory;
import org.csstudio.nams.configurator.composite.FilteredListVarianteA;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class FilterView extends ViewPart {

	private static ModelFactory modelFactory;

	public FilterView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		new FilteredListVarianteA(parent, SWT.None) {
			@Override
			protected Object[] getTableInput() {
				return modelFactory.getFilterBeans();
			}
		};
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public static void staticInject(ModelFactory modelFactory) {
		FilterView.modelFactory = modelFactory;
		
	}

}
