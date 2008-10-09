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
package de.desy.language.snl.diagram.ui.commands;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.commands.Command;

import de.desy.language.snl.diagram.model.SNLModel;
import de.desy.language.snl.diagram.model.SNLDiagram;


/**
 * A command to add a Shape to a ShapeDiagram.
 * The command can be undone or redone.
 * @author Elias Volanakis
 */
public class ShapeCreateCommand 
	extends Command 
{
	
/** The new shape. */ 
private SNLModel newShape;
/** ShapeDiagram to add to. */
private final SNLDiagram parent;
/** The bounds of the new Shape. */
private Rectangle bounds;

/**
 * Create a command that will add a new Shape to a ShapesDiagram.
 * @param newShape the new Shape that is to be added
 * @param parent the ShapesDiagram that will hold the new element
 * @param bounds the bounds of the new shape; the size can be (-1, -1) if not known
 * @throws IllegalArgumentException if any parameter is null, or the request
 * 						  does not provide a new Shape instance
 */
public ShapeCreateCommand(SNLModel newShape, SNLDiagram parent, Rectangle bounds) {
	this.newShape = newShape;
	this.parent = parent;
	this.bounds = bounds;
	setLabel("shape creation");
}

/**
 * Can execute if all the necessary information has been provided. 
 * @see org.eclipse.gef.commands.Command#canExecute()
 */
public boolean canExecute() {
	return newShape != null && parent != null && bounds != null;
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#execute()
 */
public void execute() {
	newShape.setLocation(bounds.getLocation());
	Dimension size = bounds.getSize();
	if (size.width > 0 && size.height > 0)
		newShape.setSize(size);
	redo();
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#redo()
 */
public void redo() {
	parent.addChild(newShape);
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#undo()
 */
public void undo() {
	parent.removeChild(newShape);
}
	
}