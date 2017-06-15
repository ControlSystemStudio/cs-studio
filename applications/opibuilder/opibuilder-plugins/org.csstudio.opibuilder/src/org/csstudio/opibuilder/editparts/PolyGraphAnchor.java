/****************************************************************************
* Copyright (c) 2010-2017 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
****************************************************************************/
package org.csstudio.opibuilder.editparts;

import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * <p>
 * This class represents an anchor on a polygon or polyline widget.
 * </p><p>
 * On a normal widget the there are just 8 possible positions for the connector,
 * and they are all on the widget bounding box:
 * <ul>
 * <li> 4 are on the middle of each edge of the bounding box</li>
 * <li> 4 are on each corner of the bounding box</li>
 * </ul>
 * This is true even if the widget does not will its bounding box completely.
 * </p><p>
 * On a polyline or a polygon widget the anchors are wherever there is a bend in the
 * polyline.
 * </p>
 * @author mvitorovic
 */
public class PolyGraphAnchor extends AbstractOpiBuilderAnchor {
    private int pointIndex;
    private Polyline polyline;

    public PolyGraphAnchor(final Polyline owner, final int pointIndex) {
        super(owner);
        this.polyline = owner;
        this.pointIndex = pointIndex;
    }

    @Override
    public Point getLocation(Point reference) {
        Point p = polyline.getPoints().getPoint(pointIndex);
        polyline.translateToAbsolute(p);
        return p;
    }

    @Override
    public Point getReferencePoint() {
        return getLocation(null);
    }

    @Override
    public ConnectorOrientation getOrientation() {
        return getOrientation(polyline.getPoints().getPoint(pointIndex));
    }

    private ConnectorOrientation getOrientation(final Point anchor) {
        // calculate the direction. The direction for now is decided like this:
        // if the connector is closest to the left or right side of the bounding box, then horizontal connection line is selected
        // if the connector is closest to the top or bottom side of the bounding box, then vertical connection line is selected
        final Rectangle bounds = getOwner().getBounds();
        // The bounds in is relative coordinates, so relative to the clipping/bounding box if in a linked container ==> translate the anchor as well
        final Point translatedAnchor = anchor.getCopy();
        // calculate the smallest absolute offset from the left and right bound
        int leftRight = Math.min(Math.abs(translatedAnchor.x - bounds.x), Math.abs(translatedAnchor.x - (bounds.x + bounds.width)));
        // calculate the smallest absolute offset from the top and bottom bound
        int topBottom = Math.min(Math.abs(translatedAnchor.y - bounds.y), Math.abs(translatedAnchor.y - (bounds.y + bounds.height)));
        // anchorPoint and midPoint are both in original coordinates
        if (leftRight < topBottom)
            return ConnectorOrientation.HORIZONTAL;
        else
            return ConnectorOrientation.VERTICAL;
    }
}
