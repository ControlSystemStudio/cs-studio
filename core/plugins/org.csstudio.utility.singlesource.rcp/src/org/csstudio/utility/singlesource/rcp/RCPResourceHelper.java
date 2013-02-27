/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.singlesource.rcp;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.utility.singlesource.ResourceHelper;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
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

    /** {@inheritDoc} */
    @SuppressWarnings("rawtypes")
    @Override
    public Object adapt(final IPath path, final Class adapter)
    {
        // For getInputStream() and getOutputStream() to function,
        // path must adapt to IFile.
        if (adapter == IFile.class)
        {
            final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            return root.getFile(path);
        }
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
        if (ws_file != null)
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
        
        // Have workspace file
        if (ws_file.isReadOnly())
            throw new Exception(ws_file.getName() + " is read-only");
        
        // Caller of this method receives an output stream,
        // but IFile doesn't offer an output stream API.
        // -> Create Pipe
        // Caller of this method will write to pipe output
        final PipedOutputStream pipeout = new PipedOutputStream();
        
        // Data written to pipe output is read from pipe input, passed to IFile
        final PipedInputStream pipein = new PipedInputStream(pipeout);
        
        // To avoid deadlock, create thread that handles the IFile
        final Thread writer = new Thread(input.getName() + " Writer")
        {
            @Override
            public void run()
            {
                try
                {
                    if (ws_file.exists())
                        ws_file.setContents(pipein, IResource.FORCE, new NullProgressMonitor());
                    else
                        ws_file.create(pipein, IResource.FORCE, new NullProgressMonitor());
                }
                catch (Exception ex)
                {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error writing " + input.getName(), ex);
                }
            }
        };
        writer.start();
        
        return pipeout;
    }
}
