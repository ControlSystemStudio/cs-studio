package org.csstudio.email.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/** [Headless] JUnit Plug-in demo of the EMailDialog
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EMailDialogTest
{
    final private static String host = "smtp.ornl.gov";
    final private static String from = "kasemirk@ornl.gov";
    final private static String to = from;

    @Test
    public void testEMailDialogWithImage()
    {
        final Shell shell = new Shell();


        final Dialog dlg = new EMailSenderDialog(shell, host, from, to, "Test",
                                                 "This is a test", "icons/email_image.png");
        shell.getDisplay().asyncExec(new Runnable() {
            public void run() {
                dlg.open();
            }
        });
        shell.getDisplay().asyncExec(new Runnable() {
            public void run() {
                dlg.close();
            }
        });
    }
    @Test
    public void testEMailDialogWithOutImage()
    {
        final Shell shell = new Shell();


        final Dialog dlg = new EMailSenderDialog(shell, host, from, to, "Test",
                                                 "This is a test", null);
        shell.getDisplay().asyncExec(new Runnable() {
            public void run() {
                dlg.open();
            }
        });
        shell.getDisplay().asyncExec(new Runnable() {
            public void run() {
                dlg.close();
            }
        });
    }
}
