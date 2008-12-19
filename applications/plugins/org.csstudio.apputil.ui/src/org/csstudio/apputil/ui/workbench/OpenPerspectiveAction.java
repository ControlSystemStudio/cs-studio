package org.csstudio.apputil.ui.workbench;

import org.csstudio.apputil.ui.Activator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/** An action that opens a perspective.
 *  @author Kay Kasemir
 */
public class OpenPerspectiveAction extends Action
{
    final private String ID;
    
    /** Construct the action for opening a perspective.
     *  @param icon Icon to use for the action.
     *  @param name Name to use for the action.
     *  @param ID The ID of the Perspective to open.
     */
    public OpenPerspectiveAction(final ImageDescriptor icon,
                                 final String name, final String ID)
    {
        super(name);
        setImageDescriptor(icon);
        this.ID = ID;
    }
    
    @SuppressWarnings("nls")
    @Override
    public void run()
    {
        try
        {
            final IWorkbench wb = PlatformUI.getWorkbench();
            wb.showPerspective(ID, wb.getActiveWorkbenchWindow());
        }
        catch (Exception e)
        {
            Activator.getInstance().getLog().log(
                            new Status(IStatus.ERROR, Activator.ID,
                                       "Cannot open perspective " + ID));
        }
    }
}
