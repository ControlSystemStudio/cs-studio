package org.csstudio.email.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
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
    public void testEMailDialog()
    {
        final Shell shell = new Shell();
        final String image_filename =
            MessageDialog.openQuestion(shell, "With image?", "Include image?")
            ? "icons/email_image.png"
            : null;
        Dialog dlg = new EMailDialog(shell, host, from, to, "Test",
                "This is a test", image_filename);
        dlg.open();
    }
}
