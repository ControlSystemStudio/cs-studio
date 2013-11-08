/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.widgets.extra.AbstractSelectionWidgetModel;
import org.csstudio.opibuilder.widgets.extra.AbstractSelectionWidgetModelDescription;

/**
 * @author shroffk
 * 
 */
public abstract class AbstractPointDatasetGraph2DWidgetModel extends AbstractSelectionWidgetModel {
	
	public AbstractPointDatasetGraph2DWidgetModel(AbstractSelectionWidgetModelDescription model) {
		super(model);
	}

	public static final String PROP_DATA_FORMULA = "data_formula"; //$NON-NLS-1$
	public static final String PROP_X_FORMULA = "x_formula"; //$NON-NLS-1$
	public static final String PROP_Y_FORMULA = "y_formula"; //$NON-NLS-1$
//	public static final String PROP_TOOLTIP_FORMULA = "tooltip_formula"; //$NON-NLS-1$
	public static final String PROP_RESIZABLE_AXIS = "show_axis"; //$NON-NLS-1$

	@Override
	protected void configureProperties() {
		addProperty(new StringProperty(PROP_DATA_FORMULA,
				"Data Expression (" + getDataType() + ")", WidgetPropertyCategory.Basic, ""));
		addProperty(new StringProperty(PROP_X_FORMULA,
				"X Column Expression (VString)", WidgetPropertyCategory.Basic, ""));
		addProperty(new StringProperty(PROP_Y_FORMULA,
				"Y Column Expression (VString)", WidgetPropertyCategory.Basic, ""));
//		addProperty(new StringProperty(PROP_TOOLTIP_FORMULA,
//				"Tooltip Column Formula", WidgetPropertyCategory.Basic, ""));
		addProperty(new BooleanProperty(LineGraph2DWidgetModel.PROP_RESIZABLE_AXIS,
				"Resizable Axis", WidgetPropertyCategory.Behavior, true));

	}
	
	protected String getDataType() {
		return "VTable";
	}

	public String getDataFormula() {
		return (String) getCastedPropertyValue(PROP_DATA_FORMULA);
	}

	public String getXColumnFormula() {
		return (String) getCastedPropertyValue(PROP_X_FORMULA);
	}

	public String getYColumnFormula() {
		return (String) getCastedPropertyValue(PROP_Y_FORMULA);
	}

//	public String getTooltipFormula() {
//		return (String) getCastedPropertyValue(PROP_TOOLTIP_FORMULA);
//	}

	public boolean isResizableAxis() {
		return getCastedPropertyValue(LineGraph2DWidgetModel.PROP_RESIZABLE_AXIS);
	}

}
