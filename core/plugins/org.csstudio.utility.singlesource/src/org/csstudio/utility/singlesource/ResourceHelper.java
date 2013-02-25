/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.singlesource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;

/** Helper for accessing resources.
 * 
 *  <p>This implementation provides the common support.
 *  Derived classes can add support that is specific to RCP or RAP.
 *  
 *  <p>Client code should obtain a {@link ResourceHelper} via the {@link SingleSourcePlugin}
 *  
 *  @author Kay Kasemir
 *  @author Xihui Chen, Abadie Lana, Eric Berryman - ResourceUtil of BOY, contributions to PV Table
 */
@SuppressWarnings("nls")
public class ResourceHelper
{
    /** Extension point ID for providing a {@link ResourceHelper} */
    final public static String ID = "org.csstudio.utility.singlesource.resourcehelper";
    
    /** Obtain input stream for editor input
     * 
     *  <p>Depending on the implementation, the path may be
     *  <ul>
     *  <li>Workspace location
     *  <li>Local file
     *  </ul>
     *  
     *  <p>Default implementation is limited to local files.
     *
     *  @param input IEditorInput
     *  @return {@link InputStream} or <code>null</code> if input cannot be resolved
     *  @throws Exception on error
     */
    public InputStream getInputStream(final IEditorInput input) throws Exception
    {
        // Try file outside of the workspace
        final File file = getFilesystemFile(input);
        if (file != null)
            return new FileInputStream(file);
        
        // Didn't find anything. Log adapters to aid in future extension of this code.
        final Logger logger = Logger.getLogger(getClass().getName());
        logger.fine("Cannot read from " + input.getClass().getName());
        for (String adapt : Platform.getAdapterManager().computeAdapterTypes(input.getClass()))
            logger.finer("Would adapt to " + adapt);
        
        return null;
    }
    
    /** Check if editor input is writeable
     * 
     *  <p>Default implementation is limited to local files.
     *
     *  @param input IEditorInput
     *  @return <code>true</code> if input can be written
     *  @throws Exception on error
     */
    public boolean isWritable(final IEditorInput input)
    {
        // Try file outside of the workspace
        final File file = getFilesystemFile(input);
        if (file != null)
        {   // File is either writable, or doesn't exist, yet, so we assume it could be written
            return file.canWrite()  ||  !file.exists();
        }
        
        return false;
    }
    
    /** Obtain output stream for editor input
     * 
     *  <p>Depending on the implementation, the path may be
     *  <ul>
     *  <li>Workspace location
     *  <li>Local file
     *  </ul>
     *  
     *  <p>Default implementation is limited to local files.
     *  
     *  @param input IEditorInput
     *  @return {@link OutputStream} or <code>null</code> if input cannot be resolved
     *  @throws Exception on error
     */
    public OutputStream getOutputStream(final IEditorInput input) throws Exception
    {
        // Try file outside of the workspace
        final File file = getFilesystemFile(input);
        if (file != null)
            return new FileOutputStream(file);
        
        // Didn't find anything. Log adapters to aid in future extension of this code.
        final Logger logger = Logger.getLogger(getClass().getName());
        logger.fine("Cannot write to " + input.getClass().getName());
        for (String adapt : Platform.getAdapterManager().computeAdapterTypes(input.getClass()))
            logger.finer("Would adapt to " + adapt);
        
        return null;
    }
    
    /** Attempt to locate file system file, outside of the workspace 
     *  @param input IEditorInput
     *  @return {@link File} or <code>null</code>
     */
    private File getFilesystemFile(final IEditorInput input)
    {
        final IPathEditorInput patheditor = (IPathEditorInput) input.getAdapter(IPathEditorInput.class);
        if (patheditor != null)
            return patheditor.getPath().toFile();
        return null;
    }
}
