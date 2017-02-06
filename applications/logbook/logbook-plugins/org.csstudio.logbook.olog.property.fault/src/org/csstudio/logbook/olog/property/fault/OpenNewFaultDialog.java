package org.csstudio.logbook.olog.property.fault;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.logbook.LogEntry;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenNewFaultDialog extends AbstractHandler{

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final ISelection selection = HandlerUtil.getActiveMenuSelection(event);

        IAdapterManager adapterManager = Platform.getAdapterManager();
        final List<Integer> logIds = new ArrayList<Integer>();

        if (selection instanceof IStructuredSelection) {
            IStructuredSelection strucSelection = (IStructuredSelection) selection;

            for (Object iterable_element : strucSelection.toList()) {
                logIds.add(Integer.valueOf(String.valueOf(((LogEntry) adapterManager.getAdapter(iterable_element, LogEntry.class)).getId())));
            }

        }
        FaultEditorDialog dialog = new FaultEditorDialog(HandlerUtil.getActiveShell(event), true, null, null, logIds);
        dialog.setBlockOnOpen(false);
        // Initialize the logbooks and tags

        Display.getDefault().asyncExec(() -> {
            if (dialog.open() == Window.OK) {
            }
        });
        return null;
    }

}
