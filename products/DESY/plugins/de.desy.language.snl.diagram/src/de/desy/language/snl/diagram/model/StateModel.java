/*******************************************************************************
 * Copyright (c) 2004, 2005 Elias Volanakis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Elias Volanakis - initial API and implementation
 *******************************************************************************/
package de.desy.language.snl.diagram.model;

import de.desy.language.snl.parser.nodes.StateNode;

/**
 * An elliptical shape.
 * 
 * @author Elias Volanakis
 */
public class StateModel extends SNLModel {

	private static final long serialVersionUID = 1;
	
	public static final String STATE_NAME_PROP = "StateModel.Name";

	public static final String WHENS_PROP = "StateModel.Whens";
	
	private StateNode _stateNode;
	
	public String getIconName() {
		return "ellipse16.gif";
	}

	public String toString() {
		return "State '" + _stateNode.getSourceIdentifier() + "'";
	}

	public StateNode getStateNode() {
		return _stateNode;
	}

	public void setStateNode(StateNode stateNode) {
		_stateNode = stateNode;
	}

	@Override
	public String getIdentifier() {
		return _stateNode.getSourceIdentifier();
	}

	@Override
	protected boolean canHaveChildren() {
		return false;
	}	
	
}
