package org.csstudio.logbook.olog.property.fault;

import java.util.Arrays;
import java.util.List;

import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenFaultDetail extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
        // Check if the view is already open, if open set the new fault else
        // update the old view with the new fault
        List<Fault> faults = Arrays.asList(AdapterUtil.convert(selection, Fault.class));
        try {
            DetailsView detailView = (DetailsView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .showView(DetailsView.ID);

            if (faults.size() == 1) {
                detailView.setFault(faults.get(0));

            }
        } catch (PartInitException e) {
            e.printStackTrace();
        }
        return null;
    }
}
