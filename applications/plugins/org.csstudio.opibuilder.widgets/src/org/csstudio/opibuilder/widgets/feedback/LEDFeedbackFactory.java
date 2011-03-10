/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.feedback;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.LEDModel;

/**Feedback Factory for LED.
 * @author Xihui Chen
 *
 */
public class LEDFeedbackFactory extends AbstractFixRatioSizeFeedbackFactory {


	@Override
	public int getMinimumWidth() {
		return LEDModel.MINIMUM_SIZE;
	}
	
	@Override
	public boolean isSquareSizeRequired(AbstractWidgetModel widgetModel) {
		return !((LEDModel)widgetModel).isSquareLED();
	}
	
}
