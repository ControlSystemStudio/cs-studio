package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

/**The model for grouping container widget.
 * @author Xihui Chen
 *
 */
public class GroupingContainerModel extends AbstractContainerModel {

	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.opibuilder.widgets.groupingContainer"; //$NON-NLS-1$	
	
	/** The ID of the <i>transparent</i> property. */
	public static final String PROP_TRANSPARENT = "transparent";	
	
	/** The ID of the <i>lockChildren</i> property. */
	public static final String PROP_LOCK_CHILDREN = "lock_children";
	public static final String PROP_SHOW_SCROLLBAR = "show_scrollbar";

	
	public GroupingContainerModel() {
		setSize(200, 200);
		//setBorderStyle(BorderStyle.GROUP_BOX);
	}
	
	@Override
	protected void configureProperties() {
		addProperty(new BooleanProperty(PROP_TRANSPARENT, "Transparent Background",
				WidgetPropertyCategory.Display, false));
		addProperty(new BooleanProperty(PROP_LOCK_CHILDREN, "Lock Children",
				WidgetPropertyCategory.Behavior, false));
		addProperty(new BooleanProperty(PROP_SHOW_SCROLLBAR, "Show Scrollbar",
				WidgetPropertyCategory.Behavior, true));
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
	
	/**
	* @return boolean
	* 				True, if scrollbar should be shown when necessary, false otherwise.
	*/
	public boolean isShowScrollbar() {
		return (Boolean) getProperty(PROP_SHOW_SCROLLBAR).getPropertyValue();
	}
	
}
