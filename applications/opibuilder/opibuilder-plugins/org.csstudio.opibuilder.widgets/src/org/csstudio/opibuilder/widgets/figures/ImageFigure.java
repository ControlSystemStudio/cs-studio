/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.opibuilder.widgets.figures;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.util.concurrent.atomic.AtomicInteger;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.FigureTransparencyHelper;
import org.csstudio.swt.widgets.introspection.DefaultWidgetIntrospector;
import org.csstudio.swt.widgets.introspection.Introspectable;
import org.csstudio.swt.widgets.symbol.SymbolImage;
import org.csstudio.swt.widgets.symbol.SymbolImageFactory;
import org.csstudio.swt.widgets.symbol.SymbolImageListener;
import org.csstudio.swt.widgets.symbol.SymbolImageProperties;
import org.csstudio.swt.widgets.symbol.util.IImageListener;
import org.csstudio.swt.widgets.symbol.util.ImageUtils;
import org.csstudio.swt.widgets.symbol.util.PermutationMatrix;
import org.csstudio.swt.widgets.util.TextPainter;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * An image figure.
 *
 * @author Fred Arnaud (Sopra Steria Group) - ITER
 */
public final class ImageFigure extends Figure implements Introspectable, SymbolImageListener {

    /**
     * The {@link IPath} to the image.
     */
    private IPath filePath = new Path("");
    /**
     * The image itself.
     */
    private SymbolImage image;

    private SymbolImageProperties symbolProperties;

    private AtomicInteger remainingImagesToLoad = new AtomicInteger(0);

    private boolean animationDisabled = false;

    private IImageListener imageListener;

    private AbstractWidgetModel model;

    /**
     * dispose the resources used by this figure
     */
    public void dispose() {
        if (image != null && !image.isDisposed()) {
            image.dispose();
            image = null;
        }
    }

    public void setSymbolProperties(SymbolImageProperties symbolProperties, AbstractWidgetModel model) {
        this.symbolProperties = symbolProperties;
        this.model = model;
    }

    /**
     * Sets the path to the image.
     *
     * @param newval The path to the image
     */
    public void setFilePath(final IPath newval) {
        if (newval == null) {
            return;
        }
        this.filePath = newval;
        if (image != null) {
            image.dispose();
            image = null;
        }
        if (filePath != null && !filePath.isEmpty()) {
            incrementLoadingCounter();
        }
        image = SymbolImageFactory.asynCreateSymbolImage(filePath, true, symbolProperties, this);
    }

    public boolean isLoadingImage() {
        return remainingImagesToLoad.get() > 0;
    }

    public void decrementLoadingCounter() {
        remainingImagesToLoad.decrementAndGet();
    }

    public void incrementLoadingCounter() {
        remainingImagesToLoad.incrementAndGet();
    }

    @Override
    protected void paintClientArea(Graphics gfx) {
        if (isLoadingImage()) {
            return;
        }
        ImageUtils.crop(bounds, this.getInsets());
        if (bounds.width <= 0 || bounds.height <= 0) {
            return;
        }
        if (image == null || image.isEmpty()) {
            if (!filePath.isEmpty()) {
                gfx.setBackgroundColor(getBackgroundColor());
                gfx.setForegroundColor(getForegroundColor());
                gfx.fillRectangle(bounds);
                gfx.translate(bounds.getLocation());
                TextPainter.drawText(gfx, "ERROR in loading image\n" + filePath, bounds.width / 2, bounds.height / 2,
                        TextPainter.CENTER);
            }
            return;
        }
        image.setBounds(bounds);
        image.setAbsoluteScale(gfx.getAbsoluteScale());
        image.setBackgroundColor(getBackgroundColor());
        FigureTransparencyHelper.setBackground(image, getBackgroundColor(), model);
        image.paintFigure(gfx);
        super.paintClientArea(gfx);
    }

    // ************************************************************
    // Image size calculation delegation
    // ************************************************************

    public void resizeImage() {
        Rectangle bounds = getBounds().getCopy();
        if (image != null) {
            image.setBounds(bounds);
        }
        repaint();
    }

    public void setAutoSize(final boolean autoSize) {
        if (symbolProperties != null) {
            symbolProperties.setAutoSize(autoSize);
        }
        if (image != null) {
            image.setAutoSize(autoSize);
        }
        repaint();
    }

    /**
     * @return the auto sized widget dimension according to the static imageSize
     */
    public Dimension getAutoSizedDimension() {
        // Widget dimension = Symbol Image + insets
        if (image == null) {
            return null;
        }
        Dimension dim = image.getAutoSizedDimension();
        if (dim == null) {
            return null;
        }
        return new Dimension(dim.width + getInsets().getWidth(), dim.height + getInsets().getHeight());
    }

    // ************************************************************
    // Image crop calculation delegation
    // ************************************************************

    public void setLeftCrop(final int newval) {
        if (symbolProperties != null) {
            symbolProperties.setLeftCrop(newval);
        }
        if (image != null) {
            image.setLeftCrop(newval);
        }
        repaint();
    }

    public void setRightCrop(final int newval) {
        if (symbolProperties != null) {
            symbolProperties.setRightCrop(newval);
        }
        if (image != null) {
            image.setRightCrop(newval);
        }
        repaint();
    }

    public void setBottomCrop(final int newval) {
        if (symbolProperties != null) {
            symbolProperties.setBottomCrop(newval);
        }
        if (image != null) {
            image.setBottomCrop(newval);
        }
        repaint();
    }

    public void setTopCrop(final int newval) {
        if (symbolProperties != null) {
            symbolProperties.setTopCrop(newval);
        }
        if (image != null) {
            image.setTopCrop(newval);
        }
        repaint();
    }

    // ************************************************************
    // Image flip & degree & stretch calculation delegation
    // ************************************************************

    public void setStretch(final boolean newval) {
        if (symbolProperties != null) {
            symbolProperties.setStretch(newval);
        }
        if (image != null) {
            image.setStretch(newval);
        }
        repaint();
    }

    public void setPermutationMatrix(PermutationMatrix permutationMatrix) {
        if (symbolProperties != null) {
            symbolProperties.setMatrix(permutationMatrix);
        }
        if (image != null) {
            image.setPermutationMatrix(permutationMatrix);
        }
        repaint();
    }

    public PermutationMatrix getPermutationMatrix() {
        if (image == null) {
            return PermutationMatrix.generateIdentityMatrix();
        }
        return image.getPermutationMatrix();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (image != null) {
            image.setVisible(visible);
        }
    }

    /**
     * We want to have local coordinates here.
     *
     * @return True if here should used local coordinates
     */
    @Override
    protected boolean useLocalCoordinates() {
        return true;
    }

    public BeanInfo getBeanInfo() throws IntrospectionException {
        return new DefaultWidgetIntrospector().getBeanInfo(this.getClass());
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
        if (animationDisabled == stop) {
            return;
        }
        animationDisabled = stop;
        if (symbolProperties != null) {
            symbolProperties.setAnimationDisabled(stop);
        }
        if (image != null) {
            image.setAnimationDisabled(stop);
        }
        repaint();
    }

    public void setAlignedToNearestSecond(final boolean aligned) {
        if (symbolProperties != null) {
            symbolProperties.setAlignedToNearestSecond(aligned);
        }
        if (image != null) {
            image.setAlignedToNearestSecond(aligned);
        }
        repaint();
    }

    // ************************************************************
    // Symbol Image Listener
    // ************************************************************

    public void setImageLoadedListener(IImageListener listener) {
        this.imageListener = listener;
    }

    public void symbolImageLoaded() {
        decrementLoadingCounter();
        sizeChanged();
        revalidate();
        repaint();
    }

    public void repaintRequested() {
        repaint();
    }

    public void sizeChanged() {
        if (imageListener != null)
            imageListener.imageResized(this);
    }

}
