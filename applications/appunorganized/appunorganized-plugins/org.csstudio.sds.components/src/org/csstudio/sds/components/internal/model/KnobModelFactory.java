package org.csstudio.sds.components.internal.model;

import org.csstudio.sds.components.model.KnobModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.IWidgetModelFactory;

/**
 * This class defines a widget model factory for knob widget models.
 *
 * @author Xihui Chen
 *
 */
public final class KnobModelFactory implements IWidgetModelFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractWidgetModel createWidgetModel() {
        return new KnobModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class getWidgetModelType() {
        return KnobModel.class;
    }

}
