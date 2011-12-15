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

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.XMLCommandReader;
import org.csstudio.scan.command.XMLCommandWriter;
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
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
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
 *  TODO Context menu to submit scan
 *  TODO Context menu to load scan from server?
 *  
 *  @author Kay Kasemir
 */
public class ScanEditor extends EditorPart implements ScanTreeGUIListener
{
    /** File extension used to save files */
    final private static String FILE_EXTENSION = "scn"; //$NON-NLS-1$

    private ScanTreeGUI gui;

    /** @see #isDirty() */
    private boolean is_dirty = false;

    /** {@inheritDoc} */
    @Override
    public void init(final IEditorSite site, final IEditorInput input)
            throws PartInitException
    {
        if (! (input instanceof IFileEditorInput))
            throw new PartInitException("Cannot handle input of type " + input.getClass().getName()); //$NON-NLS-1$
        setSite(site);
        setInput(input);
    }

    /** {@inheritDoc} */
    @Override
    public void createPartControl(final Composite parent)
    {
        final IFileEditorInput input = (IFileEditorInput) getEditorInput();
        
        gui = new ScanTreeGUI(parent, this);

        try
        {
            final IFile file = input.getFile();
            final List<ScanCommand> commands = XMLCommandReader.readXMLStream(file.getContents());
            gui.setCommands(commands);
            setPartName(file.getName());
        }
        catch (Exception ex)
        {
            MessageDialog.openError(parent.getShell(), Messages.Error,
                    NLS.bind(Messages.FileOpenErrorFmt,
                            new Object[] { input.getName(), ex.getMessage() }));
        }
        
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
    
    /** @see ScanTreeGUIListener */
    @Override
    public void scanTreeChanged()
    {
        setDirty(true);
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
        final IFileEditorInput input = (IFileEditorInput) getEditorInput();
        saveToFile(monitor, input.getFile());
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
