package org.csstudio.sds.behavior.desy;

import org.csstudio.sds.components.model.TextInputModel;
import org.epics.css.dal.simple.MetaData;

public class TextinputConnectionBehavior extends AbstractDesyConnectionBehavior<TextInputModel> {

    /**
     * Constructor.
     */
    public TextinputConnectionBehavior() {
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void doProcessMetaDataChange(final TextInputModel widget, final MetaData metaData) {
        // do noting
    }
}
