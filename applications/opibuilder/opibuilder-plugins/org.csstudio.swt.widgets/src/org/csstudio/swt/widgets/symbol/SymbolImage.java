/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.symbol;

import org.csstudio.swt.widgets.symbol.util.PermutationMatrix;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.ImageData;

/**
 * Main interface for Symbol Image display.
 *
 * @author Fred Arnaud (Sopra Group)
 */
public interface SymbolImage {

    public final static Color DISABLE_COLOR = CustomMediaFactory.getInstance()
            .getColor(CustomMediaFactory.COLOR_GRAY);

    /** The alpha (0 is transparency and 255 is opaque) for disabled paint */
    public static final int DISABLED_ALPHA = 100;

    public void setImagePath(IPath imagePath);

    public IPath getImagePath();

    public ImageData getOriginalImageData();

    /**
     * Dispose the resource used by this figure
     */
    public void dispose();

    public void setVisible(boolean visible);

    public boolean isDisposed();

    public boolean isEditMode();

    public boolean isEmpty();

    public void setCurrentColor(Color newColor);

    public void setColorToChange(Color newColor);

    public void setBackgroundColor(Color newColor);

    /**
     * The main drawing routine.
     *
     * @param gfx The {@link Graphics} to use
     */
    public void paintFigure(final Graphics gfx);

    public void setBounds(Rectangle newArea);

    public void setAbsoluteScale(double newScale);

    /**
     * Resizes the image.
     */
    public void resizeImage();

    /**
     * Automatically adjust the widget bounds to fit the size of the static
     * image
     *
     * @param autoSize
     */
    public void setAutoSize(final boolean autoSize);

    /**
     * Set the stretch state for the image.
     *
     * @param newval true, if it should be stretched, false otherwise)
     */
    public void setStretch(final boolean newval);

    /**
     * Get the auto sized widget dimension according to the static image size.
     *
     * @return The auto sized widget dimension.
     */
    public Dimension getAutoSizedDimension();

    /**
     * Sets the amount of pixels, which are cropped from the left.
     */
    public void setLeftCrop(final int newval);

    /**
     * Sets the amount of pixels, which are cropped from the right.
     */
    public void setRightCrop(final int newval);

    /**
     * Sets the amount of pixels, which are cropped from the bottom.
     */
    public void setBottomCrop(final int newval);

    /**
     * Sets the amount of pixels, which are cropped from the top.
     */
    public void setTopCrop(final int newval);

    public void setPermutationMatrix(final PermutationMatrix permutationMatrix);

    public PermutationMatrix getPermutationMatrix();

    public void setAnimationDisabled(final boolean stop);

    public void setListener(SymbolImageListener listener);

    public void syncLoadImage();

    public void asyncLoadImage();

    public void setAlignedToNearestSecond(boolean aligned);

}
