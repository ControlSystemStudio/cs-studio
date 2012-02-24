/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.client.ScanInfoModel;
import org.csstudio.scan.client.ScanInfoModelListener;
import org.csstudio.scan.command.CommandSequence;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.ScanCommandFactory;
import org.csstudio.scan.command.XMLCommandReader;
import org.csstudio.scan.command.XMLCommandWriter;
import org.csstudio.scan.device.DeviceInfo;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanServer;
import org.csstudio.scan.server.ScanState;
import org.csstudio.scan.ui.ScanUIActivator;
import org.csstudio.scan.ui.scantree.operations.RedoHandler;
import org.csstudio.scan.ui.scantree.operations.UndoHandler;
import org.csstudio.scan.ui.scantree.properties.ScanCommandPropertyAdapterFactory;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

/** Eclipse Editor for the Scan Tree
 *
 *  <p>Displays the scan tree and uses
 *  it as selection provider.
 *  {@link ScanCommandPropertyAdapterFactory} then adapts
 *  as necessary to support Properties view/editor.
 *
 *  @author Kay Kasemir
 */
public class ScanEditor extends EditorPart implements ScanInfoModelListener
{
    /** Editor ID defined in plugin.xml */
    final public static String ID = "org.csstudio.scan.ui.scantree.editor"; //$NON-NLS-1$

    /** File extension used to save files */
    final private static String FILE_EXTENSION = "scn"; //$NON-NLS-1$

    /** Info about scan server */
    private ScanInfoModel scan_info = null;

    /** Operations history for undo/redo.
     *
     *  <p>All scan editors share the same operations history,
     *  but each editor has its own undo context.
     *
     *  <p>This was done because the {@link UndoHandler} and {@link RedoHandler}
     *  in the editor's toolbar are shared between all scan editors,
     *  so it's natural for them to interface to just one operations history.
     */
    final private static IOperationHistory operations = OperationHistoryFactory.getOperationHistory();

    /** Undo context for undo/redo */
    final private IUndoContext undo_context;

    /** Info section, not always shown */
    private Composite info_section;

    /** Message within info section */
    private Label message;

    /** Tree GUI */
    private ScanTreeGUI gui;

    /** @see #isDirty() */
    private boolean is_dirty = false;

    /** @return Devices available on scan server */
    private volatile DeviceInfo[] devices = null;

    /** ID of scan that was submitted, the 'live' scan, or -1 */
    private volatile long scan_id = -1;

    private Button pause;

    private Button resume;

    private Button abort;

    /** Create scan editor
     *  @param input Input for editor, must be scan config file or {@link EmptyEditorInput}
     *  @return ScanEditor or <code>null</code> on error
     */
    private static ScanEditor createInstance(final IEditorInput input)
    {
        final ScanEditor editor;
        try
        {
            final IWorkbench workbench = PlatformUI.getWorkbench();
            final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            final IWorkbenchPage page = window.getActivePage();
            editor = (ScanEditor) page.openEditor(input, ID);
        }
        catch (Exception ex)
        {
            Logger.getLogger(ScanEditor.class.getName())
                .log(Level.WARNING, "Cannot create ScanEditor", ex); //$NON-NLS-1$
            return null;
        }
        return editor;
    }

    /** Create scan editor with empty configuration
     *  @return ScanEditor or <code>null</code> on error
     */
    public static ScanEditor createInstance()
    {
        return createInstance(new EmptyEditorInput());
    }

    /** Create scan editor
     *  @param scan_id ID of the scan on server
     *  @param commands Commands of the scan
     *  @return
     */
    public static ScanEditor createInstance(final ScanInfo scan, final List<ScanCommand> commands)
    {
        final ScanEditor editor = createInstance(new EmptyEditorInput());
        editor.setCommands(commands);
        if (! scan.getState().isDone())
            editor.scan_id = scan.getId();
        return editor;
    }

    /** Initialize */
    public ScanEditor()
    {
        undo_context = new UndoContext();
        operations.setLimit(undo_context, 50);
    }

    /** {@inheritDoc} */
    @Override
    public void init(final IEditorSite site, final IEditorInput input)
            throws PartInitException
    {
        setSite(site);
        setInput(input);
    }

    /** {@inheritDoc} */
    @Override
    public void createPartControl(final Composite parent)
    {
        // Get scan info model and return if that fails
        try
        {
            scan_info = ScanInfoModel.getInstance();
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(parent.getShell(), Messages.Error, ex);
            return;
        }

        fetchDevices();
        createComponents(parent);

        final IEditorInput input = getEditorInput();
        final IFile file = (IFile) input.getAdapter(IFile.class);
        if (file != null)
        {
            try
            {
                final XMLCommandReader reader = new XMLCommandReader(new ScanCommandFactory());
                final List<ScanCommand> commands = reader.readXMLStream(file.getContents());
                gui.setCommands(commands);
            }
            catch (Exception ex)
            {
                MessageDialog.openError(parent.getShell(), Messages.Error,
                        NLS.bind(Messages.FileOpenErrorFmt,
                                new Object[] { input.getName(), ex.getMessage() }));
            }
        }
        setPartName(input.getName());
        getSite().setSelectionProvider(gui.getSelectionProvider());

        scan_info.addListener(this);
    }

    /** Read device list from server */
    private void fetchDevices()
    {
        final Job job = new Job(Messages.DeviceListFetch)
        {
            @Override
            protected IStatus run(IProgressMonitor monitor)
            {
                try
                {
                    final ScanServer server = scan_info.getServer();
                    devices = server.getDeviceInfos(-1);
                }
                catch (Exception ex)
                {
                    return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                            Messages.DeviceListFetchError, ex);
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    /** Create GUI components
     *  @param parent Parent widget
     */
    private void createComponents(final Composite parent)
    {
        parent.setLayout(new FormLayout());

        // 1) Info section
        info_section = new Composite(parent, 0);
        info_section.setLayout(new GridLayout(4, false));

        message = new Label(info_section, 0);
        message.setText(Messages.ServerDisconnected);
        message.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        resume = createInfoButton(Messages.ResumeTT, "icons/resume.gif", //$NON-NLS-1$
                new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                try
                {
                    scan_info.getServer().resume(scan_id);
                    resume.setEnabled(false);
                    pause.setEnabled(true);
                }
                catch (Exception ex)
                {
                    ExceptionDetailsErrorDialog.openError(parent.getShell(), Messages.Error, ex);
                }
            }
        });
        pause = createInfoButton(Messages.PauseTT, "icons/pause.gif", //$NON-NLS-1$
                new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                try
                {
                    scan_info.getServer().pause(scan_id);
                    resume.setEnabled(true);
                    pause.setEnabled(false);
                }
                catch (Exception ex)
                {
                    ExceptionDetailsErrorDialog.openError(parent.getShell(), Messages.Error, ex);
                }
            }
        });
        abort = createInfoButton(Messages.AbortTT, "icons/abort.gif", //$NON-NLS-1$
                new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                try
                {
                    scan_info.getServer().abort(scan_id);
                }
                catch (Exception ex)
                {
                    ExceptionDetailsErrorDialog.openError(parent.getShell(), Messages.Error, ex);
                }
            }
        });

        // Initially, info section is invisible
        info_section.setVisible(false);
        FormData fd = new FormData();
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        fd.top = new FormAttachment(0);
        info_section.setLayoutData(fd);

        // 2) Scan Tree
        gui = new ScanTreeGUI(parent, this);

        fd = new FormData();
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        fd.top = new FormAttachment(0);
        fd.bottom = new FormAttachment(100);
        gui.getControl().setLayoutData(fd);
    }

    /** Add button to the info_section
     *  @param tooltip Tool tip
     *  @param icon Icon path (in scan.ui plugin)
     *  @param listener Selection listener
     *  @return Button
     */
    private Button createInfoButton(final String tooltip, final String icon,
            final SelectionListener listener)
    {
        final Button button = new Button(info_section, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.RIGHT, 0, false, false));
        button.setToolTipText(tooltip);
        button.setImage(ScanUIActivator.getImageDescriptor(icon).createImage());
        button.addSelectionListener(listener);
        return button;
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        if (scan_info != null)
        {
            scan_info.removeListener(this);
            scan_info.release();
            scan_info = null;
        }
        // Remove undo/redo operations associated with this editor
        // from the shared operations history of all scan editors
        operations.dispose(undo_context, true, true, true);

        if (resume != null)
            resume.getImage().dispose();
        if (pause != null)
            pause.getImage().dispose();
        if (abort != null)
            abort.getImage().dispose();

        super.dispose();
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        gui.setFocus();
    }

    /** Show or hide the info section
     *  @param show <code>true</code> to show
     */
    private void showInfoSection(final boolean show)
    {
        final FormData fd = (FormData) gui.getControl().getLayoutData();
        if (show)
        {
            if (info_section.isVisible())
                return;
            fd.top.control = info_section;
            info_section.setVisible(true);
        }
        else
        {
            if (! info_section.isVisible())
                return;
            fd.top.control = null;
            info_section.setVisible(false);
        }
        info_section.getParent().layout();
    }

    /** @param text Message to show or null to hide */
    private void setMessage(final String text)
    {
        if (message.isDisposed())
            return;
        message.getDisplay().asyncExec(new Runnable()
        {
            @Override
            public void run()
            {
                if (message.isDisposed())
                    return;
                if (text == null)
                    showInfoSection(false);
                else
                {
                    message.setText(text);
                    showInfoSection(true);
                }
            }
        });
    }

    private void updateButtons(final ScanState state)
    {
        if (resume.isDisposed())
            return;
        resume.getDisplay().asyncExec(new Runnable()
        {
            @Override
            public void run()
            {
                if (resume.isDisposed())
                    return;
                resume.setEnabled(state == ScanState.Paused);
                pause.setEnabled(state == ScanState.Running);
                abort.setEnabled(state == ScanState.Idle  ||  state.isActive());
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void scanUpdate(final List<ScanInfo> infos)
    {
        final long this_id = scan_id;
        if (this_id < 0)
        {   // Nothing to show, scan in editor is not 'live'
            gui.setActiveCommand(-1);
            setMessage(null);
            return;
        }

        // Get info for this scan
        ScanInfo this_scan = null;
        for (ScanInfo info : infos)
        {
            if (info.getId() == this_id)
            {
                this_scan = info;
                break;
            }
        }

        if (this_scan == null)
        {   // No info about this scan on server: Done & deleted?
            gui.setActiveCommand(-1);
            setMessage(NLS.bind(Messages.ScanSubmittedButNotRunningFmt, this_id));
            return;
        }

        // Update status of this scan in editor
        final long address = this_scan.getCurrentAddress();
        gui.setActiveCommand(address);
        setMessage(this_scan.toString());
        updateButtons(this_scan.getState());
    }

    /** {@inheritDoc} */
    @Override
    public void connectionError()
    {
        gui.setActiveCommand(-1);
        setMessage(Messages.ServerDisconnected);
    }

    /** Called when a command has been changed to update the display
     *  @param command Command that has been edited
     */
    public void refreshCommand(final ScanCommand command)
    {
        gui.refreshCommand(command);
    }

    /** @return Devices available on scan server. May be <code>null</code> */
    public DeviceInfo[] getDevices()
    {
        return devices;
    }

    /** @param commands Commands to edit */
    public void setCommands(final List<ScanCommand> commands)
    {
        gui.setCommands(commands);
        setDirty(true);
    }

    /** @return Commands displayed/edited in GUI */
    public List<ScanCommand> getCommands()
    {
        return gui.getCommands();
    }

    /** @return Currently selected scan commands or <code>null</code> */
    public List<ScanCommand> getSelectedCommands()
    {
        return gui.getSelectedCommands();
    }

    /** @return Operation history (for undo/redo) */
    public static IOperationHistory getOperationHistory()
    {
        return operations;
    }

    /** Execute an undo-able operation and add to history
     *  @param operation Operation to add to the undo/redo history
     *  @throws ExecutionException on error
     */
    public void executeForUndo(final IUndoableOperation operation) throws ExecutionException
    {
        // Prompt when in 'live' mode
        if (scan_id >= 0)
        {
            if (! MessageDialog.openConfirm(info_section.getShell(),
                    Messages.EndLiveMode, Messages.EndLiveModePrompt))
                return;
            // End live mode
            scan_id = -1;
            showInfoSection(false);
        }
        operation.execute(new NullProgressMonitor(), null);
        operation.addContext(undo_context);
        operations.add(operation);
    }

    /** Submit scan in GUI to server */
    public void submitCurrentScan()
    {
        final List<ScanCommand> commands = gui.getCommands();
        CommandSequence.setAddresses(commands);

        String name = getEditorInput().getName();
        final int sep = name.lastIndexOf('.');
        if (sep > 0)
            name = name.substring(0, sep);

        final String scan_name = name;

        // Use background Job to submit scan to server
        final Job job = new Job(Messages.SubmitScan)
        {
            @Override
            protected IStatus run(IProgressMonitor monitor)
            {
                try
                {
                    final ScanServer server = scan_info.getServer();
                    scan_id = server.submitScan(scan_name, XMLCommandWriter.toXMLString(commands));
                }
                catch (Exception ex)
                {
                    scan_id = -1;
                    return new Status(IStatus.ERROR,
                            Activator.PLUGIN_ID,
                            NLS.bind(Messages.ScanSubmitErrorFmt, ex.getMessage()),
                            ex);
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSaveAsAllowed()
    {
        return true;
    }

    /** Save current editor content to file
     *  @param monitor
     *  @param file
     *  @return <code>true</code> on success
     */
    private boolean saveToFile(final IProgressMonitor monitor, final IFile file)
    {
        try
        {
            // Write commands as XML to buffer
            final ByteArrayOutputStream buf = new ByteArrayOutputStream();
            XMLCommandWriter.write(buf, gui.getCommands());
            buf.close();

            // Write the buffer to file
            final ByteArrayInputStream stream = new ByteArrayInputStream(buf.toByteArray());
            if (file.exists())
                file.setContents(stream, IFile.FORCE, monitor);
            else
                file.create(stream, true, monitor);
            setDirty(false);
            return true;
        }
        catch (Exception ex)
        {
            MessageDialog.openError(getSite().getShell(), Messages.Error,
                    NLS.bind(Messages.FileSaveErrorFmt,
                            new Object[] { file.getName(), ex.getMessage() }));
            return false;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void doSave(final IProgressMonitor monitor)
    {
        final IEditorInput input = getEditorInput();
        final IFile file = (IFile) input.getAdapter(IFile.class);
        if (file == null)
            doSaveAs();
        else // Input is EmptyEditorInput, no file, yet
            saveToFile(monitor, file);
    }

    /** {@inheritDoc} */
    @Override
    public void doSaveAs()
    {
        final IFile file = promptForFile(null);
        if (file == null)
            return;
        if (saveToFile(new NullProgressMonitor(), file))
        {
            setInput(new FileEditorInput(file));
            setPartName(file.getName());
        }
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
        if (dlg.open() != Window.OK)
            return null;

        // The path to the new resource relative to the workspace
        IPath path = dlg.getResult();
        if (path == null)
            return null;
        // Assert it's a '.scn' file
        final String ext = path.getFileExtension();
        if (ext == null  ||  !ext.equals(FILE_EXTENSION))
            path = path.removeFileExtension().addFileExtension(FILE_EXTENSION);
        // Get the file for the new resource's path.
        final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        return root.getFile(path);
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

    /** Refresh the GUI after tree manipulations */
    public void refresh()
    {
        gui.refresh();
        setDirty(true);
    }
}
