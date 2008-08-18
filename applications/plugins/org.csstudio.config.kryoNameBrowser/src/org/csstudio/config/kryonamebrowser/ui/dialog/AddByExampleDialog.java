package org.csstudio.config.kryonamebrowser.ui.dialog;

import org.csstudio.config.kryonamebrowser.model.resolved.KryoNameResolved;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class AddByExampleDialog extends KryoNameDialog {

	private final KryoNameResolved resolved;
	private final Shell shell;

	public AddByExampleDialog(Shell parentShell, KryoNameResolved resolved) {
		super(parentShell);
		shell = parentShell;
		this.resolved = resolved;

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		super.createDialogArea(parent);

		bridge.load(resolved, true);
		nameLabel.setText(resolved.getName());
		return parent;
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

}
