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

/**
 * An elliptical shape.
 * 
 * @author Elias Volanakis
 */
public class StateSetModel extends SNLModel {

	private static final long serialVersionUID = 1;

	public String getIconName() {
		return "rectangle16.gif";
	}

	public String toString() {
		return "StateSet " + hashCode();
	}
}
