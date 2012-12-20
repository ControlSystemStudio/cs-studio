package org.csstudio.opibuilder.tools.thumbnails;

import java.util.LinkedList;

import org.apache.batik.utils.SVGUtils;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

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
		try {
			if (!(element instanceof IStorage))
				return null;

			IStorage stor = (IStorage) element;
			IPath path = stor.getFullPath();
			ImageData data = null;

			if (GIF_EXT.equalsIgnoreCase(path.getFileExtension())
					|| PNG_EXT.equalsIgnoreCase(path.getFileExtension())
					|| ICO_EXT.equalsIgnoreCase(path.getFileExtension())) {
				ImageData tmpData = new ImageData(stor.getContents());
				data = tmpData.scaledTo(MAX_ICON_WIDTH, MAX_ICON_HEIGHT);
			} else if (SVG_EXT.equalsIgnoreCase(path.getFileExtension())) {
				data = SVGUtils.loadSVG(stor.getFullPath(), stor.getContents(),
						MAX_ICON_WIDTH, MAX_ICON_HEIGHT);
			}

			if (data != null && data.width <= MAX_ICON_WIDTH
					&& data.height <= MAX_ICON_HEIGHT) {
				Image img = new Image(Display.getCurrent(), data);
				createdImages.add(img);
				return img;
			}
		} catch (Exception e) {
			e.printStackTrace();
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
