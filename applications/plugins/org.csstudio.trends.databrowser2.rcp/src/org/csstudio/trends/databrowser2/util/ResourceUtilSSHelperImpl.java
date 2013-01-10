/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * @author Davy Dequidt <davy.dequidt@iter.org>
 * 
 */
public class ResourceUtilSSHelperImpl extends ResourceUtilSSHelper {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.csstudio.opibuilder.util.ResourceUtilSSHelper#pathToInputStream(org
	 * .eclipse.core.runtime.IPath, boolean)
	 */
	@Override
	public InputStream pathToInputStream(IPath path, boolean runInUIJob)
			throws Exception {

		// Try workspace location
		final IFile workspace_file = getIFileFromIPath(path);
		// Valid file should either open, or give meaningful exception
		if (workspace_file != null && workspace_file.exists())
			return workspace_file.getContents();

		// Not a workspace file. Try local file system
		File local_file = path.toFile();
		// Path URL for "file:..." so that it opens as FileInputStream
		if (local_file.getPath().startsWith("file:"))
			local_file = new File(local_file.getPath().substring(5));
		String urlString;
		try {
			return new FileInputStream(local_file);
		} 
        catch (Exception ex)
        {
            // Could not open as local file.
            // Does it look like a URL?
            //TODO:
         	// Eclipse Path collapses "//" into "/", revert that: Is this true? Need test on Mac.        	
             urlString = path.toString();
//             if(!urlString.startsWith("platform") && !urlString.contains("://")) //$NON-NLS-1$ //$NON-NLS-2$
//                 urlString = urlString.replaceFirst(":/", "://"); //$NON-NLS-1$ //$NON-NLS-2$
        // Does it now look like a URL? If not, report the original local file problem
            if (! ResourceUtil.isURL(urlString))
                throw new Exception("Cannot open " + ex.getMessage(), ex);
        }


		// Must be an URL
		final URL url = new URL(urlString);

		return ResourceUtil.openURLStream(url, runInUIJob);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.csstudio.opibuilder.util.ResourceUtilSSHelper#isExistingWorkspaceFile
	 * (org.eclipse.core.runtime.IPath)
	 */
	@Override
	public boolean isExistingWorkspaceFile(IPath path) {
		return getIFileFromIPath(path) != null;
	}

	/**
	 * Get the IFile from IPath.
	 * 
	 * @param path
	 *            Path to file in workspace
	 * @return the IFile. <code>null</code> if no IFile on the path, file does
	 *         not exist, internal error.
	 */
	public static IFile getIFileFromIPath(final IPath path) {
		try {
			final IResource r = ResourcesPlugin.getWorkspace().getRoot()
					.findMember(path, false);
			if (r != null && r instanceof IFile) {
				final IFile file = (IFile) r;
				if (file.exists())
					return file;
			}
		} catch (Exception ex) {
			// Ignored
		}
		return null;
	}

	/**
	 * Get screenshot image from GraphicalViewer
	 * 
	 * @param viewer
	 *            the GraphicalViewer
	 * @return the screenshot image
	 */
	@Override
	public Image getScreenShotImage(GraphicalViewer viewer) {
		GC gc = new GC(viewer.getControl());
		final Image image = new Image(Display.getDefault(), viewer.getControl()
				.getSize().x, viewer.getControl().getSize().y);
		gc.copyArea(image, 0, 0);
		gc.dispose();
		return image;
	}
}
