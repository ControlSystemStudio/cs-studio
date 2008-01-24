package org.csstudio.sds.ui.internal.commands;

import java.util.Iterator;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ConnectionElement;
import org.eclipse.gef.commands.Command;

/**
 * A command to reconnect a connection to a different start point or end point.
 * The command can be undone or redone.
 * <p>
 * This command is designed to be used together with a GraphicalNodeEditPolicy.
 * To use this command propertly, following steps are necessary:
 * 
 * @author Alexander Will, Sven Wende
 * @version $Revision$
 */
public final class ConnectionReconnectCommand extends Command {

	/**
	 * The connection instance to reconnect.
	 */
	private ConnectionElement _connection;

	/**
	 * The new source endpoint.
	 */
	private AbstractWidgetModel _newSourceModel;

	/**
	 * The new target endpoint.
	 */
	private AbstractWidgetModel _newTargetModel;

	/**
	 * The original source endpoint.
	 */
	private final AbstractWidgetModel _oldSourceModel;

	/**
	 * The original target endpoint.
	 */
	private final AbstractWidgetModel _oldTargetModel;

	/**
	 * Instantiate a command that can reconnect a ConnectionElement instance to
	 * a different source or target endpoint.
	 * 
	 * @param conn
	 *            the connection instance to reconnect (non-null)
	 */
	public ConnectionReconnectCommand(final ConnectionElement conn) {
		assert conn != null : "conn!=null"; //$NON-NLS-1$
		_connection = conn;
		_oldSourceModel = conn.getSourceModel();
		_oldTargetModel = conn.getTargetModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canExecute() {
		if (_newSourceModel != null) {
			return checkSourceReconnection();
		} else if (_newTargetModel != null) {
			return checkTargetReconnection();
		}
		return false;
	}

	/**
	 * Return true, if reconnecting the connection-instance to newSource is
	 * allowed.
	 * 
	 * @return true, if reconnecting the connection-instance to newSource is
	 *         allowed
	 */
	private boolean checkSourceReconnection() {
		// connection endpoints must be different Shapes
		if (_newSourceModel.equals(_oldTargetModel)) {
			return false;
		}
		// return false, if the connection exists already
		for (Iterator iter = _newSourceModel.getSourceConnections()
				.iterator(); iter.hasNext();) {
			ConnectionElement conn = (ConnectionElement) iter.next();
			// return false if a newSource -> oldTarget connection exists
			// already
			// and it is a different instance than the connection-field
			if (conn.getTargetModel().equals(_oldTargetModel)
					&& !conn.equals(_connection)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Return true, if reconnecting the connection-instance to newSource is
	 * allowed.
	 * 
	 * @return true, if reconnecting the connection-instance to newSource is
	 *         allowed
	 */
	private boolean checkTargetReconnection() {
		// connection endpoints must be different Shapes
		if (_newTargetModel.equals(_oldSourceModel)) {
			return false;
		}
		// return false, if the connection exists already
		for (Iterator iter = _newTargetModel.getTargetConnections()
				.iterator(); iter.hasNext();) {
			ConnectionElement conn = (ConnectionElement) iter.next();
			// return false if a oldSource -> newTarget connection exists
			// already
			// and it is a differenct instance that the connection-field
			if (conn.getSourceModel().equals(_oldSourceModel)
					&& !conn.equals(_connection)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		if (_newSourceModel != null) {
			_connection.reconnect(_newSourceModel, _oldTargetModel);
		} else if (_newTargetModel != null) {
			_connection.reconnect(_oldSourceModel, _newTargetModel);
		} else {
			throw new IllegalStateException("Should not happen"); //$NON-NLS-1$
		}
	}

	/**
	 * Set a new source endpoint for this connection. When execute() is invoked,
	 * the source endpoint of the connection will be attached to the supplied
	 * Shape instance.
	 * <p>
	 * Note: Calling this method, deactivates reconnection of the <i>target</i>
	 * endpoint. A single instance of this command can only reconnect either the
	 * source or the target endpoint.
	 * </p>
	 * 
	 * @param sourceModel
	 *            a non-null widget model instance, to be used as a new source
	 *            endpoint
	 */
	public void setNewSource(final AbstractWidgetModel sourceModel) {
		assert sourceModel != null : "sourceElement!=null"; //$NON-NLS-1$
		setLabel("Verbindung bewegen");
		_newSourceModel = sourceModel;
		_newTargetModel = null;
	}

	/**
	 * Set a new target endpoint for this connection When execute() is invoked,
	 * the target endpoint of the connection will be attached to the supplied
	 * Shape instance.
	 * <p>
	 * Note: Calling this method, deactivates reconnection of the <i>source</i>
	 * endpoint. A single instance of this command can only reconnect either the
	 * source or the target endpoint.
	 * </p>
	 * 
	 * @param targetModel
	 *            a non-null widget model instance, to be used as a new target
	 *            endpoint
	 */
	public void setNewTarget(final AbstractWidgetModel targetModel) {
		assert targetModel != null : "sourceElement!=null"; //$NON-NLS-1$
		setLabel("Verbindung bewegen");
		_newSourceModel = null;
		_newTargetModel = targetModel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		_connection.reconnect(_oldSourceModel, _oldTargetModel);
	}

}
