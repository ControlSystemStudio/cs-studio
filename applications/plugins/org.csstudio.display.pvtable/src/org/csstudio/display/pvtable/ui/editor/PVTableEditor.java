/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui.editor;

import java.io.InputStream;
import java.io.OutputStream;

import org.csstudio.display.pvtable.Messages;
import org.csstudio.display.pvtable.Plugin;
import org.csstudio.display.pvtable.model.PVTableItem;
import org.csstudio.display.pvtable.model.PVTableModel;
import org.csstudio.display.pvtable.model.PVTableModelListener;
import org.csstudio.display.pvtable.persistence.PVTablePersistence;
import org.csstudio.display.pvtable.ui.PVTable;
import org.csstudio.ui.util.EmptyEditorInput;
import org.csstudio.ui.util.NoResourceEditorInput;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.csstudio.utility.singlesource.PathEditorInput;
import org.csstudio.utility.singlesource.ResourceHelper;
import org.csstudio.utility.singlesource.SingleSourcePlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

/** EditorPart for the PV Table
 *  @author Kay Kasemir
 *  @author Eric Berryman - File system support
 */
public class PVTableEditor extends EditorPart
{
    public static final String ID = PVTableEditor.class.getName();

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
            final EmptyEditorInput input = new EmptyEditorInput(Plugin.getImageDescriptor("icons/pvtable.png")); //$NON-NLS-1$
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
        
        try
        {
            final InputStream stream = 
                SingleSourcePlugin.getResourceHelper().getInputStream(input);
            if (stream != null)
            {
                final PVTablePersistence persistence =
                    PVTablePersistence.forFilename(input.getName());
                model = persistence.read(stream);
            }
            else // Empty model
                model = new PVTableModel();
        }
        catch (Exception e)
        {
            throw new PartInitException("Workspace file load error", e); //$NON-NLS-1$
        }

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

    @Override
    public void doSave(final IProgressMonitor monitor)
    {
        final IEditorInput input = getEditorInput();
        final ResourceHelper resources = SingleSourcePlugin.getResourceHelper();
        try
        {
            if (resources.isWritable(input))
            {
                final PVTablePersistence persistence = PVTablePersistence.forFilename(input.getName());
                saveToStream(monitor, persistence, resources.getOutputStream(input));
            }
            else
                doSaveAs();
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(getSite().getShell(), Messages.Error, ex);
        }
    }

    /** Save current model, mark editor as clean.
     *
     *  @param monitor <code>IProgressMonitor</code>, may be <code>null</code>.
     *  @param stream Output stream
     */
    private void saveToStream(final IProgressMonitor monitor, final PVTablePersistence persistence, final OutputStream stream)
    {
        if (monitor != null)
            monitor.beginTask("Save", IProgressMonitor.UNKNOWN); //$NON-NLS-1$
        try
        {
            persistence.write(model, stream);
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(getSite().getShell(), Messages.Error, ex);
        }        
        if (monitor != null)
            monitor.done();
        // Mark as clean
        is_dirty = false;
        firePropertyChange(IEditorPart.PROP_DIRTY);
    }

    @Override
    public void doSaveAs()
    {
        final ResourceHelper resources = SingleSourcePlugin.getResourceHelper();
        
        // If there is an original file name, try to display it
        final IPath original = resources.getPath(getEditorInput());
        final IPath path = SingleSourcePlugin.getUIHelper()
            .openSaveDialog(getEditorSite().getShell(), original, null);
        if (path == null)
            return;
        
        // Get file for the new resource's path.
        final IEditorInput new_input = new PathEditorInput(path);
        try
        {
            final OutputStream stream = resources.getOutputStream(new_input);
            final PVTablePersistence persistence = PVTablePersistence.forFilename(path.getFileExtension());
            saveToStream(null, persistence, stream);
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(getSite().getShell(), Messages.Error, ex);
        }
        // Update input and title
        setInput(new_input);
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
