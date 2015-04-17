/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;

import java.util.Arrays;
import java.util.List;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ColorProperty;
import org.csstudio.opibuilder.properties.StringListProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.swt.graphics.RGB;

/**The abstract model for choice widget.
 * @author Xihui Chen
 *
 */
public abstract class AbstractChoiceModel extends AbstractPVWidgetModel {

	
	/**
	 * Items of the choice widget.
	 */
	public static final String PROP_ITEMS = "items";//$NON-NLS-1$

	/**
	 * If this is true, items will be loaded from input Enum PV.
	 */
	public static final String PROP_ITEMS_FROM_PV = "items_from_pv";//$NON-NLS-1$
	
	/**
	 * The color of the selected item.
	 */
	public static final String PROP_SELECTED_COLOR = "selected_color";//$NON-NLS-1$

	public static final RGB DEFAULT_SELECTED_COLOR = CustomMediaFactory.COLOR_WHITE;
	
	/** True if items should be horizontal arranged.*/
	public static final String PROP_HORIZONTAL = "horizontal"; //$NON-NLS-1$
	
	public static final String[] DEFAULT_ITEMS = new String[]{"Choice 1", "Choice 2", "Choice 3"};
	
	public AbstractChoiceModel() {
		setBackgroundColor(new RGB(255,255,255));
		setForegroundColor(new RGB(0,0,0));
	}

	@Override
	protected void configureProperties() {		
		addProperty(new StringListProperty(
				PROP_ITEMS, "Items", WidgetPropertyCategory.Behavior, Arrays.asList(DEFAULT_ITEMS)));
		addProperty(new BooleanProperty(
				PROP_ITEMS_FROM_PV, "Items From PV", WidgetPropertyCategory.Behavior, true));
		addProperty(new ColorProperty(PROP_SELECTED_COLOR, "Selected Color", 
				WidgetPropertyCategory.Display, DEFAULT_SELECTED_COLOR));
		addProperty(new BooleanProperty(PROP_HORIZONTAL, "Horizontal", 
				WidgetPropertyCategory.Display, false));	
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getItems(){
		return (List<String>)getPropertyValue(PROP_ITEMS);
	}
	
	public boolean isItemsFromPV(){
		return (Boolean)getPropertyValue(PROP_ITEMS_FROM_PV);
	}
	
		

	public OPIColor getSelectedColor(){
		return (OPIColor)getPropertyValue(PROP_SELECTED_COLOR);
	}

	/**
	 * @return true if the widget is in horizontal orientation, false otherwise
	 */
	public boolean isHorizontal() {
		return (Boolean) getProperty(PROP_HORIZONTAL).getPropertyValue();
	}
	

}
