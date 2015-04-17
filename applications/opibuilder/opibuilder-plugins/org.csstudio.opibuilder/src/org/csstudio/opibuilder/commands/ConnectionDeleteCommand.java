/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.commands;

import org.csstudio.opibuilder.model.ConnectionModel;
import org.eclipse.gef.commands.Command;

/**
 * A command to disconnect (remove) a connection from its endpoints.
 * 
 * @author Xihui Chen
 */
public class ConnectionDeleteCommand extends Command {

	/** Connection Model */
	private final ConnectionModel connection;

	/**
	 * Create a command that will disconnect a connection from its endpoints.
	 * 
	 * @param conn
	 *            the connection model (non-null)
	 * @throws IllegalArgumentException
	 *             if conn is null
	 */
	public ConnectionDeleteCommand(ConnectionModel conn) {
		if (conn == null) {
			throw new IllegalArgumentException();
		}
		setLabel("Delete Connection");
		this.connection = conn;
	}

	public void execute() {
		connection.disconnect();
	}

	public void undo() {
		connection.reconnect();
	}
}
