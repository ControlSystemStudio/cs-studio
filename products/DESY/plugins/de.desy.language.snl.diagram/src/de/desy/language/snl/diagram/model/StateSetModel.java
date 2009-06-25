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

import java.util.ArrayList;
import java.util.List;

/**
 * An elliptical shape.
 * 
 * @author Elias Volanakis
 */
public class StateSetModel extends SNLModel {

	/** Property ID to use when a child is added to this diagram. */
	public static final String CHILD_ADDED_PROP = "ShapesDiagram.ChildAdded";
	/** Property ID to use when a child is removed from this diagram. */
	public static final String CHILD_REMOVED_PROP = "ShapesDiagram.ChildRemoved";
	private static final long serialVersionUID = 1;
	private List<SNLModel> shapes = new ArrayList<SNLModel>();

	public String getIconName() {
		return "rectangle16.gif";
	}

	public String toString() {
		return "StateSet " + hashCode();
	}

	@Override
	public String getIdentifier() {
		return "StateSet";
	}
	
	/**
	 * Add a shape to this diagram.
	 * 
	 * @param s
	 *            a non-null shape instance
	 * @return true, if the shape was added, false otherwise
	 */
	public boolean addChild(SNLModel s) {
		if (s != null && shapes.add(s)) {
			firePropertyChange(CHILD_ADDED_PROP, null, s);
			return true;
		}
		return false;
	}

	/**
	 * Return a List of Shapes in this diagram. The returned List should not be
	 * modified.
	 */
	public List<SNLModel> getChildren() {
		return shapes;
	}

	/**
	 * Remove a shape from this diagram.
	 * 
	 * @param s
	 *            a non-null shape instance;
	 * @return true, if the shape was removed, false otherwise
	 */
	public boolean removeChild(SNLModel s) {
		if (s != null && shapes.remove(s)) {
			firePropertyChange(CHILD_REMOVED_PROP, null, s);
			return true;
		}
		return false;
	}
}
