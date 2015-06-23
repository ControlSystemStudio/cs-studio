package de.desy.language.snl.diagram.persistence;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

/**
 * A container for the location and the size of a state or state-set
 *
 * @author Kai Meyer, Sebastian Middeke (C1 WPS)
 *
 */
public class StateLayoutData {

    private final Point _point;
    private final Dimension _dimension;

    /**
     * Constructor.
     *
     * @param point
     *            The location
     * @param dimension
     *            The size
     * @require point != null
     * @require dimension != null
     */
    public StateLayoutData(Point point, Dimension dimension) {
        assert point != null : "point != null";
        assert dimension != null : "dimension != null";

        _point = point;
        _dimension = dimension;
    }

    public Point getPoint() {
        return _point;
    }

    public Dimension getDimension() {
        return _dimension;
    }

}
