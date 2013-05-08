/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

/**
 * @author shroffk
 * 
 */
public class ScatterGraph2DWidgetModel extends AbstractWidgetModel {

	public final String ID = "org.csstudio.graphene.opiwidgets.ScatterGraph2D"; //$NON-NLS-1$

	public static final String PROP_DATA_FORMULA = "data_formula"; //$NON-NLS-1$
	public static final String PROP_X_FORMULA = "x_formula"; //$NON-NLS-1$
	public static final String PROP_Y_FORMULA = "y_formula"; //$NON-NLS-1$
	public static final String PROP_TOOLTIP_FORMULA = "tooltip_formula"; //$NON-NLS-1$
	public static final String PROP_SHOW_AXIS = "show_axis"; //$NON-NLS-1$
	public static final String CONFIGURABLE = "configurable"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.csstudio.opibuilder.model.AbstractWidgetModel#configureProperties()
	 */
	@Override
	protected void configureProperties() {
		addProperty(new StringProperty(PROP_DATA_FORMULA,
				"Data Formula", WidgetPropertyCategory.Basic, ""));
		addProperty(new StringProperty(PROP_X_FORMULA,
				"X Column Formula", WidgetPropertyCategory.Basic, ""));
		addProperty(new StringProperty(PROP_Y_FORMULA,
				"Y Column Formula", WidgetPropertyCategory.Basic, ""));
		addProperty(new StringProperty(PROP_TOOLTIP_FORMULA,
				"Tooltip Column Formula", WidgetPropertyCategory.Basic, ""));
		addProperty(new BooleanProperty(LineGraph2DWidgetModel.CONFIGURABLE,
				"configurable", WidgetPropertyCategory.Basic, true));
		addProperty(new BooleanProperty(LineGraph2DWidgetModel.PROP_SHOW_AXIS,
				"Show Axis", WidgetPropertyCategory.Display, true));

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

	public String getTooltipFormula() {
		return (String) getCastedPropertyValue(PROP_TOOLTIP_FORMULA);
	}

	public boolean getShowAxis() {
		return getCastedPropertyValue(LineGraph2DWidgetModel.PROP_SHOW_AXIS);
	}

	public boolean isConfigurable() {
		return getCastedPropertyValue(CONFIGURABLE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csstudio.opibuilder.model.AbstractWidgetModel#getTypeID()
	 */
	@Override
	public String getTypeID() {
		return ID;
	}

}
