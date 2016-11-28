/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import java.util.logging.Logger;
import org.csstudio.opibuilder.converter.model.EdmLineStyle;
import org.csstudio.opibuilder.converter.model.Edm_activeRectangleClass;

/**
 * XML conversion class for Edm_activeRectangleClass. (Use this as the standard
 * example for all other widgets!)
 *
 * @author Matevz, Xihui Chen
 */
public class Opi_activeRectangleClass extends OpiWidget {

    private static Logger log = Logger
            .getLogger("org.csstudio.opibuilder.converter.writer.Opi_activeRectangleClass");
    private static final String typeId = "Rectangle";
    private static final String name = "EDM Rectangle";
    private static final String version = "1.0";

    /**
     * Converts the Edm_activeRectangleClass to OPI Rectangle widget XML.
     */
    public Opi_activeRectangleClass(Context con, Edm_activeRectangleClass r) {
        super(con, r);
        setTypeId(typeId);
        setName(name);
        setVersion(version);
        // All EDM color properties are saved in edl file, no need to check if exist.
        new OpiColor(widgetContext, "line_color", r.getLineColor(), r);

        // If a boolean property is not exist in edl file, it is false.
        // If Double or Int property is not exist, it is 0.
        new OpiBoolean(widgetContext, "transparent", !r.isFill());

        new OpiColor(widgetContext, "background_color", r.getFillColor(), r);


        new OpiBoolean(widgetContext, "visible", !r.isInvisible());

        // If a string property is not exist, it is null.
        if (r.getAlarmPv() != null) {
            // line color alarm rule.
            if(r.isLineAlarm())
                createColorAlarmRule(r, convertPVName(r.getAlarmPv()), "line_color",
                    "lineColorAlarmRule", false);
            if(r.isFillAlarm())
                createColorAlarmRule(r, convertPVName(r.getAlarmPv()), "background_color",
                    "backColorAlarmRule", false);
        }

        int line_width = 1;
        if (r.getLineWidth() != 0) //Looks like EDM always show the line.
            line_width = r.getLineWidth();
        new OpiInt(widgetContext, "line_width", line_width);

        // EDM draws a border around the outside of the rectangle. In order to have the
        // same appearance we need to resize the widget.
        new OpiInt(widgetContext, "x", r.getX() - widgetContext.getX() - line_width/2);
        new OpiInt(widgetContext, "y", r.getY() - widgetContext.getY() - line_width/2);
        new OpiInt(widgetContext, "width", r.getW() + line_width);
        new OpiInt(widgetContext, "height", r.getH() + line_width);

        int lineStyle = 0;
        //For EDMAttribute property, use isExistInEDL
        if (r.getLineStyle().isExistInEDL()) {

            switch (r.getLineStyle().get()) {
            case EdmLineStyle.SOLID: {
                lineStyle = 0;
            }
                break;
            case EdmLineStyle.DASH: {
                lineStyle = 1;
            }
                break;
            }

        }
        new OpiInt(widgetContext, "line_style", lineStyle);

        log.config("Edm_activeRectangleClass written.");

    }

    protected void setDefaultPropertyValue() {
        super.setDefaultPropertyValue();
        new OpiBoolean(widgetContext, "transparent", true);
    }

}
