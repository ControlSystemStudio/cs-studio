package org.csstudio.sds.components.internal.model;

import org.csstudio.sds.components.model.ThermometerModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.IWidgetModelFactory;

/**
 * This class defines a widget model factory for thermometer widget models.
 *
 * @author Xihui Chen
 *
 */
public final class ThermometerModelFactory implements IWidgetModelFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractWidgetModel createWidgetModel() {
        return new ThermometerModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class getWidgetModelType() {
        return ThermometerModel.class;
    }

}
