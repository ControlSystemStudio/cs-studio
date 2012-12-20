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
import org.eclipse.gef.commands.Command;



/**
 * A command to reconnect a connection to a different start point or end point.
 * 
 * @author Xihui Chen
 */
public class ConnectionReconnectCommand extends Command {

	/** The connection model. */
	private ConnectionModel connection;

	private AbstractWidgetModel newSource, newTarget, oldSource, oldTarget;

	private String newSourceTerminal, newTargetTerminal, oldSourceTerminal, oldTargetTerminal;

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
	public ConnectionReconnectCommand(ConnectionModel conn) {
		if (conn == null) {
			throw new IllegalArgumentException();
		}
		setLabel("Reconnect");
		this.connection = conn;
		this.oldSource=conn.getSource();
		this.oldTarget= conn.getTarget();
		this.oldSourceTerminal=conn.getSourceTerminal();
		this.oldTargetTerminal=conn.getTargetTerminal();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
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
		// connection endpoints must be different widgets
		if (newSource.equals(oldTarget)) {
			return false;
		}		
		return true;
	}
	
	/**
	 * Return true, if reconnecting the connection-instance to newTarget is allowed. 
	 */
	private boolean checkTargetReconnection() {
		// connection endpoints must be different widgets
		if (newTarget.equals(oldSource)) {
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
		if(newSource != null && newSourceTerminal != null){
			connection.connect(newSource, newSourceTerminal, 
					oldTarget, oldTargetTerminal);
		}else if(newTarget != null && newTargetTerminal != null){
			connection.connect(oldSource, oldSourceTerminal, newTarget, newTargetTerminal);
		}else {
			throw new IllegalStateException("Connection requirement is not met");
		}
	}
	
	public void setNewSource(AbstractWidgetModel newSource) {
		if(newSource == null){
			throw new IllegalArgumentException();
		}
		this.newSource = newSource;
		newTarget = null;
	}	
	
	public void setNewSourceTerminal(String newSourceTerminal) {
		this.newSourceTerminal = newSourceTerminal;
		newTargetTerminal = null;
	}
	
	public void setNewTarget(AbstractWidgetModel newTarget) {
		if(newTarget == null){
			throw new IllegalArgumentException();
		}
		this.newTarget = newTarget;
		newSource = null;
	}
	
	public void setNewTargetTerminal(String newTargetTerminal) {
		this.newTargetTerminal = newTargetTerminal;
		newSourceTerminal = null;
	}



	public void undo() {
		connection.connect(oldSource, oldSourceTerminal, oldTarget, oldTargetTerminal);
	}
}
