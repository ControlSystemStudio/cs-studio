/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.csstudio.swt.widgets.Preferences;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

/**Utility functions for resources.
 * @author Xihui Chen
 *
 */
public class ResourceUtil {
	
	
	
	/**Get inputstream from path. Run in a Job. The uiTask is responsible for closing the inputstream
	 * @param path the path to load
	 * @param uiTask the task to be executed in UI thread after path is loaded.
	 * @param jobName name of the job
	 * @param errorHandler the handler to handle IO exception.
	 */
	public static void pathToInputStreamInJob(final IPath path, 
			final AbstractInputStreamRunnable uiTask, final String jobName,
			final IJobErrorHandler errorHandler){
		final Display display = Display.getCurrent();
		Job job = new Job(jobName) {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Connecting to " + path, IProgressMonitor.UNKNOWN);
				try {
					final InputStream inputStream = pathToInputStream(path);
					uiTask.setInputStream(inputStream);
					display.asyncExec(uiTask);
				} catch (Exception e) {
					errorHandler.handleError(e);
				}finally{
					monitor.done();
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
	

	/**
	 * Return the {@link InputStream} of the file that is available on the
	 * specified path. The caller is responsible for closing inputstream.
	 *
	 * @param path
	 *            The {@link IPath} to the file in the workspace, the local
	 *            file system, or a URL (http:, https:, ftp:, file:, platform:)
	 * @return The corresponding {@link InputStream}. Never <code>null</code>
	 * @throws Exception
	 */
	@SuppressWarnings("nls")
    public static InputStream pathToInputStream(final IPath path) throws Exception
    {
	   InputStream inputStream = SingleSourceHelper.workspaceFileToInputStream(path);
	   
	   if(inputStream != null)
		   return inputStream;

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
            //TODO:
        	// Eclipse Path collapses "//" into "/", revert that: Is this true? Need test on Mac.        	
            urlString = path.toString();
//            if(!urlString.startsWith("platform") && !urlString.contains("://")) //$NON-NLS-1$ //$NON-NLS-2$
//                urlString = urlString.replaceFirst(":/", "://"); //$NON-NLS-1$ //$NON-NLS-2$
            // Does it now look like a URL? If not, report the original local file problem
            if (! isURL(urlString))
                throw new Exception("Cannot open " + ex.getMessage(), ex);
        }

        // Must be a URL
        // Allow URLs with spaces. Ideally, the URL class would handle this?
        urlString = urlString.replaceAll(" ", "%20");
        URI uri = new URI(urlString);
        final URL url = uri.toURL();
        return  openURLStream(url);
	}
	
	private static InputStream openURLStream(final URL url) throws IOException {
		URLConnection connection = url.openConnection();
		int timeout = 0;
		String value = System.getProperty(Preferences.URL_FILE_LOAD_TIMEOUT); //$NON-NLS-1$
		if(value != null ){
			 try {
				timeout = Integer.parseInt(value);
			} catch (NumberFormatException e) {				
			}
		}
		if(timeout == 0){
			timeout = Preferences.getURLFileLoadTimeout();
		}
		connection.setReadTimeout(timeout);
		return connection.getInputStream();
	}
	
	
	/** Check if a URL is actually a URL
	 *  @param url Possible URL
	 *  @return <code>true</code> if considered a URL
	 */
	@SuppressWarnings("nls")
    public static boolean isURL(final String url){
		return url.contains(":/");  //$NON-NLS-1$
	}
	
//	/**
//	 * Return the {@link InputStream} of the file that is available on the
//	 * specified path.
//	 * 
//	 * @param path
//	 *            The {@link IPath} to the file
//	 * 
//	 * @return The corresponding {@link InputStream} or null
//	 * @throws Exception 
//	 */
//	public static InputStream pathToInputStream(final IPath path) throws Exception{
//		InputStream result = null;
//		
//		IResource r = null;
//		try {
//			// try workspace
//			r = ResourcesPlugin.getWorkspace().getRoot().findMember(
//					path, false);
//			if (r!= null && r instanceof IFile) {			
//				result = ((IFile) r).getContents();		
//				return result;
//			}else
//				throw new Exception();
//		} catch (Exception e) {
//			// try from local file system			
//			try {
//				result = new FileInputStream(path.toFile());
//				if(result != null)
//					return result;
//				else
//					throw new Exception();
//			} catch (Exception e1) {
//				try {
//					//try from URL					
//					String urlString = path.toString();
//					if(!urlString.contains("://")) //$NON-NLS-1$
//						urlString = urlString.replaceFirst(":/", "://"); //$NON-NLS-1$ //$NON-NLS-2$
//					URL url = new URL(urlString);
//					result = url.openStream();
//					return result;
//				} catch (Exception e2) {
//					throw new Exception("This exception includes three sub-exceptions:\n"+ 
//							e+ "\n" + e1 + "\n" + e2);
//				}				
//			}
//		}
//	}
}
