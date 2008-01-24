package org.csstudio.sds.ui.internal.commands;

import java.util.Iterator;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ConnectionElement;
import org.eclipse.gef.commands.Command;

/**
 * A command, which creates a connection.
 * 
 * @author Sven Wende
 * @version $Revision$
 * 
 */
public final class ConnectionCreateCommand extends Command {
	/**
	 * The connection instance.
	 */
	private ConnectionElement _connection;

	/**
	 * Start model for the connection.
	 */
	private final AbstractWidgetModel _sourceModel;

	/**
	 * Target model for the connection.
	 */
	private AbstractWidgetModel _targetModel;

	/**
	 * Constructor.
	 * 
	 * @param sourceModel
	 *            the source element of the connection
	 * @param lineStyle
	 *            the linestyle
	 */
	public ConnectionCreateCommand(final AbstractWidgetModel sourceModel,
			final int lineStyle) {
		assert sourceModel != null : "sourceElement!=null"; //$NON-NLS-1$
		setLabel("Verbinden");
		_sourceModel = sourceModel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canExecute() {
		// disallow source -> source connections
		if (_sourceModel.equals(_targetModel)) {
			return false;
		}
		// return false, if the source -> target connection exists already
		for (Iterator iter = _sourceModel.getSourceConnections()
				.iterator(); iter.hasNext();) {
			ConnectionElement conn = (ConnectionElement) iter.next();
			if (conn.getTargetModel().equals(_targetModel)) {
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
		// create a new connection between source and target
		_connection = new ConnectionElement(_sourceModel,
				_targetModel);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void redo() {
		_connection.reconnect();
	}

	/**
	 * Set the target element for the connection.
	 * 
	 * @param target
	 *            that target element
	 */
	public void setTarget(final AbstractWidgetModel target) {
		assert target != null : "target!=null"; //$NON-NLS-1$
		this._targetModel = target;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		_connection.disconnect();
	}
}
