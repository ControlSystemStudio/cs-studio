package org.csstudio.nams.configurator.views;

import org.csstudio.nams.configurator.composite.FilteredListVarianteA;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class AlarmtopicView extends ViewPart {

	public AlarmtopicView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		//new FilteredListVarianteB(parent, SWT.None);
		new FilteredListVarianteA(parent, SWT.None)  {
			@Override
			protected Object[] getTableInput() {
				return new String[] { "Topic A", "History 08", "Another Topic" };
			}
		};

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
