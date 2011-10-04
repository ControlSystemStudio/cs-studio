package org.csstudio.utility.displaybrowser;

import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariablePopupAction;
import org.csstudio.utility.displaybrowser.ui.SearchView;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


/**
 *
 * TODO (hrickens) :
 *
 * @author hrickens
 * @since 14.09.2011
 */
public class PVpopupAction extends ProcessVariablePopupAction {

    /**
     * {@inheritDoc}
     */
    @Override
    public void handlePVs(final IProcessVariable[] pv_names) {
        if (pv_names.length < 1) {
            return;
        }
        final IWorkbench workbench = PlatformUI.getWorkbench();
        final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        final IWorkbenchPage page = window.getActivePage();
        SearchView view;
        try {
            view = (SearchView) page.showView(SearchView.ID);
            view.setFilter(pv_names[0].getName());
        } catch (final PartInitException e) {
            e.printStackTrace();
        }
    }

}
