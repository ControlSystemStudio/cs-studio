/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.csstudio.opibuilder.converter.EdmConverterTest;
import org.csstudio.opibuilder.converter.model.EdmColor;
import org.csstudio.opibuilder.converter.model.EdmDisplay;
import org.csstudio.opibuilder.converter.model.EdmException;
import org.csstudio.opibuilder.converter.model.EdmModel;
import org.csstudio.opibuilder.converter.model.Edm_activeRectangleClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Opi_activeRectangleClassTest {

    @Test
    public void testOpi_activeRectangleClass() throws EdmException {

        System.setProperty("edm2xml.robustParsing", "false");
        System.setProperty("edm2xml.colorsFile", EdmConverterTest.COLOR_LIST_FILE);

        Document doc = XMLFileHandler.createDomDocument();
        Element root = doc.createElement("root");
        doc.appendChild(root);

        String edlFile = EdmConverterTest.RESOURCES_LOCATION + "EDMDisplayParser_example.edl";
        EdmModel.getInstance();
        EdmDisplay d = EdmModel.getDisplay(edlFile);
        assertTrue(d.getSubEntity(0) instanceof Edm_activeRectangleClass);
        Edm_activeRectangleClass r = (Edm_activeRectangleClass)d.getSubEntity(0);

        Context context = new Context(doc, root, d, 0, 0);
        Opi_activeRectangleClass o = new Opi_activeRectangleClass(context, r);
        assertTrue(o instanceof OpiWidget);

        Element e = (Element)doc.getElementsByTagName("widget").item(0);
        assertEquals("org.csstudio.opibuilder.widgets.Rectangle", e.getAttribute("typeId"));
        assertEquals("1.0", e.getAttribute("version"));

        XMLFileHandler.isElementEqual("EDM Rectangle", "name", e);

        XMLFileHandler.isElementEqual("3", "x", e);
        XMLFileHandler.isElementEqual("44", "y", e);
        XMLFileHandler.isElementEqual("113", "width", e);
        XMLFileHandler.isElementEqual("44", "height", e);

        XMLFileHandler.isColorElementEqual(new EdmColor(7), "line_color", e);
        XMLFileHandler.isColorElementEqual(new EdmColor(0), "background_color", e);

        XMLFileHandler.isElementEqual("false", "visible", e);

        //XMLFileHandler.writeXML(doc);
    }

}
