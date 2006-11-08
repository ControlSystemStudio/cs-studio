package org.csstudio.platform.ui.dialogs;

import org.csstudio.platform.ui.internal.localization.Messages;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A simple login dialog.
 * 
 * @author awill
 * 
 */
public class LoginDialog extends TitleAreaDialog {
	/**
	 * Standard constructor.
	 * 
	 * @param parentShell
	 *            The parent shell.
	 */
	public LoginDialog(final Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Text box that holds the user name.
	 */
	private Text _username;

	/**
	 * Text box that holds the password.
	 */
	private Text _password;

	/**
	 * {@inheritDoc}
	 */
	protected final Control createDialogArea(final Composite parent) {
		Control control = super.createDialogArea(parent);

		getShell().setText(Messages.getString("LoginDialog.WINDOW_TITLE")); //$NON-NLS-1$
		setTitle(Messages.getString("LoginDialog.DIALOG_TITLE")); //$NON-NLS-1$
		setMessage(Messages.getString("LoginDialog.DIALOG_DESCRIPTION")); //$NON-NLS-1$

		Composite composite = new Composite(parent, SWT.NONE);

		// Create the layout
		GridLayout layout = new GridLayout(2, false);

		// assign the layout to the panel
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parent.getFont());

		// user name
		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.getString("LoginDialog.USER_NAME")); //$NON-NLS-1$
		_username = new Text(composite, SWT.BORDER | SWT.FLAT);
		_username.setFocus();
		_username.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.FILL_HORIZONTAL));

		// password
		label = new Label(composite, SWT.NONE);
		label.setText(Messages.getString("LoginDialog.PASSWORD")); //$NON-NLS-1$
		_password = new Text(composite, SWT.BORDER | SWT.FLAT | SWT.PASSWORD);
		_password.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.FILL_HORIZONTAL));
		_password.setEchoChar('*');

		return control;
	}
}
