/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import java.util.logging.Logger;
import org.csstudio.opibuilder.converter.model.Edm_TextupdateClass;

/**
 * XML conversion class for Edm_TextupdateClass.
 * @author Matevz, Xihui Chen
 */
public class Opi_TextupdateClass extends Opi_activeShapeClass {

    private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_TextupdateClass");
    private static final String typeId = "TextUpdate";
    private static final String name = "EDM Text Update";
    private static final String version = "1.0";

    /**
     * Converts the Edm_TextupdateClass to OPI TextUpdate widget XML.
     */
    public Opi_TextupdateClass(Context con, Edm_TextupdateClass t) {
        super(con,t);
        setTypeId(typeId);
        setName(name);
        setVersion(version);

        new OpiString(widgetContext, "pv_name", convertPVName(t.getControlPv()));
        new OpiBoolean(widgetContext, "transparent", !t.isFill());

        int align = 0;
        if (t.getFontAlign()!=null){
            if(t.getFontAlign().equals("right"))
                align = 2;
            else if(t.getFontAlign().equals("center"))
                align = 1;
        }
        new OpiInt(widgetContext, "horizontal_alignment", align);

        new OpiInt(widgetContext, "border_width", t.getLineWidth());
        new OpiInt(widgetContext, "border_style", t.isLineAlarm()?0:1);
        new OpiColor(widgetContext, "border_color", t.getFgColor(), t);
        new OpiBoolean(widgetContext, "border_alarm_sensitive", t.isLineAlarm());

        // Handle format and units
        boolean precisionFromPV = true;
        boolean showUnits = true;
        // Mode maps to org.csstudio.simplepv.FormatEnum
        int mode = 0; // show default display
        if(t.getDisplayMode() == null){ // default mode in EDM; ignores precision field and shows units
            mode = 0;  // DEFAULT
            precisionFromPV = true;
            showUnits = true;
        } else { // not default mode in EDM
            // these modes specify precision and not units
            precisionFromPV = false;
            showUnits = false;
            if(t.getDisplayMode().equals("decimal")){
                mode = 1;  // DECIMAL
            } else if (t.getDisplayMode().equals("exp")) {
                mode = 2;  // EXP
            } else if (t.getDisplayMode().equals("hex")) {
                mode = 3;  // HEX32
            } else if (t.getDisplayMode().equals("engineer")){
                mode = 7;  // ENG
            }
        }

        new OpiBoolean(widgetContext, "precision_from_pv", precisionFromPV);
        new OpiBoolean(widgetContext, "show_units", showUnits);
        new OpiInt(widgetContext, "format_type", mode);
        new OpiInt(widgetContext, "precision", t.getPrecision());

        new OpiColor(widgetContext, "foreground_color", t.getFgColor(), t);

        log.config("Edm_TextupdateClass written.");
    }

}
