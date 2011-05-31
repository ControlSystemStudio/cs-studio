/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.model.AbstractShapeModel;
import org.csstudio.opibuilder.widgets.model.AbstractShapeModel.LineStyle;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Shape;

/**
 * Abstract EditPart controller for the shape widgets.
 * 
 * @author Xihui Chen
 * 
 */
public abstract class AbstractShapeEditPart extends AbstractPVWidgetEditPart {
	
	
	@Override
	public AbstractShapeModel getWidgetModel() {
		return (AbstractShapeModel)getModel();
	}
	
	@Override
	protected IFigure createFigure() {
		Shape shape = (Shape) super.createFigure();
		AbstractShapeModel model = getWidgetModel();
		shape.setOutline(model.getLineWidth() != 0);	
		shape.setLineWidth(model.getLineWidth());
		shape.setLineStyle(model.getLineStyle());
		return shape;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		// line width
		IWidgetPropertyChangeHandler lineWidthHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				Shape shape = (Shape) refreshableFigure;
				if(((Integer)newValue).equals(0))
					shape.setOutline(false);
				else{
					shape.setOutline(true);
					shape.setLineWidth((Integer) newValue);
				}
				
				return true;
			}
		};
		setPropertyChangeHandler(AbstractShapeModel.PROP_LINE_WIDTH,
				lineWidthHandler);
		
		// line style
		IWidgetPropertyChangeHandler lineStyleHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				Shape shape = (Shape) refreshableFigure;
				shape.setLineStyle(LineStyle.values()[(Integer) newValue].getStyle());
				return true;
			}
		};
		setPropertyChangeHandler(AbstractShapeModel.PROP_LINE_STYLE,
				lineStyleHandler);
		
		
		
	}
	
	
}
