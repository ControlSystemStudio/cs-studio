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
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

public class SingleSourceHelperImpl extends SingleSourceHelper{

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
		switch (textInput.getFileSource()) {
		case WORKSPACE:
			ResourceSelectionDialog dialog = 
				new ResourceSelectionDialog(Display.getCurrent().getActiveShell(),
						"Select workspace file", 
						textInput.getFileReturnPart() == FileReturnPart.DIRECTORY ? 
								null : new String[]{"*.*"}); //$NON-NLS-2$
			if(currentPath != null)
				dialog.setSelectedResource(new Path(currentPath));					 
			else if(startPath != null && startPath.trim().length() > 0)
				dialog.setSelectedResource(new Path(startPath));
			else 
				dialog.setSelectedResource(new Path(textInput.getText()));
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
				textInput.fireManualValueChange(textInput.getText());
			}
			break;
		case LOCAL:
			String fileString;
			if(textInput.getFileReturnPart() == FileReturnPart.DIRECTORY){
				 DirectoryDialog directoryDialog = new DirectoryDialog(
						Display.getCurrent().getActiveShell());
				 fileString = directoryDialog.open();
				
			}else {
				FileDialog fileDialog = new FileDialog(Display.getCurrent()
						.getActiveShell());
				if (currentPath != null)
					((FileDialog) fileDialog).setFileName(currentPath);
				fileString = fileDialog.open();
			}				
			if (fileString != null) {
				currentPath = fileString;
				switch (textInput.getFileReturnPart()) {
				case NAME_ONLY:
					IPath path = new Path(fileString).removeFileExtension();
					fileString = path.lastSegment();
					break;
				case NAME_EXT:
					fileString = new Path(fileString).lastSegment();
					break;
				case FULL_PATH:
				case DIRECTORY:
				default:
					break;
				}
				textInput.setText(fileString);
				textInput.fireManualValueChange(textInput.getText());
			}
	
			break;
		default:
			break;
		}
		
	}
	
}
