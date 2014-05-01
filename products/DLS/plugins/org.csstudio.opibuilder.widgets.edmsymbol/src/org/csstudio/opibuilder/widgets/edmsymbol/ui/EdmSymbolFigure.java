package org.csstudio.opibuilder.widgets.edmsymbol.ui;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.swt.widgets.util.AbstractInputStreamRunnable;
import org.csstudio.swt.widgets.util.IJobErrorHandler;
import org.csstudio.swt.widgets.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.graphics.Image;

public class EdmSymbolFigure extends Figure {

	private int subImageWidth = 10;
	private int subImageSelection = 0;
	private Image image;
	private static Map<String, Image> imageCache;

	public EdmSymbolFigure() {
		this(null);
	}

	public EdmSymbolFigure(IPath path) {
		super();
		if(imageCache == null) imageCache = new HashMap<String, Image>();
		setImage(path);
	}

	@Override
	protected void paintClientArea(Graphics graphics) {
		super.paintClientArea(graphics);
		if(image != null) {
			graphics.drawImage(image,
					subImageSelection * subImageWidth, 0, subImageWidth, image.getBounds().height,
					getClientArea().x, getClientArea().y, getClientArea().width, getClientArea().height);
		}
	}

	public synchronized void setImage(final IPath path) {
		if(path == null || path.isEmpty()) return;
		image = imageCache.get(path.toString());
		if(image == null) {
			AbstractInputStreamRunnable uiTask = new AbstractInputStreamRunnable() {
				public void runWithInputStream(InputStream stream) {
					synchronized (EdmSymbolFigure.this) {
						image = new Image(null, stream);
						imageCache.put(path.toString(), image);
					}
				}
			};
			ResourceUtil.pathToInputStreamInJob(path, uiTask, "Loading Image...", new IJobErrorHandler() {
				public void handleError(Exception exception) {
					System.out.println("Warning: " + exception);
					image = null; // Don't keep drawing an old image
				}
			});
		}
		repaint();
	}
	
	public void setSubImageWidth(int width) {
		this.subImageWidth = width;
		repaint();
	}
	
	public void setSubImageSelection(int imageNum) {
		this.subImageSelection = imageNum;
		repaint();
	}

}
