package org.csstudio.dct.ui.graphicalviewer.view;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Hello Gef's custom connection anchors. These anchors are supposed to be
 * located relative to their "parent" figure, i.e., to the figure of the node
 * EditPart they belong to.
 */
public class ConnectionAnchor extends AbstractConnectionAnchor {

    public boolean leftToRight = true;
    public int offsetH;
    public int offsetV;
    public boolean topDown = true;

    public ConnectionAnchor(IFigure iOwner) {
        super(iOwner);
    }

    /**
     * This method is taken from FixedConnectionAnchor in Logical Diagram Editor
     * example.
     **/
    @Override
    public Point getLocation(Point iReference) {
        Rectangle r = getOwner().getBounds();
        int x, y;
        if (topDown)
            y = r.y + offsetV;
        else
            y = r.y + r.height - offsetV;

        if (leftToRight)
            x = r.x + offsetH;
        else
            x = r.x + r.width - offsetH;

        Point p = new Point(x, y);
        getOwner().translateToAbsolute(p);

        return p;
    }
}
