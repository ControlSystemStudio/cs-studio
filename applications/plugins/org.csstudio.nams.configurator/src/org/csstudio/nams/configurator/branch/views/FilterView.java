package org.csstudio.nams.configurator.branch.views;

import org.csstudio.nams.configurator.branch.composite.FilteredListVarianteA;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class FilterView extends ViewPart {

	public FilterView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		new FilteredListVarianteA(parent, SWT.None) {
			@Override
			protected Object[] getTableInput() {
				return new String[] { "129Filter", "Maschin07", "Kry123H2" };
			}
		};
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
