/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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
