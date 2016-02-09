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
package org.csstudio.swt.widgets.figures;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;

import org.csstudio.swt.widgets.introspection.Introspectable;
import org.csstudio.swt.widgets.introspection.PolyWidgetIntrospector;
import org.csstudio.swt.widgets.util.PointsUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.csstudio.ui.util.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Polygon;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.handles.HandleBounds;
import org.eclipse.swt.graphics.Color;

/**
 * A polygon figure.
 *
 * @author Sven Wende, Stefan Hofer, Xihui chen (since import from SDS 2009/10)
 *
 */
public final class PolygonFigure extends Polygon implements HandleBounds, Introspectable {

    /**
     * The fill grade (0 - 100%).
     */
    private double fill = 100.0;

    private boolean horizontalFill;

    private boolean transparent;

    private Color lineColor = CustomMediaFactory.getInstance().getColor(
            CustomMediaFactory.COLOR_BLUE);


    /**
     * Constructor.
     */
    public PolygonFigure() {
        setFill(true);
        setBackgroundColor(ColorConstants.darkGreen);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillShape(final Graphics graphics) {
        graphics.pushState();
        Rectangle figureBounds = getBounds();
        if(!transparent){
            if(isEnabled())
                graphics.setBackgroundColor(getBackgroundColor());
            graphics.fillPolygon(getPoints());
        }
        if(getFill() > 0){
            if(isEnabled())
                graphics.setBackgroundColor(getForegroundColor());
            if(horizontalFill){
                int newW = (int) Math.round(figureBounds.width * (getFill() / 100));
                graphics
                    .setClip(new Rectangle(figureBounds.x, figureBounds.y, newW, figureBounds.height));
            }else{
                int newH = (int) Math.round(figureBounds.height * (getFill() / 100));
                graphics
                    .setClip(new Rectangle(figureBounds.x, figureBounds.y + figureBounds.height - newH,
                            figureBounds.width, newH));
            }
            graphics.fillPolygon(getPoints());

        }
        graphics.popState();
    }

    /**
     * Gets the fill grade.
     *
     * @return the fill grade
     */
    public double getFill() {
        return fill;
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle getHandleBounds() {
        return getPoints().getBounds();
    }

    /**
     * @return the lineColor
     */
    public Color getLineColor() {
        return lineColor;
    }

    /**
     * Gets the transparent state of the background.
     *
     * @return the transparent state of the background
     */
    public boolean getTransparent() {
        return transparent;
    }



    /**
     * Gets the orientation (horizontal==true | vertical==false).
     *
     * @return boolean The orientation
     */
    public boolean isHorizontalFill() {
        return horizontalFill;
    }

    @Override
    protected void outlineShape(Graphics g) {
        g.pushState();
        if(isEnabled())
            g.setForegroundColor(lineColor);
        super.outlineShape(g);
        g.popState();
    }


    /**
     * Overridden, to ensure that the bounds rectangle gets repainted each time,
     * the _points of the polygon change. {@inheritDoc}
     */
    @Override
    public void setBounds(final Rectangle rect) {
        PointList points = getPoints();
        if (!points.getBounds().equals(rect)) {
            int oldX = getLocation().x;
            int oldY = getLocation().y;
            points.translate(rect.x - oldX, rect.y - oldY);

            setPoints(PointsUtil.scalePointsBySize(points, rect.width,
                    rect.height));
        }
        invalidate();
        fireFigureMoved();
        repaint();
    }

    /**
     * Sets the fill grade.
     *
     * @param fill
     *            the fill grade.
     */
    public void setFill(final double fill) {
        if(this.fill == fill)
            return;
        this.fill = fill;
        repaint();
    }

    /**
     * Sets the orientation (horizontal==true | vertical==false).
     *
     * @param horizontal
     *            The orientation.
     */
    public void setHorizontalFill(final boolean horizontal) {
        if(this.horizontalFill == horizontal)
            return;
        horizontalFill = horizontal;
        repaint();
    }

    public void setLineColor(Color lineColor) {
        if(this.lineColor != null && this.lineColor.equals(lineColor))
            return;
        this.lineColor = lineColor;
        repaint();
    }

    /**
     * Sets the transparent state of the background.
     *
     * @param transparent
     *            the transparent state.
     */
    public void setTransparent(final boolean transparent) {
        if(this.transparent == transparent)
            return;
        this.transparent = transparent;
        repaint();
    }

    public BeanInfo getBeanInfo() throws IntrospectionException {
        return new PolyWidgetIntrospector().getBeanInfo(this.getClass());
    }
}
