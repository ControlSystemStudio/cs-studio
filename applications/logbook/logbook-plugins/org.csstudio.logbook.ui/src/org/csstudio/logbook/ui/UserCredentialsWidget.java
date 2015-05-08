/**
 *
 */
package org.csstudio.logbook.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

/**
 *
 * A Simple Widget which requests username and password. The widget is
 * horizontally organized.
 *
 * @author shroffk TODO replace with secure storage
 *
 */
public class UserCredentialsWidget extends Composite {
    private Text username;
    private Text password;
    private Label lblUsername;
    private Label lblPassword;

    public UserCredentialsWidget(Composite parent, int style) {
    super(parent, style);
    setLayout(new GridLayout(4, false));

    lblUsername = new Label(this, SWT.NONE);
    lblUsername.setText("User Name:");

    username = new Text(this, SWT.BORDER);
    username.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
        1, 1));

    lblPassword = new Label(this, SWT.NONE);
    lblPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
        false, 1, 1));
    lblPassword.setText("Password:");

    password = new Text(this, SWT.BORDER | SWT.PASSWORD);
    password.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
        1, 1));
    }

    public void setVisisble(boolean visible) {
    username.setVisible(visible);
    password.setVisible(visible);
    lblUsername.setVisible(getVisible());
    lblPassword.setVisible(visible);
    }

    public String getUsername() {
    return username.getText();
    }

    public String getPassword() {
    return password.getText();
    }
}
