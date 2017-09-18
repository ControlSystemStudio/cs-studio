/****************************************************************************
* Copyright (c) 2010-2017 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
****************************************************************************/
package org.csstudio.opibuilder.editparts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.csstudio.opibuilder.model.ConnectionModel.LineJumpAdd;
import org.csstudio.opibuilder.model.ConnectionModel.LineJumpStyle;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * This class represents a polyline connection which is able to draw a
 * "jump" over some other polyline connection.
 *
 * @author <a href="mailto:rohit.n.sarpotdar@gmail.com">Rohit Sarpotdar</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovic</a>
 */
public class PolylineJumpConnection extends PolylineConnection {
    private final static double RATIO_30_DEGREES = 0.577350269d;

    private WidgetConnectionEditPart widgetConnectionEditPart;
    private LineJumpAdd lineJumpAdd;
    private int lineJumpSize;
    private LineJumpStyle lineJumpStyle;
    private PointList pointsWithIntersection;
    private HashMap<Point, PointList> intersectionMap;
    private Point initialStartPoint;
    private Point initialEndPoint;

    public PolylineJumpConnection(WidgetConnectionEditPart widgetConnectionEditPart) {
        this.widgetConnectionEditPart = widgetConnectionEditPart;

        setClippingStrategy((childFigure) -> {
            Rectangle bounds = childFigure.getBounds();
            if ((intersectionMap != null) && (lineJumpAdd != LineJumpAdd.NONE))
                bounds.expand(lineJumpSize*2, lineJumpSize*2);
            return new Rectangle[] { bounds };
        });
    }

    @Override
    public void paint(Graphics graphics) {
        // more in line with the http://help.eclipse.org/mars/index.jsp?topic=%2Forg.eclipse.draw2d.doc.isv%2Fguide%2Fpainting.html
        // First, properties are set on the Graphics that would be inherited by children, including Font, background, and foreground Color.
        graphics.setForegroundColor(getForegroundColor());
        graphics.setLineAttributes(getLineAttributes());
        graphics.setLineWidth(getLineWidth());
        graphics.setLineStyle(getLineStyle());

        // then original implementation is called to take care of painting figure, client area, children, and border
        super.paint(graphics);
        setBounds(getBounds().expand(lineJumpSize*2, lineJumpSize*2));
        getUpdateManager().performUpdate(getBounds());
    }

    @Override
    public void paintFigure(Graphics graphics) {
        pointsWithIntersection = this.widgetConnectionEditPart.getIntersectionPoints(this);
        intersectionMap = this.widgetConnectionEditPart.getIntersectionMap();

        Point previousPoint = getStart();
        for(int i=1; i<pointsWithIntersection.size(); i++){
            Point point = pointsWithIntersection.getPoint(i);

            // Skip drawing line if segment is intersection segment and lineJumpAdd is not none
            if(isPointInIntersectionRange(previousPoint, point, intersectionMap) && !lineJumpAdd.equals(LineJumpAdd.NONE)) {
                // We have Intersection ahead
                previousPoint = point;
                continue;
            }

            Polyline line = getPolyLine(previousPoint, point);
            line.paint(graphics);
            this.setBounds(getBounds().union(line.getBounds()));

            previousPoint = point;
        }

        if ((intersectionMap != null) && (lineJumpAdd != LineJumpAdd.NONE)) {
            graphics.setLineWidth(getLineWidth());
            graphics.setLineStyle(getLineStyle());

            for (final Entry<Point, PointList> currentEntry : intersectionMap.entrySet()) {
                switch (lineJumpStyle) {
                case ARC:
                    drawArc(currentEntry, graphics);
                    break;
                case SLIDES2:
                    draw2Slides(currentEntry, graphics);
                    break;
                case SQUARE:
                    drawSquare(currentEntry, graphics);
                    break;
                default:
                    // nothing to do
                    break;
                }
            }
        }
    }

    private void drawArc(final Entry<Point, PointList> currentEntry, Graphics graphics) {
        Point intersectionPoint = currentEntry.getKey();
        PointList intersectionEdges = currentEntry.getValue();
        int angle = (int) angleOf(intersectionEdges.getFirstPoint(), intersectionEdges.getLastPoint());
        angle = angle % 180;

        // Correct small angle deviations caused by calculating intersecting points rounding to int
        if (angle > 175) {
            angle = 0;
        }

        Arc arc = new Arc(intersectionPoint.x()-lineJumpSize, intersectionPoint.y()-lineJumpSize,
                lineJumpSize*2, lineJumpSize*2, angle, 180);
        this.setBounds(getBounds().union(arc.getBounds()));
        arc.paint(graphics);
    }

    private void draw2Slides(final Entry<Point, PointList> currentEntry, Graphics graphics) {
        PointList intersectionEdges = currentEntry.getValue();
        Point tipPoint = computeTipPoint(intersectionEdges.getFirstPoint(),
                intersectionEdges.getLastPoint(), true);

        // If jump is right hand side, take it to left hand side
        if (tipPoint.x > intersectionEdges.getFirstPoint().x()) {
            tipPoint = computeTipPoint(intersectionEdges.getFirstPoint(),
            intersectionEdges.getLastPoint(), false);
        }

        Polyline triangleLine1 = getPolyLine(intersectionEdges.getFirstPoint(), tipPoint);
        this.setBounds(getBounds().union(triangleLine1.getBounds()));

        Polyline triangleLine2 =  getPolyLine(intersectionEdges.getLastPoint(), tipPoint);
        this.setBounds(getBounds().union(triangleLine2.getBounds()));
        triangleLine1.paint(graphics);
        triangleLine2.paint(graphics);
    }

    private void drawSquare(final Entry<Point, PointList> currentEntry, Graphics graphics) {
        PointList intersectionEdges = currentEntry.getValue();
        Point x1y1 = intersectionEdges.getFirstPoint();
        Point x2y2 = intersectionEdges.getLastPoint();

        int x3 = (int) (x1y1.x - (RATIO_30_DEGREES) * (x2y2.y - x1y1.y));
        int y3 = (int) (x1y1.y - (RATIO_30_DEGREES) * (x2y2.x - x1y1.x));
        Point squareCorner1 = new Point(x3, y3);

        // For Vertical Lines,  If square is on right hand side, bring it to left hand side
        // Leave margin for rounding errors
        if (Math.abs(x1y1.x - x2y2.x) < 5) {
            if (x3 > x1y1.x) {
                int length = Math.abs(x1y1.x-x3);
                x3 = x1y1.x - length;
                squareCorner1 = new Point(x3, y3);
            }
        }

        // For Horizonal Lines,  If square is on Bottom side, bring it to upper side
        // Leave margin for rounding errors
        if (Math.abs(x1y1.y - x2y2.y) < 5) {
            if (y3 > x1y1.y) {
                int length = Math.abs(x1y1.y-y3);
                y3 = x1y1.y - length;
                squareCorner1 = new Point(x3, y3);
            }
        }

        int x4 = (int) (x2y2.x + (RATIO_30_DEGREES) * (x1y1.y - x2y2.y));
        int y4 = (int) (x2y2.y + (RATIO_30_DEGREES) * (x1y1.x - x2y2.x));
        Point squareCorner2 = new Point(x4, y4);

        // For Vertical Lines,  If square is on right hand side, bring it to left hand side
        // Leave margin for rounding errors
        if (Math.abs(x1y1.x - x2y2.x) < 5) {
            if (x4 > x2y2.x) {
                int length = Math.abs(x2y2.x-x4);
                x4 = x2y2.x - length;
                squareCorner2 = new Point(x4, y4);
            }
        }

        // For Horizontal Lines,  If square is on Bottom side, bring it to upper side
        // Leave margin for rounding errors
        if (Math.abs(x1y1.y - x2y2.y) < 5) {
            if (y4 > x2y2.y) {
                int length = Math.abs(x2y2.y-y4);
                y4 = x2y2.y - length;
                squareCorner2 = new Point(x4, y4);
            }
        }

        final Polyline squareLine1 =  getPolyLine(intersectionEdges.getFirstPoint(), squareCorner1);
        this.setBounds(getBounds().union(squareLine1.getBounds()));

        final Polyline squareLine2 =  getPolyLine(squareCorner1, squareCorner2);
        this.setBounds(getBounds().union(squareLine2.getBounds()));

        final Polyline squareLine3 =  getPolyLine(squareCorner2, intersectionEdges.getLastPoint());
        this.setBounds(getBounds().union(squareLine3.getBounds()));

        squareLine1.paint(graphics);
        squareLine2.paint(graphics);
        squareLine3.paint(graphics);
    }

    private Polyline getPolyLine(Point firstPoint, Point lastPoint) {
         Polyline line = new Polyline();
         line.addPoint(firstPoint);
         line.addPoint(lastPoint);
         line.setLineWidth(getLineWidth());
         line.setLineStyle(getLineStyle());
         return line;
    }

    private static Point computeTipPoint(
                Point p0, Point p1, boolean right)
            {
                double dx = p1.x() - p0.x();
                double dy = p1.y() - p0.y();
                double length = Math.sqrt(dx*dx+dy*dy);
                double dirX = dx / length;
                double dirY = dy / length;
                double height = Math.sqrt(3)/2 * length;
                double cx = p0.x() + dx * 0.5;
                double cy = p0.y() + dy * 0.5;
                double pDirX = -dirY;
                double pDirY = dirX;
                double rx = 0;
                double ry = 0;
                if (right)
                {
                    rx = cx + height * pDirX;
                    ry = cy + height * pDirY;
                }
                else
                {
                    rx = cx - height * pDirX;
                    ry = cy - height * pDirY;
                }
                return new PrecisionPoint(rx, ry);
            }

    public static double angleOf(Point p1, Point p2) {
        final double deltaY = (p1.y - p2.y);
        final double deltaX = (p2.x - p1.x);
        final double result = Math.toDegrees(Math.atan2(deltaY, deltaX));
        return (result < 0) ? (360d + result) : result;
    }

    private boolean isPointInIntersectionRange(final Point startPoint_, final Point endPoint_,
            final HashMap<Point, PointList> intersectionMap_) {
        if(intersectionMap_ != null && startPoint_ != null) {
            Iterator<Entry<Point, PointList>> iterator = intersectionMap_.entrySet().iterator();
            while(iterator.hasNext()) {
                Entry<Point, PointList> currentPointList = iterator.next();
                PointList value = currentPointList.getValue();
                if(value != null && value.size() > 0) {
                    if(value.getFirstPoint() != null && (value.getLastPoint() != null)) {
                        if(value.getFirstPoint().equals(startPoint_)) {
                            return true;
                        }
                        if(value.getLastPoint().equals(endPoint_)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public void setLineJumpAdd(final LineJumpAdd lineJumpAdd_) {
        this.lineJumpAdd = lineJumpAdd_;
    }

    public void setLineJumpSize(int lineJumpSize_) {
        this.lineJumpSize = lineJumpSize_;
    }

    public void setLineJumpStyle(final LineJumpStyle lineJumpStyle_) {
        this.lineJumpStyle = lineJumpStyle_;
    }

    public int getLineJumpSize() {
        return lineJumpSize;
    }

    public Point getInitialStartPoint() {
        return initialStartPoint;
    }

    public void setInitialStartPoint(Point startPoint) {
        initialStartPoint = startPoint.getCopy();
    }

    public Point getInitialEndPoint() {
        return initialEndPoint;
    }

    public void setInitialEndPoint(Point endPoint) {
        initialEndPoint = endPoint.getCopy();
    }

    public WidgetConnectionEditPart getWidgetConnectionEditPart() {
        return widgetConnectionEditPart;
    }
}
