package org.csstudio.trends.databrowser.plotview;

import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.plotpart.PlotPart;
import org.csstudio.trends.databrowser.plotpart.RemoveMarkersAction;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/** An Eclipse 'view' for the data browser plot.
 *  <p>
 *  Displays the plot.
 *  
 *  TODO handle 'drop'
 *  TODO "Open in Editor"
 *  TODO persist IFile over restart
 *  
 *  @author Kay Kasemir
 */
public class PlotView extends ViewPart
{
    private static final String PLOTVIEW_FILE_PATH = "PLOTVIEW_FILE_PATH";

    /** View ID registered in plugin.xml as org.eclipse.views ID */
    public static final String ID = PlotView.class.getName();
    
    /** The underlying plot part. */
    private final PlotPart plot = new PlotPart();

    /** Instance counter used to create the "secondary ID"
     *  that's required to support multiple views of the same type.
     */
    private static long instance = 0;
    
    /** The file that contains the configuration of this plot. */
    private IFile file = null;
    
    /** Create another instance of the PlotView for the given file. */
    public static boolean activateWithFile(IFile file)
    {
        try
        {
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            IWorkbenchPage page = window.getActivePage();
            ++instance;
            PlotView view = (PlotView) page.showView(PlotView.ID,
                            String.format("Plot%d", instance), //$NON-NLS-1$
                            IWorkbenchPage.VIEW_ACTIVATE);
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

    /** Init the view, trying to read the model's file from the memento.
     *  @see ViewPart#init(IViewSite, IMemento)
     */
    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException
    {
        super.init(site, memento);
        if (memento == null)
            return;
        // Read path from the memento
        String path_txt = memento.getString(PLOTVIEW_FILE_PATH);
        if (path_txt == null  ||  path_txt.length() < 1)
            return;
        // Convert to path (relative to workspace root), then to file
        IPath path = Path.fromPortableString(path_txt);
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        init(root.getFile(path));
    }
    
    /** Load the given file into this view. */
    public void init(IFile file) throws PartInitException
    {
        plot.init(file);
        this.file = file;
        setPartName(plot.getPartName());
    }
    
    /** {@inheritDoc} */
    @Override
    public void createPartControl(Composite parent)
    {
        plot.createPartControl(parent);
        // Create actions
        Action remove_markers_action = new RemoveMarkersAction(plot.getChart());

        // Create context menu
        MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        manager.add(remove_markers_action);
        manager.add(new Separator());
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        Control ctl = plot.getChart();
        Menu menu = manager.createContextMenu(ctl);
        ctl.setMenu(menu);
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        plot.setFocus();
    }

    /** Assert proper cleanup. */
    @Override
    public void dispose()
    {
        plot.dispose();
        super.dispose();
    }

    /** Save the model's file to the memento. */
    @Override
    public void saveState(IMemento memento)
    {
        if (file == null)
            memento.putString(PLOTVIEW_FILE_PATH, ""); //$NON-NLS-1$
        else
            memento.putString(PLOTVIEW_FILE_PATH,
                              file.getFullPath().toPortableString());
    }
}
