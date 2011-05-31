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
import org.csstudio.opibuilder.properties.DoubleProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.swt.widgets.figures.TextInputFigure.FileReturnPart;
import org.csstudio.swt.widgets.figures.TextInputFigure.FileSource;
import org.csstudio.swt.widgets.figures.TextInputFigure.SelectorType;


/**The model for text input.
 * @author Xihui Chen
 *
 */
public class TextInputModel extends TextIndicatorModel {
	
	/** The minimum input value allowed.*/
	public static final String PROP_MIN = "minimum"; //$NON-NLS-1$		
	
	/** The maximum input value allowed. */
	public static final String PROP_MAX = "maximum"; //$NON-NLS-1$	
	
	/** Load limit from PV. */
	public static final String PROP_LIMITS_FROM_PV = "limits_from_pv"; //$NON-NLS-1$		
	
	/** Load limit from PV. */
	public static final String PROP_DATETIME_FORMAT = "datetime_format"; //$NON-NLS-1$		
	
	/** Load limit from PV. */
	public static final String PROP_SELECTOR_TYPE = "selector_type"; //$NON-NLS-1$		
	
	/** Load limit from PV. */
	public static final String PROP_FILE_SOURCE = "file_source"; //$NON-NLS-1$		
	
	/** Load limit from PV. */
	public static final String PROP_FILE_RETURN_PART = "file_return_part"; //$NON-NLS-1$	
	
	/** Allow multi-line input. */
	public static final String PROP_MULTILINE_INPUT = "multiline_input"; //$NON-NLS-1$
	
	/** The default value of the minimum property. */
	private static final double DEFAULT_MIN = Double.NEGATIVE_INFINITY;
	
	/** The default value of the maximum property. */
	private static final double DEFAULT_MAX = Double.POSITIVE_INFINITY;	
	
	public TextInputModel() {
		setPropertyValue(PROP_LIMITS_FROM_PV, false);
	}
	
	@Override
	public String getTypeID() {
		return "org.csstudio.opibuilder.widgets.TextInput"; //$NON-NLS-1$;
	}
	
	
	@Override
	protected void configureProperties() {
		super.configureProperties();	
		addProperty(new DoubleProperty(PROP_MIN, "Minimum", 
				WidgetPropertyCategory.Behavior, DEFAULT_MIN));
		
		addProperty(new DoubleProperty(PROP_MAX, "Maximum", 
				WidgetPropertyCategory.Behavior, DEFAULT_MAX));	
		
		addProperty(new BooleanProperty(PROP_LIMITS_FROM_PV, "Limits From PV",
				WidgetPropertyCategory.Behavior, true));
		
		addProperty(new BooleanProperty(PROP_MULTILINE_INPUT, "Multi-line Input",
				WidgetPropertyCategory.Display, false));		
		
		addProperty(new StringProperty(PROP_DATETIME_FORMAT, "Datetime Format", 
				WidgetPropertyCategory.Display, "yyyy-MM-dd HH:mm:ss")); //$NON-NLS-1$
		addProperty(new ComboProperty(PROP_SELECTOR_TYPE, "Selector Type", 
				WidgetPropertyCategory.Display, SelectorType.stringValues(), SelectorType.NONE.ordinal()));
		addProperty(new ComboProperty(PROP_FILE_SOURCE, "File Source", 
				WidgetPropertyCategory.Display, FileSource.stringValues(), FileSource.WORKSPACE.ordinal()));
		addProperty(new ComboProperty(PROP_FILE_RETURN_PART, "File Return Part", 
				WidgetPropertyCategory.Display, FileReturnPart.stringValues(), FileReturnPart.FULL_PATH.ordinal()));
		
		setPropertyVisible(PROP_DATETIME_FORMAT, false);
		setPropertyVisible(PROP_FILE_RETURN_PART, false);
		setPropertyVisible(PROP_FILE_SOURCE, false);
		setPropertyVisible(PROP_WRAP_WORDS, false);
		
		setText(""); //$NON-NLS-1$
		setBorderStyle(BorderStyle.LOWERED);
		setPropertyValue(PROP_BORDER_ALARMSENSITIVE, false);
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
	 * @return true if limits will be load from DB, false otherwise
	 */
	public boolean isLimitsFromPV() {
		return (Boolean) getProperty(PROP_LIMITS_FROM_PV).getPropertyValue();
	}
	
	public String getDateTimeFormat(){
		return (String)getPropertyValue(PROP_DATETIME_FORMAT);		
	}
	
	public SelectorType getSelectorType(){
		return SelectorType.values()[(Integer)getPropertyValue(PROP_SELECTOR_TYPE)];
	}
	
	public FileSource getFileSource(){
		return FileSource.values()[(Integer)getPropertyValue(PROP_FILE_SOURCE)];
	}
	
	public FileReturnPart getFileReturnPart(){
		return FileReturnPart.values()
			[(Integer)getPropertyValue(PROP_FILE_RETURN_PART)];
	}
	
	public boolean isMultilineInput(){
		return (Boolean)getPropertyValue(PROP_MULTILINE_INPUT);
	}
	
}
