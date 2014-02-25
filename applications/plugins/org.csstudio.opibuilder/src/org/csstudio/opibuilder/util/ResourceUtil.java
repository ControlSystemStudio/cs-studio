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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.csstudio.java.thread.ExecutionService;
import org.csstudio.java.thread.TimedCache;
import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.persistence.URLPath;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.ui.IEditorInput;

/**Utility functions for resources.
 * @author Xihui Chen, Abadie Lana, Kay Kasemir
 */
public class ResourceUtil {

	private static final int CACHE_TIMEOUT_SECONDS = 120;

	private static final ResourceUtilSSHelper IMPL;
	
	/**
	 *Cache the file from URL. 
	 */
	private static final TimedCache<URL, File> URL_CACHE = new TimedCache<>(CACHE_TIMEOUT_SECONDS);

	
	static {
		IMPL = (ResourceUtilSSHelper)ImplementationLoader.newInstance(
				ResourceUtilSSHelper.class);
	}
	
	
		
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
    public static InputStream pathToInputStream(final IPath path, boolean runInUIJob) throws Exception {	
    	return IMPL.pathToInputStream(path, runInUIJob);
	}	
	
	
	/**
	 * Returns a stream which can be used to read this editors input data.
	 * @param editorInput
	 *
	 * @return a stream which can be used to read this editors input data
	 */
	public static InputStream getInputStreamFromEditorInput(IEditorInput editorInput){
		return IMPL.getInputStreamFromEditorInput(editorInput);
		
	}
	
	/**
	 * @param path the file path
	 * @return true if the file path is an existing workspace file.
	 */
	public static boolean isExistingWorkspaceFile(IPath path){
		return IMPL.isExistingWorkspaceFile(path);
	}
	
	public static boolean isExistingLocalFile(IPath path){
		 // Not a workspace file. Try local file system
        File local_file = path.toFile();
       
//        // Path URL for "file:..." so that it opens as FileInputStream
        if (local_file.getPath().startsWith("file:"))
            local_file = new File(local_file.getPath().substring(5));
        return local_file.exists();
//        try
//        {
//            InputStream inputStream = new FileInputStream(local_file);
//            inputStream.close();
//        }
//        catch (Exception ex)
//        {
//            return false;
//        }
//        return true;
        
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
		return IMPL.getPathInEditor(input);
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
	
	/**Convert workspace path to OS system path.
	 * @param path the workspace path
	 * @return the corresponding system path. null if it is not exist.
	 */
	public static IPath workspacePathToSysPath(IPath path){
		return IMPL.workspacePathToSysPath(path);		
	}

	/** Check if a URL is actually a URL
	 *  @param url Possible URL
	 *  @return <code>true</code> if considered a URL
	 */
	@SuppressWarnings("nls")
    public static boolean isURL(final String urlString){
		try {
			new URL(urlString);
		} catch (Exception e) {
			return false;
		}
		return true;
//		return urlString.contains(":/");  //$NON-NLS-1$
	}
	
    /**Open URL stream in UI Job if runInUIJob is true.
     * @param url
     * @param runInUIJob true if this method should run as an UI Job. 
     * If it is true, this method must be called in UI thread.
     * 
     * TODO Unclear why the runInUIJob is actually used, because
     *      it will in fact NOT run in the UI, but in a background job.
     *      It will wait for the result in either case, so why a background job??
     * @return Stream for the URL
     * @throws Exception on error
     */
    public static InputStream openURLStream(final URL url, boolean runInUIJob) throws Exception
    {
        final AtomicReference<InputStream> stream = new AtomicReference<>();
        if (runInUIJob)
        {
            final Job job = new Job("OPI URL Opener")
            {
                @Override
                protected IStatus run(final IProgressMonitor monitor)
                {
                    monitor.beginTask("Connecting to " + url, IProgressMonitor.UNKNOWN);
                    try
                    {
                        stream.set(openURLStream(url));
                    }
                    catch (IOException ex)
                    {
                        OPIBuilderPlugin.getLogger().log(Level.WARNING, "URL '" + url + "' failed to open", ex);
                        monitor.setCanceled(true);
                        return Status.CANCEL_STATUS;
                    }
                    monitor.done();
                    return Status.OK_STATUS;
                }
            };
            job.schedule();
            job.join();
        }
        else // Open stream w/o UI job
            stream.set(openURLStream(url));
        return stream.get();
    }
	
	private static InputStream openURLStream(final URL url) throws IOException{
		File tempFilePath = URL_CACHE.getValue(url);
		if(tempFilePath != null){
            OPIBuilderPlugin.getLogger().log(Level.FINE, "Found cached file for URL '" + url + "'");
			return new FileInputStream(tempFilePath);
		}else{
			InputStream inputStream = openRawURLStream(url);			
			if(inputStream !=null){
				try {
					IPath urlPath = new URLPath(url.toString());
					
					// createTempFile(), at least with jdk1.7.0_45,
					// requires at least 3 chars for the 'prefix', so add "opicache"
					// to assert a minimum length
					final String cache_file_prefix = "opicache" + urlPath.removeFileExtension().lastSegment();
                    final String cache_file_suffix = "."+urlPath.getFileExtension();
					final File file = File.createTempFile(cache_file_prefix, cache_file_suffix);			
					file.deleteOnExit();	
					if(!file.canWrite())
						throw new Exception("Unable to write temporary file.");
					FileOutputStream outputStream = new FileOutputStream(file);
					byte[] buffer = new byte[1024];
					int bytesRead;
					while((bytesRead = inputStream.read(buffer))!=-1){
						outputStream.write(buffer, 0, bytesRead);
					}					
					outputStream.close();
					URL_CACHE.remember(url, file);
					inputStream.close();
					ExecutionService.getInstance().getScheduledExecutorService().schedule(new Runnable() {
						
						@Override
						public void run() {
							file.delete();
						}
					}, CACHE_TIMEOUT_SECONDS*2, TimeUnit.SECONDS);
					return new FileInputStream(file);
				} catch (Exception e) {
					OPIBuilderPlugin.getLogger().log(Level.WARNING,
							"Error to cache file from URL '" + url + "'", e);
				}	            
			}			
			return inputStream;
		}
		
	}
	
	/**Open URL Stream from remote.
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private static InputStream openRawURLStream(final URL url) throws IOException{
		if(url.getProtocol().equals("https")){			//$NON-NLS-1$
			//The code to support https protocol is provided by Eric Berryman (eric.berryman@gmail.com) from Frib
	        // Create a trust manager that does not validate certificate chains
	        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
	                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                    return null;
	                }
	                public void checkClientTrusted(X509Certificate[] certs, String authType) {
	                }
	                public void checkServerTrusted(X509Certificate[] certs, String authType) {
	                }
	            }
	        };
	
	        // Install the all-trusting trust manager
	        SSLContext sc = null;
	        try {
	            sc = SSLContext.getInstance("SSL");
	        } catch (NoSuchAlgorithmException e) {
	            e.printStackTrace();
	        }
	        try {
	            sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        } catch (KeyManagementException e) {
	            e.printStackTrace();
	        }
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	
	        // Create all-trusting host name verifier
	        HostnameVerifier allHostsValid = new HostnameVerifier() {
	            public boolean verify(String hostname, SSLSession session) {
	                return true;
	            }
	        };
	
	        // Install the all-trusting host verifier
	        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		}
		
		URLConnection connection = url.openConnection();
		connection.setReadTimeout(PreferencesHelper.getURLFileLoadingTimeout());
		return connection.getInputStream();
	}
	
	/**If the path is an connectable URL. .
	 * @param path
	 * @param runInUIJob true if this method should run as an UI Job. 
	 * If it is true, this method must be called in UI thread.
	 * @return
	 */
	public static boolean isExistingURL(final IPath path, boolean runInUIJob){
		try {
			
			URL url = new URL(path.toString());
			if(URL_CACHE.getValue(url)!= null)
				return true;
			InputStream s = openURLStream(url, runInUIJob);
			if(s != null){
				s.close();
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**If the file on path is an existing file in workspace, local file system or available URL.
	 * @param absolutePath
	 * @param runInUIJob true if this method should run as an UI Job. 
	 * If it is true, this method must be called in UI thread.
	 * @return
	 */
	public static boolean isExsitingFile(final IPath absolutePath, boolean runInUIJob){
		if(absolutePath instanceof URLPath)
			if(isExistingURL(absolutePath, runInUIJob))
				return true;
		
		if(isExistingWorkspaceFile(absolutePath))
			return true;
		if(isExistingLocalFile(absolutePath))
			return true;
		if(!(absolutePath instanceof URLPath)
				&& isExistingURL(absolutePath, runInUIJob))
			return true;
		return false;
	}
	
	/**Get the first existing file on search path. Search path is a BOY preference.
	 * @param relativePath
	 * @param runInUIJob true if this method should run as an UI Job, in which cases
	 *  this method must be called in UI thread.	 
	 * @return the first existing file on search path. null if search path doesn't exist or 
	 * no such file exist on search path.
	 */
	public static IPath getFileOnSearchPath(final IPath relativePath, boolean runInUIJob){
		IPath[] searchPaths;
		try {
			searchPaths = PreferencesHelper.getOPISearchPaths();
			if(searchPaths == null)
				return null;
			for(IPath searchPath : searchPaths){
				IPath absolutePath = searchPath.append(relativePath);
				if(isExsitingFile(absolutePath, runInUIJob))
					return absolutePath;			
			}
		} catch (Exception e) {
			return null;
		}
		
		return null;		
	}
	
	
	/**Get screenshot image from GraphicalViewer
	 * @param viewer the GraphicalViewer
	 * @return the screenshot image
	 */
	public static Image getScreenshotImage(GraphicalViewer viewer){		
		return IMPL.getScreenShotImage(viewer);
	}
	
	@SuppressWarnings("nls")
    public static String getScreenshotFile(GraphicalViewer viewer) throws Exception{
		File file;
		 // Get name for snapshot file
        try
        {
            file = File.createTempFile("opi", ".png"); //$NON-NLS-1$ //$NON-NLS-2$
            file.deleteOnExit();
        }
        catch (Exception ex)
        {
            throw new Exception("Cannot create tmp. file:\n" + ex.getMessage());
        }

        // Create snapshot file
        try
        {
            final ImageLoader loader = new ImageLoader();

            final Image image = ResourceUtil.getScreenshotImage(viewer);
            loader.data = new ImageData[]{image.getImageData()};
            image.dispose();
            loader.save(file.getAbsolutePath(), SWT.IMAGE_PNG);
        }
        catch (Exception ex)
        {
            throw new Exception(
                    NLS.bind("Cannot create snapshot in {0}:\n{1}",
                            file.getAbsolutePath(), ex.getMessage()));
        }
        return file.getAbsolutePath();
    }

}
