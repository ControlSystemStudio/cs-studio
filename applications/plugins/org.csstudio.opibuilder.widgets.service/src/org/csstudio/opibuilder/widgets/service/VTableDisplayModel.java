package org.csstudio.opibuilder.widgets.service;


import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.IPVWidgetModel;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

public class VTableDisplayModel extends AbstractWidgetModel {
	
	public final String ID = "org.csstudio.opibuilder.widgets.service.VTableDisplay"; //$NON-NLS-1$
	
	@Override
	protected void configureProperties() {
		addProperty(new StringProperty(IPVWidgetModel.PROP_PVNAME, "PV Formula", WidgetPropertyCategory.Basic, ""));
	}
	
	public String getPvFormula() {
		return (String) getCastedPropertyValue(IPVWidgetModel.PROP_PVNAME);
	}

	@Override
	public String getTypeID() {
		return ID;
	}

}
