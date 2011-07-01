package org.csstudio.swt.xygraph.util;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import org.csstudio.swt.xygraph.figures.XYGraph;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class SingleSourceHelperImpl extends SingleSourceHelper {

	private static final String NOT_IMPLEMENTED = 
			"This method has not been implemented yet for RAP";

	@Override
	protected Cursor createInternalCursor(Display display, ImageData imageData,
			int width, int height) {
		return display.getSystemCursor(SWT.CURSOR_CROSS);
	}
	@Override
	protected Image createInternalVerticalTextImage(String text, Font font, RGB color,
			boolean upToDown) {
		  org.eclipse.draw2d.geometry.Dimension titleSize = 
				  FigureUtilities.getTextExtents(text,	font);
		final int w = titleSize.height;
		final int h = titleSize.width;
	    BufferedImage image = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
	    Graphics2D gr2d = image.createGraphics();
	    gr2d.setColor( new java.awt.Color( color.red, color.green, color.blue) );
	    FontData fd = font.getFontData()[0];
	    gr2d.setFont(new java.awt.Font(fd.getName(), fd.getStyle(), fd.getHeight()));

	    AffineTransform at = new AffineTransform();
	    at.setToRotation(-Math.PI/2.0);
	    
	    gr2d.setTransform(at);
	    gr2d.drawString(text, -h, w-w/3);
	    ImageData imageData =  AWT2SWTImageConverter.convertToSWT(image);
		imageData.transparentPixel = imageData.palette.getPixel(new RGB(240, 240, 240));

	    Image swtImage = new Image(Display.getCurrent(), imageData);
	    return swtImage;
	}
	
	@Override
	protected Image getInternalXYGraphSnapShot(XYGraph xyGraph) {
		throw new RuntimeException(NOT_IMPLEMENTED);
	}
	
	@Override
	protected String getInternalImageSavePath() {
		throw new RuntimeException(NOT_IMPLEMENTED);
	}

}
