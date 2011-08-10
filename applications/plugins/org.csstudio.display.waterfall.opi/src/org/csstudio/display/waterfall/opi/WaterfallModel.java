package org.csstudio.display.waterfall.opi;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

public class WaterfallModel extends AbstractWidgetModel {
	
	public final String ID = "org.csstudio.display.waterfall.opi.Waterfall"; //$NON-NLS-1$
	
	public static final String PV_NAME = "name"; //$NON-NLS-1$	
	
	@Override
	protected void configureProperties() {
		addProperty(new StringProperty(PV_NAME, "PV name or tag", WidgetPropertyCategory.Basic, ""));
	}

	@Override
	public String getTypeID() {
		return ID;
	}
	
	public String getPvName() {
		return getCastedPropertyValue(PV_NAME);
	}

}
