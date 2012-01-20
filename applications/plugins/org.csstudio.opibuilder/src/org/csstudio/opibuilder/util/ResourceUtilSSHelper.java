/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.util;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;

/**ResourceUtil Single Source helper. The IMPL should not be null.
 * @author Xihui Chen
 */
public abstract class ResourceUtilSSHelper {
	
	/**
	 * Return the {@link InputStream} of the file that is available on the
	 * specified path.
	 *
	 * @param path
	 *            The {@link IPath} to the file in the workspace, the local
	 *            file system, or a URL (http:, https:, ftp:, file:, platform:)
	 * @param runInUIJob
	 * 				true if the task should run in UIJob, which will block UI responsiveness with a progress bar
	 * on status line. Caller must be in UI thread if this is true.
	 * @return The corresponding {@link InputStream}. Never <code>null</code>
	 * @throws Exception
	 */
    public abstract InputStream pathToInputStream(final IPath path, boolean runInUIJob) throws Exception;
	
		
	/**
	 * Returns a stream which can be used to read this editors input data.
	 * @param editorInput
	 *
	 * @return a stream which can be used to read this editors input data
	 */
	public abstract InputStream getInputStreamFromEditorInput(IEditorInput editorInput);

	/**
	 * @param path the file path
	 * @return true if the file path is an existing workspace file.
	 */
	public abstract boolean isExistingWorkspaceFile(IPath path);
	

	/**
	 * @return
	 * @throws FileNotFoundException
	 */
	public abstract IPath getPathInEditor(IEditorInput input);

	
	/**Convert workspace path to OS system path.
	 * @param path the workspace path
	 * @return the corresponding system path. null if it is not exist.
	 */
	public abstract IPath workspacePathToSysPath(IPath path);


	public abstract Image getScreenShotImage(GraphicalViewer viewer);



}
