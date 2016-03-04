package org.csstudio.sds.components.internal.model;

import org.csstudio.sds.components.model.XYGraphModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.IWidgetModelFactory;

public class XYGraphModelFactory implements IWidgetModelFactory {

    @Override
    public AbstractWidgetModel createWidgetModel() {
        return new XYGraphModel();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class getWidgetModelType() {
        return XYGraphModel.class;
    }

}
