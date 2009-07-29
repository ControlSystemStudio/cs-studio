package org.csstudio.opibuilder.model;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

/**
 * The root model for an OPI Display.
 * @author Xihui Chen
 *
 */
public class DisplayModel extends AbstractContainerModel {
	
	/**
	 * The type ID of this model.
	 */
	public static final String ID = "org.csstudio.opibuilder.Display"; //$NON-NLS-1$
	
	public static final String PROP_GRID_SPACE = "grid.space"; //$NON-NLS-1$
	public static final String PROP_SHOW_GRID = "grid.show"; //$NON-NLS-1$
	public static final String PROP_SHOW_RULER = "showRuler"; //$NON-NLS-1$
	public static final String PROP_SNAP_GEOMETRY = "snapGeometry"; //$NON-NLS-1$
	public static final String PROP_SHOW_EDIT_RANGE = "showEditRangeBorder"; //$NON-NLS-1$
	
	public DisplayModel() {
		super();
		setLocation(0, 0);
		setSize(800, 600);
	}

	@Override
	protected void configureProperties() {
		addProperty(new IntegerProperty(PROP_GRID_SPACE, "Grid Space",
				WidgetPropertyCategory.Display, true, 6, 1, 1000));
		addProperty(new BooleanProperty(PROP_SHOW_GRID, "Show Grid",
				WidgetPropertyCategory.Display, true, true));
		addProperty(new BooleanProperty(PROP_SHOW_RULER, "Show Ruler",
				WidgetPropertyCategory.Display, true, true));
		addProperty(new BooleanProperty(PROP_SNAP_GEOMETRY, "Snap to Geometry",
				WidgetPropertyCategory.Display, true, true));
		addProperty(new BooleanProperty(PROP_SHOW_EDIT_RANGE, "Show Edit Range",
				WidgetPropertyCategory.Display, true, true));
		
		removeProperty(PROP_BORDER_COLOR);
		removeProperty(PROP_BORDER_STYLE);
		removeProperty(PROP_BORDER_WIDTH);
		removeProperty(PROP_VISIBLE);
		removeProperty(PROP_ENABLED);
		setPropertyDescription(PROP_COLOR_FOREGROUND, "Grid Color");
		
	}

	public boolean isShowGrid(){
		return (Boolean)getCastedPropertyValue(PROP_SHOW_GRID);
	}
	
	public boolean isShowRuler(){
		return (Boolean)getCastedPropertyValue(PROP_SHOW_RULER);
	}
	
	public boolean isSnapToGeometry(){
		return (Boolean)getCastedPropertyValue(PROP_SNAP_GEOMETRY);
	}
	
	public boolean isShowEditRange(){
		return (Boolean)getCastedPropertyValue(PROP_SHOW_EDIT_RANGE);
	}
	
	@Override
	public String getTypeID() {
		return ID;
	}
	
	


}
