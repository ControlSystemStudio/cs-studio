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

import org.csstudio.scan.client.ScanServerConnector;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.ScanCommandFactory;
import org.csstudio.scan.command.XMLCommandReader;
import org.csstudio.scan.command.XMLCommandWriter;
import org.csstudio.scan.server.ScanServer;
import org.csstudio.scan.ui.scantree.properties.ScanCommandAdapterFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
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
 *  {@link ScanCommandAdapterFactory} then adapts
 *  as necessary to support Properties view/editor.
 *
 *  @author Kay Kasemir
 */
public class ScanEditor extends EditorPart implements ScanTreeGUIListener
{
    /** Editor ID defined in plugin.xml */
    final public static String ID = "org.csstudio.scan.ui.scantree.editor"; //$NON-NLS-1$

    /** File extension used to save files */
    final private static String FILE_EXTENSION = "scn"; //$NON-NLS-1$


    private ScanTreeGUI gui;

    /** @see #isDirty() */
    private boolean is_dirty = false;

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
        gui = new ScanTreeGUI(parent, this);

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
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        gui.setFocus();
    }

    /** Called when a command has been changed to update the display
     *  @param command Command that has been edited
     */
    public void refreshCommand(final ScanCommand command)
    {
        gui.refreshCommand(command);
    }

    /** @param commands Commands to edit */
    public void setCommands(final List<ScanCommand> commands)
    {
        gui.setCommands(commands);
        setDirty(true);
    }

    /** @see ScanTreeGUIListener */
    @Override
    public void scanTreeChanged()
    {
        setDirty(true);
    }

    /** Submit scan in GUI to server */
    public void submitCurrentScan()
    {
        final List<ScanCommand> commands = gui.getCommands();

        String name = getEditorInput().getName();
        final int sep = name.lastIndexOf('.');
        if (sep > 0)
            name = name.substring(0, sep);

        // TODO Use Job to submit scan to server
        try
        {
            final ScanServer server = ScanServerConnector.connect();
            try
            {
                server.submitScan(name, XMLCommandWriter.toXMLString(commands));
            }
            finally
            {
                ScanServerConnector.disconnect(server);
            }
        }
        catch (Exception ex)
        {
            MessageDialog.openError(getSite().getShell(), Messages.Error,
                NLS.bind(Messages.ScanSubmitErrorFmt, ex.getMessage()));
        }
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
}
