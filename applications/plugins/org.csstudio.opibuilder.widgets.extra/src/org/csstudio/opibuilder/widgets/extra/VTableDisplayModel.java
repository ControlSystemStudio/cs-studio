package org.csstudio.opibuilder.widgets.extra;


import org.csstudio.opibuilder.model.IPVWidgetModel;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.utility.pvmanager.widgets.VTableWidget;

public class VTableDisplayModel extends AbstractSelectionWidgetModel {
	
	public VTableDisplayModel() {
		super(AbstractSelectionWidgetModelDescription.newModelFrom(VTableWidget.class));
	}

	public final String ID = "org.csstudio.opibuilder.widgets.VTableDisplay"; //$NON-NLS-1$
	public static final String PROP_SELECTION_PV = "pv_result_prefix"; //$NON-NLS-1$	
	
	@Override
	protected void configureProperties() {
		addProperty(new StringProperty(IPVWidgetModel.PROP_PVNAME, "PV Formula", WidgetPropertyCategory.Basic, ""));
		addProperty(new StringProperty(PROP_SELECTION_PV, "Selection PV", WidgetPropertyCategory.Basic, ""));
	}
	
	public String getPvFormula() {
		return (String) getCastedPropertyValue(IPVWidgetModel.PROP_PVNAME);
	}
	
	public String getSelectionPv() {
		return (String) getCastedPropertyValue(PROP_SELECTION_PV);
	}

	@Override
	public String getTypeID() {
		return ID;
	}

}
