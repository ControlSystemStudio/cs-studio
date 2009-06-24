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
package de.desy.language.snl.diagram.ui.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import de.desy.language.snl.diagram.model.SNLModel;
import de.desy.language.snl.diagram.model.SNLDiagram;


/**
 * Factory that maps model elements to TreeEditParts.
 * TreeEditParts are used in the outline view of the ShapesEditor.
 * @author Elias Volanakis
 */
public class ShapesTreeEditPartFactory implements EditPartFactory {

/* (non-Javadoc)
 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
 */
public EditPart createEditPart(final EditPart context, final Object model) {
	if (model instanceof SNLModel) {
		return new ShapeTreeEditPart((SNLModel) model);
	}
	if (model instanceof SNLDiagram) {
		return new DiagramTreeEditPart((SNLDiagram) model);
	}
	return null; // will not show an entry for the corresponding model instance
}

}
