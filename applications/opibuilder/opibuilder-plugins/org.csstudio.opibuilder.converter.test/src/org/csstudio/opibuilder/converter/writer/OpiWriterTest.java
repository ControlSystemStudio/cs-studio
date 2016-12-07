/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import static org.junit.Assert.assertEquals;

import org.csstudio.opibuilder.converter.EdmConverter;
import org.csstudio.opibuilder.converter.EdmConverterTest;
import org.csstudio.opibuilder.converter.model.EdmColor;
import org.csstudio.opibuilder.converter.model.EdmException;
import org.junit.After;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OpiWriterTest {

    private String displayFile1 = EdmConverterTest.RESOURCES_LOCATION + "EDMDisplayParser_example.edl";
    private String xmlFile1 = EdmConverterTest.RESOURCES_LOCATION + "EDMDisplayParser_example.opi";
    private String displayFile2 = EdmConverterTest.RESOURCES_LOCATION + "TextUpdate_example.edl";
    private String xmlFile2 = EdmConverterTest.RESOURCES_LOCATION + "TextUpdate_example.opi";

    /**
     * Remove temporary files created by test.
     */
    @After
    public void tearDown() {
        EdmConverterTest.deleteFile(xmlFile1);
        EdmConverterTest.deleteFile(xmlFile2);
    }

    @Test
    public void testOpiWriter() throws EdmException {

        System.setProperty("edm2xml.robustParsing", "false");
        System.setProperty("edm2xml.colorsFile", EdmConverterTest.COLOR_LIST_FILE);

        OpiWriter o = OpiWriter.getInstance();
        o.writeDisplayFile(displayFile1);
        o.writeDisplayFile(displayFile2);
    }

    public void itXMLMapping() throws EdmException {

        System.setProperty("edm2xml.colorsFile", EdmConverterTest.COLOR_LIST_FILE);
        String[] args = { displayFile1 };
        EdmConverter.main(args);
        Document doc = XMLFileHandler.readXml(xmlFile1);

        if (doc != null) {

            Element e = (Element)doc.getElementsByTagName("display").item(0);
            assertEquals("org.csstudio.opibuilder.Display", e.getAttribute("typeId"));
            assertEquals("1.0", e.getAttribute("version"));
            XMLFileHandler.isElementEqual("614", "x", e);
            XMLFileHandler.isElementEqual("278", "y", e);
            XMLFileHandler.isElementEqual("280", "width", e);
            XMLFileHandler.isElementEqual("177", "height", e);
            XMLFileHandler.isFontElementEqual("helvetica-bold-r-14.0", "font", e);
            XMLFileHandler.isFontElementEqual("helvetica-bold-r-14.0", "font_ctl", e);
            XMLFileHandler.isFontElementEqual("helvetica-bold-r-14.0", "font_button", e);
            XMLFileHandler.isColorElementEqual(new EdmColor(14), "color_foreground", e);
            XMLFileHandler.isColorElementEqual(new EdmColor(3), "color_background", e);
            XMLFileHandler.isColorElementEqual(new EdmColor(14), "color_text", e);
            XMLFileHandler.isColorElementEqual("", 1, 2, 255, 0, 0, 0, "color_ctlFgColor1", e);
            XMLFileHandler.isColorElementEqual(new EdmColor(30), "color_ctlFgColor2", e);
            XMLFileHandler.isColorElementEqual(new EdmColor(3), "color_ctlBgColor1", e);
            XMLFileHandler.isColorElementEqual(new EdmColor(3), "color_ctlBgColor2", e);
            XMLFileHandler.isColorElementEqual(new EdmColor(1), "color_topshadowcolor", e);
            XMLFileHandler.isColorElementEqual(new EdmColor(11), "color_botshadowcolor", e);
            XMLFileHandler.isElementEqual("Motor control", "name", e);
            XMLFileHandler.isElementEqual("true", "grid_show", e);
            XMLFileHandler.isElementEqual("5", "grid_space", e);
            XMLFileHandler.isElementEqual("true", "scroll_disable", e);

            e = (Element)doc.getElementsByTagName("widget").item(0);
            assertEquals("org.csstudio.opibuilder.widgets.Rectangle", e.getAttribute("typeId"));
            assertEquals("1.0", e.getAttribute("version"));
            XMLFileHandler.isElementEqual("EDM Rectangle", "name", e);
            XMLFileHandler.isElementEqual("4", "x", e);
            XMLFileHandler.isElementEqual("45", "y", e);
            XMLFileHandler.isElementEqual("111", "width", e);
            XMLFileHandler.isElementEqual("42", "height", e);
            XMLFileHandler.isColorElementEqual(new EdmColor(7), "border_color", e);
            XMLFileHandler.isColorElementEqual(new EdmColor(0), "color_background", e);
            XMLFileHandler.isElementEqual("9", "border_style", e);
            XMLFileHandler.isElementEqual("2", "border_width", e);

            e = (Element)doc.getElementsByTagName("widget").item(1);
            assertEquals("org.csstudio.opibuilder.widgets.Rectangle", e.getAttribute("typeId"));
            assertEquals("1.0", e.getAttribute("version"));
            XMLFileHandler.isElementEqual("EDM Rectangle", "name", e);
            XMLFileHandler.isElementEqual("120", "x", e);
            XMLFileHandler.isElementEqual("45", "y", e);
            XMLFileHandler.isElementEqual("155", "width", e);
            XMLFileHandler.isElementEqual("42", "height", e);
            XMLFileHandler.isColorElementEqual(new EdmColor(7), "border_color", e);
            XMLFileHandler.isColorElementEqual(new EdmColor(0), "color_background", e);
            XMLFileHandler.isElementEqual("0", "border_style", e);

            e = (Element)doc.getElementsByTagName("widget").item(2);
            assertEquals("org.csstudio.opibuilder.widgets.Rectangle", e.getAttribute("typeId"));
            assertEquals("1.0", e.getAttribute("version"));
            XMLFileHandler.isElementEqual("4", "x", e);
            XMLFileHandler.isElementEqual("94", "y", e);
            XMLFileHandler.isElementEqual("271", "width", e);
            XMLFileHandler.isElementEqual("80", "height", e);
            XMLFileHandler.isColorElementEqual(new EdmColor(7), "border_color", e);
            XMLFileHandler.isColorElementEqual(new EdmColor(0), "color_background", e);
            XMLFileHandler.isElementEqual("0", "border_style", e);

            e = (Element)doc.getElementsByTagName("widget").item(3);
            assertEquals("org.csstudio.opibuilder.widgets.Label", e.getAttribute("typeId"));
            assertEquals("1.0", e.getAttribute("version"));
            XMLFileHandler.isElementEqual("EDM Label", "name", e);
            XMLFileHandler.isElementEqual("123", "x", e);
            XMLFileHandler.isElementEqual("50", "y", e);
            XMLFileHandler.isElementEqual("42", "width", e);
            XMLFileHandler.isElementEqual("13", "height", e);
            XMLFileHandler.isFontElementEqual("helvetica-bold-r-12.0", "font", e);
            XMLFileHandler.isColorElementEqual(new EdmColor(10), "color_foreground", e);
            XMLFileHandler.isColorElementEqual(new EdmColor(3), "color_background", e);
            XMLFileHandler.isElementEqual("At low", "text", e);
            XMLFileHandler.isElementEqual("true", "auto_size", e);
            XMLFileHandler.isElementEqual("1", "border_style", e);
            XMLFileHandler.isElementEqual("2", "border_width", e);
            XMLFileHandler.isElementEqual("true", "transparency", e);

            e = (Element)doc.getElementsByTagName("widget").item(4);
            assertEquals("org.csstudio.opibuilder.widgets.groupingContainer", e.getAttribute("typeId"));
            assertEquals("1.0", e.getAttribute("version"));
            XMLFileHandler.isElementEqual("8", "x", e);
            XMLFileHandler.isElementEqual("120", "y", e);
            XMLFileHandler.isElementEqual("52", "width", e);
            XMLFileHandler.isElementEqual("11", "height", e);

            e = (Element)doc.getElementsByTagName("widget").item(5);
            assertEquals("org.csstudio.opibuilder.widgets.Label", e.getAttribute("typeId"));
            assertEquals("1.0", e.getAttribute("version"));
            XMLFileHandler.isElementEqual("EDM Label", "name", e);
            XMLFileHandler.isElementEqual("18", "x", e);
            XMLFileHandler.isElementEqual("0", "y", e);
            XMLFileHandler.isElementEqual("35", "width", e);
            XMLFileHandler.isElementEqual("13", "height", e);
            XMLFileHandler.isFontElementEqual("helvetica-bold-r-12.0", "font", e);
            XMLFileHandler.isColorElementEqual(new EdmColor(10), "color_foreground", e);
            XMLFileHandler.isColorElementEqual(new EdmColor(3), "color_background", e);
            XMLFileHandler.isElementEqual("Hello\rMulti-line\rWorld", "text", e);
            XMLFileHandler.isElementEqual("true", "auto_size", e);
            XMLFileHandler.isElementEqual("0", "border_style", e);
            XMLFileHandler.isElementEqual("false", "transparency", e);
        }
    }

    @Test
    public void testXMLMapping2() throws EdmException {
    // test for TextUpdate widget mapping

        System.setProperty("edm2xml.colorsFile", EdmConverterTest.COLOR_LIST_FILE);
        String[] args = { displayFile2 };
        EdmConverter.main(args);
        Document doc = XMLFileHandler.readXml(xmlFile2);

        if (doc != null) {

            Element e = (Element)doc.getElementsByTagName("widget").item(0);
            assertEquals("org.csstudio.opibuilder.widgets.TextUpdate", e.getAttribute("typeId"));
            assertEquals("1.0", e.getAttribute("version"));

            XMLFileHandler.isElementEqual("EDM Text Update", "name", e);
            XMLFileHandler.isElementEqual("489", "x", e);
            XMLFileHandler.isElementEqual("399", "y", e);
            XMLFileHandler.isElementEqual("112", "width", e);
            XMLFileHandler.isElementEqual("22", "height", e);

            XMLFileHandler.isElementEqual("$(S)_LLRF:ResCtrl$(N):ResErr_Avg", "pv_name", e);

            XMLFileHandler.isColorElementEqual(new EdmColor(112), "foreground_color", e);
            XMLFileHandler.isColorElementEqual(new EdmColor(5), "background_color", e);
            //XMLFileHandler.isElementEqual("true", "color_fill", e);

            XMLFileHandler.isFontElementEqual("courier-medium-r-16.0", "font", e);
            //XMLFileHandler.isElementEqual("right", "font_align", e);

            //XMLFileHandler.isElementEqual("2", "border_width", e);
            //XMLFileHandler.isElementEqual("true", "foregroundcolor_alarmsensitive", e);
            //XMLFileHandler.isElementEqual("true", "border_alarm_sensitive", e);
        }
    }
}
