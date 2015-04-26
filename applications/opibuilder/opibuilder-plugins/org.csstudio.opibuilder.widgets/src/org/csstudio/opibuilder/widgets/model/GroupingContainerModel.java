/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.eclipse.draw2d.geometry.Point;

/**The model for grouping container widget.
 * @author Xihui Chen
 *
 */
public class GroupingContainerModel extends AbstractContainerModel {

	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.opibuilder.widgets.groupingContainer"; //$NON-NLS-1$	
	
	/**True if the background color is transparent. */
	public static final String PROP_TRANSPARENT = "transparent";	//$NON-NLS-1$
	
	/** True if children widgets are not selectable.*/
	public static final String PROP_LOCK_CHILDREN = "lock_children";//$NON-NLS-1$
	/**
	 * True if scrollbar is visible when children widgets are out of range. 
	 */
	public static final String PROP_SHOW_SCROLLBAR = "show_scrollbar";//$NON-NLS-1$
	

	/**Forward background and foreground color properties change to children. */
	public static final String PROP_FORWARD_COLORS = "fc";	//$NON-NLS-1$
	
	
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
		addProperty(new BooleanProperty(PROP_FORWARD_COLORS, "Forward Colors",
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
	
	/**
	* @return boolean
	* 				True, if scrollbar should be shown when necessary, false otherwise.
	*/
	public boolean isShowScrollbar() {
		return (Boolean) getProperty(PROP_SHOW_SCROLLBAR).getPropertyValue();
	}
	
	public boolean isForwardColors(){
		return (Boolean)getPropertyValue(PROP_FORWARD_COLORS);
	}
	


	@Override
	public void flipVertically() {
		int centerY = getHeight()/2;
		for(AbstractWidgetModel abstractWidgetModel : getChildren()){
			abstractWidgetModel.flipVertically(centerY);
		}
	}
	
	@Override
	public void flipHorizontally() {
		int centerX = getWidth()/2;
		for(AbstractWidgetModel abstractWidgetModel : getChildren()){
			abstractWidgetModel.flipHorizontally(centerX);
		}
	}
	
	@Override
	public void rotate90(boolean clockwise) {
		boolean oldLock = isLocked();
		setPropertyValue(PROP_LOCK_CHILDREN, false);
		Point center = new Point(getWidth()/2, getHeight()/2);
		for(AbstractWidgetModel abstractWidgetModel : getChildren()){
			abstractWidgetModel.rotate90(clockwise, center);
		}
		Point oldLoc = getLocation();
		super.rotate90(clockwise);
		Point newLoc = getLocation();
		
		int dx = newLoc.x - oldLoc.x;
		int dy = newLoc.y - oldLoc.y;
		//move back
		for(AbstractWidgetModel abstractWidgetModel : getChildren()){
			abstractWidgetModel.setLocation(
					abstractWidgetModel.getLocation().translate(-dx, -dy));
		}
		setPropertyValue(PROP_LOCK_CHILDREN, oldLock);
	}
	

	
}
