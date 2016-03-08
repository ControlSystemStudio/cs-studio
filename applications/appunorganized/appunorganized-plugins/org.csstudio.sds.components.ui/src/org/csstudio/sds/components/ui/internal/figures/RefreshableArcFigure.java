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
package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.ui.figures.BorderAdapter;
import org.csstudio.sds.ui.figures.CrossedOutAdapter;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.ui.figures.ICrossedFigure;
import org.csstudio.sds.ui.figures.IRhombusEquippedWidget;
import org.csstudio.sds.ui.figures.RhombusAdapter;
import org.csstudio.sds.util.AntialiasingUtil;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * An arc figure.
 *
 * @author jbercic
 *
 */
public final class RefreshableArcFigure extends Shape implements IAdaptable {
    /**
     * start angle and length (in degrees) of the arc should it be drawn filled? (using fill_color)
     */
    private int _startAngle = 0, _angle = 90;
    private Color _fillColor;

    /**
     * A border adapter, which covers all border handlings.
     */
    private IBorderEquippedWidget _borderAdapter;

    /**
     * Is the background transparent or not?
     */
    private boolean _transparent = true;

    /**
     * Border properties.
     */
    private int _borderWidth;

    private boolean _filled;
    private CrossedOutAdapter _crossedOutAdapter;
    private RhombusAdapter _rhombusAdapter;


    /**
     *
     * Constructor.
     */
    public RefreshableArcFigure() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean useLocalCoordinates() {
        return true;
    }

    /**
     * Fills the arc.
     * (@inheritDoc)
     */
    @Override
    protected void fillShape(final Graphics gfx) {
        // Fix HR: The background paint over the fillArc.
        // (The fillShape paint first then the outlineShape).
        _filled = true;
        if (!_transparent) {
            gfx.setBackgroundColor(getBackgroundColor());
            gfx.fillOval(getBounds().getCropped(new Insets(_borderWidth/2)));
        }
        gfx.setBackgroundColor(_fillColor);
        gfx.fillArc(getBounds()
                .getCropped(new Insets(lineWidth / 2 + lineWidth % 2 + _borderWidth)), _startAngle,
                -1*_angle);
    }

    /**
     * Draws the arc.
     * (@inheritDoc)
     */
    @Override
    protected void outlineShape(final Graphics gfx) {
        if (!_filled&& !_transparent) {
            gfx.setBackgroundColor(getBackgroundColor());
            gfx.fillOval(getBounds().getCropped(new Insets(_borderWidth/2)));
        }
        if (lineWidth > 0) {
            gfx.setLineWidth(lineWidth);
            gfx.setLineCap(SWT.CAP_FLAT);
            gfx.setLineJoin(SWT.JOIN_MITER);
            gfx.drawArc(getBounds().getCropped(
                    new Insets(lineWidth / 2 - lineWidth % 2 + _borderWidth)), _startAngle, -1*_angle);
        }
        _filled = false;
    }

    /**
     * The main drawing routine.
     */
    @Override
    public void paintFigure(final Graphics graphics) {
        AntialiasingUtil.getInstance().enableAntialiasing(graphics);
        super.paintFigure(graphics);
        _crossedOutAdapter.paint(graphics);
        _rhombusAdapter.paint(graphics);
    }

    public void setTransparent(final boolean newval) {
        _transparent = newval;
    }

    public boolean getTransparent() {
        return _transparent;
    }

    public void setBorderWidth(final int newval) {
        _borderWidth = newval;
    }

    public int getBorderWidth() {
        return _borderWidth;
    }

    public void setStartAngle(final int newval) {
        _startAngle = newval;
    }

    public int getStartAngle() {
        return _startAngle;
    }

    public void setAngle(final int newval) {
        _angle = newval;
    }

    public int getAngle() {
        return _angle;
    }

    public void setFillColor(final Color color) {
        _fillColor = color;
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
                    protected AbstractBorder createShapeBorder(final int borderWidth, final Color borderColor) {
                        if (borderWidth>0) {
                            ArcBorder border = new ArcBorder(borderWidth, borderColor);
                            return border;
                        }
                        return null;
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

    @Override
    public void setBorder(final Border border) {
        super.setBorder(border);
    }

    private final class ArcBorder extends AbstractBorder {

        private final Color _borderColor;
        private final int _borderWidth;

        public ArcBorder(final int borderWidth,final Color borderColor) {
            _borderColor = borderColor;
            _borderWidth = borderWidth;
        }

        @Override
        public Insets getInsets(final IFigure arg0) {
            return new Insets(_borderWidth);
        }

        @Override
        public void paint(final IFigure figure, final Graphics gfx, final Insets arg2) {
            gfx.setBackgroundColor(_borderColor);
            gfx.setForegroundColor(_borderColor);
            gfx.setLineWidth(_borderWidth);
            Rectangle bounds = figure.getBounds();
            Rectangle bounds2 = new Rectangle(bounds.x+_borderWidth/2,bounds.y+_borderWidth/2,bounds.width-_borderWidth,bounds.height-_borderWidth);
            gfx.drawOval(bounds2);

        }
    }

}
