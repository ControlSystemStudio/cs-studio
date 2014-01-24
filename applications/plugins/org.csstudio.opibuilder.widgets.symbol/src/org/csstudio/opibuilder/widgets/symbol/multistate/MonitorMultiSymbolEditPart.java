/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.opibuilder.widgets.symbol.multistate;

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.eclipse.draw2d.IFigure;

/**
 * @author Fred Arnaud (Sopra Group)
 */
public class MonitorMultiSymbolEditPart extends CommonMultiSymbolEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		// create & initialize the view properly (edit or runtime mode)
		final MonitorMultiSymbolFigure figure = new MonitorMultiSymbolFigure(
				getExecutionMode() == ExecutionMode.RUN_MODE);
		super.initializeCommonFigureProperties(figure);
		return figure;
	}
	
	protected void registerPropertyChangeHandlers() {
		super.registerCommonPropertyChangeHandlers();
	}

}
