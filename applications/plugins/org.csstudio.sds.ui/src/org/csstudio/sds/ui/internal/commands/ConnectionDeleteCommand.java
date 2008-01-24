package org.csstudio.sds.ui.internal.commands;

import org.csstudio.sds.model.ConnectionElement;
import org.eclipse.gef.commands.Command;

/**
 * A command to disconnect (remove) a connection from its endpoints.
 * 
 * @author Alexander Will and Sven Wende
 * @version $Revision$
 */
public final class ConnectionDeleteCommand extends Command {

	/**
	 * Connection instance to disconnect.
	 */
	private final ConnectionElement _connection;

	/**
	 * Constructor.
	 * 
	 * @param connection
	 *            the connection
	 */
	public ConnectionDeleteCommand(final ConnectionElement connection) {
		assert connection != null : "connection!=null"; //$NON-NLS-1$
		setLabel("Verbindung entfernen");
		_connection = connection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		_connection.disconnect();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		_connection.reconnect();
	}
}
