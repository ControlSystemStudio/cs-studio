package org.csstudio.ui.util;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * Utility class to register context pop-up menus.
 * <p>
 * In Eclipse RCP, pop-up have to be independently defined and attached for each part.
 * This class provides utility method to register the pop-ups so that it's
 * easier and more consistent through-out CSS.
 *
 * @author carcassi
 */
public class PopupMenuUtil {

    /**
     * Use this to install a pop-up for a view where the contribution are all taken
     * from the extension mechanism.
     *
     * @param control component that will host the pop-up menu
     * @param viewSite the view site that hosts the view
     * @param selectionProvider the selection used to create the context menu
     */
    public static void installPopupForView(Control control, IWorkbenchPartSite viewSite,
            ISelectionProvider selectionProvider) {
        MenuManager menuMgr = new MenuManager();
        menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        Menu menu = menuMgr.createContextMenu(control);
        control.setMenu(menu);
        viewSite.registerContextMenu(menuMgr, selectionProvider);
        viewSite.setSelectionProvider(selectionProvider);
    }
}
