/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.ui.internal.figures.GroupingContainerFigure;
import org.csstudio.sds.ui.editparts.AbstractContainerEditPart;
import org.eclipse.draw2d.IFigure;

/**
 * Holds a circuit, which is a container capable of holding other
 * LogicEditParts.
 */
public final class GroupingContainerEditPart extends AbstractContainerEditPart {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
	}
	
	/**
	 * {@inheritDoc}
	 */
	public IFigure getContentPane() {
		return ((GroupingContainerFigure) getFigure()).getContentsPane();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		GroupingContainerFigure groupingContainerFigure = new GroupingContainerFigure();
		return groupingContainerFigure;
	}

}
