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
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.handles.HandleBounds;
import org.eclipse.swt.SWT;

/**
 * A line figure.
 *
 * @author Sven Wende
 *
 */
public final class RefreshablePolylineFigure extends Polyline implements
    IAdaptable, HandleBounds {

    /**
     * The fill grade (0 - 100%).
     */
    private double _fill = 100.0;

    /**
     * A border adapter, which covers all border handlings.
     */
    private IBorderEquippedWidget _borderAdapter;

    /**
     * The line styles.
     */
    private final int[] _lineStyles = new int[] { SWT.LINE_SOLID,
            SWT.LINE_DASH, SWT.LINE_DOT, SWT.LINE_DASHDOT, SWT.LINE_DASHDOTDOT };

    private CrossedOutAdapter _crossedOutAdapter;

    private RhombusAdapter _rhombusAdapter;

    /**
     * Constructor.
     */
    public RefreshablePolylineFigure() {
        setFill(true);
        setBackgroundColor(ColorConstants.darkGreen);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void outlineShape(final Graphics graphics) {
        Rectangle figureBounds = getBounds();

        PointList points = getPoints();

        int newW = (int) Math.round(figureBounds.width * (getFill() / 100));
        if (newW >= figureBounds.width) {
            graphics.setForegroundColor(getForegroundColor());
            graphics.drawPolyline(points);
        } else {
            Rectangle clip = new Rectangle(figureBounds.x, figureBounds.y,
                    newW, figureBounds.height);
            graphics.pushState();
            graphics.clipRect(clip);
            graphics.setForegroundColor(getForegroundColor());
            graphics.drawPolyline(points);
            clip = new Rectangle(figureBounds.x + newW, figureBounds.y,
                    figureBounds.width - newW, figureBounds.height);
            graphics.popState();
            graphics.pushState();
            graphics.clipRect(clip);
            graphics.setForegroundColor(getBackgroundColor());
            graphics.drawPolyline(points);
            graphics.popState();
        }
        _crossedOutAdapter.paint(graphics);
        _rhombusAdapter.paint(graphics);

    }

    /**
     * Overridden, to ensure that the bounds rectangle gets repainted each time,
     * the points of the polygon change. {@inheritDoc}
     */
    @Override
    public void setBounds(final Rectangle rect) {
        invalidate();
        fireFigureMoved();
        repaint();
        int correctedWidth = rect.width + getLineWidth();
        int correctedHeight = rect.height + getLineWidth();
        Rectangle correctedRectangle = new Rectangle(rect.x, rect.y, correctedWidth, correctedHeight);
        super.setBounds(correctedRectangle);
    }

    @Override
    public void setSize(final int w, final int h) {
        int correctedWidth = w + getLineWidth();
        int correctedHeight = h + getLineWidth();
        super.setSize(correctedWidth, correctedHeight);
    }

    @Override
    public void setLocation(final Point p) {
        super.setLocation(p);
    }

    /**
     * This method is a tribute to unit tests, which need a way to test the
     * performance of the figure implementation. Implementors should produce
     * some random changes and refresh the figure, when this method is called.
     *
     */
    public void randomNoiseRefresh() {
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
    public void setLineStyle(final int lineStyle) {
        if ((lineStyle >= 0) && (lineStyle < _lineStyles.length)) {
            super.setLineStyle(_lineStyles[lineStyle]);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object getAdapter(final Class adapter) {
        if (adapter == IBorderEquippedWidget.class) {
            if (_borderAdapter == null) {
                _borderAdapter = new BorderAdapter(this);
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

}
