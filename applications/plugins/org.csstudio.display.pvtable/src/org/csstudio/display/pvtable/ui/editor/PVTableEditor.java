/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui.editor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Level;

import org.csstudio.display.pvtable.Messages;
import org.csstudio.display.pvtable.Plugin;
import org.csstudio.display.pvtable.model.AbstractPVListModelListener;
import org.csstudio.display.pvtable.model.PVListEntry;
import org.csstudio.display.pvtable.model.PVListModel;
import org.csstudio.display.pvtable.model.PVListModelListener;
import org.csstudio.display.pvtable.ui.PVTableViewerHelper;
import org.csstudio.util.editor.EmptyEditorInput;
import org.csstudio.util.editor.PromptForNewXMLFileDialog;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.ui.part.FileEditorInput;

/** EditorPart for the PV Table
 *  @author Kay Kasemir
 */
public class PVTableEditor extends EditorPart
{
    public static final String ID = PVTableEditor.class.getName();

    // The model, data, PV list.
    private PVListModel model;
    private PVListModelListener listener;
    private PVTableViewerHelper helper;
    private boolean is_dirty;

    /** Create a new, empty editor, not attached to a file.
     *  @return Returns the new editor or <code>null</code>.
     */
    public static PVTableEditor createPVTableEditor()
    {
	    try
	    {
	        IWorkbench workbench = PlatformUI.getWorkbench();
	        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
	        IWorkbenchPage page = window.getActivePage();

	        EmptyEditorInput input = new EmptyEditorInput();
	        return (PVTableEditor) page.openEditor(input, PVTableEditor.ID);
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	    }
	    return null;
    }

    public PVTableEditor()
    {
        super();
        is_dirty = false;
    }

    /** @return Returns the model edited by this editor. */
    public PVListModel getModel()
    {
        return model;
    }

    @Override
    public void init(IEditorSite site, IEditorInput input)
            throws PartInitException
    {
        // "Site is incorrect" error results if the site is not set:
        setSite(site);
        setInput(input);
        model = new PVListModel();
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

        // React to model changes via 'dirty' flag
        listener = new AbstractPVListModelListener()
        {
            @Override
            public void entriesChanged()
            {
                if (!is_dirty)
                {
                    is_dirty = true;
                    firePropertyChange(IEditorPart.PROP_DIRTY);
                }
                updateTitle();
            }

            @Override
            public void entryAdded(PVListEntry entry)
            {   entriesChanged(); }

            @Override
            public void entryRemoved(PVListEntry entry)
            {   entriesChanged(); }
        };
        model.addModelListener(listener);
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
        IFile file = (IFile) input.getAdapter(IFile.class);
        if (file != null)
            return file;
        Plugin.getLogger().log(Level.SEVERE, "getEditorInputFile got {0}",  //$NON-NLS-1$
                        input.getClass().getName());
        return null;
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
            monitor.beginTask(Messages.Editor_SaveTask, IProgressMonitor.UNKNOWN);
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
            Plugin.getLogger().log(Level.SEVERE, "Save error", e); //$NON-NLS-1$
        }
        finally
        {
            try
            {   stream.close(); }
            catch (Exception e)
            { /* NOP */ }
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
        helper = new PVTableViewerHelper(getSite(),parent, model);
        updateTitle();
    }

    /** @see org.eclipse.ui.part.WorkbenchPart#dispose() */
    @Override
    public void dispose()
    {
        helper.dispose();
        model.removeModelListener(listener);
        model.dispose();
        super.dispose();
    }

    /** Set the editor part's title and tool-tip. */
    private void updateTitle()
    {   // See plugin book p.332.
        IEditorInput input = getEditorInput();
        String title = getEditorInput().getName();
        if (model.getDescription().length() > 0)
            title = title + " - " + model.getDescription(); //$NON-NLS-1$
        setPartName(title);
        setTitleToolTip(input.getToolTipText());
    }

    @Override
    public void setFocus()
    {
        helper.getTableViewer().getTable().setFocus();
    }
}
