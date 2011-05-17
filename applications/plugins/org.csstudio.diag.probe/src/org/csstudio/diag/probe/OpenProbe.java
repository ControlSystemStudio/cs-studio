package org.csstudio.diag.probe;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenProbe extends AbstractHandler implements IHandler
{
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
        final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
        final ISelection selection = window.getActivePage().getSelection();
        final ProcessVariable[] pvs = AdapterUtil.convert(selection,
                ProcessVariable.class);
        if (pvs != null  &&  pvs.length > 0)
            Probe.activateWithPV(pvs[0]);
        return null;
    }
}
