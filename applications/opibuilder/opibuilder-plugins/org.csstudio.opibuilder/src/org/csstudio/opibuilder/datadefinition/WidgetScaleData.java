package org.csstudio.opibuilder.datadefinition;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;

/**A complex properties data that holds the options for scaling a widget.
 * @author Xihui Chen
 *
 */
public class WidgetScaleData extends AbstractComplexData {
	
	public final static String PROP_WIDTH_SCALABLE = "width_scalable"; //$NON-NLS-1$
	public final static String PROP_HEIGHT_SCALABLE = "height_scalable"; //$NON-NLS-1$
	public final static String PROP_KEEP_WH_RATIO = "keep_wh_ratio"; //$NON-NLS-1$	

	private WidgetScaleData(AbstractWidgetModel widgetModel){
		super(widgetModel);		
	}
	
	public WidgetScaleData(AbstractWidgetModel widgetModel,boolean isWidthScalable, boolean isHeightScalable, boolean keepWHRatio){
		super(widgetModel);
		setPropertyValue(PROP_WIDTH_SCALABLE, isWidthScalable);
		setPropertyValue(PROP_HEIGHT_SCALABLE, isHeightScalable);
		setPropertyValue(PROP_KEEP_WH_RATIO, keepWHRatio);
	}
	
	@Override
	protected void configureProperties() {
		addProperty(new BooleanProperty(PROP_WIDTH_SCALABLE, "Width Scalable", null, true));
		addProperty(new BooleanProperty(PROP_HEIGHT_SCALABLE, "Height Scalable", null, true));		
		addProperty(new BooleanProperty(PROP_KEEP_WH_RATIO, "Keep Width/Height Ratio", null, false));		
	}


	
	public boolean isWidthScalable(){
		return (Boolean)getPropertyValue(PROP_WIDTH_SCALABLE);
	}
	
	public boolean isHeightScalable(){
		return (Boolean)getPropertyValue(PROP_HEIGHT_SCALABLE);
	}
	
	public boolean isKeepWHRatio(){
		return (Boolean)getPropertyValue(PROP_KEEP_WH_RATIO);
	}

	
	@Override
	public AbstractComplexData createInstance() {
		return new WidgetScaleData(getWidgetModel());
	}
	
	@Override
	public String toString() {
		return ""+isWidthScalable() + " " + isHeightScalable() + //$NON-NLS-1$ //$NON-NLS-2$ 
				" " + isKeepWHRatio(); //$NON-NLS-1$
	}
}
