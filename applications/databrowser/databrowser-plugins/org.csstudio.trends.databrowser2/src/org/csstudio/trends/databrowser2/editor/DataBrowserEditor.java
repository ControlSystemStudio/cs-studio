/*******************************************************************************
 * Copyright (c) 2010-2015 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.editor;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.logging.Level;

import org.csstudio.apputil.ui.workbench.OpenViewAction;
import org.csstudio.email.EMailSender;
import org.csstudio.swt.rtplot.undo.UndoableActionManager;
import org.csstudio.trends.databrowser2.Activator;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.Perspective;
import org.csstudio.trends.databrowser2.exportview.ExportView;
import org.csstudio.trends.databrowser2.imports.SampleImporters;
import org.csstudio.trends.databrowser2.model.AxisConfig;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.csstudio.trends.databrowser2.model.ModelListener;
import org.csstudio.trends.databrowser2.model.ModelListenerAdapter;
import org.csstudio.trends.databrowser2.model.PVItem;
import org.csstudio.trends.databrowser2.persistence.XMLPersistence;
import org.csstudio.trends.databrowser2.preferences.Preferences;
import org.csstudio.trends.databrowser2.propsheet.AutoscaleAxisAction;
import org.csstudio.trends.databrowser2.propsheet.AxisMinMaxEditAction;
import org.csstudio.trends.databrowser2.propsheet.AxisNameAxisAction;
import org.csstudio.trends.databrowser2.propsheet.AxisNameEditAction;
import org.csstudio.trends.databrowser2.propsheet.DataBrowserPropertySheetPage;
import org.csstudio.trends.databrowser2.propsheet.GridAxisAction;
import org.csstudio.trends.databrowser2.propsheet.RemoveUnusedAxesAction;
import org.csstudio.trends.databrowser2.propsheet.ScaleTypeAxisAction;
import org.csstudio.trends.databrowser2.propsheet.TimeAxisGridAction;
import org.csstudio.trends.databrowser2.propsheet.TraceNameAxisAction;
import org.csstudio.trends.databrowser2.sampleview.SampleView;
import org.csstudio.trends.databrowser2.search.SearchView;
import org.csstudio.trends.databrowser2.ui.AddPVAction;
import org.csstudio.trends.databrowser2.ui.Controller;
import org.csstudio.trends.databrowser2.ui.ModelBasedPlot;
import org.csstudio.trends.databrowser2.ui.RefreshAction;
import org.csstudio.trends.databrowser2.waveformview.WaveformView;
import org.csstudio.ui.util.EmptyEditorInput;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.csstudio.ui.util.perspective.OpenPerspectiveAction;
import org.csstudio.utility.singlesource.PathEditorInput;
import org.csstudio.utility.singlesource.ResourceHelper;
import org.csstudio.utility.singlesource.SingleSourcePlugin;
import org.csstudio.utility.singlesource.UIHelper.UI;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;

/** Eclipse 'editor' for the Data Browser
 *  <p>
 *  plugin.xml registers this as an editor for data browser configuration
 *  files.
 *  @author Kay Kasemir
 *  @author Xihui Chen (Adjustment to make it work like a view in RAP)
 *  @author Naceur Benhadj (add property to hide "Property" view)
 */
@SuppressWarnings("nls")
public class DataBrowserEditor extends EditorPart
{
    /** Editor ID (same ID as original Data Browser) registered in plugin.xml */
    final public static String ID = "org.csstudio.trends.databrowser.ploteditor.PlotEditor"; //$NON-NLS-1$

    /** Data model */
    private Model model;

    /** Listener to model that updates this editor*/
    private ModelListener model_listener;

    /** GUI for the plot */
    private ModelBasedPlot plot;

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
        if (SingleSourcePlugin.isRAP())
        {
            if (Preferences.isDataBrowserSecured()
                    && !SingleSourcePlugin.getUIHelper().rapIsLoggedIn(
                            Display.getCurrent()))
            {
                if (!SingleSourcePlugin.getUIHelper().rapAuthenticate(
                        Display.getCurrent())) return null;
            }

        }

        return createInstance(new EmptyEditorInput()
        {
            @Override
            public String getName()
            {
                if (SingleSourcePlugin.isRAP()) return "Data Browser";
                return super.getName();
            }
        });
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

        if (input instanceof DataBrowserModelEditorInput)
        {   // Received model with input
            model = ((DataBrowserModelEditorInput)input).getModel();
            setInput(input);
        }
        else
        {   // Create new model
            model = new Model();
            setInput(new DataBrowserModelEditorInput(input, model));

            // Load model content from file
            try
            (
                final InputStream stream = SingleSourcePlugin.getResourceHelper().getInputStream(input);
            )
            {
                if (stream != null)
                    new XMLPersistence().load(model, stream);
            }
            catch (Exception ex)
            {
                throw new PartInitException(NLS.bind(
                        Messages.ConfigFileErrorFmt, input.getName()), ex);
            }
        }

        // Update the editor's name from "Data Browser" to title of model or file name
        // See DataBrowserModelEditorInput.getName()
        setPartName(getEditorInput().getName());

        model_listener = new ModelListenerAdapter()
        {
            @Override
            public void changedSaveChangesBehavior(final boolean save_changes)
            {
                is_dirty = save_changes;
                firePropertyChange(IEditorPart.PROP_DIRTY);
            }

            @Override
            public void changedTitle()
            {   setDirty(true);   }

            @Override
            public void changedLayout()
            {   setDirty(true);   }

            @Override
            public void changedTiming()
            {   setDirty(true);   }

            @Override
            public void changedArchiveRescale()
            {   setDirty(true);   }

            @Override
            public void changedColorsOrFonts()
            {   setDirty(true);   }

            @Override
            public void changedTimerange()
            {   setDirty(true);   }

            @Override
            public void changeTimeAxisConfig()
            {   setDirty(true);   }

            @Override
            public void changedAxis(final Optional<AxisConfig> axis)
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
            {
                site.getShell().getDisplay().asyncExec(() -> setDirty(true));
            }

            @Override
            public void changedItemDataConfig(PVItem item)
            {   setDirty(true);   }

            @Override
            public void scrollEnabled(final boolean scroll_enabled)
            {   setDirty(true);   }

            @Override
            public void changedAnnotations()
            {
                site.getShell().getDisplay().asyncExec(() -> setDirty(true));
            }
        };
        model.addListener(model_listener);
    }

    /** Provide custom property sheet for this editor */
    @Override
    public <T> T getAdapter(final Class<T> adapter)
    {
        if (adapter == IPropertySheetPage.class)
            return adapter.cast(new DataBrowserPropertySheetPage(model, plot.getPlot().getUndoableActionManager()));
        return super.getAdapter(adapter);
    }

    /** Create Plot GUI, connect to model via Controller
     *  {@inheritDoc}
     */
    @Override
    public void createPartControl(final Composite parent)
    {
        // Create GUI elements (Plot)
        parent.setLayout(new FillLayout());
        plot = new ModelBasedPlot(parent);

        // Create and start controller
        controller = new Controller(parent.getShell(), model, plot);
        try
        {
            controller.start();
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(parent.getShell(), Messages.Error, ex);
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

        createContextMenu(plot.getPlot().getPlotControl());
    }

    /** Create context menu */
    private void createContextMenu(final Control parent)
    {
        final MenuManager mm = new MenuManager();
        mm.setRemoveAllWhenShown(true);
        final Menu menu = mm.createContextMenu(parent);
        parent.setMenu(menu);
        getSite().registerContextMenu(mm, null);
        mm.addMenuListener(this::fillContextMenu);
    }

    /** Dynamically fill context menu
     *  @param manager
     */
    private void fillContextMenu(final IMenuManager manager)
    {
        final Activator activator = Activator.getDefault();
        final Shell shell = getSite().getShell();
        Point location = plot.getPlot().getDisplay().getCursorLocation();
        location = plot.getPlot().toControl(location);
        boolean inXAxis = plot.getPlot().inXAxis(location);
        int inYAxis = plot.getPlot().inYAxis(location);
        final UndoableActionManager op_manager = plot.getPlot().getUndoableActionManager();
        if(inYAxis !=-1) {
            AxisConfig axis_config = model.getAxis(inYAxis);
            manager.add(new AutoscaleAxisAction(axis_config));
            manager.add(new ScaleTypeAxisAction(axis_config));
            manager.add(new GridAxisAction(axis_config));
            manager.add(new Separator());
            manager.add(new AxisNameAxisAction(axis_config));
            manager.add(new TraceNameAxisAction(axis_config));
            manager.add(new AxisNameEditAction(axis_config));
            manager.add(new AxisMinMaxEditAction(axis_config));
        }
        else if (inXAxis)
            manager.add(new TimeAxisGridAction(Messages.GridTT, model));
        else {
            manager.add(plot.getPlot().getToolbarAction());
            manager.add(plot.getPlot().getLegendAction());
            manager.add(new Separator());
            manager.add(new AddPVAction(op_manager, shell, model, false));
            manager.add(new AddPVAction(op_manager, shell, model, true));
            final boolean is_rcp = SingleSourcePlugin.getUIHelper().getUI() == UI.RCP;
            if (is_rcp)
            {
                try
                {
                    for (IAction imp : SampleImporters.createImportActions(op_manager, shell, model))
                            manager.add(imp);
                }
                catch (Exception ex)
                {
                    ExceptionDetailsErrorDialog.openError(shell, Messages.Error, ex);
                }
            }
            manager.add(new RemoveUnusedAxesAction(op_manager, model));
            manager.add(new RefreshAction(controller));
            manager.add(new Separator());

            manager.add(new OpenPropertiesAction());
            manager.add(new OpenViewAction(SearchView.ID, Messages.OpenSearchView,
                        activator.getImageDescriptor("icons/search.gif")));
            if (is_rcp)
                manager.add(new OpenViewAction(ExportView.ID, Messages.OpenExportView,
                        activator.getImageDescriptor("icons/export.png")));
            manager.add(new OpenViewAction(SampleView.ID, Messages.InspectSamples,
                    activator.getImageDescriptor("icons/inspect.gif")));

            manager.add(new OpenPerspectiveAction(activator
                    .getImageDescriptor("icons/databrowser.png"),
                    Messages.OpenDataBrowserPerspective, Perspective.ID));
            manager.add(new OpenViewAction(WaveformView.ID,
                    Messages.OpenWaveformView, activator
                            .getImageDescriptor("icons/wavesample.gif")));

            manager.add(new Separator());
            manager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
            if (is_rcp)
            {
                manager.add(new Separator());
                manager.add(plot.getPlot().getSnapshotAction());
                if (EMailSender.isEmailSupported())
                    manager.add(new SendEMailAction(shell, plot.getPlot()));
                manager.add(new PrintAction(shell, plot.getPlot()));
                if (SendToElogAction.isElogAvailable())
                    manager.add(new SendToElogAction(shell, plot.getPlot()));
            }
        }
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
        plot.getPlot().setFocus();
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
    {   // No 'save', never 'dirty' based on model or when running as RAP
        if (!model.shouldSaveChanges()  ||  SingleSourcePlugin.isRAP())
            return;
        is_dirty = dirty;
        firePropertyChange(IEditorPart.PROP_DIRTY);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSaveAsAllowed()
    {
        return ! SingleSourcePlugin.isRAP();
    }

    /** {@inheritDoc} */
    @Override
    public void doSave(final IProgressMonitor monitor)
    {
        try
        {
            final ResourceHelper resources = SingleSourcePlugin.getResourceHelper();
            if (! resources.isWritable(getEditorInput()))
                doSaveAs();
            else
            {
                try
                (
                    final OutputStream stream = resources.getOutputStream(getEditorInput());
                )
                {
                    save(monitor, stream);
                }
            }
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(getSite().getShell(), Messages.Error, ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void doSaveAs()
    {
        final Shell shell = getSite().getShell();
        final ResourceHelper resources = SingleSourcePlugin.getResourceHelper();
        final IPath original = resources.getPath(getEditorInput());
        // Prompt & save until success or cancel
        while (true)
        {
            final IPath file = SingleSourcePlugin.getUIHelper()
                    .openSaveDialog(shell, original, Model.FILE_EXTENSION);
            if (file == null)
                return;
            try
            {
                final PathEditorInput new_input = new PathEditorInput(file);
                try
                (
                    final OutputStream stream = resources.getOutputStream(new_input);
                )
                {
                    save(new NullProgressMonitor(), stream);
                }
                // Set that file as editor's input, so that just 'save' instead of
                // 'save as' is possible from now on
                final DataBrowserModelEditorInput db_input = new DataBrowserModelEditorInput(new_input, model);
                setInput(db_input);
                setPartName(db_input.getName());
                setTitleToolTip(db_input.getToolTipText());
                return;
            }
            catch (Exception ex)
            {
                ExceptionDetailsErrorDialog.openError(getSite().getShell(), Messages.Error, ex);
            }
        }
    }

    /** Save current model content, mark editor as clean.
     *
     *  @param monitor <code>IProgressMonitor</code>, may be null.
     *  @param stream The stream to use.
     *  @return Returns <code>true</code> when successful.
     */
    private void save(final IProgressMonitor monitor, final OutputStream stream) throws Exception
    {
        monitor.beginTask(Messages.Save, IProgressMonitor.UNKNOWN);
        try
        {
            new XMLPersistence().write(model, stream);
            setDirty(false);
            return;
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(getSite().getShell(), Messages.Error, ex);
            // Writing failed, prompt for different name or 'cancel'
            doSaveAs();
        }
        finally
        {
            monitor.done();
        }
    }
}
