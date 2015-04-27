package org.csstudio.utility.caSnooperUi.ui.ChangeView;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/** Helper for creating an action that opens a view.
 *  <p>
 *  Meant to be subclassed to provide the view-ID,
 *  and the subclassed Action is then placed in the
 *  'CSS' menu bar so users can open a CSS view
 *  from the menu bar.
 *
 *  <hr>
 *  <b>Deprecated!</b>
 *  Instead of adding an <code>actionSet</code> to the plugin.xml which then
 *  uses a class derived from <code>OpenViewAction</code>, use the new
 *  menu extension point and the existing "org.eclipse.ui.views.showView"
 *  command.
 *  Markup looks like this:
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
 *  @deprecated Use new menu/command mechanism
 */
@Deprecated
public class OpenViewAction extends Action
   implements IWorkbenchWindowActionDelegate
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

    public void init(IWorkbenchWindow window)
    {
        // NOP
    }

    public void selectionChanged(IAction action, ISelection selection)
    {
        // NOP
    }

    public void run(final IAction action)
    {
        run();
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
        }
    }

    public void dispose()
    {
        // NOP
    }
}