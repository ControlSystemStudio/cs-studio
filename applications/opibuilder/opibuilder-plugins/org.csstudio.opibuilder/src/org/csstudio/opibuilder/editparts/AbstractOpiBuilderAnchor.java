package org.csstudio.opibuilder.editparts;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

abstract public class AbstractOpiBuilderAnchor extends AbstractConnectionAnchor {

    public AbstractOpiBuilderAnchor(IFigure owner) {
        super(owner);
    }

    abstract public Point getSlantDifference(Point anchorPoint, Point midPoint);
}
