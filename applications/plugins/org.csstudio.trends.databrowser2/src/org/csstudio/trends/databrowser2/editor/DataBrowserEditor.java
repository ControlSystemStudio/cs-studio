/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Level;

import org.csstudio.apputil.ui.elog.SendToElogActionHelper;
import org.csstudio.apputil.ui.workbench.OpenPerspectiveAction;
import org.csstudio.apputil.ui.workbench.OpenViewAction;
import org.csstudio.email.EMailSender;
import org.csstudio.swt.xygraph.figures.Axis;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser2.Activator;
import org.csstudio.trends.databrowser2.DataBrowserInput;
import org.csstudio.trends.databrowser2.IDataBrowserInput;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.Perspective;
import org.csstudio.trends.databrowser2.exportview.ExportView;
import org.csstudio.trends.databrowser2.imports.SampleImporters;
import org.csstudio.trends.databrowser2.model.AxisConfig;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.csstudio.trends.databrowser2.model.ModelListener;
import org.csstudio.trends.databrowser2.model.PVItem;
import org.csstudio.trends.databrowser2.propsheet.DataBrowserPropertySheetPage;
import org.csstudio.trends.databrowser2.propsheet.RemoveUnusedAxesAction;
import org.csstudio.trends.databrowser2.sampleview.SampleView;
import org.csstudio.trends.databrowser2.search.SearchView;
import org.csstudio.trends.databrowser2.ui.AddPVAction;
import org.csstudio.trends.databrowser2.ui.Controller;
import org.csstudio.trends.databrowser2.ui.Plot;
import org.csstudio.trends.databrowser2.ui.ToggleToolbarAction;
import org.csstudio.trends.databrowser2.waveformview.WaveformView;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
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
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.EditorPart;
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

    /** Data model */
    private Model model;

    /** Listener to model that updates this editor*/
	private ModelListener model_listener;

    /** GUI for the plot */
    private Plot plot;

    /** Controller that links model and plot */
    private Controller controller = null;

    /** @see #isDirty() */
    private boolean is_dirty = false;

    /** Create data browser editor
     *  @param input Input for editor, must be data browser config file
     *  @return DataBrowserEditor or <code>null</code> on error
     */
    public static DataBrowserEditor createInstance(final IEditorInput input)
    {
        final DataBrowserEditor editor;
        try
        {
        	final IWorkbench workbench = PlatformUI.getWorkbench();
        	final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        	final IWorkbenchPage page = window.getActivePage();
            editor = (DataBrowserEditor) page.openEditor(input, ID);
        }
        catch (Exception ex)
        {
            Activator.getLogger().log(Level.SEVERE, "Cannot create DataBrowserEditor", ex); //$NON-NLS-1$
            return null;
        }
        return editor;
    }

    /** Create an empty data browser editor
     *  @return DataBrowserEditor or <code>null</code> on error
     */
    public static DataBrowserEditor createInstance()
    {
    	return createInstance(new EmptyEditorInput());
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
        // Update the editor's name from "Data Browser" to file name
        setPartName(input.getName());

        if (input instanceof DataBrowserModelEditorInput)
        {   // Received model with input
        	model = ((DataBrowserModelEditorInput)input).getModel();
        	setInput(input);
        }
        else
        {   // Create new model
	        model = new Model();
	        setInput(new DataBrowserModelEditorInput(input, model));

			if (!(input instanceof EmptyEditorInput)) {
				// Load model content from file
				InputStream stream = null;
				try {
					if (input instanceof IDataBrowserInput) {
						stream = ((IDataBrowserInput) input).getInputStream();
					} else {
						final IFile workspace_file = getWorkspaceFile();
						if (workspace_file != null)
							stream = workspace_file.getContents(true);
						else {
							final File file = getInputFile();
							if (file != null)
								stream = new FileInputStream(file);
						}
					}
					if (stream == null)
						throw new PartInitException(
								"Cannot handle " + input.getName()); //$NON-NLS-1$
					model.read(stream);
				} catch (Exception ex) {
					throw new PartInitException(NLS.bind(
							Messages.ConfigFileErrorFmt, input.getName()), ex);
				}
			}
        }

        model_listener = new ModelListener()
        {
            @Override
            public void changedUpdatePeriod()
            {   setDirty(true);   }

            @Override
            public void changedArchiveRescale()
            {   setDirty(true);   }

            @Override
            public void changedColors()
            {   setDirty(true);   }

            @Override
            public void changedTimerange()
            {   setDirty(true);   }

            @Override
            public void changedAxis(final AxisConfig axis)
            {   setDirty(true);   }

            @Override
            public void itemAdded(final ModelItem item)
            {   setDirty(true);   }

            @Override
            public void itemRemoved(final ModelItem item)
            {   setDirty(true);   }

            @Override
            public void changedItemVisibility(final ModelItem item)
            {   setDirty(true);   }

            @Override
            public void changedItemLook(final ModelItem item)
            {   setDirty(true);   }

            @Override
            public void changedItemDataConfig(PVItem item)
            {   setDirty(true);   }

            @Override
            public void scrollEnabled(final boolean scroll_enabled)
            {   setDirty(true);   }


			@Override
			public void changedAnnotations()
			{   setDirty(true);   }

			@Override
			public void changedXYGraphConfig()
			{   setDirty(true);   }
        };
        model.addListener(model_listener);
    }

    /** Provide custom property sheet for this editor */
    @SuppressWarnings("rawtypes")
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
        final Canvas plot_box = new Canvas(parent, SWT.DOUBLE_BUFFERED | SWT.NO_REDRAW_RESIZE);
        plot_box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));

        plot = Plot.forCanvas(plot_box);

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

        // Only the 'page' seems to know if a part is visible or not,
        // so use PartListener to update controller's redraw handling
        getSite().getPage().addPartListener(new IPartListener2()
        {
            private boolean isThisEditor(final IWorkbenchPartReference part)
            {
                return part.getPart(false) == DataBrowserEditor.this;
            }
            // Enable redraws...
            @Override
            public void partOpened(final IWorkbenchPartReference part)
            {
                if (isThisEditor(part))
                    controller.suppressRedraws(false);
            }
            @Override
            public void partVisible(final IWorkbenchPartReference part)
            {
                if (isThisEditor(part))
                    controller.suppressRedraws(false);
            }
            // Suppress redraws...
            @Override
            public void partHidden(final IWorkbenchPartReference part)
            {
                if (isThisEditor(part))
                    controller.suppressRedraws(true);
            }
            @Override
            public void partClosed(final IWorkbenchPartReference part)
            {
                if (isThisEditor(part))
                    controller.suppressRedraws(true);
            }
            // Ignore
            @Override
            public void partInputChanged(final IWorkbenchPartReference part) { /* NOP */ }
            @Override
            public void partDeactivated(final IWorkbenchPartReference part)  { /* NOP */ }
            @Override
            public void partBroughtToTop(final IWorkbenchPartReference part) { /* NOP */ }
            @Override
            public void partActivated(final IWorkbenchPartReference part)    { /* NOP */ }
        });

        createContextMenu(plot_box);
    }

    /** Create context menu */
    private void createContextMenu(final Control parent)
    {
        final Activator activator = Activator.getDefault();
        final Shell shell = parent.getShell();
        final OperationsManager op_manager = plot.getOperationsManager();
        final MenuManager mm = new MenuManager();
        mm.add(new ToggleToolbarAction(plot));
        mm.add(new Separator());
        mm.add(new AddPVAction(op_manager, shell, model, false));
        mm.add(new AddPVAction(op_manager, shell, model, true));
        try
        {
            for (IAction imp : SampleImporters.createImportActions(op_manager, shell, model))
                    mm.add(imp);
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(parent.getShell(), Messages.Error, ex);
        }
        mm.add(new RemoveUnusedAxesAction(op_manager, model));
		if (!Activator.isRAP()) {
			mm.add(new Separator());
			mm.add(new OpenViewAction(IPageLayout.ID_PROP_SHEET,
					Messages.OpenPropertiesView, activator
							.getImageDescriptor("icons/prop_ps.gif"))); //$NON-NLS-1$
			mm.add(new OpenViewAction(SearchView.ID, Messages.OpenSearchView,
					activator.getImageDescriptor("icons/search.gif"))); //$NON-NLS-1$
			mm.add(new OpenViewAction(ExportView.ID, Messages.OpenExportView,
					activator.getImageDescriptor("icons/export.png"))); //$NON-NLS-1$
		}
		mm.add(new OpenViewAction(SampleView.ID, Messages.InspectSamples,
				activator.getImageDescriptor("icons/inspect.gif"))); //$NON-NLS-1$
		if (!Activator.isRAP()) {
			mm.add(new OpenViewAction(WaveformView.ID,
					Messages.OpenWaveformView, activator
							.getImageDescriptor("icons/wavesample.gif"))); //$NON-NLS-1$
			mm.add(new OpenPerspectiveAction(activator
					.getImageDescriptor("icons/databrowser.png"), //$NON-NLS-1$
					Messages.OpenDataBrowserPerspective, Perspective.ID));
			mm.add(new Separator());
			if (SendToElogActionHelper.isElogAvailable()) {
				mm.add(new SendToElogAction(shell, plot.getXYGraph()));
			}
			if (EMailSender.isEmailSupported()) {
				mm.add(new SendEMailAction(shell, plot.getXYGraph()));
			}
		}
		mm.add(new PrintAction(shell, plot.getXYGraph()));

        final Menu menu = mm.createContextMenu(parent);
        parent.setMenu(menu);
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
    	model.removeListener(model_listener);
        if (controller != null)
        {
            controller.stop();
            controller = null;
        }
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

    /** Get workspace for input
     *  <p>The file is 'relative' to the workspace, not 'absolute' in the
     *  file system. However, the file might be a linked resource to a
     *  file that physically resides outside of the workspace tree.
     *
     *  <p>Using this IFile is preferred because it allows the Navigator to update
     *
     *  @return IFile for the current editor input or <code>null</code> if file is outside the workspace
     */
    private IFile getWorkspaceFile()
    {
        return (IFile) getEditorInput().getAdapter(IFile.class);
    }

    /** Get plain file for input
     *
     *  <p>This has to be used for files outside of the workspace.
     *
     *  @return File for the current editor input or <code>null</code>
     */
    private File getInputFile()
    {
        final IPathEditorInput path_input = (IPathEditorInput) getEditorInput().getAdapter(IPathEditorInput.class);
        if (path_input == null)
            return null;
        return path_input.getPath().toFile();
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
        final IFile file = getWorkspaceFile();
        // Only allow saving to workspace.
        // Use Save-As to create new workspace file for
        // empty or out-of-workspace inputs
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
        setInput(new DataBrowserModelEditorInput(new DataBrowserInput(file.getFullPath()), model));
        setPartName(file.getName());
    }

    /** Prompt for file name
     *  @param old_file Old file name or <code>null</code>
     *  @return IFile for new file name
     */
    private IFile promptForFile(final IFile old_file)
    {
    	// TODO RAP and RCP
		if (Activator.isRAP()) {
                throw new RuntimeException("Not yet implemented for web version.");
		}
        final SaveAsDialog dlg = new SaveAsDialog(getSite().getShell());
        dlg.setBlockOnOpen(true);
        if (old_file != null)
            dlg.setOriginalFile(old_file);
        if (dlg.open() != Window.OK)
            return null;

        // The path to the new resource relative to the workspace
        IPath path = dlg.getResult();
        if (path == null)
            return null;
        // Assert it's an '.xml' file
        final String ext = path.getFileExtension();
        if (ext == null  ||  !ext.equals(Model.FILE_EXTENSION))
            path = path.removeFileExtension().addFileExtension(Model.FILE_EXTENSION);
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
        	// Update model with info that's kept in plot

        	// TODO Review. Why update the model when _saving_?
        	// The model should always have the correct info
        	// because it's listening to the plot,
        	// and here the data is simply written.

        	//TIME AXIS
      	  	Axis timeAxis = plot.getXYGraph().getXAxisList().get(0);
      	  	AxisConfig confTime = model.getTimeAxis();
      	  	if(confTime == null)
      	  	{
      	  		confTime = new AxisConfig(timeAxis.getTitle());
      	  		model.setTimeAxis(confTime);
      	  	}
      	  	setAxisConfig(confTime, timeAxis);

      	  	for (int i=0; i<model.getAxisCount(); i++)
      	  	{
      	  		AxisConfig conf = model.getAxis(i);
      	  		int axisIndex = model.getAxisIndex(conf);
      	  		Axis axis = plot.getXYGraph().getYAxisList().get(axisIndex);
      	  		setAxisConfig(conf, axis);
      	  	}

      	  	model.setGraphSettings(plot.getGraphSettings());
      	  	model.setAnnotations(plot.getAnnotations(), false);

        	// Write model to string
        	ByteArrayOutputStream buf = new ByteArrayOutputStream();

        	model.write(buf);
        	buf.close();

        	final ByteArrayInputStream in = new ByteArrayInputStream(buf.toByteArray());
            // Write buffer to file
            if (file.exists())
                file.setContents(in, IResource.FORCE, monitor);
            else
                file.create(in, true, monitor);

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


    /**
     * Set AxisConfigProperties from Axis
     * @param conf
     * @param axis
     */
    private void setAxisConfig(AxisConfig conf , Axis axis){

    	 //Don't fire axis change event to avoid SWT Illegal Thread Access
    	 conf.setFireEvent(false);

    	 conf.setFontData(axis.getTitleFontData());
    	 conf.setColor(axis.getForegroundColorRGB());
    	 conf.setScaleFontData(axis.getScaleFontData());


    	 //MIN MAX RANGE
    	 conf.setRange(axis.getRange().getLower(), axis.getRange().getUpper());

		 //GRID
		 conf.setShowGridLine(axis.isShowMajorGrid());
		 conf.setDashGridLine(axis.isDashGridLine());
		 conf.setGridLineColor(axis.getMajorGridColorRGB());

		 //FORMAT
		 conf.setAutoFormat(axis.isAutoFormat());
		 conf.setTimeFormatEnabled(axis.isDateEnabled());
		 conf.setFormat(axis.getFormatPattern());

		 conf.setFireEvent(true);
    }
}
