package org.csstudio.swt.widgets.util;

import java.io.InputStream;

import org.csstudio.swt.widgets.figures.TextInputFigure;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;

public abstract class SingleSourceHelper {

	private static final SingleSourceHelper IMPL;
	
	static {
		IMPL = (SingleSourceHelper)ImplementationLoader.newInstance(
				SingleSourceHelper.class);
	}
	
	public static GC getImageGC(final Image image){
		if(IMPL == null)
			return null;
		return IMPL.internalGetImageGC(image);
	}
	
	protected abstract GC internalGetImageGC(final Image image);

	public static InputStream workspaceFileToInputStream(IPath path){
		if(IMPL == null)
			return null;
		try {
			return IMPL.internalWorkspaceFileToInputStream(path);
		} catch (Exception e) {
			return null;
		}
	}

	protected abstract InputStream internalWorkspaceFileToInputStream(IPath path) throws Exception;
	
	
	public static Cursor createCursor(
			Display display, ImageData imageData, int width, int height, int backUpSWTCursorStyle){
		if(IMPL == null)
			return null;
		return IMPL.createInternalCursor(display, imageData, width, height, backUpSWTCursorStyle);
	}
	
	protected abstract Cursor createInternalCursor(
			Display display, ImageData imageData, int width, int height,int backUpSWTCursorStyle);


	public static void setGCTransform(GC gc, Transform transform){
		if(IMPL == null)
			return;
		IMPL.internalSetGCTransform(gc, transform);
	}

	protected abstract void internalSetGCTransform(GC gc, Transform transform);
	
	public static void handleTextInputFigureFileSelector(TextInputFigure textInput){
		if(IMPL==null){
			if(SWT.getPlatform().startsWith("rap")) //$NON-NLS-1$
				MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Not Applicable", 
					"File Selector does not work for RAP because there is no file system!");
			return;
		}
		IMPL.internalHandleTextInputFigureFileSelector(textInput);
	}

	protected abstract void internalHandleTextInputFigureFileSelector(TextInputFigure textInput);
	
}
