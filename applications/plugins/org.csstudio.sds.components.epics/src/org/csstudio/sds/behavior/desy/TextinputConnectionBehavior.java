package org.csstudio.sds.behavior.desy;

import org.csstudio.sds.components.model.TextInputModel;
import org.epics.css.dal.simple.MetaData;

public class TextinputConnectionBehavior extends AbstractDesyConnectionBehavior<TextInputModel> {

    @Override
    protected String[] doGetInvisiblePropertyIds() {
        return super.doGetInvisiblePropertyIds();
    }

    @Override
    protected void doProcessMetaDataChange(final TextInputModel widget, final MetaData metaData) {
        // do noting
    }
}
