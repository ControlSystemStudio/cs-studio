package org.csstudio.logbook.ui.util;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.logbook.LogEntry;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

public class ExportLogs extends AbstractHandler {

    public static final String ID = "org.csstudio.logbook.ui.exportlogs";

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
        IAdapterManager adapterManager = Platform.getAdapterManager();
        final List<LogEntry> data = new ArrayList<LogEntry>();

        if (selection instanceof IStructuredSelection) {
            IStructuredSelection strucSelection = (IStructuredSelection) selection;

            for (Object iterable_element : strucSelection.toList()) {
                data.add((LogEntry) adapterManager.getAdapter(iterable_element, LogEntry.class));
            }

        }

        if (data == null || data.isEmpty()) {
            ErrorDialog.openError(HandlerUtil.getActiveWorkbenchWindow(event).getShell(),
                    "Error",
                    "No log Entries selected to be export",
                    new Status(IStatus.ERROR, ID, null));
        } else {
            try {
                Runnable openSearchDialog = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Display.getDefault().asyncExec(new Runnable() {
                                public void run() {
                                     ExportLogsDialog dialog = new
                                     ExportLogsDialog(HandlerUtil.getActiveWorkbenchWindow(event).getShell(), data);
                                     dialog.setBlockOnOpen(true);
                                     if (dialog.open() == IDialogConstants.OK_ID) {
                                     }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                BusyIndicator.showWhile(Display.getDefault(), openSearchDialog);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return event;
    }
}
