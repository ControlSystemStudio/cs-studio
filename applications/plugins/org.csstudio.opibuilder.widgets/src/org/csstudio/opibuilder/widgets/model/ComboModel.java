package org.csstudio.opibuilder.widgets.model;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.FontProperty;
import org.csstudio.opibuilder.properties.StringListProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.MediaService;
import org.csstudio.opibuilder.util.OPIFont;
import org.eclipse.swt.graphics.RGB;

/**The model for combo widget.
 * @author Xihui Chen
 *
 */
public class ComboModel extends AbstractPVWidgetModel {

	
	public final String ID = "org.csstudio.opibuilder.widgets.combo";//$NON-NLS-1$
	/**
	 * Items of the combo.
	 */
	public static final String PROP_ITEMS = "items";//$NON-NLS-1$
	/**
	 * Font of the widget.
	 */
	public static final String PROP_FONT = "font";//$NON-NLS-1$
	/**
	 * True if items are read from the input PV which must be an Enum PV.
	 */
	public static final String PROP_ITEMS_FROM_PV = "items_from_pv";//$NON-NLS-1$
	
	public ComboModel() {
		setBackgroundColor(new RGB(255,255,255));
		setForegroundColor(new RGB(0,0,0));
	}

	@Override
	protected void configureProperties() {		
		addProperty(new StringListProperty(
				PROP_ITEMS, "Items", WidgetPropertyCategory.Behavior, new ArrayList<String>()));
		addProperty(new FontProperty(
				PROP_FONT, "Font", WidgetPropertyCategory.Display, MediaService.DEFAULT_FONT));
		addProperty(new BooleanProperty(
				PROP_ITEMS_FROM_PV, "Items From PV", WidgetPropertyCategory.Behavior, false));
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
	
	
	@Override
	public String getTypeID() {
		return ID;
	}

}
