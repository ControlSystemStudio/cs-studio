package org.csstudio.opibuilder.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

/**Utility functions for resources.
 * @author Xihui Chen
 *
 */
public class ResourceUtil {

	
	public static void disposeResources(){
	
	}
	
	/**
	 * Return the {@link InputStream} of the file that is available on the
	 * specified path.
	 * 
	 * @param path
	 *            The {@link IPath} to the file
	 * 
	 * @return The corresponding {@link InputStream} or null
	 * @throws Exception 
	 */
	public static InputStream pathToInputStream(final IPath path) throws Exception{
		InputStream result = null;
		
		IResource r = null;
		try {
			// try workspace
			r = ResourcesPlugin.getWorkspace().getRoot().findMember(
					path, false);
			if (r!= null && r instanceof IFile) {			
				result = ((IFile) r).getContents();		
				return result;
			}else
				throw new Exception();
		} catch (Exception e) {
			// try from local file system			
			try {
				result = new FileInputStream(path.toFile());
				if(result != null)
					return result;
				else
					throw new Exception();
			} catch (Exception e1) {
				try {
					//try from URL					
					String urlString = path.toString();
					if(!urlString.contains("://")) //$NON-NLS-1$
						urlString = urlString.replaceFirst(":/", "://"); //$NON-NLS-1$ //$NON-NLS-2$
					URL url = new URL(urlString);
					result = url.openStream();
					return result;
				} catch (Exception e2) {
					throw new Exception("This exception includes three sub-exceptions:\n"+ 
							e+ "\n" + e1 + "\n" + e2);
				}				
			}
		}
	}
	
	/**Get the IFile from IPath.
	 * @param path
	 * @return the IFile. null if no IFile on the path. 
	 */
	public static IFile getIFileFromIPath(final IPath path){
		IResource r = ResourcesPlugin.getWorkspace().getRoot().findMember(
				path, false);
		if (r!= null && r instanceof IFile) {			
				return (IFile)r;
		}else
			return null;

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
		if(input instanceof FileEditorInput)
			return ((FileEditorInput)input).getFile().getFullPath();
		else if(input instanceof IPathEditorInput)
			return ((IPathEditorInput)input).getPath();
		else if(input instanceof FileStoreEditorInput) {
			IPath path = URIUtil.toPath(((FileStoreEditorInput) input)
					.getURI());
			return path;
		}
			return null;
	}

	
	public static boolean isURL(String url){
		return url.contains("://");
	}

}
