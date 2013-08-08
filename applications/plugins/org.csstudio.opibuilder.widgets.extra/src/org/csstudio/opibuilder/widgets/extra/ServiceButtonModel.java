package org.csstudio.opibuilder.widgets.extra;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.ServiceMethodDescription;
import org.csstudio.opibuilder.properties.ServiceMethodProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

public class ServiceButtonModel extends AbstractWidgetModel {

    public final String ID = "org.csstudio.opibuilder.widgets.ServiceButton"; //$NON-NLS-1$
    
    private static final String SERVICE_METHOD = "service_method";

    @Override
    protected void configureProperties() {
	addProperty(new ServiceMethodProperty(SERVICE_METHOD, "Service Method Definition", WidgetPropertyCategory.Basic));	
    }

    public ServiceMethodDescription getServiceMethodDescription() {
	return (ServiceMethodDescription) getCastedPropertyValue(SERVICE_METHOD);	
    }

    @Override
    public String getTypeID() {
	return ID;
    }

}
