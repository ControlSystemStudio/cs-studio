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
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.OPIColor;
import org.eclipse.swt.graphics.RGB;

/**
 * The model for checkbox widget 
 * @author Xihui Chen
 */
public class CheckBoxModel extends AbstractPVWidgetModel implements ITextModel{
	
	
	
	/** Bit of the PV to be read and writtend.*/
	public static final String PROP_BIT = "bit"; //$NON-NLS-1$		
	
	/** Text of the label. */
	public static final String PROP_LABEL = "label"; //$NON-NLS-1$
	
	/** True if the widget size can be automatically adjusted along with the text size. */
	public static final String PROP_AUTOSIZE = "auto_size";	//$NON-NLS-1$
	
	/**
	 * The color of the selected item.
	 */
	public static final String PROP_SELECTED_COLOR = "selected_color";//$NON-NLS-1$
	
	/**
	 * Unique identifier.
	 */
	public static final String ID = "org.csstudio.opibuilder.widgets.checkbox"; //$NON-NLS-1$
	
	public CheckBoxModel() {
		setSize(100, 20);
		setForegroundColor(new RGB(0,0,0));
		setScaleOptions(true, false, false);
	}
	
	@Override
	protected void configureProperties() {				
		addProperty(new IntegerProperty(PROP_BIT, "Bit",
				WidgetPropertyCategory.Behavior, 0, -1, 63));		
		addProperty(new StringProperty(PROP_LABEL, "Label",
				WidgetPropertyCategory.Display, ""));	//$NON-NLS-1$	
		addProperty(new BooleanProperty(PROP_AUTOSIZE, "Auto Size", 
				WidgetPropertyCategory.Display, false));
		addProperty(new ColorProperty(PROP_SELECTED_COLOR, "Selected Color", 
				WidgetPropertyCategory.Display, new RGB(77, 77, 77)));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTypeID() {
		return ID;
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
	public String getLabel() {
		return (String) getProperty(PROP_LABEL).getPropertyValue();
	}
	
	@Override
	public String getText() {
		return getLabel();
	}
	
	@Override
	public void setText(String text) {
		setPropertyValue(PROP_LABEL, text);
	}
	
	public boolean isAutoSize(){
		return (Boolean)getCastedPropertyValue(PROP_AUTOSIZE);
	}
	
	public OPIColor getSelectedColor(){
		return (OPIColor)getPropertyValue(PROP_SELECTED_COLOR);
	}
}
