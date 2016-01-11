/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.symbol;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.csstudio.swt.widgets.Activator;
import org.csstudio.swt.widgets.symbol.util.ImageUtils;
import org.csstudio.swt.widgets.util.AbstractInputStreamRunnable;
import org.csstudio.swt.widgets.util.IJobErrorHandler;
import org.csstudio.swt.widgets.util.ResourceUtil;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

public class PNGSymbolImage extends AbstractSymbolImage {

    private Dimension imgDimension = null;

    private boolean loadingImage = false;

    public PNGSymbolImage(SymbolImageProperties sip, boolean runMode) {
        super(sip, runMode);
    }

    public void setOriginalImageData(ImageData originalImageData) {
        this.originalImageData = originalImageData;
        resetData();
    }

    // ************************************************************
    // Image color & paint
    // ************************************************************

    public void paintFigure(final Graphics gfx) {
        if (disposed || loadingImage || originalImageData == null)
            return;
        // Generate Data
        if (imageData == null) {
            generatePNGData();
            if (image != null && !image.isDisposed()) {
                image.dispose();
                image = null;
            }
        }
        // Create image
        if (image == null) {
            if (imageData == null)
                return;
            image = new Image(Display.getDefault(), imageData);
        }
        // Calculate areas
        if (bounds == null || imgDimension == null)
            return;
        Rectangle srcArea = new Rectangle(leftCrop, topCrop, imgDimension.width, imgDimension.height);
        Rectangle destArea = new Rectangle(bounds.x, bounds.y, imgDimension.width, imgDimension.height);
        if (backgroundColor != null) {
            gfx.setBackgroundColor(backgroundColor);
            gfx.fillRectangle(destArea);
        }
        // Draw graphic image
        if (image != null) {
            gfx.drawImage(image, srcArea, destArea);
        }
    }

    @Override
    public void resetData() {
        imageData = null;
    }

    private void generatePNGData() {
        if (disposed || originalImageData == null)
            return;

        imageData = (ImageData) originalImageData.clone();
        if (!colorToChange.equals(currentColor))
            imageData = ImageUtils.changeImageColor(currentColor, imageData);
        imageData = ImageUtils.applyMatrix(imageData, permutationMatrix);
        if (stretch && bounds != null) {
            imageData = imageData.scaledTo(bounds.width + leftCrop + rightCrop,
                    bounds.height + topCrop + bottomCrop);
        }
        int imgWidth = imageData.width;
        int imgHeight = imageData.height;

        // Avoid negative number
        topCrop = topCrop > imgHeight ? 0 : topCrop;
        leftCrop = leftCrop > imgWidth ? 0 : leftCrop;
        bottomCrop = (imgHeight - topCrop - bottomCrop) < 0 ? 0 : bottomCrop;
        rightCrop = (imgWidth - leftCrop - rightCrop) < 0 ? 0 : rightCrop;

        // Calculate areas
        int cropedWidth = imageData.width - leftCrop - rightCrop;
        int cropedHeight = imageData.height - bottomCrop - topCrop;
        Dimension newImgDimension = new Dimension(cropedWidth, cropedHeight);
        if (imgDimension == null || newImgDimension.width != imgDimension.width
                || newImgDimension.height != imgDimension.height)
            fireSizeChanged();
        imgDimension = newImgDimension;
    }

    // ************************************************************
    // Image size calculation
    // ************************************************************

    public Dimension getAutoSizedDimension() {
        // if (imgDimension == null)
        // generatePNGData();
        return imgDimension;
    }

    // ************************************************************
    // Image loading
    // ************************************************************

    public void syncLoadImage() {
        if (imagePath == null)
            return;
        InputStream stream = null;
        Image tempImage = null;
        try {
            stream = ResourceUtil.pathToInputStream(imagePath.toPortableString());
            tempImage = new Image(Display.getDefault(), stream);
            ImageData imgData = tempImage.getImageData();
            setOriginalImageData(imgData);
        } catch (Exception e) {
            Activator.getLogger().log(Level.WARNING,
                    "ERROR in loading PNG image " + imagePath, e);
        } finally {
            try {
                if (stream != null) stream.close();
                if (tempImage != null && !tempImage.isDisposed())
                    tempImage.dispose();
            } catch (IOException e) {
                Activator.getLogger().log(Level.WARNING,
                        "ERROR in closing PNG image stream ", e);
            }
        }
    }

    public void asyncLoadImage() {
        if (imagePath == null)
            return;
        loadingImage = true;
        loadImage(new IJobErrorHandler() {
            private int maxAttempts = 5;

            public void handleError(Exception exception) {
                if (maxAttempts-- > 0) {
                    try {
                        Thread.sleep(100);
                        loadImage(this);
                        return;
                    } catch (InterruptedException e) {
                    }
                }
                loadingImage = false;
                // fireSymbolImageLoaded();
                Activator.getLogger().log(Level.WARNING,
                        "ERROR in loading PNG image " + imagePath, exception);
            }
        });
    }

    private void loadImage(IJobErrorHandler errorHandler) {
        AbstractInputStreamRunnable uiTask = new AbstractInputStreamRunnable() {
            @Override
            public void runWithInputStream(InputStream stream) {
                synchronized (PNGSymbolImage.this) {
                    Image tempImage = null;
                    try {
                        tempImage = new Image(Display.getDefault(), stream);
                        ImageData imgData = tempImage.getImageData();
                        setOriginalImageData(imgData);
                    } finally {
                        try {
                            stream.close();
                            if (tempImage != null && !tempImage.isDisposed()) {
                                tempImage.dispose();
                            }
                        } catch (IOException e) {
                            Activator.getLogger().log(Level.WARNING,
                                    "ERROR in closing PNG image stream ", e);
                        }
                    }
                    loadingImage = false;
                    Display.getDefault().syncExec(new Runnable() {
                        public void run() {
                            fireSymbolImageLoaded();
                        }
                    });
                }
            }
        };
        ResourceUtil.pathToInputStreamInJob(imagePath, uiTask,
                "Loading PNG Image...", errorHandler);
    }

}
