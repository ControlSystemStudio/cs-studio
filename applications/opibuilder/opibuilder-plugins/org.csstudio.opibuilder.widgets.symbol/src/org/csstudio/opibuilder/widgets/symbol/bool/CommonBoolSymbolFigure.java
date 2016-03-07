/*******************************************************************************
* Copyright (c) 2010-2016 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.opibuilder.widgets.symbol.bool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.util.AlarmRepresentationScheme;
import org.csstudio.opibuilder.widgets.FigureTransparencyHelper;
import org.csstudio.opibuilder.widgets.symbol.util.SymbolUtils;
import org.csstudio.swt.widgets.figures.AbstractBoolFigure;
import org.csstudio.swt.widgets.symbol.SymbolImage;
import org.csstudio.swt.widgets.symbol.SymbolImageFactory;
import org.csstudio.swt.widgets.symbol.SymbolImageListener;
import org.csstudio.swt.widgets.symbol.SymbolImageProperties;
import org.csstudio.swt.widgets.symbol.util.IImageListener;
import org.csstudio.swt.widgets.symbol.util.ImageUtils;
import org.csstudio.swt.widgets.symbol.util.PermutationMatrix;
import org.csstudio.swt.widgets.util.TextPainter;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * This class defines a common figure for Boolean Symbol Image widget based on
 * {@link CommonBoolSymbolModel}.
 *
 * @author SOPRA Group
 *
 */
public abstract class CommonBoolSymbolFigure extends AbstractBoolFigure
        implements SymbolImageListener {

    private static final String[] offStates = { "Off", "OFF", "off" };
    private static final String[] onStates = { "On", "ON", "on" };

    private String baseImagePath;
    private Map<Integer, String> statesMap;

    /**
     * The {@link IPath} to the on and off images.
     */
    private IPath onImagePath = new Path("");
    private IPath offImagePath = new Path("");

    /**
     * The on and off images themselves.
     */
    private SymbolImage onImage;
    private SymbolImage offImage;
    private SymbolImageProperties symbolProperties;

    private ExecutionMode executionMode;

    private IImageListener imageListener;
    private int remainingImagesToLoad = 0;

    private Color foregroundColor;
    private boolean useForegroundColor = false;

    private boolean animationDisabled = false;

    private CommonBoolSymbolModel model;

    public CommonBoolSymbolFigure() {
        super();
        statesMap = new HashMap<Integer, String>();
        statesMap.put(0, "off");
        statesMap.put(1, "on");
    }

    @Override
    protected void updateBoolValue() {
        super.updateBoolValue();
        if (onImage == null || offImage == null)
            return;
        if (booleanValue) {
            onImage.setVisible(true);
            offImage.setVisible(false);
        } else {
            onImage.setVisible(false);
            offImage.setVisible(true);
        }
        sizeChanged();
    }

    /**
     * Return the current displayed image. If null, returns an empty image.
     */
    public SymbolImage getCurrentImage() {
        SymbolImage image = null;
        if (isEditMode()) {
            image = offImage;
        } else {
            image = booleanValue ? onImage : offImage;
        }
        if (image == null) {
            image = SymbolImageFactory.createEmptyImage(!isEditMode());
        }
        return image;
    }

    /**
     * Return all mapped images.
     */
    public Collection<SymbolImage> getAllImages() {
        Collection<SymbolImage> list = new ArrayList<SymbolImage>();
        if (onImage == null) {
            onImage = SymbolImageFactory.asynCreateSymbolImage(onImagePath,
                    !isEditMode(), symbolProperties, this);
        }
        if (offImage == null) {
            offImage = SymbolImageFactory.asynCreateSymbolImage(offImagePath,
                    !isEditMode(), symbolProperties, this);
        }
        list.add(onImage);
        list.add(offImage);
        return list;
    }

    /**
     * Dispose the image resources used by this figure.
     */
    public void dispose() {
        if (onImage != null && !onImage.isDisposed()) {
            onImage.dispose();
            onImage = null;
        }
        if (offImage != null && !offImage.isDisposed()) {
            offImage.dispose();
            offImage = null;
        }
    }

    protected boolean isEditMode() {
        return ExecutionMode.EDIT_MODE.equals(executionMode);
    }

    public void setExecutionMode(ExecutionMode executionMode) {
        this.executionMode = executionMode;
    }

    public void setSymbolProperties(SymbolImageProperties symbolProperties) {
        this.symbolProperties = symbolProperties;
    }

    // ************************************************************
    // Image loading
    // ************************************************************

    public boolean isLoadingImage() {
        return remainingImagesToLoad > 0;
    }

    public synchronized void decrementLoadingCounter() {
        remainingImagesToLoad--;
    }

    public void setImageLoadedListener(IImageListener listener) {
        this.imageListener = listener;
    }

    public void fireImageResized() {
        if (imageListener != null)
            imageListener.imageResized(this);
    }

    /**
     * Set the boolean symbol on and off image path. If the path is relative,
     * then build absolute path.
     * <p>
     * <b>Rules:</b> <br>
     * If the image selected is on, then search off image. <br>
     * If the image selected is off, then search on image. <br>
     * If one or both is not found, the image path selected is the same for
     * both.
     *
     * @param model
     * @param imagePath The path to the selected image (on or off or other)
     */
    public void setSymbolImagePath(CommonBoolSymbolModel model,
            IPath imagePath) {
        this.model = model;
        if (imagePath == null || imagePath.isEmpty()) {
            return;
        }
        if (!imagePath.isAbsolute()) {
            imagePath = org.csstudio.opibuilder.util.ResourceUtil
                    .buildAbsolutePath(model, imagePath);
        }
        // Default
        this.onImagePath = imagePath;
        this.offImagePath = imagePath;
        loadAllImages();
    }

    public void updateImagesPathFromMeta(List<String> values) {
        if (values == null || values.size() < 2)
            return; // Not boolean
        if (!offImagePath.equals(onImagePath))
            return; // Already set
        // Search on/off files
        statesMap.put(0, statesMap.get(0) + "|" + values.get(0));
        statesMap.put(1, statesMap.get(1) + "|" + values.get(1));
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                loadAllImages();
            }
        });
    }

    private void loadAllImages() {
        if (offImagePath == null || onImagePath == null)
            return;
        baseImagePath = SymbolUtils.getBaseImagePath(offImagePath, statesMap);
        if (baseImagePath != null && !baseImagePath.isEmpty()) {
            int count = 0;
            IPath path = null;
            List<String> offStatesList = new ArrayList<>(Arrays.asList(offStates));
            offStatesList.addAll(Arrays.asList(StringUtils.split(statesMap.get(0), '|')));
            while (path == null && count < offStatesList.size()) {
                path = SymbolUtils.searchStateImage(offStatesList.get(count++), baseImagePath);
            }
            if (path == null)
                path = SymbolUtils.searchStateImage(0, baseImagePath);
            if (path != null)
                offImagePath = path;
            count = 0;
            path = null;
            List<String> onStatesList = new ArrayList<>(Arrays.asList(onStates));
            onStatesList.addAll(Arrays.asList(StringUtils.split(statesMap.get(1), '|')));
            while (path == null && count < onStatesList.size()) {
                path = SymbolUtils.searchStateImage(onStatesList.get(count++), baseImagePath);
            }
            if (path == null)
                path = SymbolUtils.searchStateImage(1, baseImagePath);
            if (path != null)
                onImagePath = path;
        }
        dispose();
        remainingImagesToLoad = 2;
        onImage = SymbolImageFactory.asynCreateSymbolImage(onImagePath,
                !isEditMode(), symbolProperties, this);
        offImage = SymbolImageFactory.asynCreateSymbolImage(offImagePath,
                !isEditMode(), symbolProperties, this);
    }

    // ************************************************************
    // Image size calculation delegation
    // ************************************************************

    public void resizeImage() {
        Rectangle bounds = getBounds().getCopy();
        if (!hasDisconnectedBorder())
            ImageUtils.crop(bounds, this.getInsets());
        for (SymbolImage si : getAllImages())
            si.setBounds(bounds);
        repaint();
    }

    public void setAutoSize(final boolean autoSize) {
        if (symbolProperties != null) {
            symbolProperties.setAutoSize(autoSize);
        }
        for (SymbolImage si : getAllImages())
            si.setAutoSize(autoSize);
        repaint();
    }

    public Dimension getAutoSizedDimension() {
        // Widget dimension = Symbol Image + insets
        Dimension dim = getCurrentImage().getAutoSizedDimension();
        if (dim == null) return null;
        if (hasDisconnectedBorder()) return dim;
        return new Dimension(dim.width + getInsets().getWidth(), dim.height
                + getInsets().getHeight());
    }

    private boolean hasDisconnectedBorder() {
        return getBorder() != null
                && getBorder().equals(
                        AlarmRepresentationScheme.getDisonnectedBorder());
    }

    @Override
    public void setBorder(Border b) {
        super.setBorder(b);
        sizeChanged();
    }

    // ************************************************************
    // Image crop calculation delegation
    // ************************************************************

    public void setLeftCrop(final int newval) {
        if (symbolProperties != null) {
            symbolProperties.setLeftCrop(newval);
        }
        for (SymbolImage si : getAllImages())
            si.setLeftCrop(newval);
        repaint();
    }

    public void setRightCrop(final int newval) {
        if (symbolProperties != null) {
            symbolProperties.setRightCrop(newval);
        }
        for (SymbolImage si : getAllImages())
            si.setRightCrop(newval);
        repaint();
    }

    public void setBottomCrop(final int newval) {
        if (symbolProperties != null) {
            symbolProperties.setBottomCrop(newval);
        }
        for (SymbolImage si : getAllImages())
            si.setBottomCrop(newval);
        repaint();
    }

    public void setTopCrop(final int newval) {
        if (symbolProperties != null) {
            symbolProperties.setTopCrop(newval);
        }
        for (SymbolImage si : getAllImages())
            si.setTopCrop(newval);
        repaint();
    }

    // ************************************************************
    // Image flip & degree & stretch calculation delegation
    // ************************************************************

    public void setStretch(final boolean newval) {
        if (symbolProperties != null) {
            symbolProperties.setStretch(newval);
        }
        for (SymbolImage si : getAllImages())
            si.setStretch(newval);
        repaint();
    }

    public void setPermutationMatrix(PermutationMatrix permutationMatrix) {
        if (symbolProperties != null) {
            symbolProperties.setMatrix(permutationMatrix);
        }
        for (SymbolImage si : getAllImages())
            si.setPermutationMatrix(permutationMatrix);
        repaint();
    }

    public PermutationMatrix getPermutationMatrix() {
        return getCurrentImage().getPermutationMatrix();
    }

    // ************************************************************
    // Image color & paint
    // ************************************************************

    /**
     * The main drawing routine.
     *
     * @param gfx The {@link Graphics} to use
     */
    @Override
    public void paintFigure(final Graphics gfx) {
        if (isLoadingImage())
            return;
        Rectangle bounds = getBounds().getCopy();
        if (!hasDisconnectedBorder())
            ImageUtils.crop(bounds, this.getInsets());
        if (bounds.width <= 0 || bounds.height <= 0)
            return;
        SymbolImage symbolImage = getCurrentImage();
        if (symbolImage.isEmpty() && isEditMode()) {
            return;
        } else if (symbolImage.isEmpty()) {
            IPath imagePath = booleanValue ? onImagePath : offImagePath;
            if (!imagePath.isEmpty()) {
                gfx.setBackgroundColor(getBackgroundColor());
                gfx.setForegroundColor(getForegroundColor());
                gfx.fillRectangle(bounds);
                gfx.translate(bounds.getLocation());
                TextPainter.drawText(gfx, "ERROR in loading image\n"
                        + imagePath, bounds.width / 2, bounds.height / 2,
                        TextPainter.CENTER);
            }
            return;
        }
        symbolImage.setBounds(bounds);
        symbolImage.setAbsoluteScale(gfx.getAbsoluteScale());
        Color currentcolor = null;
        if (useForegroundColor) currentcolor = getForegroundColor();
        else currentcolor = booleanValue ? onColor : offColor;
        symbolImage.setCurrentColor(currentcolor);
        FigureTransparencyHelper.setBackground(symbolImage, getBackgroundColor(), model);
        symbolImage.paintFigure(gfx);
    }

    /**
     * @param onColor the onColor to set
     */
    public void setOnColor(Color onColor) {
        if (this.onColor != null && this.onColor.equals(onColor)) {
            return;
        }
        if ((onColor.getRed() << 16 | onColor.getGreen() << 8 | onColor.getBlue()) == 0xFFFFFF) {
            this.onColor = CustomMediaFactory.getInstance().getColor(new RGB(255, 255, 254));
        } else {
            this.onColor = onColor;
        }
        repaint();
    }

    /**
     * @param offColor  the offColor to set
     */
    public void setOffColor(Color offColor) {
        if (this.offColor != null && this.offColor.equals(offColor)) {
            return;
        }
        if ((offColor.getRed() << 16 | offColor.getGreen() << 8 | offColor.getBlue()) == 0xFFFFFF) {
            this.offColor = CustomMediaFactory.getInstance().getColor(new RGB(255, 255, 254));
        } else {
            this.offColor = offColor;
        }
        repaint();
    }

    public void setUseForegroundColor(boolean useForegroundColor) {
        this.useForegroundColor = useForegroundColor;
        repaint();
    }

    @Override
    public Color getForegroundColor() {
        return foregroundColor;
    }

    @Override
    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
        if (foregroundColor != null)
            this.boolLabel.setForegroundColor(foregroundColor);
        repaint();
    }

    @Override
    public void setBackgroundColor(Color backgroundColor) {
        super.setBackgroundColor(backgroundColor);
        if (symbolProperties != null) {
            symbolProperties.setBackgroundColor(backgroundColor);
        }
        for (SymbolImage si : getAllImages())
            si.setBackgroundColor(backgroundColor);
        repaint();
    }

    // ************************************************************
    // Override Figure class methods
    // ************************************************************

    /**
     * We want to have local coordinates here.
     *
     * @return True if here should used local coordinates
     */
    @Override
    protected boolean useLocalCoordinates() {
        return true;
    }

    @Override
    protected void layout() {
        Rectangle clientArea = getClientArea().getCopy();
        if (boolLabel.isVisible()) {
            Dimension labelSize = boolLabel.getPreferredSize();
            boolLabel.setBounds(new Rectangle(getLabelLocation(clientArea.x
                    + clientArea.width / 2 - labelSize.width / 2, clientArea.y
                    + clientArea.height / 2 - labelSize.height / 2),
                    new Dimension(labelSize.width, labelSize.height)));
        }
        super.layout();
    }

    // ************************************************************
    // Animated images
    // ************************************************************

    /**
     * @return the animationDisabled
     */
    public boolean isAnimationDisabled() {
        return animationDisabled;
    }

    public void setAnimationDisabled(final boolean stop) {
        if (animationDisabled == stop)
            return;
        animationDisabled = stop;
        if (symbolProperties != null) {
            symbolProperties.setAnimationDisabled(stop);
        }
        for (SymbolImage asi : getAllImages())
            asi.setAnimationDisabled(stop);
        repaint();
    }

    public void setAlignedToNearestSecond(final boolean aligned) {
        if (symbolProperties != null) {
            symbolProperties.setAlignedToNearestSecond(aligned);
        }
        for (SymbolImage asi : getAllImages())
            asi.setAlignedToNearestSecond(aligned);
        repaint();
    }

    // ************************************************************
    // Symbol Image Listener
    // ************************************************************

    @Override
    public void symbolImageLoaded() {
        decrementLoadingCounter();
        fireImageResized();
        repaint();
        revalidate();
    }

    @Override
    public void repaintRequested() {
        repaint();
    }

    @Override
    public void sizeChanged() {
        imageListener.imageResized(this);
    }

}
