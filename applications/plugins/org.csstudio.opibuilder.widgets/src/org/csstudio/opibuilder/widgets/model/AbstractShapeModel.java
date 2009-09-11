package org.csstudio.opibuilder.widgets.model;


import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.DoubleProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.swt.SWT;

/**
 * The abstract widget model for all shape based widgets.
 * @author Xihui Chen
 */
public abstract class AbstractShapeModel extends AbstractPVWidgetModel {
	
	/**The line sytle:
	 * 0: Solid,
	 * 1: Dash,
	 * 2: Dot,
	 * 3: DashDot,
	 * 4: DashDotDot.
	 * @author Xihui Chen
	 *
	 */
	public enum LineStyle {
		SOLID("Solid", SWT.LINE_SOLID),
		DASH("Dash", SWT.LINE_DASH),
		DOT("Dot", SWT.LINE_DOT),
		DASH_DOT("DashDot", SWT.LINE_DASHDOT),
		Dash_DOTDOT("DashDotDot", SWT.LINE_DASHDOTDOT);
				
		String description;
		int style;
		LineStyle(String description, int style){
			this.description = description;
			this.style = style;
		}
		
		/**
		 * @return SWT line style {SWT.LINE_SOLID,
			SWT.LINE_DASH, SWT.LINE_DOT, SWT.LINE_DASHDOT, SWT.LINE_DASHDOTDOT }
		 */
		public int getStyle() {
			return style;
		}
		
		@Override
		public String toString() {
			return description;
		}
		
		public static String[] stringValues(){
			String[] sv = new String[values().length];
			int i=0;
			for(LineStyle p : values())
				sv[i++] = p.toString();
			return sv;
		}
	}

	
	
	
	/**
	 * The ID of the width of the line.
	 */
	public static final String PROP_LINE_WIDTH = "linewidth";//$NON-NLS-1$
	
	/**
	 * The ID of the width of the line.
	 */
	public static final String PROP_LINE_STYLE = "linestyle";//$NON-NLS-1$
	
	/**
	 * The ID of the fill level property.
	 */
	public static final String PROP_FILL_LEVEL = "filllevel"; //$NON-NLS-1$
	
	/**
	 * The ID of the fill orientation property.
	 */
	public static final String PROP_HORIZONTAL_FILL = "horizontalfill"; //$NON-NLS-1$
	/**
	 * The ID of the antialias property.
	 */
	public static final String PROP_ANTIALIAS = "antialias"; //$NON-NLS-1$
	
	/** The ID of the <i>transparent</i> property. */
	public static final String PROP_TRANSPARENT = "transparency";	

	

	
	public AbstractShapeModel() {
		setBackgroundColor(CustomMediaFactory.COLOR_DARK_GRAY);
		setForegroundColor(CustomMediaFactory.COLOR_BLUE);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureProperties() {
		addProperty(new IntegerProperty(PROP_LINE_WIDTH, "Line Width",
				WidgetPropertyCategory.Display, 0, 0, 100));
		addProperty(new ComboProperty(PROP_LINE_STYLE, "Line Style",
				WidgetPropertyCategory.Display, LineStyle.stringValues(), 0));
		addProperty(new DoubleProperty(PROP_FILL_LEVEL, "Fill Level",
				WidgetPropertyCategory.Display, 100, 0.0, 100.0));
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
