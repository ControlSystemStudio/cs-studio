/*******************************************************************************
 * Copyright (c) 2010-2015 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.symbol;

import org.eclipse.core.runtime.IPath;

/**
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class SymbolImageFactory {

	public static SymbolImage asynCreateSymbolImage(IPath imagePath,
			boolean runMode, SymbolImageProperties sip,
			SymbolImageListener listener) {
		if (imagePath == null || imagePath.isEmpty())
			return createEmptyImage(runMode);
		SymbolImage symbolImage = createImageFromPath(imagePath, sip, runMode);
		symbolImage.setListener(listener);
		symbolImage.asyncLoadImage();
		return symbolImage;
	}

	public static SymbolImage synCreateSymbolImage(IPath imagePath,
			boolean runMode, SymbolImageProperties sip) {
		if (imagePath == null || imagePath.isEmpty())
			return createEmptyImage(runMode);
		SymbolImage symbolImage = createImageFromPath(imagePath, sip, runMode);
		symbolImage.syncLoadImage();
		return symbolImage;
	}

	public static SymbolImage createEmptyImage(boolean runMode) {
		return new PNGSymbolImage(null, runMode);
	}

	private static SymbolImage createImageFromPath(IPath imagePath,
			SymbolImageProperties sip, boolean runMode) {
		SymbolImage symbolImage = null;
		if ("png".equals(imagePath.getFileExtension().toLowerCase()))
			symbolImage = new PNGSymbolImage(sip, runMode);
		if ("svg".equals(imagePath.getFileExtension().toLowerCase()))
			symbolImage = new SVGSymbolImage(sip, runMode);
		if ("gif".equals(imagePath.getFileExtension().toLowerCase()))
			symbolImage = new GIFSymbolImage(sip, runMode);
		symbolImage.setImagePath(imagePath);
		return symbolImage;
	}

}
