package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.FontProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.swt.graphics.FontData;

public class LabelModel extends AbstractPVWidgetModel {
	
	/**
	 * The ID of the text property.
	 */
	public static final String PROP_TEXT= "text"; //$NON-NLS-1$
	/** The ID of the <i>transparent</i> property. */
	public static final String PROP_TRANSPARENT = "transparent";	//$NON-NLS-1$
	
	/** The ID of the <i>Auto Size</i> property. */
	public static final String PROP_AUTOSIZE = "auto_size";	//$NON-NLS-1$

	public static final String PROP_ALIGN_H = "horizontal_alignment";	//$NON-NLS-1$
	public static final String PROP_ALIGN_V = "vertical_alignment";	//$NON-NLS-1$
	
	public static final String PROP_SHOW_SCROLLBAR = "show_scrollbar";	//$NON-NLS-1$
	
	public static final String PROP_FONT= "font"; //$NON-NLS-1$

	
	public static final String[] H_ALIGN = new String[]{"LEFT", "CENTER", "RIGHT"};
	public static final String[] V_ALIGN = new String[]{"TOP", "MIDDLE", "BOTTOM"};
	
	protected boolean pvModel = false;
	
	public LabelModel() {
		setBackgroundColor(CustomMediaFactory.COLOR_WHITE);
		setForegroundColor(CustomMediaFactory.COLOR_BLACK);
		setSize(150, 20);
	}

	@Override
	protected void configureProperties() {
		addProperty(new FontProperty(PROP_FONT, "Font", 
				WidgetPropertyCategory.Display, CustomMediaFactory.FONT_ARIAL));
		addProperty(new StringProperty(PROP_TEXT, "Text", 
				WidgetPropertyCategory.Display, "double click to enter text", true));
		addProperty(new BooleanProperty(PROP_TRANSPARENT, "Transparent",
				WidgetPropertyCategory.Display, false));
		addProperty(new BooleanProperty(PROP_AUTOSIZE, "Auto Size", 
				WidgetPropertyCategory.Display, false));
		addProperty(new ComboProperty(PROP_ALIGN_H, "Horizontal Alignment", 
				WidgetPropertyCategory.Display, H_ALIGN, 0));
		addProperty(new ComboProperty(PROP_ALIGN_V, "Vertical Alignment", 
				WidgetPropertyCategory.Display, V_ALIGN, 0));
		addProperty(new BooleanProperty(PROP_SHOW_SCROLLBAR, "Show Scrollbar", 
				WidgetPropertyCategory.Display, true));
		if(!pvModel){
			removeProperty(PROP_PVNAME);
			removeProperty(PROP_PVVALUE);
			removeProperty(PROP_BACKCOLOR_ALARMSENSITIVE);
			removeProperty(PROP_BORDER_ALARMSENSITIVE);
			removeProperty(PROP_FORECOLOR_ALARMSENSITIVE);
		}		
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
	
	public boolean isShowScrollbar(){
		return (Boolean)getCastedPropertyValue(PROP_SHOW_SCROLLBAR);
	}
	
	
	public OPIFont getFont(){
		return (OPIFont)getCastedPropertyValue(PROP_FONT);
	}
	
	public void setFont(OPIFont font){
		setPropertyValue(PROP_FONT, font);
	}

}
