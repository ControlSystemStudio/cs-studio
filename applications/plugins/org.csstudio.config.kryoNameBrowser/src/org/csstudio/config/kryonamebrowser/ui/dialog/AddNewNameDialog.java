package org.csstudio.config.kryonamebrowser.ui.dialog;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class AddNewNameDialog extends KryoNameDialog {

	public AddNewNameDialog(Shell parentShell) {
		super(parentShell);

	}

	@Override
	protected void getButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);

		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!bridge.validate()) {
					setErrorMessage("Please fill in correctly all the required fields");
				} else {
					setErrorMessage(null);
					try {
						logic.add(bridge.getNewEntrty());
						close();
					} catch (Exception e1) {
						MessageDialog.openError(getShell(), "Error", e1
								.getMessage());
					}
				}
			}
		});
	}

}
