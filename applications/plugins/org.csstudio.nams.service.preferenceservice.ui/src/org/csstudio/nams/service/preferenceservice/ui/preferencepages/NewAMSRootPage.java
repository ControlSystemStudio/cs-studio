
package org.csstudio.nams.service.preferenceservice.ui.preferencepages;

import org.csstudio.nams.service.preferenceservice.ui.Messages;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class NewAMSRootPage extends PreferencePage implements
		IWorkbenchPreferencePage {

	@Override
    public void init(final IWorkbench workbench) {
		this.noDefaultAndApplyButton();
	}

	@Override
	protected Control createContents(final Composite parent) {
		final Label label = new Label(parent, SWT.WRAP);
		label
				.setText(Messages.NewAMSRootPage_title);
		return label;
	}
}
