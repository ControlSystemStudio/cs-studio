package org.csstudio.opibuilder.widgets.util;

import java.io.InputStream;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.widgets.figures.AbstractWebBrowserFigure;
import org.csstudio.swt.widgets.datadefinition.ColorMap;
import org.csstudio.swt.widgets.figures.TextInputFigure;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Composite;
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
	
	
	public static AbstractWebBrowserFigure createWebBrowserFigure(Composite composite, AbstractContainerModel parentModel, 
			boolean runmode, boolean showToolbar){
		return IMPL.internalCreateWebBrowserFigure(composite, parentModel, 
				runmode, showToolbar);
	}
	

	protected abstract AbstractWebBrowserFigure internalCreateWebBrowserFigure(
			Composite composite, AbstractContainerModel parentModel,
			boolean runmode, boolean showToolbar);

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
	
	
}
