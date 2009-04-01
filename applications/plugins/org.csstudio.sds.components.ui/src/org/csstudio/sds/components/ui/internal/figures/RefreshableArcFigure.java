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

import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.sds.ui.figures.BorderAdapter;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.util.AntialiasingUtil;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.FocusBorder;
import org.eclipse.draw2d.FrameBorder;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

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
    private int start_angle = 0, angle = 90;
    private RGB fill_color = new RGB(255, 0, 0);

    /**
     * A border adapter, which covers all border handlings.
     */
    private IBorderEquippedWidget _borderAdapter;

    /**
     * Is the background transparent or not?
     */
    private boolean transparent = true;

    /**
     * Border properties.
     */
    private int border_width;
    private RGB border_color = new RGB(0, 0, 0);
    private boolean filled;

    /**
     * {@inheritDoc}
     */
    protected boolean useLocalCoordinates() {
        return true;
    }

    /**
     * Fills the arc.
     */
    protected void fillShape(Graphics gfx) {
        // Fix HR: The background paint over the fillArc.
        // (The fillShape paint first then the outlineShape).
        filled = true;
        if (transparent == false) {
            gfx.setBackgroundColor(getBackgroundColor());
            gfx.fillOval(getBounds().getCropped(new Insets(border_width/2)));
        }
        gfx.setBackgroundColor(CustomMediaFactory.getInstance().getColor(fill_color));
        gfx.fillArc(getBounds()
                .getCropped(new Insets(lineWidth / 2 + lineWidth % 2 + border_width)), start_angle,
                angle);
    }

    /**
     * Draws the arc.
     */
    protected void outlineShape(Graphics gfx) {
        if (filled == false && transparent == false) {
            gfx.setBackgroundColor(getBackgroundColor());
            gfx.fillOval(getBounds().getCropped(new Insets(border_width/2)));
        }
        if (lineWidth > 0) {
            gfx.setLineWidth(lineWidth);
            gfx.setLineCap(SWT.CAP_FLAT);
            gfx.setLineJoin(SWT.JOIN_MITER);
            gfx.drawArc(getBounds().getCropped(
                    new Insets(lineWidth / 2 - lineWidth % 2 + border_width)), start_angle, angle);
        }
        filled = false;
    }

    /**
     * The main drawing routine.
     */
    public void paintFigure(Graphics gfx) {
        AntialiasingUtil.getInstance().enableAntialiasing(gfx);
        super.paintFigure(gfx);
    }

    public void setTransparent(final boolean newval) {
        transparent = newval;
    }

    public boolean getTransparent() {
        return transparent;
    }

    public void setBorderWidth(final int newval) {
        border_width = newval;
        if (newval > 0) {
//            setBorder(new LineBorder(CustomMediaFactory.getInstance().getColor(border_color),
//                    border_width));
            setBorder(new ArcBorder(CustomMediaFactory.getInstance().getColor(border_color),
                    border_width));
        } else {
            setBorder(null);
        }
    }

    public int getBorderWidth() {
        return border_width;
    }

    public void setBorderColor(final RGB newval) {
        border_color = newval;
        if (border_width > 0) {
            setBorder(new ArcBorder(CustomMediaFactory.getInstance().getColor(border_color),
                    border_width));
            // setBorder(new LineBorder(CustomMediaFactory.getInstance().getColor(border_color),
            // border_width));
        } else {
            setBorder(null);
        }
    }

    public RGB getBorderColor() {
        return border_color;
    }

    public void setStartAngle(final int newval) {
        start_angle = newval;
    }

    public int getStartAngle() {
        return start_angle;
    }

    public void setAngle(final int newval) {
        angle = newval;
    }

    public int getAngle() {
        return angle;
    }

    public void setFillColor(final RGB newval) {
        fill_color = newval;
    }

    public RGB getFillColor() {
        return fill_color;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Object getAdapter(final Class adapter) {
        if (adapter == IBorderEquippedWidget.class) {
            if (_borderAdapter == null) {
                _borderAdapter = new BorderAdapter(this);
            }
            return _borderAdapter;
        }
        return null;
    }

    private final class ArcBorder extends AbstractBorder {

        private final Color _borderColor;
        private final int _borderWidth;

        public ArcBorder(Color borderColor, int borderWidth) {
            _borderColor = borderColor;
            _borderWidth = borderWidth;
        }

        public Insets getInsets(IFigure arg0) {
            return new Insets(_borderWidth);
        }

        public void paint(IFigure arg0, Graphics gfx, Insets arg2) {
            gfx.setBackgroundColor(_borderColor);
            gfx.setForegroundColor(_borderColor);
            gfx.setLineWidth(_borderWidth);
            Rectangle bounds2 = new Rectangle(getBounds().x+_borderWidth/2,getBounds().y+_borderWidth/2,getBounds().width-_borderWidth,getBounds().height-_borderWidth);
            gfx.drawOval(bounds2);

        }

    }
}
