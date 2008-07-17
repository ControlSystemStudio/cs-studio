package org.csstudio.nams.service.preferenceservice.ui.preferencepages;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class NewAMSRootPage extends PreferencePage implements
		IWorkbenchPreferencePage {

	public NewAMSRootPage() {
		// TODO Auto-generated constructor stub
	}


	@Override
	protected Control createContents(Composite parent) {
		Label label = new Label(parent, SWT.WRAP);
		label.setText("On the sub-pages of this node you may configure the settings of the new alarm-management-system.");
		return label;
	}

	public void init(IWorkbench workbench) {
		noDefaultAndApplyButton();
	}

}
