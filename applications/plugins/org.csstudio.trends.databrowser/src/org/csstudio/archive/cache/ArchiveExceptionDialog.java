package org.csstudio.archive.cache;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** Thread-save dialog box for displaying archive related exceptions.
 *  @author Kay Kasemir
 */
public class ArchiveExceptionDialog
{
    /** Show dialog box with error info.
     *  @param shell Parent shell
     *  @param title Dialog title
     *  @param message_format <code>NLS.bind</code> format, one arg.
     *  @param ex The exception
     */
    public static void showArchiveException(final Shell shell,
                                            final String title,
                                            final String message_format,
                                            final Exception ex)
    {
        // Print exception info
        StringBuffer buf = new StringBuffer();
        buf.append(ex.getMessage());
        final Throwable cause = ex.getCause();
        if (cause != null)
            buf.append("\nCaused by:\n" + cause.getMessage()); //$NON-NLS-1$
        final String msg = buf.toString();
        buf = null;
        
        // Display error message dialog, in UI thread
        Display.getDefault().asyncExec(new Runnable()
        {
            public void run()
            {
                MessageDialog.openError(shell, title,
                            NLS.bind(message_format, msg));
            }
        });
        
    }
}
