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

import de.desy.language.snl.parser.nodes.StateSetNode;

/**
 * An elliptical shape.
 * 
 */
public class StateSetModel extends SNLModel {

	private static final long serialVersionUID = 1;
	private StateSetNode _stateSetNode;

	public String getIconName() {
		return "rectangle16.gif";
	}

	public String toString() {
		return "StateSet '" + _stateSetNode.getSourceIdentifier() + "'";
	}

	@Override
	public String getIdentifier() {
		return _stateSetNode.getSourceIdentifier();
	}
	
	public void setStateSetNode(StateSetNode node) {
		_stateSetNode = node;
	}
	
	public StateSetNode getStateSetNode() {
		return _stateSetNode;
	}

	@Override
	protected boolean canHaveChildren() {
		return true;
	}
	
}
