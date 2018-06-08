package org.csstudio.phoebus.integration.actions;

import org.csstudio.phoebus.integration.PhoebusLauncherService;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
//import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Command handler for sending an email with the current selection.
 *
 * @author carcassi
 * @author Kay Kasemir
 */
public class OpenPhoebusEmail extends AbstractHandler implements IHandler {
    /** {@inheritDoc} */
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        final Shell shell = HandlerUtil.getActiveShell(event);
        try {
            // Retrieve the selection and the current page
            // final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
            PhoebusLauncherService.launchApplication(Messages.Email);
        } catch (Exception ex) {
            ExceptionDetailsErrorDialog.openError(shell, Messages.ProbeErrorOpenProbe, ex);
        }
        return null;
    }

}
