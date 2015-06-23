package org.csstudio.security.ui;

import org.csstudio.security.SecuritySupport;
import org.csstudio.security.ui.internal.LoginDialog;
import org.junit.Test;

/** JUnit [Headless] Plug-in Demo of the {@link LoginDialog}
 *
 *  <p>Must run as Plug-In JUnit test, where preferences determine
 *  the JAAS config file, JAAS config name, authorization provider.
 *
 *  @author Kay Kasemir
 */
public class LoginDialogDemo
{
    @Test
    public void demoLogin()
    {
        final LoginDialog login = new LoginDialog(null);
        login.open();
        System.out.println(SecuritySupport.getSubject());
        System.out.println(SecuritySupport.getAuthorizations());
    }
}
