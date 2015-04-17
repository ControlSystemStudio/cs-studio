/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.commands;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.ConnectionModel;
import org.csstudio.opibuilder.util.SchemaService;
import org.eclipse.gef.commands.Command;

/**
 * A command to create a connection between two widgets.
 * 
 * @author Xihui Chen
 */
public class ConnectionCreateCommand extends Command {

	/** The connection model. */
	private ConnectionModel connection;

	private final AbstractWidgetModel source;

	private AbstractWidgetModel target;

	private String sourceTerminal, targetTerminal;

	/**
	 * Instantiate a command that can create a connection between two widgets.
	 * 
	 * @param source
	 *            the source endpoint
	 * @param terminal
	 *            terminal on the source
	 * @throws IllegalArgumentException
	 *             if source is null
	 */
	public ConnectionCreateCommand(AbstractWidgetModel source,
			String sourceTerminal) {
		if (source == null) {
			throw new IllegalArgumentException();
		}
		setLabel("Create Connection");
		this.source = source;
		this.sourceTerminal = sourceTerminal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		// disallow source -> source connections
		if (source.equals(target)) {
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		// create a new connection between source and target
		if (connection == null) {
			connection = new ConnectionModel(source.getRootDisplayModel());
			SchemaService.getInstance().applySchema(connection);
		}
		connection.setSource(source);
		connection.setSourceTerminal(sourceTerminal);
		connection.setTarget(target);
		connection.setTargetTerminal(targetTerminal);
		connection.reconnect();
	}

	public void redo() {
		connection.reconnect();
	}

	/**
	 * Set the target for the connection.
	 * 
	 * @param target
	 *            that target
	 * @throws IllegalArgumentException
	 *             if target is null
	 */
	public void setTarget(AbstractWidgetModel target) {
		if (target == null) {
			throw new IllegalArgumentException();
		}
		this.target = target;
	}

	public void setTargetTerminal(String targetTerminal) {
		this.targetTerminal = targetTerminal;
	}

	public void undo() {
		connection.disconnect();
	}
}
