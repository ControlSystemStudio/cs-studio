package org.csstudio.sds.components.internal.model;

import org.csstudio.sds.components.model.GaugeModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.IWidgetModelFactory;

/**
 * This class defines a widget model factory for gauge widget models.
 *
 * @author Xihui Chen
 *
 */
public final class GaugeModelFactory implements IWidgetModelFactory {

    /**
     * {@inheritDoc}
     */
    public AbstractWidgetModel createWidgetModel() {
        return new GaugeModel();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Class getWidgetModelType() {
        return GaugeModel.class;
    }

}
