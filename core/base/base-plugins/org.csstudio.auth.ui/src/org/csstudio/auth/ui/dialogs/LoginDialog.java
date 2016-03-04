/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.auth.ui.dialogs;


import org.csstudio.auth.security.Credentials;
import org.csstudio.auth.security.ILoginCallbackHandler;
import org.csstudio.auth.ui.internal.localization.Messages;
import org.csstudio.platform.workspace.WorkspaceIndependentStore;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * <p>A simple login dialog.</p>
 *
 * TODO ILoginCallbackHandler cleanup instead of warnings
 *
 * <p>Warning: Do not use an instance of this class as an
 * <code>ILoginCallbackHandler</code>. This class's implementation of that
 * interface incorrectly assumes that it will be called in the UI thread
 * and may not work correctly if called in another thread. Future versions of
 * this class will no longer implement <code>ILoginCallbackHandler</code>.</p>
 *
 * @author Alexander Will, Jörg Rathlev, Anže Vodovnik, Xihui Chen, Kay Kasemir
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
     * checkbox to show "Login as anonymous"
     */
    private Button _loginAnonymous;

    /**
     * Checkbox for option to remember username and password.
     */
    private Button _rememberLogin;

    /**
     * Stores the credentials object after OK has been pressed.
     */
    private Credentials _credentials;

    /**
     * The name of the last User.
     */
    private String _lastUser;

    /**
     * The dialog title.
     */
    private final String _title;

    /**
     * The message displayed in the dialog.
     */
    private final String _message;

    /**
     * Creates a new login dialog.
     * @param parentShell the parent shell.
     */
    public LoginDialog(final Shell parentShell) {
        this(parentShell, "");
    }

    /**
     * Creates a new login dialog.
     *
     * @param parentShell
     *            the parent shell.
     * @param lastUser
     *            the initial user name.
     */
    public LoginDialog(final Shell parentShell, String lastUser) {
        this(parentShell, "Login", "Please enter your user name and password.",
                lastUser);
    }

    /**
     * Creates a new login dialog.
     *
     * @param parentShell
     *            the parent shell.
     * @param title
     *            the dialog title.
     * @param message
     *            the message that is displayed in the dialog.
     * @param lastUser
     *            the initial user name.
     */
    public LoginDialog(final Shell parentShell, final String title,
            final String message, final String lastUser) {
        super(parentShell);
        _title = title;
        _message = message;
        _lastUser = lastUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(_title);
    }

    /**
     * Creates the contents of this dialog.
     */
    @Override
    protected Control createDialogArea(final Composite parent) {
        Composite parentComposite = (Composite) super.createDialogArea(parent);

        setTitle(_title);
        setMessage(_message);

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
        label.setText(Messages.LoginDialog_UserName);
        _username = new Text(contents, SWT.BORDER | SWT.FLAT);

        _username.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        _username.setText(_lastUser != null ? _lastUser : "");


        // password
        label = new Label(contents, SWT.NONE);
        label.setText(Messages.LoginDialog_Password);
        _password = new Text(contents, SWT.BORDER | SWT.FLAT | SWT.PASSWORD);
        _password.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

        // Anonymous login checkbox
        _loginAnonymous = new Button(contents, SWT.CHECK);
        _loginAnonymous.setText(Messages.LoginDialog_LoginAnonymous);
        _loginAnonymous.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
        _loginAnonymous.addSelectionListener(new SelectionAdapter(){
            @Override
            public void widgetSelected(SelectionEvent e) {
                if(_loginAnonymous.getSelection()) {
                    // Disable name/password when anonymous
                    _username.setEnabled(false);
                    _password.setEnabled(false);
                } else {
                    // (Re-)enable name/password entry
                    _username.setEnabled(true);
                    _password.setEnabled(true);
                    // ... and jump to name field
                    _username.setFocus();
                }
            }
        });

        // remember password checkbox (invisible by default)
        _rememberLogin = new Button(contents, SWT.CHECK);
        _rememberLogin.setText("Remember my user name and password");
        _rememberLogin.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
        _rememberLogin.setVisible(false);

        if(_lastUser!=null&&_lastUser.trim().length()>0){
            _password.setFocus();
        }else{
            _username.setFocus();
        }
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
        if(_loginAnonymous.getSelection())
            _credentials = Credentials.ANONYMOUS;
        else {
            _credentials = new Credentials(this._username.getText(), this._password.getText());
            WorkspaceIndependentStore.writeLastLoginUser(this._username.getText());
        }
        // reset the username & password field
        _username = null;
        _password = null;
        super.okPressed();
    }

    /**
     * Returns the credentials that were entered by the user.
     *
     * @return the credentials entered by the user, or <code>null</code> if
     *         the user did not enter any credentials.
     */
    public Credentials getLoginCredentials() {
        return _credentials;
    }

    /**
     * Opens the login window and queries the user
     * for credentials which it returns.
     *
     * @deprecated Do not use this class as an <code>ILoginCallbackHandler</code>.
     */
    @Override
    public Credentials getCredentials() {
        _credentials = null;
        this.setBlockOnOpen(true);
        this.open();
        return _credentials;
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated Do not use this class as an <code>ILoginCallbackHandler</code>.
     */
    @Override
    public void signalFailedLoginAttempt() {
        MessageDialog.openError(null, "Login", "Login failed. Please try again.");
    }
}
