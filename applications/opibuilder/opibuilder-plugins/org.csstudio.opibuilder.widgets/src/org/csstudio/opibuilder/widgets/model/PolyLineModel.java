/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.swt.widgets.figures.PolylineFigure;
import org.csstudio.swt.widgets.figures.PolylineFigure.ArrowType;
import org.eclipse.draw2d.geometry.Rectangle;



/**The model for polyline widget.
 * @author Sven Wende, Alexander Will (class of same name in SDS)
 * @author Xihui Chen
 *
 */
public class PolyLineModel extends AbstractPolyModel {

	
	
	
	
	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.opibuilder.widgets.polyline"; //$NON-NLS-1$	
	
	
	public static final String PROP_ARROW = "arrows";//$NON-NLS-1$
	
	public static final String PROP_FILL_ARROW = "fill_arrow"; //$NON-NLS-1$
	
	public static final String PROP_ARROW_LENGTH = "arrow_length"; //$NON-NLS-1$
	
	public PolyLineModel() {
		setLineWidth(1);
		
	}
	
	
	@Override
	protected void configureProperties() {
		super.configureProperties();
		removeProperty(PROP_LINE_COLOR);
		addProperty(new ComboProperty(PROP_ARROW, "Arrows", 
				WidgetPropertyCategory.Display, ArrowType.stringValues(), 0));
		addProperty(new BooleanProperty(PROP_FILL_ARROW, "Fill Arrow", 
				WidgetPropertyCategory.Display, true));
		addProperty(new IntegerProperty(PROP_ARROW_LENGTH, "Arrow Length", 
				WidgetPropertyCategory.Display, 20, 1, 1000));
		
	}
	
	@Override
	public String getTypeID() {
		return ID;
	}
	
	public int getArrowType(){
		return (Integer)getCastedPropertyValue(PROP_ARROW); 
	}
	
	public void setArrowType(int type){
		setPropertyValue(PROP_ARROW, type);
	}
	
	
	public int getArrowLength(){
		return (Integer)getCastedPropertyValue(PROP_ARROW_LENGTH);
	}
	
	public void setArrowLength(int value){
		setPropertyValue(PROP_ARROW_LENGTH, value);
	}
	
	
	
	/**
	 * Sets the specified _points for the polygon.
	 * 
	 * @param points
	 *            the polygon points
	 * @param rememberPoints true if the zero relative points should be remembered, false otherwise.
	 */
//	@Override
//	public void setPoints(final PointList points,
//			final boolean rememberPoints) {
//		if (points.size() > 0) {
//			PointList copy = points.getCopy();
//			if (rememberPoints) {
//				rememberZeroDegreePoints(copy);
//			}
//			getProperty(PROP_POINTS).setPropertyValue(points);
//			
//			updateBounds();
//		}
//	}


	/**
	 * Update the figure bounds based on points and arrows.
	 */
	public void updateBounds() {
		Rectangle bounds = PolylineFigure.getPointsBoundsWithArrows(getPoints(),
				ArrowType.values()[getArrowType()], getArrowLength(), PolylineFigure.ARROW_ANGLE);
		getProperty(PROP_XPOS).setPropertyValue(bounds.x);
		getProperty(PROP_YPOS).setPropertyValue(bounds.y);
		getProperty(PROP_WIDTH).setPropertyValue(bounds.width);
		getProperty(PROP_HEIGHT).setPropertyValue(bounds.height);
	}
	
	


	public boolean isFillArrow() {
		return (Boolean)getCastedPropertyValue(PROP_FILL_ARROW);
	}
	
	public void setFillArrow(boolean fill){
		setPropertyValue(PROP_FILL_ARROW, fill);
	}

}
