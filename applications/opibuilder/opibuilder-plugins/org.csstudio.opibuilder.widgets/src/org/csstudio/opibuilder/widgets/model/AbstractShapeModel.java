/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;


import org.csstudio.opibuilder.datadefinition.LineStyle;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ColorProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.DoubleProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * The abstract widget model for all shape based widgets.
 * @author Xihui Chen
 */
public abstract class AbstractShapeModel extends AbstractPVWidgetModel {
	/**
	 * Width of the line.
	 */
	public static final String PROP_LINE_WIDTH = "line_width";//$NON-NLS-1$
	
	/**
	 * Style of the line.
	 */
	public static final String PROP_LINE_STYLE = "line_style";//$NON-NLS-1$
	
	/**
	 * Color of the line.
	 */
	public static final String PROP_LINE_COLOR = "line_color";//$NON-NLS-1$
	
	/**
	 * The widget can be filled with foreground color if this is not zero. 
	 * It must be a value between 0 to 100.
	 */
	public static final String PROP_FILL_LEVEL = "fill_level"; //$NON-NLS-1$
	
	/**
	 * True if fill direction is horizontal.
	 */
	public static final String PROP_HORIZONTAL_FILL = "horizontal_fill"; //$NON-NLS-1$
	/**
	 * True if anti alias is enabled for the figure.
	 */
	public static final String PROP_ANTIALIAS = "anti_alias"; //$NON-NLS-1$
	
	/**
	 * Alpha value.
	 */
	public static final String PROP_ALPHA = "alpha"; //$NON-NLS-1$
	
	/** True if background is transparent. */
	public static final String PROP_TRANSPARENT = "transparent";	

	private static final RGB DEFAULT_LINE_COLOR = CustomMediaFactory.COLOR_PURPLE;
	
	
	public AbstractShapeModel() {
		setBackgroundColor(new RGB(30, 144, 255));
		setForegroundColor(CustomMediaFactory.COLOR_RED);
		setPropertyValue(PROP_BORDER_ALARMSENSITIVE, false);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureProperties() {
		addProperty(new IntegerProperty(PROP_LINE_WIDTH, "Line Width",
				WidgetPropertyCategory.Display, 0, 0, 100));
		addProperty(new IntegerProperty(PROP_ALPHA, "Alpha",
				WidgetPropertyCategory.Display, 255, 0, 255));
		addProperty(new ComboProperty(PROP_LINE_STYLE, "Line Style",
				WidgetPropertyCategory.Display, LineStyle.stringValues(), 0));
		addProperty(new ColorProperty(PROP_LINE_COLOR, "Line Color",
				WidgetPropertyCategory.Display, DEFAULT_LINE_COLOR));
		addProperty(new DoubleProperty(PROP_FILL_LEVEL, "Fill Level",
				WidgetPropertyCategory.Display, 0.0, 0.0, 100.0));
		addProperty(new BooleanProperty(PROP_HORIZONTAL_FILL, "Horizontal Fill", 
				WidgetPropertyCategory.Display, true));
		addProperty(new BooleanProperty(PROP_ANTIALIAS, "Anti Alias", 
				WidgetPropertyCategory.Display, true));
		addProperty(new BooleanProperty(PROP_TRANSPARENT, "Transparent",
				WidgetPropertyCategory.Display, false));

	}
	

	
	/**
	 * @return true if the graphics's antiAlias is on.
	 */
	public final boolean isAntiAlias(){
		return (Boolean)getCastedPropertyValue(PROP_ANTIALIAS);
	}
	
	public int getAlpha(){
		return (Integer)getPropertyValue(PROP_ALPHA); 
	}
	
	/**
	 * Returns the fill grade.
	 * 
	 * @return the fill grade
	 */
	public final double getFillLevel() {
		return (Double) getProperty(PROP_FILL_LEVEL).getPropertyValue();
	}
	
	/**set the fill level
	 * @param value 
	 */
	public final void setFillLevel(final double value){
		setPropertyValue(PROP_FILL_LEVEL, value);
	}
	
	public boolean isHorizontalFill(){
		return (Boolean)getCastedPropertyValue(PROP_HORIZONTAL_FILL);
	}
	
	public void setHoizontalFill(boolean value){
		setPropertyValue(PROP_HORIZONTAL_FILL, value);
	}
	
	/**
	 * Gets the width of the line.
	 * @return int
	 * 				The width of the line
	 */
	public int getLineWidth() {
		return (Integer) getProperty(PROP_LINE_WIDTH).getPropertyValue();
	}
	
	public void setLineWidth(int width){
		setPropertyValue(PROP_LINE_WIDTH, width);
	}
	
	/**
	 * @param style the integer value corresponding to {@link LineStyle}
	 */
	public void setLineStyle(int style){
		setPropertyValue(PROP_LINE_STYLE, style);
	}
	
	
	/**
	 * Gets the style of the line.
	 * @return int
	 * 				The style of the line
	 */
	public int getLineStyle() {
		return LineStyle.values()[(Integer) getProperty(PROP_LINE_STYLE).
		                          getPropertyValue()].getStyle();
	}
	
	public Color getLineColor(){
		return ((OPIColor)getPropertyValue(PROP_LINE_COLOR)).getSWTColor();
	}
	
	/**
	 * Returns, if this widget should have a transparent background.
	 * @return boolean
	 * 				True, if it should have a transparent background, false otherwise
	 */
	public boolean isTransparent() {
		return (Boolean) getProperty(PROP_TRANSPARENT).getPropertyValue();
	}
	
	
	public void setTransparent(boolean value){
		setPropertyValue(PROP_TRANSPARENT, value);
	}

}
