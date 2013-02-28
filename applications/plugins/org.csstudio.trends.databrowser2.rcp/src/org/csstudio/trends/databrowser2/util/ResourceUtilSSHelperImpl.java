/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.util;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * @author Davy Dequidt <davy.dequidt@iter.org>
 * 
 */
public class ResourceUtilSSHelperImpl extends ResourceUtilSSHelper {
	/**
	 * Get screenshot image from GraphicalViewer
	 * 
	 * @param viewer
	 *            the GraphicalViewer
	 * @return the screenshot image
	 */
	@Override
	public Image getScreenShotImage(GraphicalViewer viewer) {
		GC gc = new GC(viewer.getControl());
		final Image image = new Image(Display.getDefault(), viewer.getControl()
				.getSize().x, viewer.getControl().getSize().y);
		gc.copyArea(image, 0, 0);
		gc.dispose();
		return image;
	}
}
