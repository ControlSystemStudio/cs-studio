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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PlatformUI;

/**RAP implementation of ResourceUtilHelper.
 * @author Xihui Chen
 *
 */
public class ResourceUtilSSHelperImpl extends ResourceUtilSSHelper{
	private static InputStream inputStream;
	private static Object lock = new Boolean(true);
	private static final String NOT_IMPLEMENTED = 
			"This method has not been implemented yet for RAP";

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
	@SuppressWarnings("nls")
    public InputStream pathToInputStream(final IPath path, boolean runInUIJob) throws Exception
    {
	    // Not a workspace file. Try local file system
        File local_file = path.toFile();
        // Path URL for "file:..." so that it opens as FileInputStream
        if (local_file.getPath().startsWith("file:"))
            local_file = new File(local_file.getPath().substring(5));
        String urlString;
        try
        {
            return new FileInputStream(local_file);
        }
        catch (Exception ex)
        {
            // Could not open as local file.
            // Does it look like a URL?
            // Eclipse Path collapses "//" into "/", revert that:
            urlString = path.toString();
//            if(!urlString.contains("://"))
//                urlString = urlString.replaceFirst(":/", "://");
            // Does it now look like a URL? If not, report the original local file problem
//            if (! ResourceUtil.isURL(urlString))
//                throw new Exception("Cannot open " + ex.getMessage(), ex);
        }

        // Must be a URL
        final URL url = new URL(urlString);
        inputStream = null;
        
		if (runInUIJob) {
			synchronized (lock) {
				IRunnableWithProgress openURLTask = new IRunnableWithProgress() {

					public void run(IProgressMonitor monitor)
							throws InvocationTargetException,
							InterruptedException {
						try {
							monitor.beginTask("Connecting to " + url,
									IProgressMonitor.UNKNOWN);
							inputStream = openURLStream(url);
						} catch (IOException e) {
							throw new InvocationTargetException(e,
									"Timeout while connecting to " + url);
						} finally {
							monitor.done();
						}
					}

				};
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.run(true, false, openURLTask);
			}
		}else
			return openURLStream(url);
       
		return inputStream;        

	}
	
	private static InputStream openURLStream(final URL url) throws IOException {
		URLConnection connection = url.openConnection();
//		connection.setReadTimeout(PreferencesHelper.getURLFileLoadingTimeout());
		return connection.getInputStream();
	}
	
	public boolean isExistingLocalFile(IPath path){
		 // Not a workspace file. Try local file system
        File local_file = path.toFile();
        // Path URL for "file:..." so that it opens as FileInputStream
        if (local_file.getPath().startsWith("file:"))
            local_file = new File(local_file.getPath().substring(5));
        try
        {
            InputStream inputStream = new FileInputStream(local_file);
            inputStream.close();
        }
        catch (Exception ex)
        {
            return false;
        }
        return true;
        
	}

	@Override
	public boolean isExistingWorkspaceFile(IPath path) {
		return false;
	}

	@Override
	public Image getScreenShotImage(GraphicalViewer viewer) {
		throw new RuntimeException(NOT_IMPLEMENTED);
	}	

	



}
