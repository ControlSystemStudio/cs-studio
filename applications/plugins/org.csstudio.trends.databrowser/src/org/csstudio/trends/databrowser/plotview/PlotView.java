package org.csstudio.trends.databrowser.plotview;

import org.csstudio.swt.chart.Chart;
import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.plotpart.PlotPart;
import org.csstudio.trends.databrowser.plotpart.RemoveMarkersAction;
import org.csstudio.util.file.FileUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
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
 *  Displays the plot, no editing except for pan/zoom.
 *  
 *  @author Kay Kasemir
 */
public class PlotView extends ViewPart
{
    /** Memento tag for config file. */
    private static final String PLOTVIEW_FILE_PATH = "PLOTVIEW_FILE_PATH"; //$NON-NLS-1$

    /** Memento tag for button bar. */
    private static final String BUTTON_BAR_VISIBLE = "BUTTON_BAR_VISIBLE"; //$NON-NLS-1$
    
    /** View ID registered in plugin.xml as org.eclipse.views ID */
    public static final String ID = PlotView.class.getName();
    
    /** The underlying plot part. */
    private final PlotPart plot_part = new PlotPart();

    /** Instance counter used to create the "secondary ID"
     *  that's required to support multiple views of the same type.
     */
    private static long instance = 0;

    private boolean initially_show_button_bar = false;
    
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
        final String path_txt = memento.getString(PLOTVIEW_FILE_PATH);
        if (path_txt == null  ||  path_txt.length() < 1)
            return;
        init(FileUtil.getWorkspaceFile(path_txt));
        
       final String show_bar_txt = memento.getString(BUTTON_BAR_VISIBLE);
       if (show_bar_txt == null  ||  show_bar_txt.length() < 1)
           return;
       initially_show_button_bar = Boolean.parseBoolean(show_bar_txt);
    }
    
    /** Load the given file into this view. */
    public void init(IFile file) throws PartInitException
    {
        plot_part.init(file);
        setPartName(plot_part.getPartName());
    }
    
    /** {@inheritDoc} */
    @Override
    public void createPartControl(Composite parent)
    {
        plot_part.createPartControl(parent, false);

        // Initially hide the button bar?
        plot_part.getInteractiveChart().showButtonBar(initially_show_button_bar);
        
        // Create context menu
        MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        manager.add(plot_part.createShowButtonBarAction());
        final Chart chart = plot_part.getInteractiveChart().getChart();
        manager.add(new RemoveMarkersAction(chart));
        manager.add(new Separator());
        manager.add(new OpenAsPlotEditorAction(plot_part));
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        Menu menu = manager.createContextMenu(chart);
        chart.setMenu(menu);
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        plot_part.setFocus();
    }

    /** Assert proper cleanup. */
    @Override
    public void dispose()
    {
        plot_part.dispose();
        super.dispose();
    }

    /** Save the model's file to the memento. */
    @Override
    public void saveState(IMemento memento)
    {
        memento.putString(PLOTVIEW_FILE_PATH,
                          FileUtil.toPortablePath(plot_part.getFile()));
        final boolean visible =
            plot_part.getInteractiveChart().getButtonBar().isVisible();
        memento.putString(BUTTON_BAR_VISIBLE, Boolean.toString(visible));
    }
}
