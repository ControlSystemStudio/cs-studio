package org.csstudio.sds.components.internal.model;

import org.csstudio.sds.components.model.TankModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.IWidgetModelFactory;

/**
 * This class defines a widget model factory for tank widget models.
 *
 * @author Xihui Chen
 *
 */
public final class TankModelFactory implements IWidgetModelFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractWidgetModel createWidgetModel() {
        return new TankModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class getWidgetModelType() {
        return TankModel.class;
    }

}
