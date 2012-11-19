package org.csstudio.opibuilder.widgets.symbol.multistate;

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.eclipse.draw2d.IFigure;

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
