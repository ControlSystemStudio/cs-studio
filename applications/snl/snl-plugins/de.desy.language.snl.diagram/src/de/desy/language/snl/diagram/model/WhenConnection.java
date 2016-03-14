package de.desy.language.snl.diagram.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;

import de.desy.language.snl.parser.nodes.WhenNode;


/**
 * A connection between two distinct shapes.
 */
public class WhenConnection extends ModelElement {
    /**
     * Used for indicating that a Connection with solid line style should be
     * created.
     *
     * @see org.eclipse.gef.examples.shapes.parts.ShapeEditPart#createEditPolicies()
     */
    public static final Integer SOLID_CONNECTION = 1;
    /** Property ID to use when the line style of this connection is modified. */
    private static final long serialVersionUID = 1;

    /** True, if the connection is attached to its endpoints. */
    private boolean isConnected;
    /** Line drawing style for this connection. */
    private int lineStyle = SOLID_CONNECTION;
    /** Connection's source endpoint. */
    private SNLModel source;
    /** Connection's target endpoint. */
    private SNLModel target;

    private WhenNode _whenNode;

    private List<Point> _bendPoints;

    /**
     * Create a (solid) connection between two distinct shapes.
     *
     * @param source
     *            a source endpoint for this connection (non null)
     * @param target
     *            a target endpoint for this connection (non null)
     * @throws IllegalArgumentException
     *             if any of the parameters are null or source == target
     * @see #setLineStyle(int)
     */
    public WhenConnection(SNLModel source, SNLModel target) {
        _bendPoints = new ArrayList<Point>();
        reconnect(source, target);
    }

    public WhenNode getWhenNode() {
        return _whenNode;
    }

    public void setWhenNode(WhenNode whenNode) {
        _whenNode = whenNode;
    }

    /**
     * Disconnect this connection from the shapes it is attached to.
     */
    public void disconnect() {
        if (isConnected) {
            source.removeConnection(this);
            target.removeConnection(this);
            isConnected = false;
        }
    }

    /**
     * Returns the line drawing style of this connection.
     *
     * @return an int value (Graphics.LINE_DASH or Graphics.LINE_SOLID)
     */
    public int getLineStyle() {
        return lineStyle;
    }

    /**
     * Returns the lineStyle as String for the Property Sheet
     *
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
     */
    @Override
    public Object getPropertyValue(Object id) {
        return super.getPropertyValue(id);
    }

    /**
     * Returns the source endpoint of this connection.
     *
     * @return a non-null Shape instance
     */
    public SNLModel getSource() {
        return source;
    }

    /**
     * Returns the target endpoint of this connection.
     *
     * @return a non-null Shape instance
     */
    public SNLModel getTarget() {
        return target;
    }

    /**
     * Reconnect this connection. The connection will reconnect with the shapes
     * it was previously attached to.
     */
    public void reconnect() {
        if (!isConnected) {
            source.addConnection(this);
            target.addConnection(this);
            isConnected = true;
        }
    }

    /**
     * Reconnect to a different source and/or target shape. The connection will
     * disconnect from its current attachments and reconnect to the new source
     * and target.
     *
     * @param newSource
     *            a new source endpoint for this connection (non null)
     * @param newTarget
     *            a new target endpoint for this connection (non null)
     * @throws IllegalArgumentException
     *             if any of the paramers are null or newSource == newTarget
     */
    public void reconnect(SNLModel newSource, SNLModel newTarget) {
        if (newSource == null || newTarget == null || newSource == newTarget) {
            throw new IllegalArgumentException();
        }
        disconnect();
        this.source = newSource;
        this.target = newTarget;
        reconnect();
    }

    public void addBendPoint(Point bendPoint, int index) {
        _bendPoints.add(index, bendPoint);
        firePropertyChange("BendPoint added", null, _bendPoints);
    }

    public void removeBendPoint(int index) {
        _bendPoints.remove(index);
        firePropertyChange("BendPoint removed", null, _bendPoints);
    }

    public void moveBendPoint(int index, Point newLocation) {
        Point point = _bendPoints.get(index);
        point.setLocation(newLocation);
        firePropertyChange("BendPoint moved", null, _bendPoints);
    }

    public List<Point> getBendPoints() {
        return _bendPoints;
    }

    @Override
    public String getIdentifier() {
        return _whenNode.getSourceIdentifier();
    }

}