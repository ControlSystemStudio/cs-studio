package org.csstudio.config.kryonamebrowser.ui.dialog;

import org.csstudio.config.kryonamebrowser.model.entry.KryoNameEntry;
import org.csstudio.config.kryonamebrowser.model.resolved.KryoNameResolved;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class EditNameDialog extends KryoNameDialog {

	private final KryoNameResolved resolved;

	public EditNameDialog(Shell parentShell, KryoNameResolved resolved) {
		super(parentShell);
		this.resolved = resolved;

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		super.createDialogArea(parent);

		bridge.load(resolved, false);
		nameLabel.setText(resolved.getName());
		return parent;
	}

	@Override
	protected void getButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Edit");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					KryoNameEntry update = new KryoNameEntry();
					update.setId(resolved.getId());
					update.setLabel(desc.getText());
					logic.updateLabel(update);
					close();
				} catch (Exception e1) {
					MessageDialog.openError(getShell(), "Error", e1
							.getMessage());
				}
			}
		});
	}

}
