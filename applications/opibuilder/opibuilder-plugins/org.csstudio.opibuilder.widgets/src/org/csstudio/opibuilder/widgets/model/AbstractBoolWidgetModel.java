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
import org.csstudio.opibuilder.properties.ColorProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.swt.widgets.figures.AbstractBoolFigure.BoolLabelPosition;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * This class defines a common widget model for all the boolean widgets. 
 * @author Xihui Chen
 */
public abstract class AbstractBoolWidgetModel extends AbstractPVWidgetModel {
	
	
	
	/** Bit of the PV to be read and controlled. Set to -1 will write value 0/1 to the PV. */
	public static final String PROP_BIT = "bit"; //$NON-NLS-1$		
	
	/** Label text when boolean widget is on. */
	public static final String PROP_ON_LABEL = "on_label"; //$NON-NLS-1$
	
	/** Label text when boolean widget is off. */
	public static final String PROP_OFF_LABEL = "off_label"; //$NON-NLS-1$
	
	/** Widget color when boolean widget is on. */
	public static final String PROP_ON_COLOR = "on_color"; //$NON-NLS-1$
	
	/** Widget color when boolean widget is off. */
	public static final String PROP_OFF_COLOR = "off_color"; //$NON-NLS-1$
	
	/** True if the boolean label should be visible. */
	public static final String PROP_SHOW_BOOL_LABEL = "show_boolean_label"; //$NON-NLS-1$
	
	public static final String PROP_BOOL_LABEL_POS = "boolean_label_position"; //$NON-NLS-1$
	
	
	/**Data type to be operated by this widget, could be Bit or Enum. If it is Bit, the widget
	 * will write 0/1 to the corresponding bit of the PV. If it is ENUM, it will write On State/Off State 
	 * property value to the PV.*/
	public static final String PROP_DATA_TYPE = "data_type"; //$NON-NLS-1$
	
	/**If data type is Enum, it is the string value which will be written 
	 * to the PV when widget is on. */
	public static final String PROP_ON_STATE = "on_state"; //$NON-NLS-1$
	
	/**If data type is Enum, it is the string value which will be written 
	 * to the PV when widget is off. */
	public static final String PROP_OFF_STATE = "off_state"; //$NON-NLS-1$
	
	/** The default color of the on color property. */
	private static final RGB DEFAULT_ON_COLOR = new RGB(0,255,0);
	/** The default color of the off color property. */
	private static final RGB DEFAULT_OFF_COLOR = new RGB(0, 100 ,0);
	
	/** The default string of the on label property. */
	private static final String DEFAULT_ON_LABEL = "ON";
	/** The default string of the off label property. */
	private static final String DEFAULT_OFF_LABEL = "OFF";
	
	

	@Override
	protected void configureProperties() {				
		addProperty(new IntegerProperty(PROP_BIT, "Bit",
				WidgetPropertyCategory.Behavior, -1, -1, 63));		
		addProperty(new BooleanProperty(PROP_SHOW_BOOL_LABEL, "Show Boolean Label",
				WidgetPropertyCategory.Display,false));		
		addProperty(new StringProperty(PROP_ON_LABEL, "On Label",
				WidgetPropertyCategory.Display, DEFAULT_ON_LABEL));	
		addProperty(new StringProperty(PROP_OFF_LABEL, "Off Label",
				WidgetPropertyCategory.Display, DEFAULT_OFF_LABEL));	
		addProperty(new ColorProperty(PROP_ON_COLOR, "On Color",
				WidgetPropertyCategory.Display, DEFAULT_ON_COLOR));
		addProperty(new ColorProperty(PROP_OFF_COLOR, "Off Color",
				WidgetPropertyCategory.Display, DEFAULT_OFF_COLOR));		
		addProperty(new ComboProperty(PROP_DATA_TYPE, "Data Type",
				WidgetPropertyCategory.Behavior, new String[]{"Bit", "Enum"}, 0));
		addProperty(new StringProperty(PROP_ON_STATE, "On State", 
				WidgetPropertyCategory.Behavior, ""));
		addProperty(new StringProperty(PROP_OFF_STATE, "Off State",
				WidgetPropertyCategory.Behavior, ""));
		addProperty(new ComboProperty(PROP_BOOL_LABEL_POS, "Boolean Label Position", 
				WidgetPropertyCategory.Display, BoolLabelPosition.stringValues(), 0));
		
	}
	

	/**
	 * @return the bit. If bit is -1, the value channel must be enum, otherwise, 
	 * it must be numeric value 
	 */
	public Integer getBit() {
		return (Integer) getProperty(PROP_BIT).getPropertyValue();
	}


	/**
	 * @return the on label
	 */
	public String getOnLabel() {
		return (String) getProperty(PROP_ON_LABEL).getPropertyValue();
	}

	/**
	 * @return the off label
	 */
	public String getOffLabel() {
		return (String) getProperty(PROP_OFF_LABEL).getPropertyValue();
	}
	
	/**
	 * @return the on color
	 */
	public Color getOnColor() {
		return getSWTColorFromColorProperty(PROP_ON_COLOR);
	}	
	/**
	 * @return the off color
	 */
	public Color getOffColor() {
		return getSWTColorFromColorProperty(PROP_OFF_COLOR);
	}	
	
	/**
	 * @return true if the boolean label should be shown, false otherwise
	 */
	public boolean isShowBoolLabel() {
		return (Boolean) getProperty(PROP_SHOW_BOOL_LABEL).getPropertyValue();
	}
	
	public int getDataType(){
		return (Integer)getPropertyValue(PROP_DATA_TYPE);
	}
	
	public String getOnState(){
		return (String)getPropertyValue(PROP_ON_STATE);
	}
	
	public String getOffState(){
		return (String)getPropertyValue(PROP_OFF_STATE);
	}
	
	public BoolLabelPosition getBoolLabelPosition(){
		return BoolLabelPosition.values()
				[(Integer)getPropertyValue(PROP_BOOL_LABEL_POS)];
	}
	
	
}
