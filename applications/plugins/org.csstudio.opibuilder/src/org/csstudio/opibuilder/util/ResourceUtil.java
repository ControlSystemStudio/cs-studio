package org.csstudio.opibuilder.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

/**Utility functions for resources.
 * @author Xihui Chen
 *
 */
public class ResourceUtil {

	
	public static final Cursor CURSOR_HAND = new Cursor(Display.getDefault(), SWT.CURSOR_HAND);
	public static final Cursor CURSOR_NO = new Cursor(Display.getDefault(), SWT.CURSOR_NO);
	
	public static void disposeResources(){
		CURSOR_HAND.dispose();
		CURSOR_NO.dispose();
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
