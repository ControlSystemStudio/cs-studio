/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.opibuilder.widgets.feedback;

import org.csstudio.opibuilder.feedback.DefaultGraphicalFeedbackFactory;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.ArrayModel;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.gef.requests.ChangeBoundsRequest;

/**
 * The feedback factory for array widget.
 * @author Xihui
 *
 */
public class ArrayFeedbackFactory extends DefaultGraphicalFeedbackFactory {

	@Override
	public void showChangeBoundsFeedback(AbstractWidgetModel widgetModel,
			PrecisionRectangle bounds, IFigure feedbackFigure,
			ChangeBoundsRequest request) {
		ArrayModel arrayModel = (ArrayModel)widgetModel;	
		
		if(arrayModel.getChildren().isEmpty()){
			super.showChangeBoundsFeedback(widgetModel, bounds, feedbackFigure, request);
			return;
		}
				
		Dimension sizeDelta = request.getSizeDelta();		
		if(arrayModel.isHorizontal()){
			int eWidth = arrayModel.getChildren().get(0).getWidth();
			bounds.width -=sizeDelta.width;
			sizeDelta.width = Math.round((float)sizeDelta.width/eWidth)*eWidth;
			bounds.width +=sizeDelta.width;
		}else{
			int eHeight = arrayModel.getChildren().get(0).getHeight();
			bounds.height -=sizeDelta.height;
			sizeDelta.height = Math.round((float)sizeDelta.height/eHeight)*eHeight;
			bounds.height +=sizeDelta.height;
		}
		
		super.showChangeBoundsFeedback(widgetModel, bounds, feedbackFigure, request);
		
	}
	
	
	
	
}
