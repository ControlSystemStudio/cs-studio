package org.csstudio.opibuilder.widgets.model;

import java.util.Arrays;
import java.util.List;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ColorProperty;
import org.csstudio.opibuilder.properties.FontProperty;
import org.csstudio.opibuilder.properties.StringListProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.swt.graphics.RGB;

/**The abstract model for choice widget.
 * @author Xihui Chen
 *
 */
public abstract class AbstractChoiceModel extends AbstractPVWidgetModel {

	
	public static final String PROP_ITEMS = "items";//$NON-NLS-1$
	public static final String PROP_FONT = "font";//$NON-NLS-1$
	public static final String PROP_ITEMS_FROM_PV = "items_from_pv";//$NON-NLS-1$
	
	public static final String PROP_SELECTED_COLOR = "selected_color";//$NON-NLS-1$

	public static final RGB DEFAULT_SELECTED_COLOR = CustomMediaFactory.COLOR_WHITE;
	
	public static final String[] DEFAULT_ITEMS = new String[]{"Choice 1", "Choice 2", "Choice 3"};
	
	public AbstractChoiceModel() {
		setBackgroundColor(new RGB(255,255,255));
		setForegroundColor(new RGB(0,0,0));
	}

	@Override
	protected void configureProperties() {		
		addProperty(new StringListProperty(
				PROP_ITEMS, "Items", WidgetPropertyCategory.Behavior, Arrays.asList(DEFAULT_ITEMS)));
		addProperty(new FontProperty(
				PROP_FONT, "Font", WidgetPropertyCategory.Display, CustomMediaFactory.FONT_ARIAL));
		addProperty(new BooleanProperty(
				PROP_ITEMS_FROM_PV, "Items From PV", WidgetPropertyCategory.Behavior, true));
		addProperty(new ColorProperty(PROP_SELECTED_COLOR, "Selected Color", 
				WidgetPropertyCategory.Display, DEFAULT_SELECTED_COLOR));
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getItems(){
		return (List<String>)getPropertyValue(PROP_ITEMS);
	}
	
	public boolean isItemsFromPV(){
		return (Boolean)getPropertyValue(PROP_ITEMS_FROM_PV);
	}
	
	public OPIFont getFont(){
		return (OPIFont)getCastedPropertyValue(PROP_FONT);
	}
		

	public OPIColor getSelectedColor(){
		return (OPIColor)getPropertyValue(PROP_SELECTED_COLOR);
	}

}
