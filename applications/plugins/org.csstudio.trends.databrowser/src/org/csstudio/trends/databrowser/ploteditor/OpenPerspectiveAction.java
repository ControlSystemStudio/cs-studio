package org.csstudio.trends.databrowser.ploteditor;

import org.csstudio.trends.databrowser.Plugin;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/** An action that opens a perspective.
 *  @author Kay Kasemir
 */
public class OpenPerspectiveAction extends Action
{
    private String ID;
    
    /** Construct the action for opening a perspective.
     *  @param name Name to use for the action.
     *  @param ID The ID of the Perspective to open.
     */
    public OpenPerspectiveAction(String name, String ID)
    {
        super(name);
        setImageDescriptor(Plugin.getImageDescriptor("icons/chart.gif")); //$NON-NLS-1$
        this.ID = ID;
    }
    
    @Override
    public void run()
    {
        try
        {
            IWorkbench wb = PlatformUI.getWorkbench();
            wb.showPerspective(ID, wb.getActiveWorkbenchWindow());
        }
        catch (Exception e)
        {
            Plugin.logException("Cannot open " + ID, e); //$NON-NLS-1$
        }
    }
}
