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
import org.csstudio.opibuilder.converter.model.Edm_activeArcClass;

/**
 * XML conversion class for Edm_activeArcClass
 * @author Matevz
 */
public class Opi_activeArcClass extends OpiShapeClass {

    private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activeArcClass");
    private static final String typeId = "arc";
    private static final String name = "EDM arc";
    private static final String version = "1.0";

    /**
     * Converts the Edm_activeArcClass to OPI Rectangle widget XML.
     */
    public Opi_activeArcClass(Context con, Edm_activeArcClass r) {
        super(con, r);
        setTypeId(typeId);
        setVersion(version);
        setName(name);

        int line_width = 1;
        if (r.getLineWidth() != 0) // Looks like EDM always show the line.
            line_width = r.getLineWidth();

        // CS-Studio shrinks the widget by the line width when one is applied, which seems
        // like a bug, this works around that issue.
        new OpiInt(widgetContext, "x", r.getX() - widgetContext.getX() - line_width);
        new OpiInt(widgetContext, "y", r.getY() - widgetContext.getY() - line_width);
        new OpiInt(widgetContext, "width", r.getW() + line_width * 2);
        new OpiInt(widgetContext, "height", r.getH() + line_width * 2);

        new OpiColor(widgetContext, "foreground_color",r.getLineColor(), r);


        new OpiBoolean(widgetContext, "fill", r.isFill());

        new OpiColor(widgetContext, "background_color", r.getFillColor(), r);


        if (r.getAlarmPv() != null) {
            // line color alarm rule.
            if (r.isLineAlarm())
                createColorAlarmRule(r, convertPVName(r.getAlarmPv()), "foreground_color",
                        "lineColorAlarmRule", false);
            if (r.isFillAlarm())
                createColorAlarmRule(r, convertPVName(r.getAlarmPv()), "background_color",
                        "backColorAlarmRule", false);
        }

        int lineStyle = 0;
        if (r.getLineStyle().isExistInEDL()) {

            switch (r.getLineStyle().get()) {
            case EdmLineStyle.SOLID: {
                lineStyle = 0;
            } break;
            case EdmLineStyle.DASH: {
                lineStyle = 1;
            } break;
            }

        }
        new OpiInt(widgetContext, "line_style", lineStyle);


        new OpiDouble(widgetContext, "start_angle", r.getStartAngle());

        new OpiDouble(widgetContext, "total_angle",
                    r.getAttribute("totalAngle").isExistInEDL()?r.getTotalAngle():180);

        log.config("Edm_activeArcClass written.");

    }

    protected void setDefaultPropertyValue(){
        super.setDefaultPropertyValue();
        new OpiBoolean(widgetContext, "transparent", true);
    }

}
