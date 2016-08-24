/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.csstudio.opibuilder.converter.EdmConverterTest;
import org.csstudio.opibuilder.converter.model.EdmColor;
import org.csstudio.opibuilder.converter.model.EdmDisplay;
import org.csstudio.opibuilder.converter.model.EdmException;
import org.csstudio.opibuilder.converter.model.EdmModel;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OpiDisplayTest {

    // <display typeId="org.csstudio.opibuilder.Display" ... >


    @Test
    public void testOpiDisplay() throws EdmException {

        System.setProperty("edm2xml.robustParsing", "false");
        System.setProperty("edm2xml.colorsFile", EdmConverterTest.COLOR_LIST_FILE);

        // init document
        Document doc = XMLFileHandler.createDomDocument();

        String edlFile = EdmConverterTest.RESOURCES_LOCATION + "EDMDisplayParser_example.edl";
        EdmModel.getInstance();
        EdmDisplay display = EdmModel.getDisplay(edlFile);

        new OpiDisplay(doc, display, edlFile);

        Element e = (Element)doc.getElementsByTagName("display").item(0);
        assertEquals("org.csstudio.opibuilder.Display", e.getAttribute("typeId"));
        assertEquals("1.0", e.getAttribute("version"));

        XMLFileHandler.isElementEqual("614", "x", e);
        XMLFileHandler.isElementEqual("278", "y", e);
        XMLFileHandler.isElementEqual("280", "width", e);
        XMLFileHandler.isElementEqual("177", "height", e);

        XMLFileHandler.isFontElementEqual("helvetica-bold-r-14.0", "font", e);

        XMLFileHandler.isColorElementEqual(new EdmColor(14), "foreground_color", e);
        XMLFileHandler.isColorElementEqual(new EdmColor(3), "background_color", e);

        XMLFileHandler.isElementEqual("Motor control", "name", e);
        XMLFileHandler.isElementEqual("true", "show_grid", e);
        XMLFileHandler.isElementEqual("5", "grid_space", e);

        //XMLFileHandler.writeXML(doc);
    }

    @Test
    public void testOptionality() throws EdmException {

        System.setProperty("edm2xml.robustParsing", "false");
        System.setProperty("edm2xml.colorsFile", EdmConverterTest.COLOR_LIST_FILE);

        // init document
        Document doc = XMLFileHandler.createDomDocument();

        String edlFile = EdmConverterTest.RESOURCES_LOCATION + "EDMDisplay_optionals.edl";
        EdmModel.getInstance();
        EdmDisplay d = EdmModel.getDisplay(edlFile);

        new OpiDisplay(doc, d, edlFile);
        //try {
        //    XMLFileHandler.printDocument(doc, System.out);
        //} catch (IOException | TransformerException e1) {
            // TODO Auto-generated catch block
        //    e1.printStackTrace();
        //}

        Element e = (Element)doc.getElementsByTagName("display").item(0);
        assertEquals("org.csstudio.opibuilder.Display", e.getAttribute("typeId"));
        assertEquals("1.0", e.getAttribute("version"));

        XMLFileHandler.isElementEqual("614", "x", e);
        XMLFileHandler.isElementEqual("278", "y", e);
        XMLFileHandler.isElementEqual("280", "width", e);
        XMLFileHandler.isElementEqual("177", "height", e);

        XMLFileHandler.isFontElementEqual("helvetica-bold-r-14.0", "font", e);

        XMLFileHandler.isColorElementEqual(new EdmColor(14), "foreground_color", e);
        XMLFileHandler.isColorElementEqual(new EdmColor(3), "background_color", e);

        // should be missing!
        //assertFalse(XMLFileHandler.isChildElement("name", e));
        XMLFileHandler.isElementEqual("true", "show_grid", e);
        // should be missing!
        assertFalse(XMLFileHandler.isChildElement("grid_space", e));
        {
            e = (Element)doc.getElementsByTagName("widget").item(0);
            assertEquals("org.csstudio.opibuilder.widgets.Rectangle", e.getAttribute("typeId"));
            assertEquals("1.0", e.getAttribute("version"));

            XMLFileHandler.isElementEqual("EDM Rectangle", "name", e);

            XMLFileHandler.isElementEqual("4", "x", e);
            XMLFileHandler.isElementEqual("45", "y", e);
            XMLFileHandler.isElementEqual("112", "width", e);
            XMLFileHandler.isElementEqual("43", "height", e);

            XMLFileHandler.isColorElementEqual(new EdmColor(7), "line_color", e);
            // should be missing!
            XMLFileHandler.isElementEqual("0", "line_style", e);
        }

        {
            e = (Element)doc.getElementsByTagName("widget").item(2);
            assertEquals("org.csstudio.opibuilder.widgets.TextUpdate", e.getAttribute("typeId"));
            assertEquals("1.0", e.getAttribute("version"));

            //XMLFileHandler.writeXML(doc);

            XMLFileHandler.isElementEqual("EDM Text Update", "name", e);
            XMLFileHandler.isElementEqual("490", "x", e);
            XMLFileHandler.isElementEqual("400", "y", e);
            XMLFileHandler.isElementEqual("111", "width", e);
            XMLFileHandler.isElementEqual("21", "height", e);

            XMLFileHandler.isElementEqual("$(S)_LLRF:ResCtrl$(N):ResErr_Avg", "pv_name", e);

            XMLFileHandler.isColorElementEqual(new EdmColor(112), "foreground_color", e);
            XMLFileHandler.isColorElementEqual(new EdmColor(5), "background_color", e);

            XMLFileHandler.isFontElementEqual("courier-medium-r-16.0", "font", e);
            XMLFileHandler.isElementEqual("false", "border_alarm_sensitive", e);
        }
    }
}
