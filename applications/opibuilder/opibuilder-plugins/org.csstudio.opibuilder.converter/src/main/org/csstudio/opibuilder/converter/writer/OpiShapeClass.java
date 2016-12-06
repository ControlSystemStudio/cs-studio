package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.Edm_activeShapeClass;

public class OpiShapeClass extends OpiWidget {

    public OpiShapeClass(Context parentContext, Edm_activeShapeClass r) {
        super(parentContext, r);
        
        int line_width = 1;
        if (r.getLineWidth() != 0) // Looks like EDM always show the line.
            line_width = r.getLineWidth();
        new OpiInt(widgetContext, "line_width", line_width);

        // EDM draws a border around the outside of the rectangle. In order to have the
        // same appearance we need to resize the widget.
        new OpiInt(widgetContext, "x", r.getX() - widgetContext.getX() - line_width/2);
        new OpiInt(widgetContext, "y", r.getY() - widgetContext.getY() - line_width/2);
        new OpiInt(widgetContext, "width", r.getW() + line_width);
        new OpiInt(widgetContext, "height", r.getH() + line_width);
    }

}
