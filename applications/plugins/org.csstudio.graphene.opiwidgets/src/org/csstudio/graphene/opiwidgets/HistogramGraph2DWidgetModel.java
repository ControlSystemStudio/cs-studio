/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.HistogramGraph2DWidget;
import org.csstudio.graphene.MouseSelectionMethod;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.widgets.extra.AbstractSelectionWidgetModelDescription;
import static org.csstudio.graphene.opiwidgets.ModelPropertyConstants.*;



/**
 * @author shroffk
 * 
 */
public class HistogramGraph2DWidgetModel extends AbstractGraph2DWidgetModel {

	public HistogramGraph2DWidgetModel() {
		super(AbstractSelectionWidgetModelDescription.newModelFrom(HistogramGraph2DWidget.class));
	}

	public final String ID = "org.csstudio.graphene.opiwidgets.HistogramGraph2D"; //$NON-NLS-1$

	@Override
	public String getTypeID() {
		return ID;
	}
	
	@Override
	protected void configureProperties() {
		super.configureProperties();
		addProperty(new BooleanProperty(PROP_HIGHLIGHT_SELECTION_VALUE,
				"Highlight Selection Value", WidgetPropertyCategory.Basic, false));
		addProperty(new StringProperty(PROP_SELECTION_VALUE_PV,
				"Selection Value PV (VNumberArray)", WidgetPropertyCategory.Basic, ""));
		addProperty(new ComboProperty(PROP_MOUSE_SELECTION_METHOD,
				"Mouse Selection Method", WidgetPropertyCategory.Basic, MouseSelectionMethod.labels(), 0));
	}
	
	public String getSelectionValuePv() {
		return (String) getCastedPropertyValue(PROP_SELECTION_VALUE_PV);
	}
	
	public MouseSelectionMethod getMouseSelectionMethod() {
		return MouseSelectionMethod.valueOf(MouseSelectionMethod.labels()[(Integer) getCastedPropertyValue(PROP_MOUSE_SELECTION_METHOD)]);
	}

	public boolean isHighlightSelectionValue() {
		return (Boolean) getCastedPropertyValue(PROP_HIGHLIGHT_SELECTION_VALUE);
	}
	
	@Override
	protected String getDataType() {
		return "VNumberArray";
	}

}
