package org.csstudio.opibuilder.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

/**Utility functions for resources.
 * @author Xihui Chen
 *
 */
public class ResourceUtil {

	
	private static final String ELLIPSIS = ".."; //$NON-NLS-1$

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
	 * @throws CoreException 
	 * @throws FileNotFoundException 
	 */
	public static InputStream pathToInputStream(final IPath path) throws CoreException, FileNotFoundException {
		InputStream result = null;

		// try workspace
		IResource r = ResourcesPlugin.getWorkspace().getRoot().findMember(
				path, false);
		if (r!= null && r instanceof IFile) {			
				result = ((IFile) r).getContents();			
		} else {
			// try from local file system			
			result = new FileInputStream(path.toFile());
		}

		return result;
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
		int i=0;
		String[] absolutePathSegs = fullPath.segments();
		for(String seg : refPath.segments()){
			if(!seg.equals(fullPath.segment(i)))
				break;			
			i++;			
		}
		int ellipsisCount = refPath.segmentCount() - i;
		int remainedSegsCount = fullPath.segmentCount() -i;
		StringBuilder sb = new StringBuilder();
		for(int j = 0; j<ellipsisCount + remainedSegsCount - 1; j++){
			if(j < ellipsisCount)
				sb.append(ELLIPSIS + IPath.SEPARATOR);
			else
				sb.append(absolutePathSegs[i++] + IPath.SEPARATOR);
		}
		sb.append(absolutePathSegs[i]);
		return new Path(sb.toString());
	}
	
	/**
	 * @return
	 * @throws FileNotFoundException 
	 */
	public static IFile getFileInEditor(IEditorInput input) throws FileNotFoundException {
		IFile file = null;		
		if(input instanceof FileEditorInput)
			file = ((FileEditorInput)input).getFile();
		else if (input instanceof FileStoreEditorInput) {
			IPath path = URIUtil.toPath(((FileStoreEditorInput) input)
					.getURI());
			//read file
			IFile[] files = 
				ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(
						ResourcesPlugin.getWorkspace().getRoot().getLocation().append(path));
			
			if(files.length < 1)			
				throw new FileNotFoundException("The file " + path.toString() + "does not exist!");
		
			file = files[0];
		}
		return file;
	}


}
