package org.csstudio.opibuilder.widgets.extra;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.ServiceMethodDescription;
import org.csstudio.opibuilder.properties.ServiceMethodProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

public class ServiceButtonModel extends AbstractWidgetModel {

    public final String ID = "org.csstudio.opibuilder.widgets.ServiceButton"; //$NON-NLS-1$

    public static final String SERVICE_METHOD = "service_method";
    public static final String PROP_LABEL = "label"; //$NON-NLS-1$

    @Override
    protected void configureProperties() {
    addProperty(new ServiceMethodProperty(SERVICE_METHOD,
        "Service Method Definition", WidgetPropertyCategory.Basic));
    addProperty(new StringProperty(PROP_LABEL, "Label", WidgetPropertyCategory.Display, "Execute"));
    }

    public ServiceMethodDescription getServiceMethodDescription() {
    return (ServiceMethodDescription) getCastedPropertyValue(SERVICE_METHOD);
    }

    public String getLabel() {
    return (String) getProperty(PROP_LABEL).getPropertyValue();
    }

    @Override
    public String getTypeID() {
    return ID;
    }

}
