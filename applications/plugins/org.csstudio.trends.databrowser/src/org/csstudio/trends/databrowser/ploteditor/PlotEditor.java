package org.csstudio.trends.databrowser.ploteditor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.csstudio.archive.util.TimestampUtil;
import org.csstudio.platform.ui.workbench.FileEditorInput;
import org.csstudio.platform.util.ITimestamp;
import org.csstudio.trends.databrowser.Controller;
import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.archiveview.ArchiveView;
import org.csstudio.trends.databrowser.configview.ConfigView;
import org.csstudio.trends.databrowser.exportview.ExportView;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.model.ModelListener;
import org.csstudio.trends.databrowser.sampleview.SampleView;
import org.csstudio.util.editor.EmptyEditorInput;
import org.csstudio.util.editor.PromptForNewXMLFileDialog;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

/** The eclipse 'editor' for the data browser.
 *  This is really more of a 'view' to the user, but since each data browser
 *  plot stands for its own configuration (model data), they're Eclipse
 *  'editors'.
 *  @author Kay Kasemir
 */
public class PlotEditor extends EditorPart
{
    public static final String ID = PlotEditor.class.getName();
    private Model model;
    private Controller controller;
    private Action remove_markers_action, add_action;
    private Action config_action, archive_action, sample_action, export_action;
    private BrowserUI gui;
    private boolean is_dirty = false;
    private ModelListener listener;

    /** Create a new, empty editor, not attached to a file.
     *  @return Returns the new editor or <code>null</code>.
     */
    public static PlotEditor createChartEditor()
    {
    	try
    	{
		    IWorkbench workbench = PlatformUI.getWorkbench();
		    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		    IWorkbenchPage page = window.getActivePage();
		    
		    EmptyEditorInput input = new EmptyEditorInput();
		    PlotEditor editor =
		        (PlotEditor) page.openEditor(input, PlotEditor.ID);
		    return editor;
    	}
    	catch (Exception e)
    	{
    		System.out.println(e.getMessage());
    	}
    	return null;
    }
    
    /** @return Returns the model. */
    public Model getModel()
    {   return model; }
    
    /** @reutrn The current 'start' time of the graph. */
    public ITimestamp getStart()
    { 
        return TimestampUtil.fromDouble(
                        gui.getChart().getXAxis().getLowValue()); 
    }
    
    /** @reutrn The current 'start' time of the graph. */
    public ITimestamp getEnd()
    { 
        return TimestampUtil.fromDouble(
                        gui.getChart().getXAxis().getHighValue()); 
    }

    /** @return Returns the controller. */
    public Controller getController()
    {   return controller;  }
    
    @Override
    public void init(IEditorSite site, IEditorInput input)
            throws PartInitException
    {
        setSite(site);
        setInput(input);
        // Load model content from file
        model = new Model();
        IFile file = getEditorInputFile();
        if (file != null)
        {
            try
            {
                InputStream stream = file.getContents();
                model.load(stream);
                stream.close();
            }
            catch (Exception e)
            {
                throw new PartInitException("Load error", e); //$NON-NLS-1$
            }
        }

        // Update 'dirty' state whenever anything changes
        listener = new ModelListener()
        {
            public void periodsChanged()
            {   entriesChanged();  }

            public void entriesChanged()
            {
                if (!is_dirty)
                {
                    is_dirty = true;
                    firePropertyChange(IEditorPart.PROP_DIRTY);
                }
                updateTitle();
            }
            
            public void entryAdded(IModelItem new_item) 
            {   entriesChanged();  }
            
            public void entryConfigChanged(IModelItem item) 
            {   entriesChanged();  }
            
            public void entryLookChanged(IModelItem item) 
            {   /* so what */ }
            
            public void entryArchivesChanged(IModelItem item)
            {   entriesChanged();  }

            public void entryRemoved(IModelItem removed_item) 
            {   entriesChanged();  }
        };
        model.addListener(listener);
    }
    
    /** @return Returns the <code>IFile</code> for the current editor input. */
    private IFile getEditorInputFile()
    {  
        IEditorInput input = getEditorInput();
        if (input instanceof EmptyEditorInput)
            return null;
        // Side Note:
        // After some back and forth, trying to avoid the resource/workspace/
        // project/container/file stuff and instead sticking with the 
        // java.io.file, I found it best to give up and use the Eclipse
        // resource API, since otherwise one keeps converting between those
        // two APIs anyway, plus runs into errors with 'resources' being
        // out of sync....

        // IEditorInput happens to come as IPathEditorInput, which
        // only has the file system 'location' via getPath(),
        // and not the workspace-relative path.
        // Resource gymnastics: Convert file system 'location'...
        IPath location = ((IPathEditorInput) input).getPath();
        // .. into file inside the workspace.
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IFile file = root.getFileForLocation(location);
        return file;
    }

    @Override
    public void doSave(IProgressMonitor monitor)
    {
        IFile file = getEditorInputFile();
        if (file != null)
            saveToFile(monitor, file);
        else
            doSaveAs();
    }
    
    /** Save current model content to given file, mark editor as clean.
     * 
     *  @param monitor <code>IProgressMonitor</code>, may be null.
     *  @param file The file to use. May not exist, but I think its container has to.
     *  @return Returns <code>true</code> when successful.
     */
    private boolean saveToFile(IProgressMonitor monitor, IFile file)
    {
        boolean ok = true;
        if (monitor != null)
            monitor.beginTask(Messages.SaveBrowserConfig,
                            IProgressMonitor.UNKNOWN);
        InputStream stream =
            new ByteArrayInputStream(model.getXMLContent().getBytes());
        try
        {
            if (file.exists())
                file.setContents(stream, true, false, monitor);
            else
                file.create(stream, true, monitor);
            if (monitor != null)
                monitor.done();
            // Mark as clean
            is_dirty = false;
            firePropertyChange(IEditorPart.PROP_DIRTY);
        }
        catch (Exception e)
        {
            ok = false;
            if (monitor != null)
                monitor.setCanceled(true);
            Plugin.logException("Save error", e); //$NON-NLS-1$
        }
        finally
        {
            try
            {   stream.close(); }
            catch (Exception e) {}
        }
        return ok;
    }

    @Override
    public void doSaveAs()
    {
        IFile file = PromptForNewXMLFileDialog.run(
                getSite().getShell(), getEditorInputFile());
        if (file == null  ||  !saveToFile(null, file))
            return;
        // Update input and title
        // TODO IDE FileEditorInput
        setInput(new FileEditorInput(file));
        updateTitle();
    }

    @Override
    public boolean isDirty()
    {   return is_dirty;  }

    @Override
    public boolean isSaveAsAllowed()
    {   return true;  }
    
    @Override
    public void createPartControl(Composite parent)
    {
        gui = new BrowserUI(parent, 0);
        controller = new Controller(model, gui);
        createActions();
        createContextMenu();
        updateTitle();
    }

    /** @see org.eclipse.ui.part.WorkbenchPart#dispose() */
    @Override
    public void dispose()
    {
        controller.dispose();
        model.removeListener(listener);
        super.dispose();
    }
    
    /** Set the editor part's title and tool-tip. */
    private void updateTitle()
    {   // See plugin book p.332.
        IEditorInput input = getEditorInput();
        String title = getEditorInput().getName();
        setPartName(title);
        setTitleToolTip(input.getToolTipText());
    }

    @Override
    public void setFocus()
    {   gui.setFocus();  }

    /** Create all actions. */
    private void createActions()
    {
        remove_markers_action = new RemoveMarkersAction(gui.getChart());
        add_action = new AddPVAction(model);
        config_action = new OpenViewAction(this, Messages.OpenConfigView, ConfigView.ID);
        archive_action = new OpenViewAction(this, Messages.OpenArchiveView, ArchiveView.ID);
        sample_action = new OpenViewAction(this, Messages.OpenSampleView, SampleView.ID);
        export_action = new OpenViewAction(this, Messages.OpenExportView, ExportView.ID);
    }

    /** Create and connect the context menu. */
    private void createContextMenu()
    {
        // Create context menu
        MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        manager.add(remove_markers_action);
        manager.add(add_action);
        manager.add(config_action);
        manager.add(archive_action);
        manager.add(sample_action);
        manager.add(export_action);
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

        Control ctl = controller.getBrowserUI().getChart();
        Menu menu = manager.createContextMenu(ctl);
        ctl.setMenu(menu);
        // TODO: publish the menu so others can extend it?
        //getSite().registerContextMenu(manager, selectionProvider);
    }
}
