package org.csstudio.sds.components.internal.model;

import org.csstudio.sds.components.model.SixteenBinaryBarModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.IWidgetModelFactory;

/**
 * Factory for {@link SixteenBinaryBarModel}.
 *
 * @author Alen Vrecko
 *
 */
public class SixteenBinaryBarModelFactory implements IWidgetModelFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractWidgetModel createWidgetModel() {
        return new SixteenBinaryBarModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class getWidgetModelType() {
        return SixteenBinaryBarModel.class;
    }

}