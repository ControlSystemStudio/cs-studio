/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.BubbleGraph2DWidget;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.widgets.extra.AbstractSelectionWidgetModelDescription;

import static org.csstudio.graphene.opiwidgets.ModelPropertyConstants.*;

/**
 * @author shroffk
 * 
 */
public class BubbleGraph2DWidgetModel extends AbstractPointDatasetGraph2DWidgetModel {
	
	public BubbleGraph2DWidgetModel() {
		super(AbstractSelectionWidgetModelDescription.newModelFrom(BubbleGraph2DWidget.class));
	}

	public final String ID = "org.csstudio.graphene.opiwidgets.BubbleGraph2D"; //$NON-NLS-1$

	@Override
	public String getTypeID() {
		return ID;
	}

	public static final String PROP_SIZE_FORMULA = "size_formula"; //$NON-NLS-1$
	public static final String PROP_COLOR_FORMULA = "color_formula"; //$NON-NLS-1$
	
	@Override
	protected void configureProperties() {
		super.configureProperties();
		addProperty(new StringProperty(PROP_SIZE_FORMULA,
				"Size Column Expression (VString)", WidgetPropertyCategory.Basic, ""));
		addProperty(new StringProperty(PROP_COLOR_FORMULA,
				"Color Column Expression (VString)", WidgetPropertyCategory.Basic, ""));
		addProperty(new BooleanProperty(PROP_HIGHLIGHT_SELECTION_VALUE,
				"Highlight Selection Value", WidgetPropertyCategory.Basic, false));
		addProperty(new StringProperty(PROP_SELECTION_VALUE_PV,
				"Selection Value PV (VTable)", WidgetPropertyCategory.Basic, ""));
	}
	
	public String getSelectionValuePv() {
		return (String) getCastedPropertyValue(PROP_SELECTION_VALUE_PV);
	}

	public boolean isHighlightSelectionValue() {
		return (Boolean) getCastedPropertyValue(PROP_HIGHLIGHT_SELECTION_VALUE);
	}
	
	public String getSizeColumnFormula() {
		return (String) getCastedPropertyValue(PROP_SIZE_FORMULA);
	}
	
	public String getColorColumnFormula() {
		return (String) getCastedPropertyValue(PROP_COLOR_FORMULA);
	}

}
