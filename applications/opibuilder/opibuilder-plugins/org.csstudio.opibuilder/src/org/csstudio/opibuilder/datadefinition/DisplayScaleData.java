package org.csstudio.opibuilder.datadefinition;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;

/**A complex properties data that holds display scale options.
 * @author Xihui Chen
 *
 */
public class DisplayScaleData extends AbstractComplexData {
	
	public DisplayScaleData(AbstractWidgetModel widgetModel) {
		super(widgetModel);
	}

	/**
	 * Automatically scale all widgets when display resizes. The scale behavior of 
	 * each widget is determined by its scale options property. 
	 */
	public final static String PROP_AUTO_SCALE_WIDGETS = "auto_scale_widgets"; //$NON-NLS-1$
	/**
	 * The minimum height of the display to be allowed after scaling.
	 * The display will stop scaling height if height of the display
	 * after scaled will be less than this value. It will use display's original height as 
	 * minimum height if this is set to -1.
	 */
	public final static String PROP_MIN_HEIGHT = "min_height"; //$NON-NLS-1$
	/**
	 * The minimum width of the display to be allowed after scaling.
	 * The display will stop scaling width if height of the display
	 * after scaled will be less than this value. It will use display's original width as 
	 * minimum width if this is set to -1.
	 */
	public final static String PROP_MIN_WIDTH = "min_width"; //$NON-NLS-1$
	

	@Override
	protected void configureProperties() {
		addProperty(new BooleanProperty(PROP_AUTO_SCALE_WIDGETS, "Auto Scale Widgets", null, false));
		addProperty(new IntegerProperty(PROP_MIN_WIDTH, "Minimum Width", null, -1));	
		addProperty(new IntegerProperty(PROP_MIN_HEIGHT, "Minimum Height", null, -1));		
	}


	
	public boolean isAutoScaleWidgets(){
		return (Boolean)getPropertyValue(PROP_AUTO_SCALE_WIDGETS);
	}
	
	public int getMinimumHeight(){
		return (Integer)getPropertyValue(PROP_MIN_HEIGHT);
	}
	
	public int getMinimumWidth(){
		return (Integer)getPropertyValue(PROP_MIN_WIDTH);
	}

	
	@Override
	public AbstractComplexData createInstance() {
		return new DisplayScaleData(getWidgetModel());
	}
	
	@Override
	public String toString() {
		return ""+isAutoScaleWidgets() + " " + getMinimumWidth() + //$NON-NLS-1$ //$NON-NLS-2$ 
				" " +getMinimumHeight(); //$NON-NLS-1$
	}
}
