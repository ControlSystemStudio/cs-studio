package de.desy.language.snl.diagram.ui.commands;

import java.util.Iterator;

import org.eclipse.gef.commands.Command;

import de.desy.language.snl.diagram.model.SNLModel;
import de.desy.language.snl.diagram.model.WhenConnection;


/**
 * A command to reconnect a connection to a different start point or end point.
 * The command can be undone or redone.
 * <p>
 * This command is designed to be used together with a GraphicalNodeEditPolicy.
 * To use this command propertly, following steps are necessary:
 * </p>
 * <ol>
 * <li>Create a subclass of GraphicalNodeEditPolicy.</li>
 * <li>Override the <tt>getReconnectSourceCommand(...)</tt> method.
 * Here you need to obtain the Connection model element from the ReconnectRequest,
 * create a new ConnectionReconnectCommand, set the new connection <i>source</i> by calling
 * the <tt>setNewSource(Shape)</tt> method and return the command instance.
 * <li>Override the <tt>getReconnectTargetCommand(...)</tt> method.</li>
 * Here again you need to obtain the Connection model element from the ReconnectRequest,
 * create a new ConnectionReconnectCommand, set the new connection <i>target</i> by calling
 * the <tt>setNewTarget(Shape)</tt> method and return the command instance.</li>
 * </ol>
 * @see de.desy.language.snl.diagram.ui.parts.ShapeEditPart#createEditPolicies() for an
 *              example of the above procedure.
 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy
 * @see #setNewSource(SNLModel)
 * @see #setNewTarget(SNLModel)
 */
public class ConnectionReconnectCommand extends Command {

/** The connection instance to reconnect. */
private final WhenConnection connection;
/** The new source endpoint. */
private SNLModel newSource;
/** The new target endpoint. */
private SNLModel newTarget;
/** The original source endpoint. */
private final SNLModel oldSource;
/** The original target endpoint. */
private final SNLModel oldTarget;

/**
 * Instantiate a command that can reconnect a Connection instance to a different source
 * or target endpoint.
 * @param conn the connection instance to reconnect (non-null)
 * @throws IllegalArgumentException if conn is null
 */
public ConnectionReconnectCommand(final WhenConnection conn) {
    if (conn == null) {
        throw new IllegalArgumentException();
    }
    this.connection = conn;
    this.oldSource = conn.getSource();
    this.oldTarget = conn.getTarget();
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#canExecute()
 */
@Override
public boolean canExecute() {
    if (newSource != null) {
        return checkSourceReconnection();
    } else if (newTarget != null) {
        return checkTargetReconnection();
    }
    return false;
}

/**
 * Return true, if reconnecting the connection-instance to newSource is allowed.
 */
private boolean checkSourceReconnection() {
    // connection endpoints must be different Shapes
    if (newSource.equals(oldTarget)) {
        return false;
    }
    // return false, if the connection exists already
    for (final Iterator<WhenConnection> iter = newSource.getSourceConnections().iterator(); iter.hasNext();) {
        final WhenConnection conn = iter.next();
        // return false if a newSource -> oldTarget connection exists already
        // and it is a different instance than the connection-field
        if (conn.getTarget().equals(oldTarget) &&  !conn.equals(connection)) {
            return false;
        }
    }
    return true;
}

/**
 * Return true, if reconnecting the connection-instance to newTarget is allowed.
 */
private boolean checkTargetReconnection() {
    // connection endpoints must be different Shapes
    if (newTarget.equals(oldSource)) {
        return false;
    }
    // return false, if the connection exists already
    for (final Iterator<WhenConnection> iter = newTarget.getTargetConnections().iterator(); iter.hasNext();) {
        final WhenConnection conn = (WhenConnection) iter.next();
        // return false if a oldSource -> newTarget connection exists already
        // and it is a differenct instance that the connection-field
        if (conn.getSource().equals(oldSource) && !conn.equals(connection)) {
            return false;
        }
    }
    return true;
}

/**
 * Reconnect the connection to newSource (if setNewSource(...) was invoked before)
 * or newTarget (if setNewTarget(...) was invoked before).
 */
@Override
public void execute() {
    if (newSource != null) {
        connection.reconnect(newSource, oldTarget);
    } else if (newTarget != null) {
        connection.reconnect(oldSource, newTarget);
    } else {
        throw new IllegalStateException("Should not happen");
    }
}

/**
 * Set a new source endpoint for this connection.
 * When execute() is invoked, the source endpoint of the connection will be attached
 * to the supplied Shape instance.
 * <p>
 * Note: Calling this method, deactivates reconnection of the <i>target</i> endpoint.
 * A single instance of this command can only reconnect either the source or the target
 * endpoint.
 * </p>
 * @param connectionSource a non-null Shape instance, to be used as a new source endpoint
 * @throws IllegalArgumentException if connectionSource is null
 */
public void setNewSource(final SNLModel connectionSource) {
    if (connectionSource == null) {
        throw new IllegalArgumentException();
    }
    setLabel("move connection startpoint");
    newSource = connectionSource;
    newTarget = null;
}

/**
 * Set a new target endpoint for this connection
 * When execute() is invoked, the target endpoint of the connection will be attached
 * to the supplied Shape instance.
 * <p>
 * Note: Calling this method, deactivates reconnection of the <i>source</i> endpoint.
 * A single instance of this command can only reconnect either the source or the target
 * endpoint.
 * </p>
 * @param connectionTarget a non-null Shape instance, to be used as a new target endpoint
 * @throws IllegalArgumentException if connectionTarget is null
 */
public void setNewTarget(final SNLModel connectionTarget) {
    if (connectionTarget == null) {
        throw new IllegalArgumentException();
    }
    setLabel("move connection endpoint");
    newSource = null;
    newTarget = connectionTarget;
}

/**
 * Reconnect the connection to its original source and target endpoints.
 */
@Override
public void undo() {
    connection.reconnect(oldSource, oldTarget);
}

}
