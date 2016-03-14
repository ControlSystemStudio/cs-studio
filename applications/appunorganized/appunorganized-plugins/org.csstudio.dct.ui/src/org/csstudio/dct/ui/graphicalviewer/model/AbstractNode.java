package org.csstudio.dct.ui.graphicalviewer.model;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.IElement;

/**
 * Base class for elements of the graphical model which represent nodes. Nodes
 * may be connected by {@link Connection}s.
 *
 * @author Sven Wende
 *
 * @param <E>
 */
public abstract class AbstractNode<E extends IElement> extends AbstractBase {
    public static final String SOURCE_CONNECTIONS_PROP = "Variable.SourceConnections"; //$NON-NLS-1$
    public static final String TARGET_CONNECTIONS_PROP = "Variable.TargetConnections"; //$NON-NLS-1$

    private E element;

    private List<Connection> _sourceConnections;
    private List<Connection> _targetConnections;

    /**
     * Standard constructor.
     *
     * @param element
     *            the corresponding element in the original dct model
     */
    public AbstractNode(E element) {
        assert element != null;
        this.element = element;
        _sourceConnections = new ArrayList<Connection>();
        _targetConnections = new ArrayList<Connection>();

    }

    /**
     * Accept method for the standard GOF visitor pattern.
     *
     * @param visitor
     *            the visitor
     */
    public abstract void accept(INodeVisitor visitor);

    /**
     * Subclasses should return an appropriate caption for the node depending on
     * the supplied element from the original dct model.
     *
     * @param element
     *            the orgiginal element from the dct model
     *
     * @return an appropriate caption
     */
    protected abstract String doGetCaption(E element);

    /**
     * Returns the corresponding element in the original dct model.
     *
     * @return the corresponding element in the original dct model
     */
    public E getElement() {
        return element;
    }

    /**
     * Returns a list of outgoing connections.
     *
     * @return a non-null List instance, the list may be empty
     */
    public List<Connection> getSourceConnections() {
        return new ArrayList<Connection>(_sourceConnections);
    }

    /**
     * Returns a list of incoming connections.
     *
     * @return a non-null List instance, the list may be empty
     */
    public List<Connection> getTargetConnections() {
        return new ArrayList<Connection>(_targetConnections);
    }

    /**
     * Returns the caption of this element.
     *
     * @return a non-null String instance
     */
    @Override
    public String getCaption() {
        String s = doGetCaption(element);

        return s != null ? s : "Unknown";
    }

    /**
     * Add an incoming or outgoing connection to this node. This method will be
     * called from the {@link Connection}.
     *
     * @param connection
     *            a connection
     */
    public void addConnection(Connection connection) {
        if (connection != null && connection.getSourceNode() != connection.getTargetNode()) {
            if (connection.getSourceNode() == this) {
                _sourceConnections.add(connection);
                firePropertyChange(SOURCE_CONNECTIONS_PROP, null, connection);
            } else if (connection.getTargetNode() == this) {
                _targetConnections.add(connection);
                firePropertyChange(TARGET_CONNECTIONS_PROP, null, connection);
            }
        }
    }

    /**
     * Removes an incoming or outgoing connection from this node. This method
     * should stay package protected and will be called from the Connection
     * class only.
     *
     * @param connection
     *            a connection
     */
    public void removeConnection(Connection connection) {
        if (connection != null) {
            if (connection.getSourceNode() == this) {
                _sourceConnections.remove(connection);
                firePropertyChange(SOURCE_CONNECTIONS_PROP, null, connection);
            } else if (connection.getTargetNode() == this) {
                _targetConnections.remove(connection);
                firePropertyChange(TARGET_CONNECTIONS_PROP, null, connection);
            }
        }
    }

    /**
     * Removes all connections from this node.
     */
    public void removeAllConnections() {
        _sourceConnections = new ArrayList<Connection>();
        _targetConnections = new ArrayList<Connection>();
    }

}
