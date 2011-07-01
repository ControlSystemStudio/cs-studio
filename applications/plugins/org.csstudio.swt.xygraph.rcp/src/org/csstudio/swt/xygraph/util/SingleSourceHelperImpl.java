package org.csstudio.swt.xygraph.util;

import org.csstudio.swt.xygraph.figures.XYGraph;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

public class SingleSourceHelperImpl extends SingleSourceHelper {

	@Override
	protected Cursor createInternalCursor(Display display, ImageData imageData,
			int width, int height) {
		return new Cursor(display, imageData, width, height);
	}
	
	@Override
	protected Image createInternalVerticalTextImage(String text, Font font,
			boolean upToDown) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected Image getInternalXYGraphSnapShot(XYGraph xyGraph) {
		Rectangle bounds = xyGraph.getBounds();
		Image image = new Image(null, bounds.width + 6, bounds.height + 6);
		GC gc = new GC(image);
		SWTGraphics graphics = new SWTGraphics(gc);
		graphics.translate(-bounds.x + 3, -bounds.y + 3);
		graphics.setForegroundColor(xyGraph.getForegroundColor());
		graphics.setBackgroundColor(xyGraph.getBackgroundColor());
		xyGraph.paint(graphics);
		gc.dispose();
		return image;
	}
	
	@Override
	protected String getInternalImageSavePath() {
		FileDialog dialog = new FileDialog(Display.getDefault().getShells()[0], SWT.SAVE);
		dialog.setFilterNames(new String[] {"PNG Files", "All Files (*.*)" });
        dialog.setFilterExtensions(new String[] { "*.png", "*.*" }); // Windows
	    String path = dialog.open();
	    return path;
	}

}
