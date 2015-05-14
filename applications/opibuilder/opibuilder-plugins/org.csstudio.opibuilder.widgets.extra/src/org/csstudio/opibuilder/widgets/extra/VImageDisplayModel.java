package org.csstudio.opibuilder.widgets.extra;


import org.csstudio.opibuilder.model.IPVWidgetModel;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.utility.pvmanager.widgets.VImageWidget;

public class VImageDisplayModel extends AbstractSelectionWidgetModel {

    public VImageDisplayModel() {
        super(AbstractSelectionWidgetModelDescription.newModelFrom(VImageWidget.class));
    }

    public final String ID = "org.csstudio.opibuilder.widgets.VImageDisplay"; //$NON-NLS-1$

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
