package org.csstudio.graphene;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

public class HistogramModel extends AbstractWidgetModel {
	
	public final String ID = "org.csstudio.graphene.Histogram"; //$NON-NLS-1$
	
	public static final String SHOW_TIME_AXIS = "show_time_axis"; //$NON-NLS-1$	
	
	@Override
	protected void configureProperties() {
		addProperty(new StringProperty(AbstractPVWidgetModel.PROP_PVNAME, "PV Name", WidgetPropertyCategory.Basic, ""));
	}
	
	public ProcessVariable getProcessVariable() {
		return new ProcessVariable((String) getCastedPropertyValue(AbstractPVWidgetModel.PROP_PVNAME));
	}

	@Override
	public String getTypeID() {
		return ID;
	}

}
