package org.csstudio.opibuilder.model;

import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

public class DisplayModel extends AbstractContainerModel {
	
	/**
	 * The type ID of this model.
	 */
	public static final String ID = "org.csstudio.opibuilder.displaymodel"; //$NON-NLS-1$
	
	public static final String PROP_GRID_SPACE = "grid.space"; //$NON-NLS-1$
	
	public DisplayModel() {
		super();
		setLocation(0, 0);
		setSize(800, 600);
	}

	@Override
	protected void configureProperties() {
		addProperty(new IntegerProperty(PROP_GRID_SPACE, "Grid Space",
				WidgetPropertyCategory.Display, true, 6, 1, 1000));
		removeProperty(PROP_BORDER_COLOR);
		removeProperty(PROP_BORDER_STYLE);
		removeProperty(PROP_BORDER_WIDTH);
	}

	@Override
	public String getTypeID() {
		return ID;
	}
	
	


}
