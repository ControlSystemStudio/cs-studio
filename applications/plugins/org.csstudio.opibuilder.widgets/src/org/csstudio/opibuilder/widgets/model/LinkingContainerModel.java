/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;


import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.model.AbstractLinkingContainerModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.opibuilder.widgets.editparts.LinkingContainerEditpart;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.GraphicalViewer;

/**The model for linking container widget.
 * @author Xihui Chen
 *
 */
public class LinkingContainerModel extends AbstractLinkingContainerModel {

	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.opibuilder.widgets.linkingContainer"; //$NON-NLS-1$	

	/**
	 * The ID of the auto zoom property.
	 */
	public static final String PROP_ZOOMTOFITALL = "zoom_to_fit"; //$NON-NLS-1$
	
	public static final String PROP_AUTO_SIZE = "auto_size"; //$NON-NLS-1$
	
	
	/**
	 * The default value of the height property.
	 */
	private static final int DEFAULT_HEIGHT = 200;

	/**
	 * The default value of the width property.
	 */
	private static final int DEFAULT_WIDTH = 200;

	/**
	 * The geographical size of the children.
	 */
	private Dimension childrenGeoSize = null;
	
	public LinkingContainerModel() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setBorderStyle(BorderStyle.LOWERED);
	}
	
	@Override
	protected void configureProperties() {
		
		addProperty(new BooleanProperty(PROP_ZOOMTOFITALL, "Zoom to Fit", WidgetPropertyCategory.Display, true));
		addProperty(new BooleanProperty(PROP_AUTO_SIZE, "Auto Size", WidgetPropertyCategory.Display, false));
	}

	@Override
	public String getTypeID() {
		return ID;
	}

	/**
	 * Returns the auto zoom state.
	 * @return the auto zoom state
	 */
	public boolean isAutoFit() {
		return (Boolean) getProperty(PROP_ZOOMTOFITALL).getPropertyValue();
	}
	
	public boolean isAutoSize() {
		return (Boolean) getProperty(PROP_AUTO_SIZE).getPropertyValue();
	}
	
	@Override
	public boolean isChildrenOperationAllowable() {
		return false;
	}
	
	@Override
	public void scale(double widthRatio, double heightRatio) {
		super.scale(widthRatio, heightRatio);
		if(!isAutoFit())
			scaleChildren();
		
	}

	/**
	 * Scale its children. 
	 */
	public void scaleChildren() {
		if(isAutoFit())
			return;
		//The linking container model doesn't hold its children actually, so it 
		// has to ask editpart to get its children.
		GraphicalViewer viewer = getRootDisplayModel().getViewer();
		if(viewer == null)
			return;
		LinkingContainerEditpart editpart = 
				(LinkingContainerEditpart) viewer.
				getEditPartRegistry().
				get(this);
		Dimension size = getSize();
		double newWidthRatio = size.width/(double)getOriginSize().width;
		double newHeightRatio = size.height/(double)getOriginSize().height;
		boolean allowScale = true;
		if(getDisplayModel() != null){
			allowScale = getDisplayModel().getDisplayScaleData().isAutoScaleWidgets();
			if(allowScale){
				int minWidth = getDisplayModel().getDisplayScaleData()
						.getMinimumWidth();

				if (minWidth < 0) {
					minWidth = getDisplayModel().getWidth();
				}
				int minHeight = getDisplayModel().getDisplayScaleData()
						.getMinimumHeight();
				if (minHeight < 0) {
					minHeight = getDisplayModel().getHeight();
				}
				if (getWidth() * newWidthRatio < minWidth)
					newWidthRatio = minWidth / (double) getOriginSize().width;
				if (getHeight() * newHeightRatio < minHeight)
					newHeightRatio = minHeight
							/ (double) getOriginSize().height;
			}
			
		}
		if(allowScale)
			for(Object child : editpart.getChildren())
				((AbstractBaseEditPart)child).getWidgetModel().scale(newWidthRatio, newHeightRatio);
	}
	
	@Override
	public Dimension getOriginSize() {
		if(childrenGeoSize == null)			
			return super.getOriginSize();
		else
			return childrenGeoSize;
	}
	
	public void setChildrenGeoSize(Dimension childrenGeoSize) {
		this.childrenGeoSize = childrenGeoSize;
	}
}
