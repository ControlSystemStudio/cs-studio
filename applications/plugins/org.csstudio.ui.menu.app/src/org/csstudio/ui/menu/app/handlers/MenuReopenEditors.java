package org.csstudio.ui.menu.app.handlers;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ReopenEditorMenu;

/** Dynamic menu contribution for re-opening recent files
 *  @author Xihui Chen - Previous code in SNS CSS ApplicationActionBarAdvisor
 *  @author Kay Kasemir
 */
@SuppressWarnings("restriction")
public class MenuReopenEditors extends ContributionItem
{
    @Override
    public boolean isDynamic()
    {
        return true;
    }

    @SuppressWarnings("nls")
    @Override
    public void fill(final Menu menu, final int index)
    {
        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        final IContributionItem recent;

        // Using public API, a separator is added before the actual menu entries.
        // recent = ContributionItemFactory.REOPEN_EDITORS.create(window);
        // Accessing private API, one can pass 'false' to prevent that separator
        recent = new ReopenEditorMenu(window, "reopenEditors", false);

        recent.fill(menu, index);
    }
}
