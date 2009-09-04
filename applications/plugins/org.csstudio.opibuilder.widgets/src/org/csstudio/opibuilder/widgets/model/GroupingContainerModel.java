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
	
	/** The ID of the <i>lockChildren</i> property. */
	public static final String PROP_LOCK_CHILDREN = "lockChildren";

	public GroupingContainerModel() {
		setSize(200, 200);
		setBorderStyle(BorderStyle.GROUP_BOX);
	}
	
	@Override
	protected void configureProperties() {
		addProperty(new BooleanProperty(PROP_TRANSPARENT, "Transparent Background",
				WidgetPropertyCategory.Display, false));
		addProperty(new BooleanProperty(PROP_LOCK_CHILDREN, "Lock Children",
				WidgetPropertyCategory.Behavior, false));

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
	
	/**
	* @return boolean
	* 				True, if the children should be locked, false otherwise
	*/
	public boolean isLocked() {
		return (Boolean) getProperty(PROP_LOCK_CHILDREN).getPropertyValue();
	}
	
}
