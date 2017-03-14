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
package org.csstudio.swt.widgets.figures;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;

import org.csstudio.swt.widgets.introspection.Introspectable;
import org.csstudio.swt.widgets.introspection.ShapeWidgetIntrospector;
import org.csstudio.swt.widgets.util.GraphicsUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.csstudio.ui.util.Draw2dSingletonUtil;
import org.csstudio.ui.util.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

/**
 * A rectangle figure.
 *
 * @author Sven Wende (original author), Xihui Chen (since import from SDS 2009/10)
 *
 */
public final class OPIRectangleFigure extends RectangleFigure implements Introspectable {
    /**
     * The fill grade (0 - 100%).
     */
    private double fill = 100;
    private boolean runMode;

    /**
     * The orientation (horizontal==true | vertical==false).
     */
    private boolean horizontalFill = true;

    /** The transparent state of the background. */
    private boolean transparent = false;
    /** Whether the rectangle should be selectable at runtime. */
    private boolean selectable;

    private Color lineColor = CustomMediaFactory.getInstance().getColor(
            CustomMediaFactory.COLOR_PURPLE);

    private Color backGradientStartColor =ColorConstants.white;
    private Color foreGradientStartColor =ColorConstants.white;
    private boolean gradient=false;
    private boolean useAdvancedGraphics=GraphicsUtil.useAdvancedGraphics();

    public OPIRectangleFigure(boolean runMode) {
        this.runMode = runMode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected synchronized void fillShape(final Graphics graphics) {
        Rectangle figureBounds = getClientArea();
        if (!transparent) {
            if(isEnabled())
                graphics.setBackgroundColor(getBackgroundColor());
            if(gradient && useAdvancedGraphics){
                graphics.setForegroundColor(backGradientStartColor);
                graphics.fillGradient(figureBounds, horizontalFill);
            }else
                graphics.fillRectangle(figureBounds);
        }
        if(getFill() > 0){
            if(isEnabled())
                graphics.setBackgroundColor(getForegroundColor());
            Rectangle fillRectangle;
            if (horizontalFill) {
                int newW = (int) Math.round(figureBounds.width * (getFill() / 100));
                fillRectangle = new Rectangle(figureBounds.x,figureBounds.y,newW,figureBounds.height);
            } else {
                int newH = (int) Math.round(figureBounds.height * (getFill() / 100));
                fillRectangle = new Rectangle(figureBounds.x,figureBounds.y+figureBounds.height-newH,figureBounds.width,newH);
            }
            if(gradient && useAdvancedGraphics){
                graphics.setForegroundColor(foreGradientStartColor);
                graphics.fillGradient(fillRectangle, horizontalFill);
            }else
                graphics.fillRectangle(fillRectangle);
        }
    }

    public BeanInfo getBeanInfo() throws IntrospectionException {
        return new ShapeWidgetIntrospector().getBeanInfo(this.getClass());
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
     * @return boolean
     *                 The orientation
     */
    public boolean isHorizontalFill() {
        return horizontalFill;
    }

    /**
     * @return the gradientStartColor
     */
    public Color getBackGradientStartColor() {
        return backGradientStartColor;
    }

    public Color getForeGradientStartColor() {
        return foreGradientStartColor;
    }

    /**
     * @return the gradient
     */
    public boolean isGradient() {
        return gradient;
    }

    /**
     * @param gradient the gradient to set
     */
    public void setGradient(boolean gradient) {
        this.gradient = gradient;
        repaint();
    }

    /**Set gradient start color.
     * @param gradientStartColor
     */
    public void setBackGradientStartColor(Color gradientStartColor) {
        this.backGradientStartColor = gradientStartColor;
        repaint();
    }

    public void setForeGradientStartColor(Color foreGradientStartColor) {
        this.foreGradientStartColor = foreGradientStartColor;
        repaint();
    }


    /**
     * @see Shape#outlineShape(Graphics)
     */
    protected void outlineShape(Graphics graphics) {
        float lineInset = Math.max(1.0f, getLineWidth()) / 2.0f;
        int inset1 = (int)Math.floor(lineInset);
        int inset2 = (int)Math.ceil(lineInset);

        Rectangle r = Draw2dSingletonUtil.getRectangle().setBounds(getClientArea());
        r.x += inset1 ;
        r.y += inset1;
        r.width -= inset1 + inset2;
        r.height -= inset1 + inset2;
        if(isEnabled())
            graphics.setForegroundColor(lineColor);
        graphics.drawRectangle(r);
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
        this.horizontalFill = horizontal;
        repaint();
    }


    /**
     * @param lineColor the lineColor to set
     */
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

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    @Override
    public boolean containsPoint(int x, int y) {
        if(runMode && !selectable)
            return false;
        else
            return super.containsPoint(x, y);
    }

}
