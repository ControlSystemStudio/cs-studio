/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.feedback;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.MeterModel;
import org.csstudio.swt.widgets.figures.MeterFigure;

/**Feedback Factory for LED.
 * @author Xihui Chen
 *
 */
public class MeterFeedbackFactory extends AbstractFixRatioSizeFeedbackFactory {

	@Override
	public int getMinimumWidth() {
		return MeterModel.MINIMUM_WIDTH;
	}
	
	@Override
	public int getHeightFromWidth(int width, AbstractWidgetModel widgetModel) {
		return (int) (MeterFigure.HW_RATIO * (width));
	}

	@Override
	public int getWidthFromHeight(int height, AbstractWidgetModel widgetModel) {
		return (int) (height/MeterFigure.HW_RATIO );
	}
	
}
