package org.csstudio.opibuilder.widgets.util;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.widgets.figures.AbstractWebBrowserFigure;
import org.csstudio.opibuilder.widgets.figures.WebBrowserFigure;
import org.csstudio.opibuilder.widgets.properties.ColorMapProperty;
import org.csstudio.swt.widgets.datadefinition.ColorMap;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public class SingleSourceHelperImpl extends SingleSourceHelper {

	private static final String NOT_IMPLEMENTED = 
			"This method has not been implemented yet for RAP";

	@Override
	protected Cursor createInternalCursor(Display display, ImageData imageData,
			int width, int height, int style) {
		return display.getSystemCursor(style);
	}

	@Override
	protected GC internalGetImageGC(Image image) {
		throw new RuntimeException(NOT_IMPLEMENTED);
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
		return new WebBrowserFigure(editPart);
	}

	@Override
	protected void internalSetGCTransform(GC gc, Transform transform) {
		throw new RuntimeException(NOT_IMPLEMENTED);		
	}

	@Override
	protected void internalSwtWidgetAddMouseTrackListener(Control control,
			MouseTrackListener listener) {
		
	}

	@Override
	protected void internalSWTControlTraverse(Control control, int traversal) {
		
	}
	
	
	

}
