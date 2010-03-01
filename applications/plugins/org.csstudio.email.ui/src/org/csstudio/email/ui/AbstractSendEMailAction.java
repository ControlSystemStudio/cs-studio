package org.csstudio.email.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;

/** Base class for Action that sends an email
 *  @author Kay Kasemir
 */
abstract public class AbstractSendEMailAction extends Action
{
    final private Shell shell;
    final private String from, subject, body, image_filename;
    
    public AbstractSendEMailAction(final Shell shell, final String from,
            final String subject,
            final String body, final String image_filename)
    {
        super("Send E-Mail...",
              Activator.getImageDescriptor("icons/email.gif"));
        this.shell = shell;
        this.from = from;
        this.subject = subject;
        this.body = body;
        this.image_filename = image_filename;
    }

    @Override
    public void run()
    {
        Dialog dlg = new EMailDialog(shell, Preferences.getSMTP_Host(), from,
                "<enter destination email>", subject, body, image_filename);
        dlg.open();
    }
}
