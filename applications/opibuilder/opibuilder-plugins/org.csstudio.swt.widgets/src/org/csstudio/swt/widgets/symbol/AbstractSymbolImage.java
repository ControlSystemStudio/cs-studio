/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.symbol;

import org.csstudio.swt.widgets.symbol.util.PermutationMatrix;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Main class for Symbol Image display.
 *
 * @author Fred Arnaud (Sopra Group)
 */
public abstract class AbstractSymbolImage implements SymbolImage {

    private boolean runMode;

    protected double scale = 1.0;
    protected Rectangle bounds;

    protected IPath imagePath;

    protected Image image;
    protected ImageData imageData;
    protected ImageData originalImageData;

    private SymbolImageListener listener;

    protected Color currentColor;
    protected Color colorToChange;
    protected Color backgroundColor;

    protected int leftCrop = 0;
    protected int rightCrop = 0;
    protected int bottomCrop = 0;
    protected int topCrop = 0;
    protected boolean stretch = false;
    protected boolean autoSize = true;
    protected PermutationMatrix oldPermutationMatrix = null;
    protected PermutationMatrix permutationMatrix = PermutationMatrix
            .generateIdentityMatrix();

    protected boolean animationDisabled = false;
    protected boolean alignedToNearestSecond = false;
    protected boolean visible = true;
    protected boolean disposed = false;

    public AbstractSymbolImage(SymbolImageProperties sip, boolean runMode) {
        this.runMode = runMode;
        this.currentColor = new Color(Display.getCurrent(), new RGB(0, 0, 0));
        this.colorToChange = new Color(Display.getCurrent(), new RGB(0, 0, 0));
        fillProperties(sip);
    }

    private void fillProperties(SymbolImageProperties sip) {
        if (sip == null)
            return;
        this.topCrop = sip.getTopCrop();
        this.bottomCrop = sip.getBottomCrop();
        this.leftCrop = sip.getLeftCrop();
        this.rightCrop = sip.getRightCrop();
        this.permutationMatrix = sip.getMatrix();
        this.stretch = sip.isStretch();
        this.autoSize = sip.isAutoSize();
        this.animationDisabled = sip.isAnimationDisabled();
        this.alignedToNearestSecond = sip.isAlignedToNearestSecond();
        this.backgroundColor = sip.getBackgroundColor() == null ? new Color(
                Display.getCurrent(), new RGB(0, 0, 0)) : sip.getBackgroundColor();
        this.colorToChange = sip.getColorToChange() == null ? new Color(
                Display.getCurrent(), new RGB(0, 0, 0)) : sip.getColorToChange();
    }

    public IPath getImagePath() {
        return imagePath;
    }

    public void setImagePath(IPath imagePath) {
        this.imagePath = imagePath;
    }

    public ImageData getOriginalImageData() {
        return originalImageData;
    }

    public void dispose() {
        disposed = true;
        if (image != null && !image.isDisposed()) {
            image.dispose();
            image = null;
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isDisposed() {
        return disposed;
    }

    public boolean isEditMode() {
        return !runMode;
    }

    public boolean isEmpty() {
        return originalImageData == null;
    }

    // ************************************************************
    // Image color & paint
    // ************************************************************

    public abstract void resetData();

    public void setCurrentColor(Color newColor) {
        if (isEditMode())
            return;
        if (newColor == null
                || (currentColor != null && currentColor.equals(newColor)))
            return;
        this.currentColor = newColor;
        resetData();
    }

    public void setColorToChange(Color newColor) {
        if (isEditMode())
            return;
        if (newColor == null
                || (colorToChange != null && colorToChange.equals(newColor)))
            return;
        this.colorToChange = newColor;
        resetData();
    }

    public void setBackgroundColor(Color newColor) {
        if ((this.backgroundColor == null && newColor == null)
                || (this.backgroundColor != null && this.backgroundColor.equals(newColor)))
            return;
        this.backgroundColor = newColor;
    }

    // ************************************************************
    // Image size calculation
    // ************************************************************

    public abstract Dimension getAutoSizedDimension();

    public void resizeImage() {
        resetData();
    }

    public void setBounds(Rectangle newBounds) {
        if (newBounds == null || newBounds.equals(this.bounds)
                || newBounds.width <= 0 || newBounds.height <= 0)
            return;
        if (this.bounds == null) {
            this.bounds = newBounds.getCopy();
            resizeImage();
            return;
        }
        Rectangle oldBounds = this.bounds.getCopy();
        this.bounds = newBounds.getCopy();
        if (autoSize) {
            Dimension dim = getAutoSizedDimension();
            if (dim == null)
                return;
            if (newBounds.width != dim.width || newBounds.height != dim.height)
                resizeImage();
        } else {
            if (oldBounds.width != newBounds.width
                    || oldBounds.height != newBounds.height)
                resizeImage();
        }
    }

    public void setAbsoluteScale(double newScale) {
        if (this.scale == newScale)
            return;
        this.scale = newScale;
    }

    public void setAutoSize(final boolean autoSize) {
        if (this.autoSize == autoSize)
            return;
        this.autoSize = autoSize;
        if (!stretch && autoSize)
            resizeImage();
    }

    public void setStretch(final boolean newval) {
        if (stretch == newval)
            return;
        stretch = newval;
        resizeImage();
    }

    // ************************************************************
    // Image crop calculation
    // ************************************************************

    public void setLeftCrop(final int newval) {
        if (leftCrop == newval || newval < 0) {
            return;
        }
        leftCrop = newval;
        resizeImage();
    }

    public void setRightCrop(final int newval) {
        if (rightCrop == newval || newval < 0) {
            return;
        }
        rightCrop = newval;
        resizeImage();
    }

    public void setBottomCrop(final int newval) {
        if (bottomCrop == newval || newval < 0) {
            return;
        }
        bottomCrop = newval;
        resizeImage();
    }

    public void setTopCrop(final int newval) {
        if (topCrop == newval || newval < 0) {
            return;
        }
        topCrop = newval;
        resizeImage();
    }

    // ************************************************************
    // Image rotation calculation
    // ************************************************************

    public void setPermutationMatrix(final PermutationMatrix permutationMatrix) {
        this.oldPermutationMatrix = this.permutationMatrix;
        this.permutationMatrix = permutationMatrix;
        if (permutationMatrix == null
                || (oldPermutationMatrix != null && oldPermutationMatrix
                        .equals(permutationMatrix)))
            return;
        resizeImage();
    }

    public PermutationMatrix getPermutationMatrix() {
        return permutationMatrix;
    }

    // ************************************************************
    // Animated images
    // ************************************************************

    public void setAnimationDisabled(final boolean stop) {
        if (animationDisabled == stop)
            return;
        animationDisabled = stop;
    }

    public void setAlignedToNearestSecond(boolean aligned) {
        this.alignedToNearestSecond = aligned;
    }

    // ************************************************************
    // Image loading
    // ************************************************************

    public void setListener(SymbolImageListener listener) {
        this.listener = listener;
    }

    protected void fireSymbolImageLoaded() {
        if (listener != null)
            listener.symbolImageLoaded();
    }

    protected void fireSizeChanged() {
        if (listener != null && visible)
            listener.sizeChanged();
    }

    protected void repaint() {
        if (listener != null && visible)
            listener.repaintRequested();
    }

}
