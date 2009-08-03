package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.platform.ui.util.CustomMediaFactory;

public class TextIndicatorModel extends AbstractPVWidgetModel {

	
	
	/** The ID of the <i>transparent</i> property. */
	public static final String PROP_TRANSPARENT = "transparency";	//$NON-NLS-1$
	
	/** The ID of the <i>Auto Size</i> property. */
	public static final String PROP_AUTOSIZE = "auto_size";	//$NON-NLS-1$
	
	/**
	 * The ID of the text property.
	 */
	public static final String PROP_TEXT= "text"; //$NON-NLS-1$
	
	
	public TextIndicatorModel() {
		setSize(100, 20);
		setForegroundColor(CustomMediaFactory.COLOR_BLACK);
	}
	
	
	@Override
	public String getTypeID() {
		return "org.csstudio.opibuilder.widgets.TextIndicator"; //$NON-NLS-1$;
	}
	
	
	@Override
	protected void configureProperties() {
		addProperty(new BooleanProperty(PROP_TRANSPARENT, "Transparent Background",
				WidgetPropertyCategory.Display, true, false));
		addProperty(new BooleanProperty(PROP_AUTOSIZE, "Auto Size", 
				WidgetPropertyCategory.Display, true, false));
		addProperty(new StringProperty(PROP_TEXT, "Text", 
				WidgetPropertyCategory.Display, false, "######"));
		setPropertyVisible(PROP_BACKCOLOR_ALARMSENSITIVE, true);
		setPropertyVisible(PROP_BORDER_ALARMSENSITIVE, true);
		setPropertyVisible(PROP_FORECOLOR_ALARMSENSITIVE, true);		
	}


	public boolean isTransparent(){
		return (Boolean)getCastedPropertyValue(PROP_TRANSPARENT);
	}
	
	public boolean isAutoSize(){
		return (Boolean)getCastedPropertyValue(PROP_AUTOSIZE);
	}
	
	public String getText(){
		return (String)getCastedPropertyValue(PROP_TEXT);
	}
	
	public void setText(String text){
		setPropertyValue(PROP_TEXT, text);
	}
}
