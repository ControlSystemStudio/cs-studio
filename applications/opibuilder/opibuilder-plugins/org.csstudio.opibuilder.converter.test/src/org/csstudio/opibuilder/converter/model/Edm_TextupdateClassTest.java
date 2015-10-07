/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.csstudio.opibuilder.converter.EdmConverterTest;
import org.junit.Test;

public class Edm_TextupdateClassTest {

    @Test
    public void testEdm_TextupdateClass() throws EdmException {

        System.setProperty("edm2xml.robustParsing", "false");
        System.setProperty("edm2xml.colorsFile", EdmConverterTest.COLOR_LIST_FILE);

        String edlFile = EdmConverterTest.RESOURCES_LOCATION + "TextUpdate_example.edl";
        EdmModel.getInstance();
        EdmDisplay d = EdmModel.getDisplay(edlFile);

        EdmEntity e = d.getSubEntity(0);
        assertTrue(e instanceof Edm_TextupdateClass);
        Edm_TextupdateClass t = (Edm_TextupdateClass)e;

        assertEquals(10, t.getMajor());
        assertTrue(t.getAttribute("major") instanceof EdmInt);
        assertEquals(0, t.getMinor());
        assertTrue(t.getAttribute("minor") instanceof EdmInt);
        assertEquals(0, t.getRelease());
        assertTrue(t.getAttribute("release") instanceof EdmInt);
        assertEquals(490, t.getX());
        assertTrue(t.getAttribute("x") instanceof EdmInt);
        assertEquals(400, t.getY());
        assertTrue(t.getAttribute("y") instanceof EdmInt);
        assertEquals(110, t.getW());
        assertTrue(t.getAttribute("w") instanceof EdmInt);
        assertEquals(20, t.getH());
        assertTrue(t.getAttribute("h") instanceof EdmInt);

        assertEquals("$(S)_LLRF:ResCtrl$(N):ResErr_Avg", t.getControlPv());
        assertTrue(t.getAttribute("controlPv") instanceof EdmString);

        EdmComparatorTest.isColorEqual(new EdmColor(112), t.getFgColor());
        assertTrue(t.getAttribute("fgColor") instanceof EdmColor);
        EdmComparatorTest.isColorEqual(new EdmColor(5), t.getBgColor());
        assertTrue(t.getAttribute("bgColor") instanceof EdmColor);
        assertEquals(true, t.isFill());
        assertTrue(t.getAttribute("fill") instanceof EdmBoolean);

        EdmComparatorTest.isFontEqual("courier-medium-r-16.0", t.getFont());
        assertTrue(t.getAttribute("font") instanceof EdmFont);
        assertEquals("right", t.getFontAlign());
        assertTrue(t.getAttribute("fontAlign") instanceof EdmString);

        assertEquals(2, t.getLineWidth());
        assertTrue(t.getAttribute("lineWidth") instanceof EdmInt);
        assertEquals(true, t.isLineAlarm());
        assertTrue(t.getAttribute("lineAlarm") instanceof EdmBoolean);
        assertEquals(true, t.isFgAlarm());
        assertTrue(t.getAttribute("fgAlarm") instanceof EdmBoolean);
    }
}
