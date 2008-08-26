package org.csstudio.config.kryonamebrowser.ui.dialog;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class AddNewNameDialog extends KryoNameDialog {

	private final Shell shell;

	public AddNewNameDialog(Shell parentShell) {
		super(parentShell);
		shell = parentShell;

	}

	@Override
	protected void getButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Add");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!bridge.validate()) {
					setErrorMessage("Please fill in correctly all the required fields");
				} else {
					setErrorMessage(null);
					callUpdate = true;
					try {
						logic.add(bridge.calculateNewEntrty());
						MessageDialog.openInformation(shell, "Info",
								"Operation was successful");
					} catch (Exception e1) {
						MessageDialog.openError(getShell(), "Error", e1
								.getMessage());
					}
				}
			}
		});
	}

	@Override
	protected String getDescription() {
		return "Please fill in correctly all the required fields";

	}

	@Override
	protected String getTitle() {
		return "Add New Kryo Name";

	}

}
