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

import org.csstudio.swt.widgets.figureparts.PolarPoint;
import org.csstudio.swt.widgets.introspection.Introspectable;
import org.csstudio.swt.widgets.introspection.PolyWidgetIntrospector;
import org.csstudio.swt.widgets.util.PointsUtil;
import org.csstudio.ui.util.ColorConstants;
import org.csstudio.ui.util.Draw2dSingletonUtil;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Geometry;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.handles.HandleBounds;

/**
 * A polyline figure.
 * @author Sven Wende (original author)
 * @author Xihui Chen (imported from SDS since 2009/9)
 *
 */
public final class PolylineFigure extends Polyline implements HandleBounds, Introspectable {

    public static enum ArrowType{
        None("None"),
        From("From"),
        To("To"),
        Both("Both");

        String description;

        private ArrowType(String desc) {
            this.description = desc;
        }

        public static String[] stringValues(){
            String[] sv = new String[values().length];
            int i=0;
            for(ArrowType p : values())
                sv[i++] = p.toString();
            return sv;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    public static final double ARROW_ANGLE = Math.PI/10;
    /**Calculate the three points for an arrow.
     * @param startPoint the start point of the line
     * @param endPoint the end point of the line
     * @param l the length of the arrow line
     * @param angle the radians angle between the line and the arrow line.
     * @return A point list which includes the three points:
     * <br>0: Right arrow point;
     * <br>1: Left arrow point;
     * <br>2: Intersection point.
     */
    public static PointList calcArrowPoints(Point startPoint, Point endPoint,
            int l, double angle){

        PointList result = new PointList();

        PolarPoint ppE = PolarPoint.point2PolarPoint(endPoint, startPoint);

        PolarPoint ppR = new PolarPoint(l, ppE.theta - angle);
        PolarPoint ppL = new PolarPoint(l, ppE.theta + angle);

        //the intersection point bettwen arrow and line.
        PolarPoint ppI = new PolarPoint((int) (l * Math.cos(angle)), ppE.theta);

        Point pR = ppR.toPoint().translate(endPoint);
        Point pL = ppL.toPoint().translate(endPoint);
        Point pI = ppI.toPoint().translate(endPoint);

        result.addPoint(pR);
        result.addPoint(pL);
        result.addPoint(pI);

        return result;

    }
    public static Rectangle getPointsBoundsWithArrows(PointList points, ArrowType arrowType, int arrowLength, double arrowAngle){
        PointList copy = points.getCopy();
        if(points.size() >=2){
            if(arrowType == ArrowType.To || arrowType == ArrowType.Both)
                copy.addAll(calcArrowPoints(points.getPoint(points.size()-2),
                    points.getLastPoint(), arrowLength, arrowAngle));
            if(arrowType == ArrowType.From || arrowType == ArrowType.Both)
                copy.addAll(calcArrowPoints(points.getPoint(1),
                    points.getFirstPoint(), arrowLength, arrowAngle));
        }
        return copy.getBounds();
    }
    /**
     * The fill grade (0 - 100%).
     */
    private double fill = 100.0;

    private boolean horizontalFill;

    private boolean transparent;

    private boolean fillArrow = true;



    private ArrowType arrowType;

    private int arrowLineLength = 30;

    private static final Rectangle LINEBOUNDS = Draw2dSingletonUtil.getRectangle();
    /**
     * Constructor.
     */
    public PolylineFigure() {
        setFill(true);
        setBackgroundColor(ColorConstants.darkGreen);
    }

    private void drawPolyLineWithArrow(Graphics graphics){
        PointList points = getPoints().getCopy();

        graphics.pushState();

        if(points.size() >= 2){
            Point endPoint = points.getLastPoint();
            Point firstPoint = points.getFirstPoint();
            if(arrowType == ArrowType.To || arrowType == ArrowType.Both){
                //draw end arrow
                PointList arrowPoints = calcArrowPoints(points.getPoint(points.size()-2),
                            endPoint, arrowLineLength, ARROW_ANGLE);
                if(fillArrow)
                    points.setPoint(arrowPoints.getLastPoint(), points.size()-1);
                arrowPoints.setPoint(endPoint, 2);
                if(fillArrow){
                    if(isEnabled())
                        graphics.setBackgroundColor(graphics.getForegroundColor());
                    graphics.fillPolygon(arrowPoints);

                }else{
                    graphics.drawLine(endPoint, arrowPoints.getFirstPoint());
                    graphics.drawLine(endPoint, arrowPoints.getMidpoint());
                }
            }
            if(arrowType == ArrowType.From || arrowType == ArrowType.Both){
                //draw start arrow
                PointList arrowPoints = calcArrowPoints(points.getPoint(1),
                        firstPoint, arrowLineLength, ARROW_ANGLE);
                if(fillArrow)
                    points.setPoint(arrowPoints.getLastPoint(), 0);
                arrowPoints.setPoint(firstPoint, 2);
                if(fillArrow){
                    if(isEnabled())
                        graphics.setBackgroundColor(graphics.getForegroundColor());
                    graphics.fillPolygon(arrowPoints);
                }else{
                    graphics.drawLine(firstPoint, arrowPoints.getFirstPoint());
                    graphics.drawLine(firstPoint, arrowPoints.getMidpoint());
                }
            }
        }
        graphics.drawPolyline(points);
        graphics.popState();
    }

    /**
     * @return the arrowLineLength
     */
    public int getArrowLineLength() {
        return arrowLineLength;
    }


    /**
     * @return the arrowType
     */
    public ArrowType getArrowType() {
        return arrowType;
    }


    /**
     * Overridden, to ensure that the bounds rectangle gets repainted each time,
     * the points of the polygon change. {@inheritDoc}
     */
    /*@Override
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
    public void setLocation(Point p) {
        super.setLocation(p);
    }
*/


    @Override
    public Rectangle getBounds() {

        if(arrowType == ArrowType.None)
            return super.getBounds();
        if (bounds == null) {
            bounds = getPointsBoundsWithArrows(
                    getPoints(), arrowType, arrowLineLength, ARROW_ANGLE);
            int expand = (int)(getLineWidthFloat() / 2.0f);
                bounds = bounds.getExpanded(expand, expand);
        }

        return bounds;
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
     * Gets the transparent state of the background.
     *
     * @return the transparent state of the background
     */
    public boolean getTransparent() {
        return transparent;
    }

    /**
     * @return the fillArrow
     */
    public boolean isFillArrow() {
        return fillArrow;
    }


    /**
     * Gets the orientation (horizontal==true | vertical==false).
     *
     * @return boolean The orientation
     */
    public boolean isHorizontalFill() {
        return horizontalFill;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void outlineShape(final Graphics graphics) {
        Rectangle figureBounds = getBounds();

        graphics.pushState();
        if(!transparent){
            if(isEnabled())
                graphics.setForegroundColor(getBackgroundColor());
            drawPolyLineWithArrow(graphics);
        }
        if(getFill() > 0){
            //set clip by fill level
            if(horizontalFill){

                int newW = (int) Math.round(figureBounds.width * (getFill() / 100));

                graphics
                    .clipRect(new Rectangle(figureBounds.x, figureBounds.y, newW, figureBounds.height));
            }else{
                int newH = (int) Math.round(figureBounds.height * (getFill() / 100));
                graphics
                    .clipRect(new Rectangle(figureBounds.x, figureBounds.y + figureBounds.height - newH,
                            figureBounds.width, newH));
            }
            if(isEnabled())
                graphics.setForegroundColor(getForegroundColor());
            drawPolyLineWithArrow(graphics);
        }
        graphics.popState();

    }

    /**
     * Translates this Figure's bounds, without firing a move.
     * @param dx The amount to translate horizontally
     * @param dy The amount to translate vertically
     * @see #translate(int, int)
     * @since 2.0
     */
    @Override
    public void primTranslate(int dx, int dy) {
        bounds.x += dx;
        bounds.y += dy;
        if (useLocalCoordinates()) {
            fireCoordinateSystemChanged();
            return;
        }
        for (int i = 0; i < getChildren().size(); i++)
            ((IFigure)getChildren().get(i)).translate(dx, dy);
    }

    public void setArrowLineLength(int arrowLineLength) {
        if(this.arrowLineLength == arrowLineLength)
            return;
        this.arrowLineLength = arrowLineLength;
        repaint();
    }

    public void setArrowType(ArrowType arrowType) {
        if(this.arrowType == arrowType)
            return;
        this.arrowType = arrowType;
        repaint();
    }


    @Override
    public void setBounds(Rectangle rect) {
        PointList points = getPoints();
        if(!points.getBounds().equals(rect)){
            int oldX = getLocation().x;
            int oldY = getLocation().y;
            points.translate(rect.x - oldX, rect.y - oldY);

            setPoints(PointsUtil.scalePointsBySize(points, rect.width, rect.height));
        }
        super.setBounds(rect);
        //figure should be forced to be moved since the bounds of a polyline might be unchanged.
        fireFigureMoved();

        //bounds = bounds.getExpanded(lineWidth / 2, lineWidth / 2);
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

    public void setFillArrow(boolean fillArrow) {
        if(this.fillArrow == fillArrow)
            return;
        this.fillArrow = fillArrow;
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

    @Override
    public Dimension getMinimumSize(int wHint, int hHint) {
        if(wHint == -1 && hHint == -1)
            return new Dimension(1,1);
        return super.getMinimumSize(wHint, hHint);
    }

    /**Override this to fix a bug in draw2d polyline: the polyline width should be considered.
     * @see org.eclipse.draw2d.IFigure#containsPoint(int, int)
     */
    @Override
    public boolean containsPoint(int x, int y) {
        int tolerance = (int) Math.max(getLineWidthFloat() / 2.0f,
                2);
        LINEBOUNDS.setBounds(getBounds());
        LINEBOUNDS.expand(tolerance, tolerance);
        if (!LINEBOUNDS.contains(x, y))
            return false;
        return shapeContainsPoint(x, y, tolerance) || childrenContainsPoint(x, y);
    }

    /**Override this to fix a bug in draw2d polyline: the polyline width should be considered.
    */
    protected boolean shapeContainsPoint(int x, int y, int tolerance) {
        return Geometry.polylineContainsPoint(getPoints(), x, y, tolerance);
    }

    public BeanInfo getBeanInfo() throws IntrospectionException {
        return new PolyWidgetIntrospector().getBeanInfo(this.getClass());
    }
}
