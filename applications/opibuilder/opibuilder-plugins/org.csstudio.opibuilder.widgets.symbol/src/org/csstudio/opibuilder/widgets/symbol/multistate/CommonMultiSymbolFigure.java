/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.symbol.multistate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.util.AlarmRepresentationScheme;
import org.csstudio.opibuilder.widgets.FigureTransparencyHelper;
import org.csstudio.opibuilder.widgets.symbol.Activator;
import org.csstudio.opibuilder.widgets.symbol.util.SymbolLabelPosition;
import org.csstudio.opibuilder.widgets.symbol.util.SymbolUtils;
import org.csstudio.swt.widgets.symbol.SymbolImage;
import org.csstudio.swt.widgets.symbol.SymbolImageFactory;
import org.csstudio.swt.widgets.symbol.SymbolImageListener;
import org.csstudio.swt.widgets.symbol.SymbolImageProperties;
import org.csstudio.swt.widgets.symbol.util.IImageListener;
import org.csstudio.swt.widgets.symbol.util.ImageUtils;
import org.csstudio.swt.widgets.symbol.util.PermutationMatrix;
import org.csstudio.swt.widgets.util.TextPainter;
import org.csstudio.ui.util.CustomMediaFactory;
import org.csstudio.ui.util.Draw2dSingletonUtil;
import org.diirt.vtype.VBoolean;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

/**
 * @author Fred Arnaud (Sopra Group)
 *
 * Form memory leak fix the states from double are rounded to long, so we do not create almost infinite number
 * of images for every state from double value.
 *
 * TODO: This has to be globally refactored so we use only the images that actually contain staes. No negative states should exist.
 *
 * @author Borut Terpinc
 */
public abstract class CommonMultiSymbolFigure extends Figure implements SymbolImageListener {

    private static final String BOOLEAN_VALUE_TRUE = "true";
    protected String baseImagePath;
    protected Map<Integer, String> statesMap;

    protected SymbolImageProperties symbolProperties;

    // symbol label attributes
    protected Label label;
    protected boolean showSymbolLabel = false;
    protected SymbolLabelPosition labelPosition = SymbolLabelPosition.DEFAULT;
    private Point labelLocation;

    /**
     * The {@link IPath} to the states images.
     */
    protected IPath symbolImagePath;
    protected SymbolImage currentSymbolImage;
    protected Map<Integer, SymbolImage> images;

    protected int currentStateIndex = -1;
    protected int previousStateIndex = -1;
    protected List<String> statesStr;
    protected List<Long> statesLong;

    private ExecutionMode executionMode;

    private IImageListener imageListener;
    private int remainingImagesToLoad = 0;

    protected Color onColor = CustomMediaFactory.getInstance().getColor(CommonMultiSymbolModel.DEFAULT_ON_COLOR);
    protected Color offColor = CustomMediaFactory.getInstance().getColor(CommonMultiSymbolModel.DEFAULT_OFF_COLOR);

    private Color foregroundColor;
    private boolean useForegroundColor = false;

    private boolean animationDisabled = false;
    private CommonMultiSymbolModel model;

    public CommonMultiSymbolFigure(boolean runMode) {
        this.executionMode = runMode ? ExecutionMode.RUN_MODE : ExecutionMode.EDIT_MODE;
        statesStr = new ArrayList<String>();
        statesLong = new ArrayList<Long>();
        images = new HashMap<Integer, SymbolImage>();
        statesMap = new HashMap<Integer, String>();
        label = new Label("STATE") {
            @Override
            public boolean containsPoint(int x, int y) {
                return false;
            }
        };
        label.setVisible(showSymbolLabel);
        // Add label to children
        add(label);
    }

    /**
     * Return the current displayed image. If null, returns an empty image.
     */
    public SymbolImage getSymbolImage() {
        if (ExecutionMode.RUN_MODE.equals(executionMode) && currentStateIndex >= 0) {
            SymbolImage imageToReturn = images.get(currentStateIndex);
            if (imageToReturn == null) {
                imageToReturn = SymbolImageFactory.createEmptyImage(true);
            }
            return imageToReturn;
        }
        if (currentSymbolImage == null) { // create an empty image
            currentSymbolImage = SymbolImageFactory.createEmptyImage(executionMode == ExecutionMode.RUN_MODE);
        }
        return currentSymbolImage;
    }

    /**
     * Return all mapped images.
     */
    public Collection<SymbolImage> getAllImages() {
        Collection<SymbolImage> list = new ArrayList<SymbolImage>();
        if (isEditMode() && currentSymbolImage != null) {
            list.add(currentSymbolImage);
        }
        if (!isEditMode() && images != null && !images.isEmpty()) {
            list = images.values();
        }
        return list;
    }

    /**
     * Dispose all the resources used by this figure
     */
    public void disposeAll() {
        disposeCurrent();
        for (SymbolImage img : getAllImages()) {
            if (img != null && !img.isDisposed()) {
                img.dispose();
                img = null;
            }
        }
        images.clear();
    }

    /**
     * Dispose the current resource used by this figure
     */
    public void disposeCurrent() {
        if (currentSymbolImage != null && !currentSymbolImage.isDisposed()) {
            currentSymbolImage.dispose();
            currentSymbolImage = null;
        }
    }

    public void setExecutionMode(ExecutionMode executionMode) {
        this.executionMode = executionMode;
    }

    public boolean isEditMode() {
        return ExecutionMode.EDIT_MODE.equals(executionMode);
    }

    public void setSymbolProperties(SymbolImageProperties symbolProperties) {
        this.symbolProperties = symbolProperties;
    }

    // ************************************************************
    // States management
    // ************************************************************

    public synchronized void setState(int stateIndex) {
        if (stateIndex >= 0 && stateIndex < statesStr.size()) {
            getSymbolImage().setVisible(false);
            currentStateIndex = stateIndex;
            getSymbolImage().setVisible(true);
            String currentState = statesStr.get(currentStateIndex);
            if (currentState != null)
                label.setText(currentState);
            sizeChanged();
            repaint();
        } else {
            // TODO: display alert ?
        }
    }

    public synchronized void setState(Double in_state) {


        long state = (Math.round(in_state));
        int index = statesLong.indexOf(state);

        if (index < 0) { // search if image exists
            statesLong.add(state);
            String strValue = String.valueOf(state);
                      statesStr.add(strValue);
            index = statesLong.indexOf(state);
            statesMap.put(index, strValue);
            IPath path = findImage(index);
            if (path == null)
                path = symbolImagePath;
            remainingImagesToLoad = 1;
            loadImageFromFile(path, index);
        }
        setState(index);
    }

    public synchronized void setState(String state) {

        int index = statesStr.indexOf(state);
        if (index < 0) { // search if image exists
            try {
                statesLong.add(Long.valueOf(state));
            } catch (NumberFormatException e) {
                statesLong.add(null);
            }
            statesStr.add(state);
            index = statesStr.indexOf(state);
            statesMap.put(index, state);
            IPath path = findImage(index);
            if (path == null)
                path = symbolImagePath;
            remainingImagesToLoad = 1;
            loadImageFromFile(path, index);
        }
        setState(index);
    }

    public synchronized void setState(VBoolean stateBoolean) {

        String state = stateBoolean.getValue().toString().toLowerCase();
        int index = statesStr.indexOf(state);
        if (index < 0) { // search if image exists
            try {
                statesLong.add(Long.valueOf(state));
            } catch (NumberFormatException e) {
                statesLong.add(null);
            }

            statesStr.add(state);
            index = BOOLEAN_VALUE_TRUE.equals(state)?1:0;

            statesMap.put(index, state);
            initRemainingStates(statesMap);
            IPath path = findImage(index);
            if (path == null)
                path = symbolImagePath;
            remainingImagesToLoad = 1;
            loadImageFromFile(path, index);
        }
        setState(index);
    }

    private void initRemainingStates(Map<Integer, String> statesMapCurrent) {
        for(int i=2; i<8; i++){
            statesMapCurrent.put(i,String.valueOf(i));
        }
    }

    /**
     * Set all the state string values.
     *
     * @param states the states
     */
    public void setStates(List<String> states) {
        this.statesStr = states;
        statesMap.clear();
        int stateIndex = 0;
        for (String state : states) {
            statesMap.put(stateIndex++, state);
            try {
                this.statesLong.add(Long.valueOf(state));
            } catch (NumberFormatException e) {
                this.statesLong.add(null);
            }
        }
        loadAllImages();
    }

    public String getCurrentState() {
        return statesStr.get(currentStateIndex);
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
        imageListener.imageResized(this);
    }

    /**
     * Set user selected image path (edit mode)
     *
     * @param model
     * @param imagePath
     */
    public void setSymbolImagePath(CommonMultiSymbolModel model, IPath imagePath) {
        this.model = model;
        if (imagePath == null || imagePath.isEmpty())
            return;
        if (!SymbolUtils.isExtensionAllowed(imagePath)) {
            Activator.getLogger().log(Level.WARNING, "ERROR in loading image, extension not allowed " + imagePath);
            return;
        }
        if (!imagePath.isAbsolute()) {
            imagePath = org.csstudio.opibuilder.util.ResourceUtil.buildAbsolutePath(model, imagePath);
        }
        symbolImagePath = imagePath;
        loadAllImages();
    }

    private void loadAllImages() {
        disposeAll();
        if (statesMap == null || statesMap.isEmpty()) {
            remainingImagesToLoad = 1;
            loadImageFromFile(symbolImagePath, null);
            return;
        }
        // Set threads variables
        remainingImagesToLoad = statesMap.size();
        baseImagePath = SymbolUtils.getBaseImagePath(symbolImagePath, statesMap);
        if (baseImagePath != null && !baseImagePath.isEmpty()) {
            // Retrieve & set images paths
            for (int stateIndex = 0; stateIndex < statesMap.size(); stateIndex++) {
                IPath path = findImage(stateIndex);
                if (path == null)
                    loadImageFromFile(symbolImagePath, stateIndex);
                else
                    loadImageFromFile(path, stateIndex);
            }
        } else { // Image do not match any state
            // TODO: alert state image missing
            for (int stateIndex = 0; stateIndex < statesMap.size(); stateIndex++) {
                // Load default image for all states
                loadImageFromFile(symbolImagePath, stateIndex);
            }
        }
    }

    private IPath findImage(int stateIndex) {
        if (symbolImagePath == null || symbolImagePath.isEmpty())
            return null;
        if (statesMap == null || statesMap.isEmpty())
            return null;
        baseImagePath = SymbolUtils.getBaseImagePath(symbolImagePath, statesMap);
        if (baseImagePath == null || baseImagePath.isEmpty())
            return null;
        IPath path = SymbolUtils.searchStateImage(statesMap.get(stateIndex), baseImagePath);
        if (path == null)
            path = SymbolUtils.searchStateImage(stateIndex, baseImagePath);

        return path;
    }

    private void loadImageFromFile(final IPath imagePath, final Integer stateIndex) {
        if (imagePath != null && !imagePath.isEmpty()) {
            switch (executionMode) {
            case RUN_MODE:
                if (stateIndex != null) {
                    SymbolImage img = SymbolImageFactory.asynCreateSymbolImage(imagePath, true, symbolProperties,
                            this);
                if (stateIndex != currentStateIndex)
                        img.setVisible(false);
                    if (images != null)
                        images.put(stateIndex, img);
                } else {
                    disposeCurrent();
                    currentSymbolImage = SymbolImageFactory.asynCreateSymbolImage(imagePath, true,
                            symbolProperties, this);
                }
                break;
            case EDIT_MODE:
                disposeCurrent();
                currentSymbolImage = SymbolImageFactory.asynCreateSymbolImage(imagePath, false, symbolProperties,
                        this);
                break;
            }
        }
    }

    // ************************************************************
    // Image color & paint
    // ************************************************************

    public void setOffColor(Color offColor) {
        if (this.offColor != null && this.offColor.equals(offColor))
            return;
        this.offColor = offColor;
        repaint();
    }

    public void setOnColor(Color onColor) {
        if (this.onColor != null && this.onColor.equals(onColor))
            return;
        this.onColor = onColor;
        repaint();
    }

    public void setUseForegroundColor(boolean useForegroundColor) {
        this.useForegroundColor = useForegroundColor;
        repaint();
    }

    @Override
    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
        if (foregroundColor != null)
            this.label.setForegroundColor(foregroundColor);
        repaint();
    }

    @Override
    public Color getForegroundColor() {
        return foregroundColor;
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

    @Override
    public void paintFigure(final Graphics gfx) {
        if (isLoadingImage())
            return;
        Rectangle bounds = getBounds().getCopy();
        if (!hasDisconnectedBorder())
            ImageUtils.crop(bounds, this.getInsets());
        if (bounds.width <= 0 || bounds.height <= 0)
            return;
        SymbolImage symbolImage = getSymbolImage();
        if (symbolImage.isEmpty() && isEditMode()) {
            return;
        } else if (symbolImage.isEmpty()) {
            gfx.setBackgroundColor(getBackgroundColor());
            gfx.setForegroundColor(getForegroundColor());
            gfx.fillRectangle(bounds);
            gfx.translate(bounds.getLocation());
            TextPainter.drawText(gfx, "??", bounds.width / 2, bounds.height / 2, TextPainter.CENTER);
            return;
        }
        symbolImage.setBounds(bounds);
        symbolImage.setAbsoluteScale(gfx.getAbsoluteScale());
        Color currentcolor = null;
        if (useForegroundColor)
            currentcolor = getForegroundColor();
        else
            currentcolor = currentStateIndex <= 0 ? offColor : onColor;
        symbolImage.setCurrentColor(currentcolor);
        FigureTransparencyHelper.setBackground(symbolImage, getBackgroundColor(), model);
        symbolImage.paintFigure(gfx);
    }

    // ************************************************************
    // Label management
    // ************************************************************

    protected Point getLabelLocation(final int x, final int y) {
        return getLabelLocation(new Point(x, y));
    }

    /**
     * @param defaultLocation The default location.
     * @return the location of the symbol label
     */
    protected Point getLabelLocation(Point defaultLocation) {
        if (labelLocation == null)
            calculateLabelLocation(defaultLocation);
        return labelLocation;
    }

    protected void calculateLabelLocation(Point defaultLocation) {
        if (labelPosition == SymbolLabelPosition.DEFAULT) {
            labelLocation = defaultLocation;
            return;
        }
        Rectangle textArea = getClientArea();
        Dimension textSize = Draw2dSingletonUtil.getTextUtilities().getTextExtents(label.getText(), getFont());
        int x = 0;
        if (textArea.width > textSize.width) {
            switch (labelPosition) {
            case CENTER:
            case TOP:
            case BOTTOM:
                x = (textArea.width - textSize.width) / 2;
                break;
            case RIGHT:
            case TOP_RIGHT:
            case BOTTOM_RIGHT:
                x = textArea.width - textSize.width;
                break;
            default:
                break;
            }
        }
        int y = 0;
        if (textArea.height > textSize.height) {
            switch (labelPosition) {
            case CENTER:
            case LEFT:
            case RIGHT:
                y = (textArea.height - textSize.height) / 2;
                break;
            case BOTTOM:
            case BOTTOM_LEFT:
            case BOTTOM_RIGHT:
                y = textArea.height - textSize.height;
                break;
            default:
                break;
            }
        }
        if (useLocalCoordinates())
            labelLocation = new Point(x, y);
        else
            labelLocation = new Point(x + textArea.x, y + textArea.y);
    }

    public void setSymbolLabelPosition(SymbolLabelPosition labelPosition) {
        this.labelPosition = labelPosition;
        labelPosition = null;
        repaint();
        revalidate();
    }

    /**
     * @param showSymbolLabel the showSymbolLabel to set
     */
    public void setShowSymbolLabel(boolean showSymbolLabel) {
        if (this.showSymbolLabel == showSymbolLabel)
            return;
        this.showSymbolLabel = showSymbolLabel;
        label.setVisible(showSymbolLabel);
    }

    /**
     * @return the showSymbolLabel
     */
    public boolean isShowSymbolLabel() {
        return showSymbolLabel;
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
        Dimension dim = getSymbolImage().getAutoSizedDimension();
        if (dim == null)
            return null;
        if (hasDisconnectedBorder())
            return dim;
        return new Dimension(dim.width + getInsets().getWidth(), dim.height + getInsets().getHeight());
    }

    private boolean hasDisconnectedBorder() {
        return getBorder() != null && getBorder().equals(AlarmRepresentationScheme.getDisonnectedBorder());
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
        return getSymbolImage().getPermutationMatrix();
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
    public void setFont(Font f) {
        super.setFont(f);
        label.setFont(f);
        revalidate();
    }

    @Override
    public void invalidate() {
        labelLocation = null;
        super.invalidate();
    }

    @Override
    protected void layout() {
        Rectangle clientArea = getClientArea().getCopy();
        if (label.isVisible()) {
            Dimension labelSize = label.getPreferredSize();
            label.setBounds(new Rectangle(getLabelLocation(clientArea.x + clientArea.width / 2 - labelSize.width / 2,
                    clientArea.y + clientArea.height / 2 - labelSize.height / 2), new Dimension(labelSize.width,
                            labelSize.height)));
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
