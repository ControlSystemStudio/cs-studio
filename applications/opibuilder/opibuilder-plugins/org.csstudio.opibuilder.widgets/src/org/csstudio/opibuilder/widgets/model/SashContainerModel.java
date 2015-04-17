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
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.DoubleProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.swt.widgets.figures.SashContainerFigure;
import org.csstudio.swt.widgets.figures.SashContainerFigure.SashStyle;

/**
 * The model for sash container widget.
 * 
 * @author Xihui Chen
 * 
 */
public class SashContainerModel extends AbstractContainerModel {

	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.opibuilder.widgets.sashContainer"; //$NON-NLS-1$	

	/** True if the background color is transparent. */
	public static final String PROP_TRANSPARENT = "transparent"; //$NON-NLS-1$	

	public static final String PROP_SASH_POSITION = "sash_position"; //$NON-NLS-1$

	public static final String PROP_SASH_STYLE = "sash_style"; //$NON-NLS-1$

	public static final String PROP_SASH_WIDTH = "sash_width"; //$NON-NLS-1$

	public static final String PROP_HORIZONTAL = "horizontal"; //$NON-NLS-1$

//	/** True if children widgets are not selectable.*/
//	public static final String PROP_LOCK_CHILDREN = "lock_children";
//	
	public static final String PROP_PANEL1_AUTO_SCALE_CHILDREN = "panel1_auto_scale_children"; //$NON-NLS-1$

	public static final String PROP_PANEL2_AUTO_SCALE_CHILDREN = "panel2_auto_scale_children"; //$NON-NLS-1$

	
	
	public SashContainerModel() {
		setSize(400, 300);
	}

	@Override
	protected void configureProperties() {
		addProperty(new BooleanProperty(PROP_TRANSPARENT, "Transparent",
				WidgetPropertyCategory.Display, false));
		addProperty(new BooleanProperty(PROP_HORIZONTAL, "Horizontal",
				WidgetPropertyCategory.Display, true));
		addProperty(new ComboProperty(PROP_SASH_STYLE, "Sash Style",WidgetPropertyCategory.Display,
				SashContainerFigure.SashStyle.stringValues(), 2));
		addProperty(new DoubleProperty(PROP_SASH_POSITION, "Sash Position", WidgetPropertyCategory.Display, 0.5));
		addProperty(new IntegerProperty(PROP_SASH_WIDTH, "Sash Width", WidgetPropertyCategory.Display, 3, 1, 100));
//		addProperty(new BooleanProperty(PROP_LOCK_CHILDREN, "Lock Children",
//				WidgetPropertyCategory.Behavior, false));

		addProperty(new BooleanProperty(PROP_PANEL1_AUTO_SCALE_CHILDREN,
				"Auto Scale Children (at Runtime)", new WidgetPropertyCategory() {
					@Override
					public String toString() {
						return "Panel 1 (Left/Up)";
					}
				}, false));
		addProperty(new BooleanProperty(PROP_PANEL2_AUTO_SCALE_CHILDREN,
				"Auto Scale Children (at Runtime)", new WidgetPropertyCategory() {
					@Override
					public String toString() {
						return "Panel 2 (Right/Down)";
					}
				}, false));
	}

	@Override
	public String getTypeID() {
		return ID;
	}

	/**
	 * Returns, if this widget should have a transparent background.
	 * 
	 * @return boolean True, if it should have a transparent background, false
	 *         otherwise
	 */
	public boolean isTransparent() {
		return (Boolean) getProperty(PROP_TRANSPARENT).getPropertyValue();
	}

	/**
	 * @return boolean True, if sash is horizontal, false otherwise
	 */
	public boolean isHorizontal() {
		return (Boolean) getProperty(PROP_HORIZONTAL).getPropertyValue();		
	}
	
//	/**
//	* @return boolean
//	* 				True, if the children should be locked, false otherwise
//	*/
//	public boolean isLocked() {
//		return (Boolean) getProperty(PROP_LOCK_CHILDREN).getPropertyValue();
//	}

	/**
	 * @return boolean True, if panel 1 will auto scale children when sash moved, false
	 *         otherwise.
	 */
	public boolean isPanel1AutoScaleChildren() {
		return (Boolean) getProperty(PROP_PANEL1_AUTO_SCALE_CHILDREN).getPropertyValue();
	}

	/**
	 * @return boolean True, if panel 2 will auto scale children when sash moved, false
	 *         otherwise.
	 */
	public boolean isPanel2AutoScaleChildren() {
		return (Boolean) getProperty(PROP_PANEL2_AUTO_SCALE_CHILDREN).getPropertyValue();
	}
	
	public SashStyle getSashStyle(){
		return SashStyle.values()[(Integer)getPropertyValue(PROP_SASH_STYLE)];
	}
	
	public int getSashWidth(){
		return (Integer) getPropertyValue(PROP_SASH_WIDTH);
	}
	
	public double getSashPosition(){
		return (Double)getPropertyValue(PROP_SASH_POSITION);				
	}
	
	@Override
	public void flipVertically() {
		int centerY = getHeight() / 2;
		for (AbstractWidgetModel abstractWidgetModel : getChildren()) {
			abstractWidgetModel.flipVertically(centerY);
		}
	}

	@Override
	public void flipHorizontally() {
		int centerX = getWidth() / 2;
		for (AbstractWidgetModel abstractWidgetModel : getChildren()) {
			abstractWidgetModel.flipHorizontally(centerX);
		}
	}

	@Override
	public void rotate90(boolean clockwise) {
		setPropertyValue(PROP_HORIZONTAL, !(Boolean)getPropertyValue(PROP_HORIZONTAL));
	}
	
	@Override
	public void scaleChildren() {
		//Its children will be scaled when this widget layouts, so it needs to do nothing here
	}

}
