package de.desy.language.snl.diagram.ui.commands;

import org.eclipse.gef.commands.Command;

import de.desy.language.snl.diagram.model.WhenConnection;

/**
 * A command to disconnect (remove) a connection from its endpoints.
 * The command can be undone or redone.
 */
public class ConnectionDeleteCommand extends Command {

/** Connection instance to disconnect. */
private final WhenConnection connection;

/**
 * Create a command that will disconnect a connection from its endpoints.
 * @param conn the connection instance to disconnect (non-null)
 * @throws IllegalArgumentException if conn is null
 */
public ConnectionDeleteCommand(final WhenConnection conn) {
    if (conn == null) {
        throw new IllegalArgumentException();
    }
    setLabel("connection deletion");
    this.connection = conn;
}


/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#execute()
 */
@Override
public void execute() {
    connection.disconnect();
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#undo()
 */
@Override
public void undo() {
    connection.reconnect();
}
}
