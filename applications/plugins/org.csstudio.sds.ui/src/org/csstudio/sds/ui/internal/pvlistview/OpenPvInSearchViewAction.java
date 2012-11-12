package org.csstudio.sds.ui.internal.pvlistview;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenPvInSearchViewAction extends AbstractHandler {

	@Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
		String pvName = "";

        final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
        final ProcessVariable[] pvs = AdapterUtil.convert(selection, ProcessVariable.class);
        for (ProcessVariable pv : pvs) {
        	if (!(pv.getName().isEmpty())) {
				pvName = pv.getName();
				break;
			}
		}

		if (!pvName.isEmpty()) {
			try {
				if (!pvName.isEmpty()) {
					PvSearchView pvSearchView = (PvSearchView) PlatformUI
							.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().showView(PvSearchView.VIEW_ID);
					pvSearchView.searchFor(pvName);
				}
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
