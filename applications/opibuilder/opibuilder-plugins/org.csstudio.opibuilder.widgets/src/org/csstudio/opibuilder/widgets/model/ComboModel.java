/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.StringListProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.eclipse.swt.graphics.RGB;

/**The model for combo widget.
 * @author Xihui Chen
 *
 */
public class ComboModel extends AbstractPVWidgetModel {

	
	public final String ID = "org.csstudio.opibuilder.widgets.combo";//$NON-NLS-1$
	/**
	 * Items of the combo.
	 */
	public static final String PROP_ITEMS = "items";//$NON-NLS-1$

	/**
	 * True if items are read from the input PV which must be an Enum PV.
	 */
	public static final String PROP_ITEMS_FROM_PV = "items_from_pv";//$NON-NLS-1$
	
	public ComboModel() {
		setBackgroundColor(new RGB(255,255,255));
		setForegroundColor(new RGB(0,0,0));
		setScaleOptions(true, false, false);
	}

	@Override
	protected void configureProperties() {		
		addProperty(new StringListProperty(
				PROP_ITEMS, "Items", WidgetPropertyCategory.Behavior, new ArrayList<String>()));

		addProperty(new BooleanProperty(
				PROP_ITEMS_FROM_PV, "Items From PV", WidgetPropertyCategory.Behavior, false));
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getItems(){
		return (List<String>)getPropertyValue(PROP_ITEMS);
	}
	
	public boolean isItemsFromPV(){
		return (Boolean)getPropertyValue(PROP_ITEMS_FROM_PV);
	}
	
	
	@Override
	public String getTypeID() {
		return ID;
	}

}
