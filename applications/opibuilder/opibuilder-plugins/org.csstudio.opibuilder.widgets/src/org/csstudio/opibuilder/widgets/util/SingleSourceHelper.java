package org.csstudio.opibuilder.widgets.util;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.widgets.figures.AbstractWebBrowserFigure;
import org.csstudio.swt.widgets.datadefinition.ColorMap;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Control;
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

		
	public static AbstractWidgetProperty createColorMapProperty(String prop_id, String description,
			WidgetPropertyCategory category, ColorMap defaultValue){
		return IMPL.internalCreateColorMapProperty(prop_id, description,
			category, defaultValue);
	}
	
	
	protected abstract AbstractWidgetProperty internalCreateColorMapProperty(
			String prop_id, String description,
			WidgetPropertyCategory category, ColorMap defaultValue);
	
	
	public static AbstractWebBrowserFigure createWebBrowserFigure(
			AbstractBaseEditPart editPart, boolean showToolbar){
		return IMPL.internalCreateWebBrowserFigure(editPart, showToolbar);
	}
	

	protected abstract AbstractWebBrowserFigure internalCreateWebBrowserFigure(
			AbstractBaseEditPart editPart,  boolean showToolbar);

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
	
	public static void swtWidgetAddMouseTrackListener(
			Control control, MouseTrackListener listener){
		if(IMPL == null)
			return;
		IMPL.internalSwtWidgetAddMouseTrackListener(control, listener);
	}

	protected abstract void internalSwtWidgetAddMouseTrackListener(Control control,
			MouseTrackListener listener);
	
	/**RAP control doesn't have control.traverse. This is used to fake this operation in RAP.
	 * @param control
	 * @param traversal
	 */
	public static void swtControlTraverse(Control control, int traversal){
		if(IMPL == null)
			return;
		IMPL.internalSWTControlTraverse(control, traversal);
	}

	protected abstract void internalSWTControlTraverse(Control control, int traversal);	
}
