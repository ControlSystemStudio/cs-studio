package org.csstudio.diag.epics.pvtree;

import java.util.logging.Level;
import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/** Command handler for opening PV Tree onthe current selection.
 *  Linked from popup menu that is sensitive to {@link ProcessVariable}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class OpenPVTree extends AbstractHandler implements IHandler
{
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
        // Retrieve the selection and the current page
        final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
        final IWorkbenchPage page = window.getActivePage();
        final ISelection selection = page.getSelection();
        final ProcessVariable[] pvs = AdapterUtil.convert(selection, ProcessVariable.class);

        if (pvs != null  &&  pvs.length > 0)
        {
            final PVTreeView view;
            try
            {
                view = (PVTreeView) page.showView(PVTreeView.ID);
            }
            catch (Exception ex)
            {
                Plugin.getLogger().log(Level.SEVERE, "Cannot open PVTreeView" , ex);
                return null;
            }
            view.setPVName(pvs[0].getName());
        }
        return null;
    }
}
