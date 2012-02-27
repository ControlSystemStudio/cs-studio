/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.eclipse.draw2d.geometry.Point;


/**The widget model 
 * @author jbercic (class of same name in SDS)
 * @author Xihui Chen
 *
 */
public class ArcModel extends AbstractShapeModel {
	
	
	public final String ID = "org.csstudio.opibuilder.widgets.arc";
	/**
	 * True if the arc should be filled.
	 */
	public static final String PROP_FILL = "fill";//$NON-NLS-1$

	/**
	 * Start angle (in degree) of the arc. 
	 */
	public static final String PROP_START_ANGLE = "start_angle";//$NON-NLS-1$
	
	/**
	 * Total angle (in degree) of the arc. 
	 */
	public static final String PROP_TOTAL_ANGLE = "total_angle";//$NON-NLS-1$
	
	
	public ArcModel() {
		setLineWidth(1);
		setScaleOptions(true, true, true);
	}
	
	@Override
	protected void configureProperties() {
		super.configureProperties();
		removeProperty(PROP_FILL_LEVEL);
		removeProperty(PROP_HORIZONTAL_FILL);
		removeProperty(PROP_TRANSPARENT);
		removeProperty(PROP_LINE_COLOR);
		addProperty(new BooleanProperty(PROP_FILL, "Fill", 
				WidgetPropertyCategory.Display, false));
		addProperty(new IntegerProperty(PROP_START_ANGLE, "Start Angle", 
				WidgetPropertyCategory.Display, 0));
		addProperty(new IntegerProperty(PROP_TOTAL_ANGLE, "Total Angle", 
				WidgetPropertyCategory.Display, 90));
	
	}
	
	@Override
	public String getTypeID() {
		return ID;
	}
	
	public boolean isFill(){
		return (Boolean)getCastedPropertyValue(PROP_FILL);
	}
	
	public void setFill(boolean value){
		setPropertyValue(PROP_FILL, value);
	}
	
	public int getStartAngle(){
		return (Integer)getCastedPropertyValue(PROP_START_ANGLE);
	}
	
	public void setStartAngle(int angle){
		setPropertyValue(PROP_START_ANGLE, angle);
	}
	
	public int getTotalAngle(){
		return (Integer)getCastedPropertyValue(PROP_TOTAL_ANGLE);
	}
	
	public void setTotalAngle(int angle){
		setPropertyValue(PROP_TOTAL_ANGLE, angle);
	}
	
	@Override
	public void flipHorizontally() {
		super.flipHorizontally();
		setStartAngle(regularDegree(180 - getStartAngle() - getTotalAngle()));
	}
	
	@Override
	public void flipHorizontally(int centerX) {
		super.flipHorizontally(centerX);
		setStartAngle(regularDegree(180 - getStartAngle() - getTotalAngle()));
	}
	
	@Override
	public void flipVertically() {
		super.flipVertically();
		setStartAngle(regularDegree(360 - getStartAngle() - getTotalAngle()));
	}
	
	@Override
	public void flipVertically(int centerY) {
		super.flipVertically(centerY);
		setStartAngle(regularDegree(360 - getStartAngle() - getTotalAngle()));
	}
	
	@Override
	public void rotate90(boolean clockwise) {
		super.rotate90(clockwise);
		setStartAngle(regularDegree(getStartAngle()+ (clockwise? -90 : 90)));
	}
	
	@Override
	public void rotate90(boolean clockwise, Point center) {
		super.rotate90(clockwise, center);		
		setStartAngle(regularDegree(getStartAngle()+ (clockwise? -90 : 90)));
	}
	
	/**Make a degree in [0, 360).
	 * @param r angle in degree.
	 * @return A degree between [0, 360).
	 */
	private static int regularDegree(int r){
		if(r >=360){
			return r%360;
		}else if(r <0)
			return 360 + r%360;
		return r;
	}
	
	
}
