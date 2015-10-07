/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.csstudio.opibuilder.converter.EdmConverterTest;
import org.junit.Test;

public class Edm_activeXTextClassTest {

    @Test
    public void testEdm_activeXTextClass() throws EdmException {

        System.setProperty("edm2xml.robustParsing", "false");
        System.setProperty("edm2xml.colorsFile", EdmConverterTest.COLOR_LIST_FILE);

        String edlFile = EdmConverterTest.RESOURCES_LOCATION + "EDMDisplayParser_example.edl";
        EdmModel.getInstance();
        EdmDisplay d = EdmModel.getDisplay(edlFile);

        EdmEntity e = d.getSubEntity(6);
        assertTrue(e instanceof Edm_activeXTextClass);
        Edm_activeXTextClass t = (Edm_activeXTextClass)e;

        assertEquals(4, t.getMajor());
        assertTrue(t.getAttribute("major") instanceof EdmInt);
        assertEquals(1, t.getMinor());
        assertTrue(t.getAttribute("minor") instanceof EdmInt);
        assertEquals(0, t.getRelease());
        assertTrue(t.getAttribute("release") instanceof EdmInt);
        assertEquals(123, t.getX());
        assertTrue(t.getAttribute("x") instanceof EdmInt);
        assertEquals(50, t.getY());
        assertTrue(t.getAttribute("y") instanceof EdmInt);
        assertEquals(42, t.getW());
        assertTrue(t.getAttribute("w") instanceof EdmInt);
        assertEquals(13, t.getH());
        assertTrue(t.getAttribute("h") instanceof EdmInt);

        EdmComparatorTest.isFontEqual("helvetica-bold-r-12.0", t.getFont());
        assertTrue(t.getAttribute("font") instanceof EdmFont);

        EdmComparatorTest.isColorEqual(new EdmColor(10), t.getFgColor());
        assertTrue(t.getAttribute("fgColor") instanceof EdmColor);
        EdmComparatorTest.isColorEqual(new EdmColor(3), t.getBgColor());
        assertTrue(t.getAttribute("bgColor") instanceof EdmColor);

        assertEquals("At low", t.getValue().get());
        assertTrue(t.getAttribute("value") instanceof EdmMultilineText);
        assertEquals(true, t.isAutoSize());
        assertTrue(t.getAttribute("autoSize") instanceof EdmBoolean);

        assertEquals(true, t.isBorder());
        assertTrue(t.getAttribute("border") instanceof EdmBoolean);
        assertEquals(2, t.getLineWidth());
        assertTrue(t.getAttribute("lineWidth") instanceof EdmInt);
        assertEquals(true, t.isUseDisplayBg());
        assertTrue(t.getAttribute("useDisplayBg") instanceof EdmBoolean);

        assertEquals("$(S)_LLRF:FCM$(N):cavAmpCheck.SEVR", t.getVisPv());
        assertTrue(t.getAttribute("visPv") instanceof EdmString);
        assertEquals(-1.1, t.getVisMin(), 0.01);
        assertTrue(t.getAttribute("visMin") instanceof EdmDouble);
        assertEquals(10.78, t.getVisMax(), 0.01);
        assertTrue(t.getAttribute("visMax") instanceof EdmDouble);
        assertTrue(t.isVisInvert());
        assertTrue(t.getAttribute("visInvert") instanceof EdmBoolean);

        e = d.getSubEntity(7).getSubEntity(0);
        assertTrue(e instanceof Edm_activeXTextClass);
        t = (Edm_activeXTextClass)e;

        assertEquals(4, t.getMajor());
        assertTrue(t.getAttribute("major") instanceof EdmInt);
        assertEquals(1, t.getMinor());
        assertTrue(t.getAttribute("minor") instanceof EdmInt);
        assertEquals(0, t.getRelease());
        assertTrue(t.getAttribute("release") instanceof EdmInt);
        assertEquals(26, t.getX());
        assertTrue(t.getAttribute("x") instanceof EdmInt);
        assertEquals(120, t.getY());
        assertTrue(t.getAttribute("y") instanceof EdmInt);
        assertEquals(35, t.getW());
        assertTrue(t.getAttribute("w") instanceof EdmInt);
        assertEquals(13, t.getH());
        assertTrue(t.getAttribute("h") instanceof EdmInt);

        EdmComparatorTest.isFontEqual("helvetica-bold-r-12.0", t.getFont());
        assertTrue(t.getAttribute("font") instanceof EdmFont);

        EdmComparatorTest.isColorEqual(new EdmColor(10), t.getFgColor());
        assertTrue(t.getAttribute("fgColor") instanceof EdmColor);
        EdmComparatorTest.isColorEqual(new EdmColor(3), t.getBgColor());
        assertTrue(t.getAttribute("bgColor") instanceof EdmColor);

        assertEquals("Hello\rMulti-line\rWorld", t.getValue().get());
        assertTrue(t.getAttribute("value") instanceof EdmMultilineText);
        assertEquals(true, t.isAutoSize());
        assertTrue(t.getAttribute("autoSize") instanceof EdmBoolean);

        assertEquals(false, t.isBorder());
        assertTrue(t.getAttribute("border") instanceof EdmBoolean);

        assertFalse(t.getAttribute("lineWidth").isInitialized());

        assertEquals(false, t.isUseDisplayBg());
        assertTrue(t.getAttribute("useDisplayBg") instanceof EdmBoolean);
    }
}
