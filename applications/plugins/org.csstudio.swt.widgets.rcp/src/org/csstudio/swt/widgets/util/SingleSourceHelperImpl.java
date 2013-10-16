/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.util;

import java.io.InputStream;

import org.csstudio.swt.widgets.figures.TextInputFigure;
import org.csstudio.swt.widgets.figures.TextInputFigure.FileReturnPart;
import org.csstudio.ui.util.dialogs.ResourceSelectionDialog;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

public class SingleSourceHelperImpl extends SingleSourceHelper{

	private static final String SEPARATOR = "|"; //$NON-NLS-1$


	@Override
	protected InputStream internalWorkspaceFileToInputStream(IPath path) throws Exception {
		 // Try workspace location
	    final IFile workspace_file = getIFileFromIPath(path);
	    // Valid file should either open, or give meaningful exception
	    if (workspace_file != null  &&  workspace_file.exists())
	        return workspace_file.getContents();
	    else 
	    	throw new Exception();
	}

	
	/**Get the IFile from IPath.
	 * @param path Path to file in workspace
	 * @return the IFile. <code>null</code> if no IFile on the path, file does not exist, internal error.
	 */
	public static IFile getIFileFromIPath(final IPath path)
	{
	    try
	    {
    		final IResource r = ResourcesPlugin.getWorkspace().getRoot().findMember(
    				path, false);
    		if (r!= null && r instanceof IFile)
		    {
    		    final IFile file = (IFile) r;
    		    if (file.exists())
    		        return file;
		    }
	    }
	    catch (Exception ex)
	    {
	        // Ignored
	    }
	    return null;
	}


	@Override
	protected GC internalGetImageGC(Image image) {
		return new GC(image);
	}


	@Override
	protected Cursor createInternalCursor(Display display, ImageData imageData,
			int width, int height, int backUpSWTCursorStyle) {
		return new Cursor(display, imageData, width, height);
	}


	@Override
	protected void internalSetGCTransform(GC gc, Transform transform) {
		gc.setTransform(transform);
	}


	@Override
	protected void internalHandleTextInputFigureFileSelector(
			TextInputFigure textInput) {
		String startPath = textInput.getStartPath();
		String currentPath = textInput.getCurrentPath();
		switch (textInput.getFileReturnPart()) {
		case DIRECTORY:
		case FULL_PATH:
			currentPath = textInput.getText();
			break;		
		default:
			if (currentPath == null) {
				if (startPath == null)
					currentPath = textInput.getText();
				else
					currentPath = startPath;
			}
			break;
		}
		
		switch (textInput.getFileSource()) {
		case WORKSPACE:
			ResourceSelectionDialog dialog = 
				new ResourceSelectionDialog(Display.getCurrent().getActiveShell(),
						"Select workspace file", 
						textInput.getFileReturnPart() == FileReturnPart.DIRECTORY ? 
								null : new String[]{"*.*"}); //$NON-NLS-2$
			if(currentPath != null)
				dialog.setSelectedResource(new Path(currentPath));					 
			if(dialog.open() == Window.OK){
				IPath path = dialog.getSelectedResource();
				currentPath = path.toPortableString();
				String fileString = currentPath;
				switch (textInput.getFileReturnPart()) {
				case NAME_ONLY:
					fileString = path.removeFileExtension().lastSegment();
					break;
				case NAME_EXT:
					fileString = path.lastSegment();
					break;
				case FULL_PATH:
				case DIRECTORY:
				default:
					break;
				}
				textInput.setText(fileString);
				textInput.setCurrentPath(currentPath);
				textInput.fireManualValueChange(textInput.getText());
			}
			break;
		case LOCAL:
			IPath paths[] = null;
			if(textInput.getFileReturnPart() == FileReturnPart.DIRECTORY){
				 DirectoryDialog directoryDialog = new DirectoryDialog(
						Display.getCurrent().getActiveShell());
				 directoryDialog.setFilterPath(currentPath);
				 String directory = directoryDialog.open();
				 if(directory!=null)
					 paths = new Path[]{new Path(directory)};
				
			}else {
				FileDialog fileDialog = new FileDialog(Display.getCurrent()
						.getActiveShell(), SWT.MULTI);
				if (currentPath != null)
					((FileDialog) fileDialog).setFileName(currentPath);
				String firstPath = fileDialog.open();
				if(firstPath != null){
					paths = new Path[fileDialog.getFileNames().length];
					paths[0] = new Path(firstPath);
					for (int i = 1; i < paths.length; i++) {
						paths[i] = paths[0].removeLastSegments(1).append(
								fileDialog.getFileNames()[i]);
					}
				}
			}				
			if (paths != null) {
				currentPath = paths[0].toOSString();
				StringBuilder result=new StringBuilder();
				switch (textInput.getFileReturnPart()) {
				case NAME_ONLY:
					for(int i=0; i<paths.length;i++){
						if(i>0)
							result.append(SEPARATOR);
						result.append(paths[i].removeFileExtension().lastSegment());
					}					
					break;
				case NAME_EXT:
					for(int i=0; i<paths.length;i++){
						if(i>0)
							result.append(SEPARATOR);
						result.append(paths[i].lastSegment());
					}
					break;
				case FULL_PATH:
				case DIRECTORY:
				default:
					for(int i=0; i<paths.length;i++){
						if(i>0)
							result.append(SEPARATOR);
						result.append(paths[i].toOSString());
					}
					break;
				}
				textInput.setText(result.toString());
				textInput.setCurrentPath(currentPath);
				textInput.fireManualValueChange(textInput.getText());
			}
	
			break;
		default:
			break;
		}
		
	}
	
}
