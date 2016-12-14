package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.EdmWidget;

public class OpiButtonClass extends OpiWidget {

    public OpiButtonClass(Context parentContext, EdmWidget r) {
        super(parentContext, r);

        // Expand size by 1px to match EDM
        new OpiInt(widgetContext, "width", r.getW() + 1);
        new OpiInt(widgetContext, "height", r.getH() + 1);
    }

}
