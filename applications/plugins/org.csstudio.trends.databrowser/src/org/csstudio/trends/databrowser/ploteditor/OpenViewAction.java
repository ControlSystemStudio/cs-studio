package org.csstudio.trends.databrowser.ploteditor;

import org.csstudio.trends.databrowser.Plugin;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

/** An action that opens a view.
 *  @author Kay Kasemir
 */
public class OpenViewAction extends Action
{
    private IWorkbenchPage page;
    private String ID;
    
    /** Construct the action for opening a view.
     *  @param part The parent part from which we get the 'page'.
     *  @param name Name to use for the action.
     *  @param ID The ID of the View to open.
     */
    public OpenViewAction(IWorkbenchPart part, String name, String ID)
    {
        super(name);
        setImageDescriptor(Plugin.getImageDescriptor("icons/chart.gif")); //$NON-NLS-1$
        page = part.getSite().getPage();
        this.ID = ID;
    }
    
    @Override
    public void run()
    {
        try
        {
            // TODO General question: How would one create a 'detached' view?
            page.showView(ID);
        }
        catch (Exception e)
        {
            Plugin.logException("Cannot open " + ID, e); //$NON-NLS-1$
        }
    }
}
