package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.platform.ui.util.CustomMediaFactory;

public class LabelModel extends AbstractWidgetModel {
	
	/**
	 * The ID of the text property.
	 */
	public static final String PROP_TEXT= "text"; //$NON-NLS-1$
	/** The ID of the <i>transparent</i> property. */
	public static final String PROP_TRANSPARENT = "transparency";	//$NON-NLS-1$
	
	/** The ID of the <i>Auto Size</i> property. */
	public static final String PROP_AUTOSIZE = "auto_size";	//$NON-NLS-1$

	public static final String PROP_ALIGN_H = "horizontal_alignment";	//$NON-NLS-1$
	public static final String PROP_ALIGN_V = "vertical_alignment";	//$NON-NLS-1$
	
	public static final String[] H_ALIGN = new String[]{"LEFT", "CENTER", "RIGHT"};
	public static final String[] V_ALIGN = new String[]{"TOP", "MIDDLE", "BOTTOM"};
	public LabelModel() {
		setBackgroundColor(CustomMediaFactory.COLOR_WHITE);
		setForegroundColor(CustomMediaFactory.COLOR_BLACK);
		setSize(100, 20);
	}

	@Override
	protected void configureProperties() {
		addProperty(new StringProperty(PROP_TEXT, "Text", 
				WidgetPropertyCategory.Display, "Input Text", true));
		addProperty(new BooleanProperty(PROP_TRANSPARENT, "Transparent Background",
				WidgetPropertyCategory.Display, false));
		addProperty(new BooleanProperty(PROP_AUTOSIZE, "Auto Size", 
				WidgetPropertyCategory.Display, false));
		addProperty(new ComboProperty(PROP_ALIGN_H, "Horizontal Alignment", 
				WidgetPropertyCategory.Display, H_ALIGN, 0));
		addProperty(new ComboProperty(PROP_ALIGN_V, "Vertical Alignment", 
				WidgetPropertyCategory.Display, V_ALIGN, 0));
	}

	public int getHorizontalAlignment(){
		return (Integer)getCastedPropertyValue(PROP_ALIGN_H);
	}
	
	
	public int getVerticalAlignment(){
		return (Integer)getCastedPropertyValue(PROP_ALIGN_V);
	}
	
	@Override
	public String getTypeID() {
		return "org.csstudio.opibuilder.widgets.Label"; //$NON-NLS-1$
	}
	
	public String getText(){
		return (String)getCastedPropertyValue(PROP_TEXT);
	}
	
	public void setText(String text){
		setPropertyValue(PROP_TEXT, text);
	}
	
	public boolean isTransparent(){
		return (Boolean)getCastedPropertyValue(PROP_TRANSPARENT);
	}
	
	public boolean isAutoSize(){
		return (Boolean)getCastedPropertyValue(PROP_AUTOSIZE);
	}
	

}
