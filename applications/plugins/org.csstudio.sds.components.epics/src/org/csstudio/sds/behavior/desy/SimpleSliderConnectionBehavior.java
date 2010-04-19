package org.csstudio.sds.behavior.desy;

import org.csstudio.sds.components.model.SimpleSliderModel;
import org.epics.css.dal.simple.MetaData;

public class SimpleSliderConnectionBehavior extends AbstractDesyConnectionBehavior<SimpleSliderModel> {

    @Override
    protected String[] doGetInvisiblePropertyIds() {
        return super.doGetInvisiblePropertyIds();
    }

    @Override
    protected void doProcessMetaDataChange(final SimpleSliderModel widget, final MetaData metaData) {
        // do noting
    }
}
