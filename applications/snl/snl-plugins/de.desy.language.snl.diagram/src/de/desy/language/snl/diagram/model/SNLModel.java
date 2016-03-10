package de.desy.language.snl.diagram.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

/**
 * Abstract prototype of a shape. Has a size (width and height), a location (x
 * and y position) and a list of incoming and outgoing connections. Use
 * subclasses to instantiate a specific shape.
 *
 * @see de.desy.language.snl.diagram.model.RectangularShape
 * @see de.desy.language.snl.diagram.model.StateModel
 */
public abstract class SNLModel extends SNLElement {

    /**
     * ID for the Height property value (used for by the corresponding property
     * descriptor).
     */
    private static final String HEIGHT_PROP = "Shape.Height";
    /** Property ID to use when the location of this shape is modified. */
    public static final String LOCATION_PROP = "Shape.Location";
    private static final long serialVersionUID = 1;
    /** Property ID to use then the size of this shape is modified. */
    public static final String SIZE_PROP = "Shape.Size";
    /** Property ID to use when the list of outgoing connections is modified. */
    public static final String SOURCE_CONNECTIONS_PROP = "Shape.SourceConn";
    /** Property ID to use when the list of incoming connections is modified. */
    public static final String TARGET_CONNECTIONS_PROP = "Shape.TargetConn";

    /**
     * ID for the Width property value (used for by the corresponding property
     * descriptor).
     */
    private static final String WIDTH_PROP = "Shape.Width";

    /**
     * ID for the X property value (used for by the corresponding property
     * descriptor).
     */
    private static final String XPOS_PROP = "Shape.xPos";
    /**
     * ID for the Y property value (used for by the corresponding property
     * descriptor).
     */
    private static final String YPOS_PROP = "Shape.yPos";

    /** Location of this shape. */
    private Point location = new Point(0, 0);
    /** Size of this shape. */
    private Dimension size = new Dimension(50, 50);
    /** List of outgoing Connections. */
    private List<WhenConnection> sourceConnections = new ArrayList<WhenConnection>();
    /** List of incoming Connections. */
    private List<WhenConnection> targetConnections = new ArrayList<WhenConnection>();

    /**
     * Add an incoming or outgoing connection to this shape.
     *
     * @param conn
     *            a non-null connection instance
     * @throws IllegalArgumentException
     *             if the connection is null or has not distinct endpoints
     */
    void addConnection(WhenConnection conn) {
        if (conn == null || conn.getSource() == conn.getTarget()) {
            throw new IllegalArgumentException();
        }
        if (conn.getSource() == this) {
            sourceConnections.add(conn);
            firePropertyChange(SOURCE_CONNECTIONS_PROP, null, conn);
        } else if (conn.getTarget() == this) {
            targetConnections.add(conn);
            firePropertyChange(TARGET_CONNECTIONS_PROP, null, conn);
        }
    }

    public abstract String getIconName();

    /**
     * Return the Location of this shape.
     *
     * @return a non-null location instance
     */
    public Point getLocation() {
        return location.getCopy();
    }

    /**
     * Return the property value for the given propertyId, or null.
     * <p>
     * The property view uses the IDs from the IPropertyDescriptors array to
     * obtain the value of the corresponding properties.
     * </p>
     *
     * @see #descriptors
     * @see #getPropertyDescriptors()
     */
    @Override
    public Object getPropertyValue(Object propertyId) {
        if (XPOS_PROP.equals(propertyId)) {
            return Integer.toString(location.x);
        }
        if (YPOS_PROP.equals(propertyId)) {
            return Integer.toString(location.y);
        }
        if (HEIGHT_PROP.equals(propertyId)) {
            return Integer.toString(size.height);
        }
        if (WIDTH_PROP.equals(propertyId)) {
            return Integer.toString(size.width);
        }
        return super.getPropertyValue(propertyId);
    }

    /**
     * Return the Size of this shape.
     *
     * @return a non-null Dimension instance
     */
    public Dimension getSize() {
        return size.getCopy();
    }

    /**
     * Return a List of outgoing Connections.
     */
    public List<WhenConnection> getSourceConnections() {
        return new ArrayList<WhenConnection>(sourceConnections);
    }

    /**
     * Return a List of incoming Connections.
     */
    public List<WhenConnection> getTargetConnections() {
        return new ArrayList<WhenConnection>(targetConnections);
    }

    /**
     * Remove an incoming or outgoing connection from this shape.
     *
     * @param conn
     *            a non-null connection instance
     * @throws IllegalArgumentException
     *             if the parameter is null
     */
    void removeConnection(WhenConnection conn) {
        if (conn == null) {
            throw new IllegalArgumentException();
        }
        if (conn.getSource() == this) {
            sourceConnections.remove(conn);
            firePropertyChange(SOURCE_CONNECTIONS_PROP, null, conn);
        } else if (conn.getTarget() == this) {
            targetConnections.remove(conn);
            firePropertyChange(TARGET_CONNECTIONS_PROP, null, conn);
        }
    }

    /**
     * Set the Location of this shape.
     *
     * @param newLocation
     *            a non-null Point instance
     * @throws IllegalArgumentException
     *             if the parameter is null
     */
    public void setLocation(Point newLocation) {
        if (newLocation == null) {
            throw new IllegalArgumentException();
        }
        location.setLocation(newLocation);
        firePropertyChange(LOCATION_PROP, null, location);
    }

    /**
     * Set the property value for the given property id. If no matching id is
     * found, the call is forwarded to the superclass.
     * <p>
     * The property view uses the IDs from the IPropertyDescriptors array to set
     * the values of the corresponding properties.
     * </p>
     *
     * @see #descriptors
     * @see #getPropertyDescriptors()
     */
    @Override
    public void setPropertyValue(Object propertyId, Object value) {
        if (XPOS_PROP.equals(propertyId)) {
            int x = Integer.parseInt((String) value);
            setLocation(new Point(x, location.y));
        } else if (YPOS_PROP.equals(propertyId)) {
            int y = Integer.parseInt((String) value);
            setLocation(new Point(location.x, y));
        } else if (HEIGHT_PROP.equals(propertyId)) {
            int height = Integer.parseInt((String) value);
            setSize(new Dimension(size.width, height));
        } else if (WIDTH_PROP.equals(propertyId)) {
            int width = Integer.parseInt((String) value);
            setSize(new Dimension(width, size.height));
        } else {
            super.setPropertyValue(propertyId, value);
        }
    }

    /**
     * Set the Size of this shape. Will not modify the size if newSize is null.
     *
     * @param newSize
     *            a non-null Dimension instance or null
     */
    public void setSize(Dimension newSize) {
        if (newSize != null) {
            size.setSize(newSize);
            firePropertyChange(SIZE_PROP, null, size);
        }
    }

}