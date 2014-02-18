/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.opibuilder.widgets.symbol.bool;

import org.eclipse.draw2d.IFigure;

/**
 * Edit part controller Monitor Boolean Symbol Image widget based on
 * {@link MonitorBoolSymbolModel}.
 * 
 * @author SOPRA Group
 * 
 */
public class MonitorBoolSymbolEditpart extends CommonBoolSymbolEditpart {

	@Override
	protected IFigure doCreateFigure() {
		MonitorBoolSymbolFigure figure = new MonitorBoolSymbolFigure();
		initializeCommonFigureProperties(figure, getWidgetModel());
		return (IFigure) figure;
	}

	/**
	 * Get the monitor widget model.
	 * 
	 * @return the monitor widget model.
	 */
	@Override
	public MonitorBoolSymbolModel getWidgetModel() {
		return (MonitorBoolSymbolModel) super.getWidgetModel();
	}

	@Override
	public void deactivate() {
		super.deactivate();
		((MonitorBoolSymbolFigure) getFigure()).dispose();
	}

}
