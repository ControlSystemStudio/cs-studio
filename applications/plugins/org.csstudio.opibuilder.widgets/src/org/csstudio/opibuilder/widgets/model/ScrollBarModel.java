/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.DoubleProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.geometry.Point;

/**
 *The model of scroll bar widget.
 * @author Xihui Chen
 */
public class ScrollBarModel extends AbstractPVWidgetModel {
	
	public final String ID = "org.csstudio.opibuilder.widgets.scrollbar";

	
	/** The ID of the minimum property. */
	public static final String PROP_MIN = "minimum"; //$NON-NLS-1$		
	
	/** The ID of the maximum property. */
	public static final String PROP_MAX = "maximum"; //$NON-NLS-1$	

	/** The amount the scrollbar will move when the page up or page down areas are
	pressed.*/
	public static final String PROP_PAGE_INCREMENT = "page_increment"; //$NON-NLS-1$		
	
	/**the amount the scrollbar will move when the up or down arrow buttons are
	pressed.*/	
	public static final String PROP_STEP_INCREMENT = "step_increment"; //$NON-NLS-1$	
	
	/**The length of the dragging bar.*/	
	public static final String PROP_BAR_LENGTH = "bar_length"; //$NON-NLS-1$	
	
	/** The ID of the horizontal property. */
	public static final String PROP_HORIZONTAL = "horizontal"; //$NON-NLS-1$
	
	public static final String PROP_LIMITS_FROM_PV = "limits_from_pv"; //$NON-NLS-1$		
	
	public static final String PROP_SHOW_VALUE_TIP = "show_value_tip"; //$NON-NLS-1$	
	
	/** The default value of the minimum property. */
	private static final double DEFAULT_MIN = 0;
	
	/** The default value of the maximum property. */
	private static final double DEFAULT_MAX = 100;	
	
	private static final double DEFAULT_PAGE_INCREMENT = 10;	
	private static final double DEFAULT_STEP_INCREMENT = 1;	
	private static final double DEFAULT_BAR_LENGTH = 10;	
	
	
	public ScrollBarModel() {
		setSize(120, 20);
		setForegroundColor(CustomMediaFactory.COLOR_BLACK);
	}
	
	
	@Override
	protected void configureProperties() {		
	
		addProperty(new DoubleProperty(PROP_MIN, "Minimum", 
				WidgetPropertyCategory.Behavior, DEFAULT_MIN));
		
		addProperty(new DoubleProperty(PROP_MAX, "Maximum", 
				WidgetPropertyCategory.Behavior, DEFAULT_MAX));			
		
		addProperty(new DoubleProperty(PROP_STEP_INCREMENT, "Step Increment", 
				WidgetPropertyCategory.Behavior, DEFAULT_STEP_INCREMENT), true);
		
		addProperty(new DoubleProperty(PROP_PAGE_INCREMENT, "Page Increment", 
				WidgetPropertyCategory.Behavior, DEFAULT_PAGE_INCREMENT), true);
		
		addProperty(new DoubleProperty(PROP_BAR_LENGTH, "Bar Length", 
				WidgetPropertyCategory.Behavior, DEFAULT_BAR_LENGTH));
		
		addProperty(new BooleanProperty(PROP_HORIZONTAL, "Horizontal", 
				WidgetPropertyCategory.Display, true));
		
		addProperty(new BooleanProperty(PROP_LIMITS_FROM_PV, "Limits From PV",
				WidgetPropertyCategory.Behavior, true));
		
		addProperty(new BooleanProperty(PROP_SHOW_VALUE_TIP, "Show Value Tip",
				WidgetPropertyCategory.Display, true));
		
	}	


	/**
	 * @return the minimum value
	 */
	public Double getMinimum() {
		return (Double) getProperty(PROP_MIN).getPropertyValue();
	}


	/**
	 * @return the maximum value
	 */
	public Double getMaximum() {
		return (Double) getProperty(PROP_MAX).getPropertyValue();
	}

	
	/**
	 * @return true if the widget is in horizontal orientation, false otherwise
	 */
	public boolean isHorizontal() {
		return (Boolean) getProperty(PROP_HORIZONTAL).getPropertyValue();
	}

	/**
	 * @return the page increment
	 */
	public Double getPageIncrement() {
		return (Double) getProperty(PROP_PAGE_INCREMENT).getPropertyValue();
	}
	
	/**
	 * @return the step increment
	 */
	public Double getStepIncrement() {
		return (Double) getProperty(PROP_STEP_INCREMENT).getPropertyValue();
	}
	
	/**
	 * @return the length of the dragging bar.
	 */
	public Double getBarLength() {
		return (Double) getProperty(PROP_BAR_LENGTH).getPropertyValue();
	}
	/**
	 * @return true if limits will be load from DB, false otherwise
	 */
	public boolean isLimitsFromPV() {
		return (Boolean) getProperty(PROP_LIMITS_FROM_PV).getPropertyValue();
	}
	
	public boolean isShowValueTip() {
		return (Boolean) getProperty(PROP_SHOW_VALUE_TIP).getPropertyValue();
	}
	
	@Override
	public String getTypeID() {
		return ID;
	}
	
	@Override
	public void rotate90(boolean clockwise) {
		setPropertyValue(PROP_HORIZONTAL, !isHorizontal());
	}
	
	@Override
	public void rotate90(boolean clockwise, Point center) {
		super.rotate90(clockwise, center);
		setPropertyValue(PROP_HORIZONTAL, !isHorizontal());
		super.rotate90(true);
	}

	
	
}
