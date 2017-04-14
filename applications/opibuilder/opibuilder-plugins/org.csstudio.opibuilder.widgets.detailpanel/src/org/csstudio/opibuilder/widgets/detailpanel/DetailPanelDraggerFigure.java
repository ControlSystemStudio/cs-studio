package org.csstudio.opibuilder.widgets.detailpanel;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

public class DetailPanelDraggerFigure extends Figure {
    /* Drawing information */
    public static final int W = 13;
    private static final int V = W-1;
    private PointList triangle1;
    private PointList triangle2;
    private PointList triangle3;
    private PointList triangle4;
    private Point offset;

    /* Constructor */
    public DetailPanelDraggerFigure() {
        // Initialise the points
        triangle1 = new PointList();
        triangle1.addPoint(V/4+1, V/4);
        triangle1.addPoint(V/2, 0);
        triangle1.addPoint(3*V/4-1, V/4);
        triangle2 = new PointList();
        triangle2.addPoint(3*V/4, V/4+1);
        triangle2.addPoint(V, V/2);
        triangle2.addPoint(3*V/4, 3*V/4-1);
        triangle3 = new PointList();
        triangle3.addPoint(3*V/4-1, 3*V/4);
        triangle3.addPoint(V/2, V);
        triangle3.addPoint(V/4+1, 3*V/4);
        triangle4 = new PointList();
        triangle4.addPoint(V/4, 3*V/4-1);
        triangle4.addPoint(0, V/2);
        triangle4.addPoint(V/4, V/4+1);
        offset = new Point(0,0);
    }

    // Draw the dragger
    @Override
    protected void paintFigure(Graphics graphics) {
        super.paintFigure(graphics);
        adjustOffset();
        graphics.fillPolygon(triangle1);
        graphics.fillPolygon(triangle2);
        graphics.fillPolygon(triangle3);
        graphics.fillPolygon(triangle4);
        graphics.drawPolygon(triangle1);
        graphics.drawPolygon(triangle2);
        graphics.drawPolygon(triangle3);
        graphics.drawPolygon(triangle4);
    }

    // Move the triangles into the desired position
    protected void adjustOffset() {
        Rectangle rect = getBounds();
        Point newOffset = rect.getTopLeft();
        if(newOffset != offset) {
            triangle1.performTranslate(-offset.x, -offset.y);
            triangle2.performTranslate(-offset.x, -offset.y);
            triangle3.performTranslate(-offset.x, -offset.y);
            triangle4.performTranslate(-offset.x, -offset.y);
            triangle1.performTranslate(newOffset.x, newOffset.y);
            triangle2.performTranslate(newOffset.x, newOffset.y);
            triangle3.performTranslate(newOffset.x, newOffset.y);
            triangle4.performTranslate(newOffset.x, newOffset.y);
            offset = newOffset;
        }
    }
}
