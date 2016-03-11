package org.csstudio.sds.components.internal.model;

import org.csstudio.sds.components.model.XMeterModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.IWidgetModelFactory;

/**
 * This class defines a widget model factory for XMeter widget models.
 *
 * @author Xihui Chen
 *
 */
public final class XMeterModelFactory implements IWidgetModelFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractWidgetModel createWidgetModel() {
        return new XMeterModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class getWidgetModelType() {
        return XMeterModel.class;
    }

}
