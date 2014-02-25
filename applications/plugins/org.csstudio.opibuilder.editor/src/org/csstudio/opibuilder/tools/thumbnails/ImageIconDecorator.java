package org.csstudio.opibuilder.tools.thumbnails;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.batik.utils.SVGUtils;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

/** Decorator for image files.
 * 
 *  <p>Displays a small version of image in its icon.
 *
 *  @author Xihui Chen (original check-in 2012-07-10)
 */
public class ImageIconDecorator implements ILabelDecorator {

	private static final int MAX_ICON_WIDTH = 16;
	private static final int MAX_ICON_HEIGHT = 16;
	private static final String PNG_EXT = "png";
	private static final String GIF_EXT = "gif";
	private static final String ICO_EXT = "ico";
	private static final String SVG_EXT = "svg";

	private LinkedList<Image> createdImages = new LinkedList<Image>();

	public ImageIconDecorator() {
	}

	@Override
	public Image decorateImage(Image image, Object element) {
	    if (!(element instanceof IStorage))
	        return null;
	    
	    final IStorage stor = (IStorage) element;
	    final IPath path = stor.getFullPath();
		try {
			ImageData data = null;

			final String ext = path.getFileExtension();
            if (GIF_EXT.equalsIgnoreCase(ext)
					|| PNG_EXT.equalsIgnoreCase(ext)
					|| ICO_EXT.equalsIgnoreCase(ext)) {
				ImageData tmpData = new ImageData(stor.getContents());
				data = tmpData.scaledTo(MAX_ICON_WIDTH, MAX_ICON_HEIGHT);
			} else if (SVG_EXT.equalsIgnoreCase(ext)) {
				data = SVGUtils.loadSVG(path, stor.getContents(),
						MAX_ICON_WIDTH, MAX_ICON_HEIGHT);
			}

			if (data != null && data.width <= MAX_ICON_WIDTH
					&& data.height <= MAX_ICON_HEIGHT) {
				Image img = new Image(Display.getCurrent(), data);
				createdImages.add(img);
				return img;
			}
		} catch (Exception e) {
		    Logger.getLogger(getClass().getName())
		        .log(Level.WARNING, "Cannot create icon for " + path, e);
		}
		return null;
	}
	
	@Override
	public String decorateText(String text, Object element) {
		return null;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {

	}

	@Override
	public void dispose() {
		for (Image img : createdImages)
			img.dispose();
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

}
