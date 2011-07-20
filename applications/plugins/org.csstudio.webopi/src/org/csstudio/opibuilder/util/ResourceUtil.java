/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.persistence.URLPath;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osgi.util.NLS;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PlatformUI;

/**Utility functions for resources.
 * @author Xihui Chen, Abadie Lana, Kay Kasemir
 */
public class ResourceUtil {
	private static InputStream inputStream;
	private static Object lock = new Boolean(true);
	
	
	
		
	/**
	 *Return the {@link InputStream} of the file that is available on the
	 * specified path. The task will run in UIJob. Caller must be in UI thread too.
	 * @see #pathToInputStream(IPath, boolean)
	 */
	public static InputStream pathToInputStream(final IPath path) throws Exception {
		return pathToInputStream(path, true);
	}
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
    public static InputStream pathToInputStream(final IPath path, boolean runInUIJob) throws Exception
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
            if(!urlString.contains("://"))
                urlString = urlString.replaceFirst(":/", "://");
            // Does it now look like a URL? If not, report the original local file problem
            if (! isURL(urlString))
                throw new Exception("Cannot open " + ex.getMessage(), ex);
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
		connection.setReadTimeout(PreferencesHelper.getURLFileLoadingTimeout());
		return connection.getInputStream();
	}
	
	

	
	
	
	public static boolean isExistingLocalFile(IPath path){
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

	/**Build the absolute path from the file path (without the file name part)
	 * of the widget model and the relative path.
	 * @param model the widget model
	 * @param relativePath the relative path
	 * @return the absolute path.
	 */
	public static IPath buildAbsolutePath(AbstractWidgetModel model, IPath relativePath){
		if(relativePath == null || relativePath.isEmpty() || relativePath.isAbsolute())
			return relativePath;
		return model.getRootDisplayModel().getOpiFilePath().
			removeLastSegments(1).append(relativePath);
	}

	/**Build the relative path from a reference path.
	 * @param refPath the reference path which does not include the file name.
	 * @param fullPath the absolute full path which includes the file name.
	 * @return the relative to path to refPath.
	 */
	public static IPath buildRelativePath(IPath refPath, IPath fullPath){
		if(refPath == null || fullPath == null)
			throw new NullPointerException();
		return fullPath.makeRelativeTo(refPath);
	}

	/**
	 * @return
	 * @throws FileNotFoundException
	 */
	public static IPath getPathInEditor(IEditorInput input){
		if(input instanceof IPathEditorInput)
			return ((IPathEditorInput)input).getPath();		
		return null;
	}

	/**Returns IPath from String.
	 * @param input the path string.
	 * @return {@link URLPath} if input is an URL; returns {@link Path} otherwise.
	 */
	public static IPath getPathFromString(String input){
		if(input == null)
			return null;
		if(isURL(input))
			return new URLPath(input);
		else
			return new Path(input);
	}
	


	/** Check if a URL is actually a URL
	 *  @param url Possible URL
	 *  @return <code>true</code> if considered a URL
	 */
	@SuppressWarnings("nls")
    public static boolean isURL(final String url){
		return url.contains(":/");  //$NON-NLS-1$
	}	
}
