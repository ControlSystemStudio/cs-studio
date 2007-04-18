package org.csstudio.alarm.treeView.images;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public class LdapImageCache {
	private final Map imageMap = new HashMap();
	
	public Image getImage(ImageDescriptor imageDescriptor){
		if (imageDescriptor==null)
			return null;
		Image image = (Image) imageMap.get(imageDescriptor);
		if (image == null) {
			image = imageDescriptor.createImage();
			imageMap.put(imageDescriptor,image);
		}
		return image;
	}
}
