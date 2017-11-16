/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import java.util.logging.Logger;

import org.csstudio.opibuilder.converter.model.Edm_activeXTextDspClass_noedit;

/**
 * XML conversion class for EDM text monitor widget
 * @author Xihui Chen
 */
public class Opi_activeXTextDspClass_noedit extends OpiWidget {

    private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activeXTextDspClass");
    private static final String typeId = "TextUpdate";
    private static final String name = "EDM TextInput";
    private static final String version = "1.0";

    /**
     * Converts the Edm_activeRectangleClass to OPI Rectangle widget XML.
     */
    public Opi_activeXTextDspClass_noedit(Context con, Edm_activeXTextDspClass_noedit r) {
        super(con, r);
        setTypeId(typeId);
        setName(name);
        setVersion(version);

        if(r.getAttribute("controlPv").isExistInEDL())
        {
            new OpiString(widgetContext, "pv_name", convertPVName(r.getControlPv()));
        }

        // If precision is not specified, default to using precision from the PV.
        // This matches EDM's behaviour.
        if (r.isLimitsFromDb() || ! r.getAttribute("precision").isExistInEDL()) {
            new OpiBoolean(widgetContext, "precision_from_pv", true);
        } else {
            new OpiBoolean(widgetContext, "precision_from_pv", false);
            new OpiInt(widgetContext, "precision", r.getPrecision());
        }

        new OpiBoolean(widgetContext, "show_units", r.isShowUnits());
        // Border alarm sensitivity only applies in EDM if already alarm sensitive.
        new OpiBoolean(widgetContext, "border_alarm_sensitive", r.isUseAlarmBorder() && r.isFgAlarm());

        int a=0;
        if(r.getFontAlign()==null)
            a=0;
        else if(r.getFontAlign().equals("right"))
            a=2;
        else if(r.getFontAlign().equals("center"))
            a=1;
        new OpiInt(widgetContext, "horizontal_alignment", a);

        int f=0;
        if (r.getFormat() != null) {
            if (r.getFormat().equals("float"))
                f = 1;
            else if (r.getFormat().equals("exponential"))
                f = 2;
            else if (r.getFormat().equals("decimal"))
                f = 1;
            else if (r.getFormat().equals("hex"))
                f = 3;
            else if (r.getFormat().equals("string"))
                f = 4;
        }
        new OpiInt(widgetContext, "format_type", f);

        new OpiString(widgetContext, "border_style", "None");
        new OpiBoolean(widgetContext, "transparent", r.isTransparent());


        log.config("Edm_activeXTextDspClass written.");

    }

}

