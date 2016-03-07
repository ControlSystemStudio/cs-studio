/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.ui.figures.BorderAdapter;
import org.csstudio.sds.ui.figures.CrossedOutAdapter;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.ui.figures.ICrossedFigure;
import org.csstudio.sds.ui.figures.IRhombusEquippedWidget;
import org.csstudio.sds.ui.figures.RhombusAdapter;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polygon;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.handles.HandleBounds;
import org.eclipse.swt.graphics.Color;

/**
 * A polygon figure.
 *
 * @author Sven Wende & Stefan Hofer
 *
 */
public final class RefreshablePolygonFigure extends Polygon implements
    IAdaptable, HandleBounds {

    /**
     * The fill grade (0 - 100%).
     */
    private double _fill = 100.0;

    /**
     * A border adapter, which covers all border handlings.
     */
    private IBorderEquippedWidget _borderAdapter;

    private CrossedOutAdapter _crossedOutAdapter;

    private RhombusAdapter _rhombusAdapter;


    /**
     * Constructor.
     */
    public RefreshablePolygonFigure() {
        setFill(true);
        setBackgroundColor(ColorConstants.darkGreen);

    }

    @Override
    public void paint(final Graphics graphics) {
        super.paint(graphics);
        Rectangle figureBounds = new Rectangle(getBounds());
        if(figureBounds.width<figureBounds.height) {
            if(figureBounds.width>12) {
                figureBounds.y = figureBounds.y+5;
                figureBounds.height = figureBounds.height-10;
            } else if(figureBounds.width>6) {
                figureBounds.y = figureBounds.y+2;
                figureBounds.height = figureBounds.height-4;
            }
        } else {
            if(figureBounds.width>12) {
                figureBounds.x = figureBounds.x+5;
                figureBounds.width = figureBounds.width-10;
            } else if(figureBounds.width>6) {
                figureBounds.x = figureBounds.x+2;
                figureBounds.width = figureBounds.width-4;
            }
        }
        _crossedOutAdapter.paint(graphics);
        _rhombusAdapter.paint(graphics);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillShape(final Graphics graphics) {
        Rectangle figureBounds = getBounds();

        int newW = (int) Math.round(figureBounds.width * (getFill() / 100));

        graphics
                .setClip(new Rectangle(figureBounds.x, figureBounds.y, newW, figureBounds.height));
        graphics.setBackgroundColor(getForegroundColor());
        graphics.fillPolygon(getPoints());
        graphics.setClip(new Rectangle(figureBounds.x + newW, figureBounds.y, figureBounds.width
                - newW, figureBounds.height));
        graphics.setBackgroundColor(getBackgroundColor());
        graphics.fillPolygon(getPoints());
    }

    /**
     * Overridden, to ensure that the bounds rectangle gets repainted each time,
     * the _points of the polygon change. {@inheritDoc}
     */
    @Override
    public void setBounds(final Rectangle rect) {
        invalidate();
        fireFigureMoved();
        repaint();
    }

    /**
     * This method is a tribute to unit tests, which need a way to test the
     * performance of the figure implementation. Implementors should produce
     * some random changes and refresh the figure, when this method is called.
     *
     */
    public void randomNoiseRefresh() {
        setFill(Math.random() * 100);
        repaint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rectangle getHandleBounds() {
        return getPoints().getBounds();
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
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object getAdapter(final Class adapter) {
        if (adapter == IBorderEquippedWidget.class) {
            if(_borderAdapter==null) {
                _borderAdapter = new BorderAdapter(this) {
                    @Override
                    protected AbstractBorder createShapeBorder(final int borderWidth,
                            final Color borderColor) {
                        PolygonBorder border = new PolygonBorder(borderWidth);
                        border.setBorderColor(borderColor);
                        return border;
                    }
                };
            }
            return _borderAdapter;
        } else if(adapter == ICrossedFigure.class) {
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
     * The Border for this {@link RefreshablePolygonFigure}.
     * @author Kai MEyer
     *
     */
    private final class PolygonBorder extends AbstractBorder {
        /**
         * The Color of the border.
         */
        private Color _borderColor;
        /**
         * The width of the border.
         */
        private int _borderWidth = 1;

        /**
         * Constructor.
         * @param borderWidth
         *                 The width for the border
         */
        public PolygonBorder(final int borderWidth) {
            _borderWidth = borderWidth;
        }

        /**
         * Sets the Color of the border.
         * @param borderColor
         *             The Color for the border
         */
        public void setBorderColor(final Color borderColor) {
            _borderColor = borderColor;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Insets getInsets(final IFigure figure) {
            return new Insets(_borderWidth);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void paint(final IFigure figure, final Graphics graphics, final Insets insets) {
            graphics.setBackgroundColor(_borderColor);
            graphics.setForegroundColor(_borderColor);
            graphics.setLineWidth(_borderWidth);
            graphics.drawPolygon(getPoints());
        }
    }

}
