package org.csstudio.trends.databrowser.plotpart;

import java.io.InputStream;

import org.csstudio.swt.chart.Chart;
import org.csstudio.swt.chart.InteractiveChart;
import org.csstudio.swt.chart.actions.PrintCurrentImageAction;
import org.csstudio.swt.chart.actions.RemoveMarkersAction;
import org.csstudio.swt.chart.actions.RemoveSelectedMarkersAction;
import org.csstudio.swt.chart.actions.SaveCurrentImageAction;
import org.csstudio.swt.chart.actions.ShowButtonBarAction;
import org.csstudio.swt.chart.actions.UpdateSelectedMarkersAction;
import org.csstudio.trends.databrowser.model.Model;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

/** Base for a ViewPart or EditorPart that displays a DataBrowser plot.
 *  @author Kay Kasemir
 */
public class PlotPart
{
    private Model model = new Model();
    private Controller controller;
    private BrowserUI gui;
    private IFile file;

    /** Initialize the model from given file. */
    public void init(final IFile file) throws PartInitException
    {
        if (file == null)
            return;
        // Load model content from file
        try
        {
            final InputStream stream = file.getContents();
            model.load(stream);
            stream.close();
        }
        catch (Exception ex)
        {
            throw new PartInitException("Load error for "  //$NON-NLS-1$
                            + file.getName(), ex);
        }
        this.file = file;
    }
    
    /** Get a name suitable for the part's title.
     *  <p>
     *  That's the plain file name, without path and suffix,
     *  or "" which translates into the default title.
     */
    @SuppressWarnings("nls")
    public String getPartName()
    {
        if (file == null)
            return "";
        final String name = file.getName();
        final int dot = name.lastIndexOf(".");
        return dot > 1  ?  name.substring(0, dot)  :  name;
    }
    
    /** @return The model's file. */
    public IFile getFile()
    {
        return file;
    }
    
    /** @return The model. */
    public Model getModel()
    {  
        return model;
    }
    
    /** @return The InteractiveChart within the GUI. */
    public InteractiveChart getInteractiveChart()
    {
        return gui.getInteractiveChart();
    }
    
    /** Creates the SWT controls for DataBrowser plot.
     *  @see IWorkbenchPart#createPartControl
     */
    public void createPartControl(final Composite parent,
                                  final boolean allow_drop)
    {
        gui = new BrowserUI(model, parent, 0);
        controller = new Controller(model, gui, allow_drop);
    }
    
    /** Add plot actions that are common to editor and view:
     *  Button bar, Markers, ...
     */
    public void addContextMenuPlotActions(final MenuManager context_menu)
    {
        final InteractiveChart ichart = getInteractiveChart();
        final Chart chart = ichart.getChart();
        final Action remove_markers_action = new RemoveMarkersAction(chart);
        final RemoveSelectedMarkersAction remove_marker_action =
            new RemoveSelectedMarkersAction(chart);
        final UpdateSelectedMarkersAction update_markers_action =
            new UpdateSelectedMarkersAction(chart);
        context_menu.add(new ShowButtonBarAction(ichart));
        context_menu.add(remove_markers_action);
        context_menu.add(remove_marker_action);
        context_menu.add(update_markers_action);
        
        context_menu.addMenuListener(new IMenuListener()
        {
            public void menuAboutToShow(IMenuManager manager)
            {
                remove_marker_action.updateEnablement();
                update_markers_action.updateEnablement();
            }
        });
    }

    /** Add export actions that are common to editor and view:
     *  To file, printer, logbook, ...
     */
    public void addContextMenuExportActions(final MenuManager context_menu)
    {
        final InteractiveChart ichart = getInteractiveChart();
        final Chart chart = ichart.getChart();

        context_menu.add(new Separator());
        context_menu.add(new SaveCurrentImageAction(chart));
        context_menu.add(new PrintCurrentImageAction(chart));
        if (SendToElogAction.isElogAvailable())
            context_menu.add(new SendToElogAction(chart));
    }

    /** @see IWorkbenchPart#setFocus */
    public void setFocus()
    {   
        gui.setFocus(); 
    }
    
    /** Must be called for cleanup. */
    public void dispose()
    {
        gui.dispose();
        controller.dispose();
    }
}
