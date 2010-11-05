package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.FontProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.swt.widgets.figures.TextFigure.H_ALIGN;
import org.csstudio.swt.widgets.figures.TextFigure.V_ALIGN;

/**The model for label widget.
 * @author Xihui Chen
 *
 */
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
	
	
	public static final String PROP_FONT= "font"; //$NON-NLS-1$

	
	protected boolean pvModel = false;
	
	public LabelModel() {
		setBackgroundColor(CustomMediaFactory.COLOR_WHITE);
		setForegroundColor(CustomMediaFactory.COLOR_BLACK);
		setSize(150, 20);	
	}

	@Override
	protected void configureProperties() {
		
		addProperty(new FontProperty(PROP_FONT, "Font", 
				WidgetPropertyCategory.Display, "Default")); //$NON-NLS-1$
		addProperty(new StringProperty(PROP_TEXT, "Text", 
				WidgetPropertyCategory.Display, "double click to enter text", true));
		addProperty(new BooleanProperty(PROP_TRANSPARENT, "Transparent",
				WidgetPropertyCategory.Display, false));
		addProperty(new BooleanProperty(PROP_AUTOSIZE, "Auto Size", 
				WidgetPropertyCategory.Display, false));
		addProperty(new ComboProperty(PROP_ALIGN_H, "Horizontal Alignment", 
				WidgetPropertyCategory.Display, H_ALIGN.stringValues(), 1));
		addProperty(new ComboProperty(PROP_ALIGN_V, "Vertical Alignment", 
				WidgetPropertyCategory.Display, V_ALIGN.stringValues(), 1));

		if(!pvModel){
			setTooltip("");
			removeProperty(PROP_PVNAME);
			removeProperty(PROP_PVVALUE);
			removeProperty(PROP_BACKCOLOR_ALARMSENSITIVE);
			removeProperty(PROP_BORDER_ALARMSENSITIVE);
			removeProperty(PROP_FORECOLOR_ALARMSENSITIVE);
		}		
	}

	public H_ALIGN getHorizontalAlignment(){
		return H_ALIGN.values()[
		              (Integer)getCastedPropertyValue(PROP_ALIGN_H)];
	}
	
	
	public V_ALIGN getVerticalAlignment(){
		return V_ALIGN.values()[
				      (Integer)getCastedPropertyValue(PROP_ALIGN_V)];	}
	
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
	
	public void setText(String text, boolean fire){
		getProperty(PROP_TEXT).setPropertyValue(text, fire);
	}
	
	public boolean isTransparent(){
		return (Boolean)getCastedPropertyValue(PROP_TRANSPARENT);
	}
	
	public boolean isAutoSize(){
		return (Boolean)getCastedPropertyValue(PROP_AUTOSIZE);
	}
	
	
	public OPIFont getFont(){
		return (OPIFont)getCastedPropertyValue(PROP_FONT);
	}
	
	public void setFont(OPIFont font){
		setPropertyValue(PROP_FONT, font);
	}

}
