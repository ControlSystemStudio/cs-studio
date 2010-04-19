package org.csstudio.sds.behavior.desy;

import org.csstudio.sds.components.model.BargraphModel;
import org.epics.css.dal.simple.MetaData;

public class BargraphAlarmBehavior extends AbstractDesyAlarmBehavior<BargraphModel> {
   
    @Override
    protected String[] doGetInvisiblePropertyIds() {
        return super.doGetInvisiblePropertyIds();
    }

    @Override
    protected void doProcessMetaDataChange(final BargraphModel widget, final MetaData metaData) {
        // do nothing
    }

}
