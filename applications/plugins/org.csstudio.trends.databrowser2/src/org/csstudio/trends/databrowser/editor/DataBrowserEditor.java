package org.csstudio.trends.databrowser.editor;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.csstudio.apputil.ui.workbench.OpenPerspectiveAction;
import org.csstudio.email.ui.AbstractSendEMailAction;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.workbench.OpenViewAction;
import org.csstudio.trends.databrowser.Activator;
import org.csstudio.trends.databrowser.Messages;
import org.csstudio.trends.databrowser.Perspective;
import org.csstudio.trends.databrowser.exportview.ExportView;
import org.csstudio.trends.databrowser.model.AxisConfig;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.model.ModelItem;
import org.csstudio.trends.databrowser.model.ModelListener;
import org.csstudio.trends.databrowser.model.PVItem;
import org.csstudio.trends.databrowser.propsheet.DataBrowserPropertySheetPage;
import org.csstudio.trends.databrowser.sampleview.InspectSamplesAction;
import org.csstudio.trends.databrowser.search.SearchView;
import org.csstudio.trends.databrowser.ui.AddPVAction;
import org.csstudio.trends.databrowser.ui.Controller;
import org.csstudio.trends.databrowser.ui.Plot;
import org.csstudio.trends.databrowser.waveformview.OpenWaveformAction;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.properties.IPropertySheetPage;

/** Eclipse 'editor' for the Data Browser
 *  <p>
 *  plugin.xml registers this as an editor for data browser configuration
 *  files.
 *  @author Kay Kasemir
 */
public class DataBrowserEditor extends EditorPart
{
    /** Editor ID (same ID as original Data Browser) registered in plugin.xml */
    final public static String ID = "org.csstudio.trends.databrowser.ploteditor.PlotEditor"; //$NON-NLS-1$

    /** plugin.xml registers the editor for this file extension  */
    final public static String FILE_EXTENSION = "plt"; //$NON-NLS-1$

    /** Data model */
    private Model model;
    
    /** GUI for the plot */
    private Plot plot;
    
    /** Controller that links model and plot */
    private Controller controller;

    /** @see #isDirty() */
    private boolean is_dirty = false;

    /** Create an empty data browser editor
     *  @return DataBrowserEditor or <code>null</code> on error
     */
    public static DataBrowserEditor createInstance()
    {
        final DataBrowserEditor editor;
        try
        {
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            IWorkbenchPage page = window.getActivePage();
            editor = (DataBrowserEditor) page.openEditor(new EmptyEditorInput(), ID);
        }
        catch (Exception ex)
        {
            CentralLogger.getInstance().error("Cannot create DataBrowserEditor", ex); //$NON-NLS-1$
            return null;
        }
        
        //  Try to switch to the DataBrowser perspective
        final IWorkbench workbench = PlatformUI.getWorkbench();
        final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        try
        {
            workbench.showPerspective(Perspective.ID, window);
        }
        catch (WorkbenchException ex)
        {
            // Never mind
        }
        return editor;
    }

    /** @return Model displayed/edited by this EditorPart */
    public Model getModel()
    {
        return model;
    }

    /** Initialize model from editor input
     *  {@inheritDoc}
     */
    @Override
    public void init(final IEditorSite site, final IEditorInput input)
            throws PartInitException
    {
        setSite(site);
        setInput(input);
        // Update the editor's name from "Data Browser" to file name
        setPartName(input.getName());
        model = new Model();
        // If it's a file, load content into Model
        final IFile file = getInputFile();
        if (file != null)
        {
            try
            {
                model.read(file.getContents(true));
            }
            catch (Exception ex)
            {
                throw new PartInitException(NLS.bind(Messages.ConfigFileErrorFmt, input.getName()), ex);
            }
        }
        else if (! (input instanceof EmptyEditorInput))
            throw new PartInitException("Cannot handle " + input.getName()); //$NON-NLS-1$

        // Update 'dirty' state when model changes in any way
        model.addListener(new ModelListener()
        {
            public void changedUpdatePeriod()
            {   setDirty(true);   }

            public void changedColors()
            {   setDirty(true);   }

            public void changedTimerange()
            {   setDirty(true);   }

            public void changedAxis(final AxisConfig axis)
            {   setDirty(true);   }

            public void itemAdded(final ModelItem item)
            {   setDirty(true);   }

            public void itemRemoved(final ModelItem item)
            {   setDirty(true);   }
            
            public void changedItemVisibility(final ModelItem item)
            {   setDirty(true);   }
            
            public void changedItemLook(final ModelItem item)
            {   setDirty(true);   }

            public void changedItemDataConfig(PVItem item)
            {   setDirty(true);   }

            public void scrollEnabled(final boolean scroll_enabled)
            {   setDirty(true);   }
        });
    }
    
    /** Provide custom property sheet for this editor */
    @SuppressWarnings("unchecked")
    @Override
    public Object getAdapter(final Class adapter)
    {
        if (adapter == IPropertySheetPage.class)
            return new DataBrowserPropertySheetPage(model, plot.getOperationsManager());
        return super.getAdapter(adapter);
    }

    /** Create Plot GUI, connect to model via Controller
     *  {@inheritDoc}
     */
    @Override
    public void createPartControl(final Composite parent)
    {
        // Create GUI elements (Plot)
        final GridLayout layout = new GridLayout();
        parent.setLayout(layout);
        
        // Canvas that holds the graph
        final Canvas plot_box = new Canvas(parent, 0);
        plot_box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));

        plot = new Plot(plot_box);
        
        // Create and start controller
        controller = new Controller(parent.getShell(), model, plot);
        try
        {
            controller.start();
        }
        catch (Exception ex)
        {
            MessageDialog.openError(parent.getShell(), Messages.Error,
                    NLS.bind(Messages.ControllerStartErrorFmt, ex.getMessage()));
        }
        
        createContextMenu(plot_box);
    }

    /** Create context menu */
    private void createContextMenu(final Control parent)
    {
        final Activator activator = Activator.getDefault();
        final Shell shell = parent.getShell();
        final MenuManager mm = new MenuManager();
        mm.add(plot.getToggleToolbarAction());
        mm.add(new Separator());
        mm.add(new AddPVAction(plot.getOperationsManager(), shell, model, false));
        mm.add(new AddPVAction(plot.getOperationsManager(), shell, model, true));
        mm.add(new Separator());
        mm.add(new OpenViewAction(IPageLayout.ID_PROP_SHEET, Messages.OpenPropertiesView,
                activator.getImageDescriptor("icons/prop_ps.gif"))); //$NON-NLS-1$
        mm.add(new OpenViewAction(SearchView.ID, Messages.OpenSearchView,
                activator.getImageDescriptor("icons/search.gif"))); //$NON-NLS-1$
        mm.add(new OpenViewAction(ExportView.ID, Messages.OpenExportView,
                activator.getImageDescriptor("icons/export.png"))); //$NON-NLS-1$
        mm.add(new InspectSamplesAction());
        mm.add(new OpenWaveformAction());
        mm.add(new OpenPerspectiveAction(
                activator.getImageDescriptor("icons/databrowser.png"), //$NON-NLS-1$
                Messages.OpenDataBrowserPerspective,
                Perspective.ID));
        mm.add(new Separator());
        if (SendToElogAction.isElogAvailable())
            mm.add(new SendToElogAction(shell, plot.getXYGraph()));
        mm.add(new SendEMailAction(shell, plot.getXYGraph()));
        mm.add(new PrintAction(shell, plot.getXYGraph()));
        
        final Menu menu = mm.createContextMenu(parent);
        parent.setMenu(menu);
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        controller.stop();
        super.dispose();
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        // NOP
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDirty()
    {
        return is_dirty;
    }

    /** Update the 'dirty' flag
     *  @param dirty <code>true</code> if model changed and needs to be saved
     */
    protected void setDirty(final boolean dirty)
    {
        is_dirty = dirty;
        firePropertyChange(IEditorPart.PROP_DIRTY);
    }
    
    /** @return IFile for the current editor input or <code>null</code>
     *  The file is 'relative' to the workspace, not 'absolute' in the
     *  file system. However, the file might be a linked resource to a
     *  file that physically resides outside of the workspace tree.
     */
    private IFile getInputFile()
    {
        return (IFile) getEditorInput().getAdapter(IFile.class);
    }
    

    /** {@inheritDoc} */
    @Override
    public boolean isSaveAsAllowed()
    {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void doSave(final IProgressMonitor monitor)
    {
        final IFile file = getInputFile();
        if (file == null)
            doSaveAs();
        else
            saveToFile(monitor, file);
    }

    /** {@inheritDoc} */
    @Override
    public void doSaveAs()
    {
        final IFile file = promptForFile(null);
        if (file == null)
            return;
        if (! saveToFile(new NullProgressMonitor(), file))
            return;
        // Set that file as editor's input, so that just 'save' instead of
        // 'save as' is possible from now on
        setInput(new FileEditorInput(file));
        setPartName(file.getName());
    }
    
    /** Prompt for file name
     *  @param old_file Old file name or <code>null</code>
     *  @return IFile for new file name
     */
    private IFile promptForFile(final IFile old_file)
    {
        final SaveAsDialog dlg = new SaveAsDialog(getSite().getShell());
        dlg.setBlockOnOpen(true);
        if (old_file != null)
            dlg.setOriginalFile(old_file);
        if (dlg.open() != SaveAsDialog.OK)
            return null;
        
        // The path to the new resource relative to the workspace
        IPath path = dlg.getResult();
        if (path == null)
            return null;
        // Assert it's an '.xml' file
        final String ext = path.getFileExtension();
        if (ext == null  ||  !ext.equals(FILE_EXTENSION))
            path = path.removeFileExtension().addFileExtension(FILE_EXTENSION);
        // Get the file for the new resource's path.
        final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        return root.getFile(path);
    }

    /** Save current model content to given file, mark editor as clean.
     * 
     *  @param monitor <code>IProgressMonitor</code>, may be null.
     *  @param file The file to use. May not exist, but I think its container has to.
     *  @return Returns <code>true</code> when successful.
     */
    private boolean saveToFile(final IProgressMonitor monitor, final IFile file)
    {
        monitor.beginTask(Messages.Save, IProgressMonitor.UNKNOWN);
        try
        {
            // Create pipes so that model can write its content to pipe,
            // while IFile API reads other end and in turn write that to the file.
            final PipedOutputStream out = new PipedOutputStream();
            final InputStream in = new PipedInputStream(out);
            
            // Writer thread to avoid pipe deadlock
            final Thread write_thread = new Thread(new Runnable()
            {
                  public void run()
                  {
                      try
                      {
                          model.write(out);
                      }
                      catch (Exception ex)
                      {
                          ex.printStackTrace();
                      }
                  }
            });
            write_thread.start();
            // IFile reads other end of pipe, writes into file
            if (file.exists())
                file.setContents(in, IFile.FORCE, monitor);
            else
                file.create(in, true, monitor);
            // Write thread should have finished...
            write_thread.join();
            setDirty(false);
        }
        catch (Exception ex)
        {
            MessageDialog.openError(getSite().getShell(),
                    Messages.Error,
                    NLS.bind(Messages.FileSaveErrorFmt, file.getName(), ex.getMessage()));
            return false;
        }
        finally
        {
            monitor.done();
        }
        return true;
    }
}
