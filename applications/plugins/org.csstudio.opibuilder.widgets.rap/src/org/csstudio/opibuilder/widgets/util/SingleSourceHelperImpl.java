package org.csstudio.opibuilder.widgets.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.widgets.figures.AbstractWebBrowserFigure;
import org.csstudio.opibuilder.widgets.figures.WebBrowserFigure;
import org.csstudio.opibuilder.widgets.properties.ColorMapProperty;
import org.csstudio.swt.widgets.datadefinition.ColorMap;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Composite;
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
			Composite composite, AbstractContainerModel parentModel,
			boolean runmode, boolean showToolbar) {
		return new WebBrowserFigure(composite, parentModel, runmode);
	}

	@Override
	protected void internalSetGCTransform(GC gc, Transform transform) {
		throw new RuntimeException(NOT_IMPLEMENTED);		
	}
	
	
	

}
