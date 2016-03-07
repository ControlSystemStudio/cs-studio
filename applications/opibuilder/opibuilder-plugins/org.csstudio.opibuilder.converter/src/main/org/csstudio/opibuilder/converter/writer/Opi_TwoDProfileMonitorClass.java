/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;

import org.csstudio.opibuilder.converter.model.Edm_TwoDProfileMonitorClass;
import org.w3c.dom.Element;

/**
 * XML conversion class for Edm_TwoDProfileMonitorClass.
 *
 * @author Xihui Chen
 */
public class Opi_TwoDProfileMonitorClass extends OpiWidget {

    private static Logger log = Logger
            .getLogger("org.csstudio.opibuilder.converter.writer.Edm_TwoDProfileMonitorClass");
    private static final String typeId = "intensityGraph";
    private static final String name = "EDM Edm_TwoDProfileMonitorClass";
    private static final String version = "1.0";

    /**
     * Converts the Edm_TwoDProfileMonitorClass to OPI Rectangle widget XML.
     */
    public Opi_TwoDProfileMonitorClass(Context con, Edm_TwoDProfileMonitorClass r) {
        super(con, r);
        setTypeId(typeId);
        setName(name);
        setVersion(version);

        new OpiInt(widgetContext, "maximum", 255);
        new OpiInt(widgetContext, "minimum", 0);
        new OpiBoolean(widgetContext, "rgb_mode", false);

        // No legend or axes on the TwoDProfileMonitor
        new OpiBoolean(widgetContext, "show_ramp", false);
        new OpiBoolean(widgetContext, "x_axis_visible", false);
        new OpiBoolean(widgetContext, "y_axis_visible", false);


        if(r.getDataPvStr()!=null)
            new OpiString(widgetContext, "pv_name", convertPVName(r.getDataPvStr()));

        if (r.getUseFalseColourPvStr() != null) {
            String useFalseColourPvStr = r.getUseFalseColourPvStr();
            if (useFalseColourPvStr.equals("0")) {
                new OpiString(widgetContext, "color_map", "GrayScale");
            } else if (useFalseColourPvStr.equals("1")) {
                new OpiString(widgetContext, "color_map", "JET");
            } else {
                createColourMapRule(r);
            }
        }

        if(r.isPvBasedDataSize() && r.getWidthPvStr() != null && r.getHeightPvStr() != null){
            createPVOutputRule(r, convertPVName(r.getWidthPvStr()), "data_width", "pv0", "DataWidthRule");
            createPVOutputRule(r, convertPVName(r.getHeightPvStr()), "data_height", "pv0", "DataHeightRule");
        }else{
            new OpiInt(widgetContext, "data_width", r.getDataWidth());
            new OpiInt(widgetContext, "data_height", Integer.parseInt(r.getHeightPvStr()));
        }

        log.config("Edm_activeRectangleClass written.");

    }

    /**
     * Create a rule that switches colour map based on value of the PV
     * specified in EDM.
     * @param r EdmWidget for 2D profile monitor
     */
    private void createColourMapRule(Edm_TwoDProfileMonitorClass r) {
        List<String> pvNames = Arrays.asList(convertPVName(r.getUseFalseColourPvStr()));
        LinkedHashMap<String, Element> expressions = new LinkedHashMap<>();
        Element jetNode = widgetContext.getDocument().createElement("value");
        jetNode.setTextContent("\"JET\"");
        expressions.put("pvInt0==1", jetNode);
        Element grayScaleNode = widgetContext.getDocument().createElement("value");
        grayScaleNode.setTextContent("\"GrayScale\"");
        expressions.put("true", grayScaleNode);
        new OpiRule(widgetContext, "ColourMapRule", "color_map", true, pvNames, expressions);
    }
}
