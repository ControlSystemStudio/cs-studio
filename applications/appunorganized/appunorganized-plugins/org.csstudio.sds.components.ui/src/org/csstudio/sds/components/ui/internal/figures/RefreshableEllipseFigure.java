/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, Member of the Helmholtz Association,
 * (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.ui.figures.BorderAdapter;
import org.csstudio.sds.ui.figures.CrossedOutAdapter;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.ui.figures.ICrossedFigure;
import org.csstudio.sds.ui.figures.IRhombusEquippedWidget;
import org.csstudio.sds.ui.figures.RhombusAdapter;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

/**
 * An ellipse figure.
 *
 * @author Sven Wende, Alexander Will
 *
 */
public final class RefreshableEllipseFigure extends Ellipse implements IAdaptable {

    /**
     * The fill grade (0 - 100%).
     */
    private double _fill = 100.0;

    /**
     * The orientation (horizontal==true | vertical==false).
     */
    private boolean _orientationHorizontal = true;

    /**
     * The transparent state of the background.
     */
    private boolean _transparent = false;

    /**
     * A border adapter, which covers all border handlings.
     */
    private IBorderEquippedWidget _borderAdapter;


    private IRhombusEquippedWidget _rhombusAdapter;

    private CrossedOutAdapter _crossedOutAdapter;

    public RefreshableEllipseFigure() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillShape(final Graphics graphics) {
        Rectangle figureBounds = getBounds().getCopy();
        figureBounds.crop(this.getInsets());

        Rectangle backgroundRectangle;
        Rectangle fillRectangle;
        if (_orientationHorizontal) {
            int newW = (int) Math.round(figureBounds.width * (getFill() / 100));
            backgroundRectangle = new Rectangle(figureBounds.x + newW,
                                                figureBounds.y,
                                                figureBounds.width - newW,
                                                figureBounds.height);
            fillRectangle = new Rectangle(figureBounds.x, figureBounds.y, newW, figureBounds.height);
        } else {
            int newH = (int) Math.round(figureBounds.height * (getFill() / 100));
            backgroundRectangle = new Rectangle(figureBounds.x,
                                                figureBounds.y,
                                                figureBounds.width,
                                                figureBounds.height - newH);
            fillRectangle = new Rectangle(figureBounds.x, figureBounds.y + figureBounds.height
                    - newH, figureBounds.width, newH);
        }
        if (!_transparent) {
            graphics.pushState();
            graphics.setClip(backgroundRectangle);
            graphics.setBackgroundColor(getBackgroundColor());
            graphics.fillOval(figureBounds);
            graphics.popState();
        }

        graphics.pushState();
        graphics.clipRect(fillRectangle);
        graphics.setBackgroundColor(getForegroundColor());
        graphics.fillOval(figureBounds);
        graphics.popState();
        _crossedOutAdapter.paint(graphics);
        _rhombusAdapter.paint(graphics);
     }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintFigure(final Graphics graphics) {
        this.fillShape(graphics);
    }

    /**
     * This method is a tribute to unit tests, which need a way to test the performance of the
     * figure implementation. Implementors should produce some random changes and refresh the
     * figure, when this method is called.
     *
     */
    public void randomNoiseRefresh() {
        setFill(Math.random() * 100);
        repaint();
    }

    /**
     * Sets the fill grade.
     *
     * @param fill
     *            the fill grade.
     */
    public void setFill(final double fill) {
        _fill = fill;
    }

    /**
     * Gets the fill grade.
     *
     * @return the fill grade
     */
    public double getFill() {
        return _fill;
    }

    /**
     * Sets the transparent state of the background.
     *
     * @param transparent
     *            the transparent state.
     */
    public void setTransparent(final boolean transparent) {
        _transparent = transparent;
    }

    /**
     * Gets the transparent state of the background.
     *
     * @return the transparent state of the background
     */
    public boolean getTransparent() {
        return _transparent;
    }

    /**
     * Sets the orientation (horizontal==true | vertical==false).
     *
     * @param horizontal
     *            The orientation.
     */
    public void setOrientation(final boolean horizontal) {
        _orientationHorizontal = horizontal;
    }

    /**
     * Gets the orientation (horizontal==true | vertical==false).
     *
     * @return boolean The orientation
     */
    public boolean getOrientation() {
        return _orientationHorizontal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object getAdapter(final Class adapter) {
        if (adapter == IBorderEquippedWidget.class) {
            if (_borderAdapter == null) {
                _borderAdapter = new BorderAdapter(this) {
                    @Override
                    protected AbstractBorder createShapeBorder(final int borderWidth,
                                                               final Color borderColor) {
                        EllipseBorder border = new EllipseBorder(borderWidth);
                        border.setBorderColor(borderColor);
                        return border;
                    }
                };
            }
            return _borderAdapter;
        }  else if(adapter == ICrossedFigure.class) {
            if(_crossedOutAdapter==null) {
                _crossedOutAdapter = new CrossedOutAdapter(this);
            }
            return _crossedOutAdapter;
        } else if(adapter == IRhombusEquippedWidget.class) {
            if(_rhombusAdapter==null) {
                _rhombusAdapter = new RhombusAdapter(this);
            }
            return _rhombusAdapter;
        }

        return null;
    }

    /**
     * The Border for this {@link RefreshableEllipseFigure}.
     *
     * @author Kai Meyer
     *
     */
    private final class EllipseBorder extends AbstractBorder {
        /**
         * The insets for this Border.
         */
        private final Insets _insets;
        /**
         * The Color of the border.
         */
        private Color _borderColor;
        /**
         * The width of the border.
         */
        private int _borderWidth = 0;

        /**
         * Constructor.
         *
         * @param borderWidth
         *            The width for the border
         */
        public EllipseBorder(final int borderWidth) {
            _insets = new Insets(Math.max(borderWidth - 1, 0));
            _borderWidth = borderWidth;
        }

        /**
         * Sets the Color of the border.
         *
         * @param borderColor
         *            The Color for the border
         */
        public void setBorderColor(final Color borderColor) {
            _borderColor = borderColor;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Insets getInsets(final IFigure figure) {
            return _insets;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void paint(final IFigure figure, final Graphics graphics, final Insets insets) {
            if (_borderWidth > 0) {
                Rectangle rectangle = figure.getBounds().getCopy();
                graphics.setBackgroundColor(_borderColor);
                graphics.setForegroundColor(_borderColor);
                graphics.setLineWidth(_borderWidth);
                int correction = (int) Math.ceil((double) (_borderWidth) / 2);
                rectangle.crop(new Insets(correction - 1, correction - 1, correction, correction));
                graphics.drawOval(rectangle);
            }
        }

    }

}
