/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;


/**The model for a rounded rectangle widget.
 * 
 * @author Xihui Chen
 *
 */
public class RoundedRectangleModel extends AbstractShapeModel {
	
	
	public final String ID = "org.csstudio.opibuilder.widgets.RoundedRectangle";

	public static final String PROP_CORNER_WIDTH = "corner_width"; //$NON-NLS-1$

	public static final String PROP_CORNER_HEIGHT = "corner_height"; //$NON-NLS-1$

	private static final int DEFAULT_CORNER_WIDTH = 16;
	
	private static final int DEFAULT_CORNER_HEIGHT = 16;


	@Override
	public String getTypeID() {
		return ID;
	}
	
	
	@Override
	protected void configureProperties() {
		super.configureProperties();
		addProperty(new IntegerProperty(PROP_CORNER_WIDTH, "Corner Width", 
				WidgetPropertyCategory.Display, DEFAULT_CORNER_WIDTH));
		addProperty(new IntegerProperty(PROP_CORNER_HEIGHT, "Corner Height", 
				WidgetPropertyCategory.Display, DEFAULT_CORNER_HEIGHT));
	}
	
	/**
	 * @return the corner width
	 */
	public final int getCornerWidth(){
		return (Integer)getPropertyValue(PROP_CORNER_WIDTH);
	}
	

	/**
	 * @return the corner height
	 */
	public final int getCornerHeight(){
		return (Integer)getPropertyValue(PROP_CORNER_HEIGHT);
	}
	


}
