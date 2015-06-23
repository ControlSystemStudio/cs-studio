package org.csstudio.opibuilder.widgets.extra;


import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

public class ServicePanelModel extends AbstractWidgetModel {

    public final String ID = "org.csstudio.opibuilder.widgets.ServicePanel"; //$NON-NLS-1$

    public static final String SERVICE_METHOD_NAME = "service_method_name"; //$NON-NLS-1$
    public static final String PV_ARGUMENT_PREFIX = "pv_argument_prefix"; //$NON-NLS-1$
    public static final String PV_RESULT_PREFIX = "pv_result_prefix"; //$NON-NLS-1$

    @Override
    protected void configureProperties() {
        addProperty(new StringProperty(SERVICE_METHOD_NAME, "Service Method", WidgetPropertyCategory.Basic, ""));
        addProperty(new StringProperty(PV_ARGUMENT_PREFIX, "PV Argument Prefix", WidgetPropertyCategory.Basic, ""));
        addProperty(new StringProperty(PV_RESULT_PREFIX, "PV Result Prefix", WidgetPropertyCategory.Basic, ""));
    }

    public String getServiceMethodName() {
        return (String) getCastedPropertyValue(SERVICE_METHOD_NAME);
    }

    public String getPvArgumentPrefix() {
        return (String) getCastedPropertyValue(PV_ARGUMENT_PREFIX);
    }

    public String getPvResultPrefix() {
        return (String) getCastedPropertyValue(PV_RESULT_PREFIX);
    }

    @Override
    public String getTypeID() {
        return ID;
    }

}
