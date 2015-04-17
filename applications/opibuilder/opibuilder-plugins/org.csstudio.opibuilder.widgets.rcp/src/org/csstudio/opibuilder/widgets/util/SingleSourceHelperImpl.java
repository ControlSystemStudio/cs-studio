package org.csstudio.opibuilder.widgets.util;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.widgets.figures.AbstractWebBrowserFigure;
import org.csstudio.opibuilder.widgets.figures.WebBrowserFigure;
import org.csstudio.opibuilder.widgets.properties.ColorMapProperty;
import org.csstudio.swt.widgets.datadefinition.ColorMap;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public class SingleSourceHelperImpl extends SingleSourceHelper{

	
	
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
	protected AbstractWidgetProperty internalCreateColorMapProperty(
			String prop_id, String description,
			WidgetPropertyCategory category, ColorMap defaultValue) {
		return new ColorMapProperty(prop_id, description, category, defaultValue);
	}


	@Override
	protected AbstractWebBrowserFigure internalCreateWebBrowserFigure(
			AbstractBaseEditPart editPart, boolean showToolbar) {
		return new WebBrowserFigure(editPart, showToolbar);
	}


	@Override
	protected void internalSwtWidgetAddMouseTrackListener(Control control,
			MouseTrackListener listener) {
		control.addMouseTrackListener(listener);
		if(control instanceof Composite){
			for(Control c : ((Composite)control).getChildren()){
				internalSwtWidgetAddMouseTrackListener(c, listener);						
			}
		}
	}


	@Override
	protected void internalSWTControlTraverse(Control control, int traversal) {
		control.traverse(traversal);
	}


	
}
