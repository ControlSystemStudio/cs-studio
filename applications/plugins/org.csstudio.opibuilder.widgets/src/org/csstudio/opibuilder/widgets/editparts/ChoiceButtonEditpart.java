/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.widgets.model.ChoiceButtonModel;
import org.csstudio.swt.widgets.figures.AbstractChoiceFigure;
import org.csstudio.swt.widgets.figures.ChoiceButtonFigure;

/**The editpart of choice button widget.
 * @author Xihui Chen
 *
 */
public class ChoiceButtonEditpart extends AbstractChoiceEditPart {

	@Override
	protected AbstractChoiceFigure createChoiceFigure() {
		ChoiceButtonFigure figure = new ChoiceButtonFigure(
				getExecutionMode() == ExecutionMode.RUN_MODE);
		return figure;
	}
	
	@Override
	public ChoiceButtonModel getWidgetModel() {
		return (ChoiceButtonModel)getModel();
	}
	

}
