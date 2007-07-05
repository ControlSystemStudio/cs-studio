package org.csstudio.platform.ui.dialogs;


import org.csstudio.platform.security.Credentials;
import org.csstudio.platform.security.ILoginCallbackHandler;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A simple login dialog.
 * 
 * @author Alexander Will, Jõrg Rathlev, Anže Vodovnik
 */
public class LoginDialog extends TitleAreaDialog implements ILoginCallbackHandler  {

	/**
	 * Text box that holds the user name.
	 */
	private Text _username;

	/**
	 * Text box that holds the password.
	 */
	private Text _password;
	
	/**
	 * Checkbox for option to remember username and password.
	 */
	private Button _rememberLogin;

	/**
	 * Stores the credentials object after OK has been pressed.
	 */
	private Credentials _credentials;
	
	/**
	 * Creates a new login dialog.
	 * @param parentShell the parent shell.
	 */
	public LoginDialog(final Shell parentShell) {
		super(parentShell);
	}
	
	/**
	 * Creates the contents of this dialog.
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		Composite parentComposite = (Composite) super.createDialogArea(parent);

		getShell().setText("Login - Control System Studio");
		setTitle("Login");
		setMessage("Please enter your user name and password.");

		// Create the layout
		Composite contents = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		contents.setLayout(layout);
		contents.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true));
		contents.setFont(parent.getFont());

		// user name
		Label label = new Label(contents, SWT.NONE);
		label.setText("User name:");
		_username = new Text(contents, SWT.BORDER | SWT.FLAT);
		_username.setFocus();
		_username.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		// password
		label = new Label(contents, SWT.NONE);
		label.setText("Password:");
		_password = new Text(contents, SWT.BORDER | SWT.FLAT | SWT.PASSWORD);
		_password.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		
		// remember password checkbox (invisible by default)
		_rememberLogin = new Button(contents, SWT.CHECK);
		_rememberLogin.setText("Remember my user name and password");
		_rememberLogin.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		_rememberLogin.setVisible(false);
		
		return parentComposite;
	}
	
	/**
	 * Sets the window title of this login dialog window.
	 * @param title the title.
	 */
	public final void setWindowTitle(final String title) {
		getShell().setText(title);
	}
	
	/**
	 * Sets whether the remember password checkbox is visible (default is
	 * <code>false</code>.
	 * @param visible <code>true</code> to display the checkbox.
	 */
	public final void setRememberPasswordVisible(final boolean visible) {
		_rememberLogin.setVisible(visible);
	}
	
	/**
	 * Returns whether the user checked the remember password option.
	 * @return <code>true</code> if the user wants the password to be
	 *         remembered, <code>false</code> otherwise.
	 */
	public final boolean isRememberPasswordChecked() {
		return _rememberLogin.getSelection();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void okPressed() {
		_credentials = new Credentials(this._username.getText(), 
										this._password.getText());
		// reset the username & password field
		_username = null;
		_password = null;
		super.okPressed();
	}
	
	/**
	 * Opens the login window and queries the user
	 * for credentials which it returns.
	 */
	public Credentials getCredentials() {
		_credentials = null;
		this.setBlockOnOpen(true);
		this.open();
		return _credentials;
	}

	public void signalFailedLoginAttempt() {
		MessageDialog.openError(null, "Login", "Login failed. Please try again.");
	}
}
