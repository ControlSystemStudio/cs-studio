package org.csstudio.sds.behavior.desy;

import org.csstudio.sds.components.model.AdvancedSliderModel;
import org.epics.css.dal.simple.MetaData;

public class AdvancedSliderAlarmBehavior extends AbstractDesyAlarmBehavior<AdvancedSliderModel> {

    @Override
    protected String[] doGetInvisiblePropertyIds() {
        return super.doGetInvisiblePropertyIds();
    }

    @Override
    protected void doProcessMetaDataChange(final AdvancedSliderModel widget, final MetaData metaData) {
        // do nothing
    }
    
}
