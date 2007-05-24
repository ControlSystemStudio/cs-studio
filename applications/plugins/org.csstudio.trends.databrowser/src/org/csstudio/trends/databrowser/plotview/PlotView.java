package org.csstudio.trends.databrowser.plotview;

import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.plotpart.PlotPart;
import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/** An Eclipse 'view' for the data browser plot.
 *  <p>
 *  Displays the plot.
 *  
 *  TODO multiple views
 *  TODO remove marker menu
 *  TODO handle 'drop'
 *  TODO consolidate messages
 *  
 *  @author Kay Kasemir
 */
public class PlotView extends ViewPart
{
    public static final String ID = PlotView.class.getName();
    
    private final PlotPart plot = new PlotPart();

    public static boolean activateWithFile(IFile file)
    {
        try
        {
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            IWorkbenchPage page = window.getActivePage();
            PlotView view = (PlotView) page.showView(PlotView.ID);
            view.init(file);
            return true;
        }
        catch (Exception ex)
        {
            Plugin.logException("activateWithFile", ex); //$NON-NLS-1$
            ex.printStackTrace();
        }
        return false;
    }

    public void init(IFile file) throws PartInitException
    {
        plot.init(file);
    }
    
    @Override
    public void createPartControl(Composite parent)
    {
        plot.createPartControl(parent);
    }

    @Override
    public void setFocus()
    {
        plot.setFocus();
    }

    @Override
    public void dispose()
    {
        plot.dispose();
        super.dispose();
    }
}
