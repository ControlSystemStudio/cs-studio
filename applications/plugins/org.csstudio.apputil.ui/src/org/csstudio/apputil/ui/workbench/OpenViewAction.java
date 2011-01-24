package org.csstudio.apputil.ui.workbench;

import org.csstudio.apputil.ui.dialog.ErrorDetailDialog;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/** Helper for creating an action that opens a view.
 *  <p>
 *  This is for actions created at runtime.
 *  <p>
 *  For declarative actions, use the  menu extension point with
 *  the "org.eclipse.ui.views.showView":
 *
 *  <pre>
 *  &lt;extension
 *       point="org.eclipse.ui.menus"&gt;
 *    &lt;menuContribution
 *          allPopups="false"
 *          locationURI="menu:alarm"&gt;
 *       &lt;command
 *             commandId="org.eclipse.ui.views.showView"
 *             icon="icons/my_icon.gif"
 *             label="My View"
 *             mnemonic="M"
 *             tooltip="Open My View"&gt;
 *             style="push"
 *          &lt;parameter
 *                name="org.eclipse.ui.views.showView.viewId"
 *                value="org.csstudio.mystuff.myview"&gt;
 *          &lt;/parameter&gt;
 *       &lt;/command&gt;
 *    &lt;/menuContribution&gt;
 * &lt;/extension&gt;
 * </pre>
 *
 *  @author Kay Kasemir
 */
public class OpenViewAction extends Action
{
    /** ID of the view to open */
    final private String id;

    /** @param id ID of the view to open */
    public OpenViewAction(final String id)
    {
        this.id = id;
    }

    /** @param id ID of the view to open
     *  @param label Action label
     */
    public OpenViewAction(String id, String label)
    {
        super(label);
        this.id = id;
    }

    /** @param id ID of the view to open
     *  @param label Action label
     *  @param icon Icon
     */
    public OpenViewAction(String id, String label,
            ImageDescriptor icon)
    {
        super(label, icon);
        this.id = id;
    }

    @SuppressWarnings("nls")
    @Override
    public void run()
    {
        try
        {
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            IWorkbenchPage page = window.getActivePage();
            page.showView(id);
        }
        catch (Exception ex)
        {
            new ErrorDetailDialog(null, "Error", "Error opening view '" + id + "'",
                    ex.getClass().getName() + ", " + ex.getMessage()).open();
        }
    }
}