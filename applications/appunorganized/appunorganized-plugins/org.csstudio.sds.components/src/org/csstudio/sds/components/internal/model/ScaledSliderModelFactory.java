package org.csstudio.sds.components.internal.model;

import org.csstudio.sds.components.model.ScaledSliderModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.IWidgetModelFactory;

/**
 * This class defines a widget model factory for scaled slider widget models.
 *
 * @author Xihui Chen
 *
 */
public final class ScaledSliderModelFactory implements IWidgetModelFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractWidgetModel createWidgetModel() {
        return new ScaledSliderModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class getWidgetModelType() {
        return ScaledSliderModel.class;
    }

}
