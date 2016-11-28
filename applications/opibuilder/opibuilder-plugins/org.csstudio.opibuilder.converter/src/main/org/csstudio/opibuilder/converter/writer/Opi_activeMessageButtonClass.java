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

import org.csstudio.opibuilder.converter.model.EdmColor;
import org.csstudio.opibuilder.converter.model.EdmWidget;
import org.csstudio.opibuilder.converter.model.Edm_activeMessageButtonClass;
import org.w3c.dom.Element;

/**
 * XML conversion class for Edm_activeMessageButton
 *
 * @author Lei Hu, Xihui Chen, Will Rogers
 */
public class Opi_activeMessageButtonClass extends OpiWidget {

    private static final String typeId = "BoolButton";
    private static final String name = "EDM Message Button";
    private static final String version = "1.0";

    /**
     * Converts the Edm_activeMessageButtonClass to various OPI widgets.
     *
     * This is tricky to convert.  The natural conversion is to a BoolButton since
     * only that has actions on both push and release.  However, the BoolButton automatically
     * puts 1 and 0 to its PV on push and release, so unless the message button puts those
     * values BoolButton is not the correct widget.
     *  - if the message button puts 1 and 0, use a BoolButton
     *  - if the message button puts other values, use a BoolButton with explicit actions
     *   - this means that pv_name is not set and the button is not greyed out if the PV
     *     is read-only
     *  - if the message button only has action on push, use an action button
     *   - it is still not possible to change the text on push and release
     *  - if the message button is invisible, use an invisible rectangle
     *   - this can't do any action on release; if both actions are there the order
     *     is indeterminate
     */
    public Opi_activeMessageButtonClass(Context con, Edm_activeMessageButtonClass r) {
        super(con, r);
        setName(name);
        setVersion(version);

        // remember whether this is a BoolButton
        boolean isBoolButton = false;

        // Expand size by 1px to match EDM
        new OpiInt(widgetContext, "width", r.getW() + 1);
        new OpiInt(widgetContext, "height", r.getH() + 1);

        if (r.isInvisible()) {  // invisible message button
            setTypeId("Rectangle");
            new OpiString(widgetContext, "pv_name", convertPVName(r.getControlPv()));
            new OpiBoolean(widgetContext, "transparent", true);
            new OpiInt(widgetContext, "line_width", 0);
            createPutAction("$(pv_name)", r.getPressValue());
            createPutAction("$(pv_name)", r.getReleaseValue());
        } else if (r.getPressValue() == "1" && r.getReleaseValue() == "0") { // standard BoolButton
            setTypeId(typeId);
            isBoolButton = true;
            new OpiString(widgetContext, "pv_name", convertPVName(r.getControlPv()));
        } else if (r.getReleaseValue() != null) {  // non-standard bool button using actions
            setTypeId(typeId);
            isBoolButton = true;
            new OpiInt(widgetContext, "push_action_index", r.getPressValue()==null?1:0);
            new OpiInt(widgetContext, "released_action_index", r.getPressValue()==null?0:1);
            // For this widget we can't use $(pv_name) and have to pass the PV name itself
            createPutAction(r.getControlPv(), r.getPressValue());
            createPutAction(r.getControlPv(), r.getReleaseValue());
        } else if (r.getReleaseValue() == null) {  // no release action; use ActionButton
            setTypeId("ActionButton");
            new OpiString(widgetContext, "text", r.getOffLabel());
            new OpiString(widgetContext, "pv_name", convertPVName(r.getControlPv()));
            createPutAction("$(pv_name)", r.getPressValue());
        }

        if (r.getControlPv() != null) {
            createOnOffColorRule(r, r.getControlPv(), "background_color", r.getOnColor(),
                    r.getOffColor(), "OnOffBackgroundRule");
        }

        // Set BoolButton-specific options.
        if (isBoolButton) {
            new OpiString(widgetContext, "on_label", r.getOnLabel());
            new OpiString(widgetContext, "off_label", r.getOffLabel());
            new OpiBoolean(widgetContext, "show_led", false);
            new OpiBoolean(widgetContext, "show_boolean_label", true);
            new OpiBoolean(widgetContext, "square_button", true);
            new OpiColor(widgetContext, "on_color", r.getOnColor(), r);
            new OpiColor(widgetContext, "off_color", r.getOffColor(), r);

            if (r.getOnLabel() != null)
                new OpiString(widgetContext, "on_label", r.getOnLabel());
            if (r.getOffLabel() != null)
                new OpiString(widgetContext, "off_label", r.getOffLabel());

            new OpiBoolean(widgetContext, "toggle_button", r.isToggle());
            if (r.getPassword() != null)
                new OpiString(widgetContext, "password", r.getPassword());

            new OpiInt(widgetContext, "show_confirm_dialog", r.getPassword() != null ? 1 : 0);
        }
    }

    /**
     * Create a an action to write the specified PV to the specified value.
     * @param pvName
     * @param value
     */
    protected void createPutAction(String pvName, String value) {
        if (value == null) {
            // just don't add an action
            return;
        }
        Element pvNameNode = widgetContext.getDocument().createElement("pv_name");
        pvNameNode.setTextContent(pvName);
        Element valueNode = widgetContext.getDocument().createElement("value");
        valueNode.setTextContent(value);
        // Hook actions to click in case of invisible rectangle, which can't
        // handle mouseDown and mouseUp separately.
        new OpiAction(widgetContext, "WRITE_PV", Arrays.asList(pvNameNode, valueNode),
                true, true);
    }

    /**
     * Create a rule that make a color property alarm sensitive.
     *
     * @param edmWidgetClass
     * @param edmAlarmAttr
     * @param edmAlarmPVAttr
     * @param opiProperty
     */
    protected void createOnOffColorRule(EdmWidget edmWidgetClass, String pvName,
            String opiProperty, EdmColor onColor, EdmColor offColor, String ruleName) {
        LinkedHashMap<String, Element> expressions = new LinkedHashMap<String, Element>();
        Element valueNode;
        Element colorNode;
        valueNode = widgetContext.getDocument().createElement("value");
        colorNode = widgetContext.getDocument().createElement("color");
        colorNode.setAttribute("name", onColor.getName());
        colorNode.setAttribute("red", "" + OpiColor.colorComponentTo8Bits(onColor.getRed()));
        colorNode.setAttribute("green", "" + OpiColor.colorComponentTo8Bits(onColor.getGreen()));
        colorNode.setAttribute("blue", "" + OpiColor.colorComponentTo8Bits(onColor.getBlue()));
        valueNode.appendChild(colorNode);
        expressions.put("widget.getValue() == 1", valueNode);

        valueNode = widgetContext.getDocument().createElement("value");
        colorNode = widgetContext.getDocument().createElement("color");
        colorNode.setAttribute("name", offColor.getName());
        colorNode.setAttribute("red", "" + OpiColor.colorComponentTo8Bits(offColor.getRed()));
        colorNode.setAttribute("green", "" + OpiColor.colorComponentTo8Bits(offColor.getGreen()));
        colorNode.setAttribute("blue", "" + OpiColor.colorComponentTo8Bits(offColor.getBlue()));
        valueNode.appendChild(colorNode);
        expressions.put("true", valueNode);

        new OpiRule(widgetContext, ruleName, opiProperty, false, Arrays.asList(pvName), expressions);
    }

}
