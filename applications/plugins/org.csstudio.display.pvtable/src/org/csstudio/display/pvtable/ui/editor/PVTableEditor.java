/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui.editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.logging.Level;

import org.csstudio.display.pvtable.Plugin;
import org.csstudio.display.pvtable.model.PVTableItem;
import org.csstudio.display.pvtable.model.PVTableModel;
import org.csstudio.display.pvtable.model.PVTableModelListener;
import org.csstudio.display.pvtable.ui.PVTable;
import org.csstudio.display.pvtable.xml.PVTableXMLPersistence;
import org.csstudio.ui.util.NoResourceEditorInput;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
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

/** EditorPart for the PV Table
 *  @author Kay Kasemir
 */
public class PVTableEditor extends EditorPart
{
    public static final String ID = PVTableEditor.class.getName();

    private static final String FILE_EXTENSION = "pvs"; //$NON-NLS-1$

    private PVTableModel model;
    private PVTable gui;
    private boolean is_dirty;

    /** Create a new, empty editor, not attached to a file.
     *  @return Returns the new editor or <code>null</code>.
     */
    public static PVTableEditor createPVTableEditor()
    {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        final IWorkbenchPage page = window.getActivePage();
        try
        {
            final EmptyEditorInput input = new EmptyEditorInput();
            return (PVTableEditor) page.openEditor(input, PVTableEditor.ID);
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(page.getActivePart().getSite().getShell(), "Cannot create PV Table", ex); //$NON-NLS-1$
        }
        return null;
    }

    public PVTableEditor()
    {
        super();
        is_dirty = false;
    }

    @Override
    public void init(final IEditorSite site, final IEditorInput input)
            throws PartInitException
    {
        // "Site is incorrect" error results if the site is not set:
        setSite(site);
        setInput(new NoResourceEditorInput(input));
        final IFile file = getEditorInputFile();
        if (file != null)
        {
            try
            {
                final InputStream stream = file.getContents();
                model = PVTableXMLPersistence.read(stream);
            }
            catch (Exception e)
            {
                throw new PartInitException("Load error", e); //$NON-NLS-1$
            }
        }
        else // Empty model
            model = new PVTableModel();

        model.addListener(new PVTableModelListener()
        {
            @Override
            public void tableItemChanged(final PVTableItem item)
            {
                // Ignore
            }
            
            @Override
            public void tableItemsChanged()
            {
                // Ignore
            }
            
            @Override
            public void modelChanged()
            {
                if (!is_dirty)
                {
                    is_dirty = true;
                    firePropertyChange(IEditorPart.PROP_DIRTY);
                }
                updateTitle();
            }
        });
    }

    @Override
    public void createPartControl(final Composite parent)
    {
        gui = new PVTable(parent, getSite());
        gui.setModel(model);
        updateTitle();
    }

    @Override
    public void setFocus()
    {
        gui.getTableViewer().getTable().setFocus();
    }

    /** @return Table model */
    public PVTableModel getModel()
    {
        return model;
    }
    
    /** @return Table viewer */
    public TableViewer getTableViewer()
    {
        return gui.getTableViewer();
    }

    /** @return Returns the <code>IFile</code> for the current editor input.
     *  The file is 'relative' to the workspace, not 'absolute' in the
     *  file system. However, the file might be a linked resource to a
     *  file that physically resides outside of the workspace tree.
     */
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
        return (IFile) input.getAdapter(IFile.class);
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
    private boolean saveToFile(final IProgressMonitor monitor, final IFile file)
    {
        boolean ok = true;
        if (monitor != null)
            monitor.beginTask("Save", IProgressMonitor.UNKNOWN); //$NON-NLS-1$
        
        // Write model buffer, then stream from there into file...
        final PVTableModel model = gui.getModel();
        final String xml;
        {
            final ByteArrayOutputStream buf = new ByteArrayOutputStream();
            PVTableXMLPersistence.write(model, buf);
            xml = buf.toString();
        }
        try
        {
            final ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes());
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
            Plugin.getLogger().log(Level.SEVERE, "Save error", e); //$NON-NLS-1$
        }
        return ok;
    }

    @Override
    public void doSaveAs()
    {
        // Prompt for file name
        final SaveAsDialog dlg = new SaveAsDialog(getEditorSite().getShell());
        dlg.setBlockOnOpen(true);
        dlg.setOriginalFile(getEditorInputFile());
        if (dlg.open() != Window.OK)
            return;
        IPath path = dlg.getResult();
        if (path == null)
            return;
        
        // Assert file extension
        if (! FILE_EXTENSION.equals(path.getFileExtension()))
            path = path.removeFileExtension().addFileExtension(FILE_EXTENSION);
        
        // Get file for the new resource's path.
        final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        final IFile file = root.getFile(path);

        if (!saveToFile(null, file))
            return;
        // Update input and title
        setInput(new FileEditorInput(file));
        updateTitle();
    }

    @Override
    public boolean isDirty()
    {   
        return is_dirty;  
    }

    @Override
    public boolean isSaveAsAllowed()
    {  
        return true; 
    }


    /** Set the editor part's title and tool-tip. */
    private void updateTitle()
    {
        final IEditorInput input = getEditorInput();
        setPartName(input.getName());
        setTitleToolTip(input.getToolTipText());
    }
}
