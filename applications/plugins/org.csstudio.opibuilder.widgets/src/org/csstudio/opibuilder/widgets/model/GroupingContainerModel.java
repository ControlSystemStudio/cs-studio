package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.visualparts.BorderStyle;

public class GroupingContainerModel extends AbstractContainerModel {

	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.opibuilder.widgets.groupingContainer"; //$NON-NLS-1$	
	
	/** The ID of the <i>transparent</i> property. */
	public static final String PROP_TRANSPARENT = "transparency";	

	
	public GroupingContainerModel() {
		setBorderStyle(BorderStyle.GROUP_BOX);
	}
	
	@Override
	protected void configureProperties() {
		addProperty(new BooleanProperty(PROP_TRANSPARENT, "Transparent Background",
				WidgetPropertyCategory.Display, false));
	}

	@Override
	public String getTypeID() {
		return ID;
	}

	
	/**
	 * Returns, if this widget should have a transparent background.
	 * @return boolean
	 * 				True, if it should have a transparent background, false otherwise
	 */
	public boolean isTransparent() {
		return (Boolean) getProperty(PROP_TRANSPARENT).getPropertyValue();
	}
}
