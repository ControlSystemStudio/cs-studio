/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
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
        String ext = imagePath.getFileExtension().toLowerCase();
        if ("png".equals(ext) || "jpg".equals(ext) || "jpeg".equals(ext)
                || "bmp".equals(ext)) {
            symbolImage = new PNGSymbolImage(sip, runMode);
        } else if ("svg".equals(ext)) {
            symbolImage = new SVGSymbolImage(sip, runMode);
        } else if ("gif".equals(ext)) {
            symbolImage = new GIFSymbolImage(sip, runMode);
        }
        if (symbolImage == null) {
            return createEmptyImage(runMode);
        }
        symbolImage.setImagePath(imagePath);
        return symbolImage;
    }

}
