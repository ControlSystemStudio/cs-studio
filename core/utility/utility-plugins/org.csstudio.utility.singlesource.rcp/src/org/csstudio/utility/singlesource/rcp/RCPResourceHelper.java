/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.singlesource.rcp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.csstudio.utility.singlesource.ResourceHelper;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;

/** Resource Helper for RCP
 *
 *  <p>Adds workspace file support to the basic {@link ResourceHelper}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RCPResourceHelper extends ResourceHelper
{
    /** {@inheritDoc} */
    @Override
    public IPath getPath(final IEditorInput input)
    {
        final IFile ws_file = (IFile) input.getAdapter(IFile.class);
        if (ws_file != null)
            return ws_file.getFullPath();
        return super.getPath(input);
    }

    /** {@inheritDoc} */
    @Override
    public boolean exists(final IPath path)
    {
        // Try workspace file
        final IResource resource =
            ResourcesPlugin.getWorkspace().getRoot().findMember(path);
        if (resource != null  &&
            resource.isAccessible() &&
            resource instanceof IFile)
            return true;

        return super.exists(path);
    }

    /** Obtain file for path within workspace
     *  @param path Path to a resource in the workspace
     *  @return IFile for path or <code>null</code>
     */
    static IFile getFileForPath(final IPath path)
    {
        if (path == null)
            return null;
        final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        return root.getFile(path);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("rawtypes")
    @Override
    public Object adapt(final IPath path, final Class adapter)
    {
        // For getInputStream() and getOutputStream() to function,
        // path must adapt to IFile.
        if (adapter == IFile.class)
            return getFileForPath(path);
        return super.adapt(path, adapter);
    }

    /** {@inheritDoc} */
    @Override
    public InputStream getInputStream(final IPath path) throws Exception
    {
        // Try workspace file
        final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        final IResource ws_file = root.findMember(path);
        if (ws_file instanceof IFile)
            return ((IFile)ws_file).getContents(true);

        return super.getInputStream(path);
    }

    /** {@inheritDoc} */
    @Override
    public InputStream getInputStream(final IEditorInput input) throws Exception
    {
        // Try workspace file
        final IFile ws_file = (IFile) input.getAdapter(IFile.class);
        if (ws_file != null  &&  ws_file.exists())
            return ws_file.getContents(true);

        return super.getInputStream(input);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isWritable(final IEditorInput input)
    {
        // Try workspace file
        final IFile ws_file = (IFile) input.getAdapter(IFile.class);
        if (ws_file != null)
            return !ws_file.isReadOnly();

        // Fall back to non-workspace implementation
        return super.isWritable(input);
    }

    /** {@inheritDoc} */
    @Override
    public OutputStream getOutputStream(final IEditorInput input) throws Exception
    {
        // Try workspace file
        final IFile ws_file = (IFile) input.getAdapter(IFile.class);
        // Fall back to non-workspace implementation
        if (ws_file == null)
            return super.getOutputStream(input);

        // Determine the path to the file.
        IPath p = getPath(input);
        // back to non-workspace implementation if null
        if (p == null)
          return super.getOutputStream(input);

        // If workspace root does not contain the path (i.e. the file is outside
        // of the workspace) but the file does actually exist then revert to the
        // super non-workspace implementation.
        // Otherwise if the file path is within the workspace (findMember = true)
        // and/or it is a new file (does not exist yet) then continue in this
        // implementation.
        final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        if (root.findMember(p) == null && super.exists(p)) {
          return super.getOutputStream(input);
        }

        // Have workspace file
        // Check write access.
        if (ws_file.isReadOnly())
            throw new Exception("File " + ws_file.getName() + " is read-only");
        // isReadOnly() only tests the file itself,
        // but parent directory can still prohibit writing.
        // --> Test by actually writing
        final InputStream dummy_data = new ByteArrayInputStream(new byte[0]);
        if (ws_file.exists())
            ws_file.setContents(dummy_data, IResource.FORCE, new NullProgressMonitor());
        else
            ws_file.create(dummy_data, IResource.FORCE, new NullProgressMonitor());

        // Caller of this method receives an output stream,
        // but IFile doesn't offer an output stream API.
        // -> Create Pipe
        // Caller of this method will write to pipe output
        final PipedOutputStream pipeout = new PipedOutputStream();

        // Data written to pipe output is read from pipe input, passed to IFile
        final PipedInputStream pipein = new PipedInputStream(pipeout);

        // To avoid deadlock, create thread that handles the IFile
        final Job writer = Job.create("Write " + input.getName(),
            (final IProgressMonitor monitor) ->
            {
                try
                {
                    ws_file.setContents(pipein, IResource.FORCE, monitor);
                }
                catch (Exception ex)
                {   // Cannot directly notify the code which uses `pipeout`,
                    // but closing pipes so writing code gets Exception when
                    // it tries to write more.
                    try
                    {
                        pipeout.close();
                        pipein.close();
                    }
                    catch (Throwable ignored) {}
                    // Notify user
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error writing " + input.getName(), ex);
                    Display.getDefault().asyncExec(() ->
                    {
                        ExceptionDetailsErrorDialog.openError(null, "Error Writing File", ex);
                    });
                }
                return Status.OK_STATUS;
            });
        writer.schedule();

        return pipeout;
    }
}
