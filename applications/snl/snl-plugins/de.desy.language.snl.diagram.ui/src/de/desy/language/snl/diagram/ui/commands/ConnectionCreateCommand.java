package de.desy.language.snl.diagram.ui.commands;

import java.util.Iterator;

import org.eclipse.gef.commands.Command;

import de.desy.language.snl.diagram.model.WhenConnection;
import de.desy.language.snl.diagram.model.SNLModel;


/**
 * A command to create a connection between two shapes.
 * The command can be undone or redone.
 * <p>
 * This command is designed to be used together with a GraphicalNodeEditPolicy.
 * To use this command properly, following steps are necessary:
 * </p>
 * <ol>
 * <li>Create a subclass of GraphicalNodeEditPolicy.</li>
 * <li>Override the <tt>getConnectionCreateCommand(...)</tt> method,
 * to create a new instance of this class and put it into the CreateConnectionRequest.</li>
 * <li>Override the <tt>getConnectionCompleteCommand(...)</tt>  method,
 * to obtain the Command from the ConnectionRequest, call setTarget(...) to set the
 * target endpoint of the connection and return this command instance.</li>
 * </ol>
 * @see de.desy.language.snl.diagram.ui.parts.ShapeEditPart#createEditPolicies() for an
 *              example of the above procedure.
 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy
 */
public class ConnectionCreateCommand extends Command {
/** The connection instance. */
private WhenConnection connection;

/** Start endpoint for the connection. */
private final SNLModel source;
/** Target endpoint for the connection. */
private SNLModel target;

/**
 *    Instantiate a command that can create a connection between two shapes.
 * @param source the source endpoint (a non-null Shape instance)
 * @param lineStyle the desired line style. See Connection#setLineStyle(int) for details
 * @throws IllegalArgumentException if source is null
 * @see WhenConnection#setLineStyle(int)
 */
public ConnectionCreateCommand(final SNLModel source) {
    if (source == null) {
        throw new IllegalArgumentException();
    }
    setLabel("connection creation");
    this.source = source;
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#canExecute()
 */
@Override
public boolean canExecute() {
    // disallow source -> source connections
    if (source.equals(target)) {
        return false;
    }
    // return false, if the source -> target connection exists already
    for (final Iterator<WhenConnection> iter = source.getSourceConnections().iterator(); iter.hasNext();) {
        final WhenConnection conn = iter.next();
        if (conn.getTarget().equals(target)) {
            return false;
        }
    }
    return true;
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#execute()
 */
@Override
public void execute() {
    // create a new connection between source and target
    connection = new WhenConnection(source, target);
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#redo()
 */
@Override
public void redo() {
    connection.reconnect();
}

/**
 * Set the target endpoint for the connection.
 * @param target that target endpoint (a non-null Shape instance)
 * @throws IllegalArgumentException if target is null
 */
public void setTarget(final SNLModel target) {
    if (target == null) {
        throw new IllegalArgumentException();
    }
    this.target = target;
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#undo()
 */
@Override
public void undo() {
    connection.disconnect();
}
}
