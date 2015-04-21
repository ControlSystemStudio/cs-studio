/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.model.AbstractLayoutModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

/**The widget model for grid layout widget which layout widgets in grids.
 * @author Xihui Chen
 *
 */
public class GridLayoutModel extends AbstractLayoutModel {
	

	/** Number of columns*/
	public static final String PROP_NUMBER_OF_COLUMNS = "number_of_columns"; //$NON-NLS-1$	
	
	/** Fill extra spaces in a grid*/
	public static final String PROP_FILL_GRIDS = "fill_grids"; //$NON-NLS-1$	
	
	
	/** The gap between grids*/
	public static final String PROP_GRID_GAP = "grid_gap"; //$NON-NLS-1$	
	
	
	
	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.opibuilder.widgets.gridLayout"; //$NON-NLS-1$	
	

	@Override
	protected void configureProperties() {
		addProperty(new IntegerProperty(PROP_NUMBER_OF_COLUMNS, "Number of Columns", 
				WidgetPropertyCategory.Display, 3, 1, 1000));
		addProperty(new BooleanProperty(PROP_FILL_GRIDS, "Fill Grids", 
				WidgetPropertyCategory.Display, false));
		addProperty(new IntegerProperty(PROP_GRID_GAP, "Grid Gap", 
				WidgetPropertyCategory.Display, 2, 0, 100));
		
		removeProperty(PROP_FONT);
	
	}

	@Override
	public String getTypeID() {
		return ID;
	}
	
	public int getNumberOfColumns(){
		return (Integer)getPropertyValue(PROP_NUMBER_OF_COLUMNS);
	}
	
	public boolean isFillGrids(){
		return (Boolean)getPropertyValue(PROP_FILL_GRIDS);
	}

	public int getGridGap(){
		return (Integer)getPropertyValue(PROP_GRID_GAP);
	}
	
}
