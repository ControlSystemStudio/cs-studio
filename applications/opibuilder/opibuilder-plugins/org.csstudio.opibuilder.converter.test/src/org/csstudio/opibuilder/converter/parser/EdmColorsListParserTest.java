/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.csstudio.opibuilder.converter.EdmConverterTest;
import org.csstudio.opibuilder.converter.model.EdmColor;
import org.csstudio.opibuilder.converter.model.EdmException;
import org.junit.Test;

public class EdmColorsListParserTest {

    private String colorsFile = EdmConverterTest.RESOURCES_LOCATION + "colors_example.list";
    private String colorsError1 = EdmConverterTest.RESOURCES_LOCATION + "colors_error1.list";
    private String colorsError2 = EdmConverterTest.RESOURCES_LOCATION + "colors_error2.list";
    private String colorsError3 = EdmConverterTest.RESOURCES_LOCATION + "colors_error3.list";
    private String colorsError4 = EdmConverterTest.RESOURCES_LOCATION + "colors_error4.list";
    private String colorsRobust = EdmConverterTest.RESOURCES_LOCATION + "colors_robust.list";

    @Test
    public void testEdmColorsListParser() throws EdmException {

        EdmColorsListParser p = new EdmColorsListParser(colorsFile);

        assertEquals("attribute_count", 7, p.getRoot().getAttributeCount());

        assertEquals(EdmColor.STATIC, p.getRoot().getAttribute("0").getValue(0));
        assertEquals("Disconn/Invalid", p.getRoot().getAttribute("0").getValue(1));
        assertEquals("65535 65535 65535", p.getRoot().getAttribute("0").getValue(2));

        assertEquals(EdmColor.STATIC, p.getRoot().getAttribute("107").getValue(0));
        assertEquals("Top Shadow", p.getRoot().getAttribute("107").getValue(1));
        assertEquals("60652 60652 60652", p.getRoot().getAttribute("107").getValue(2));

        assertEquals(EdmColor.STATIC, p.getRoot().getAttribute("5").getValue(0));
        assertEquals("Wid-alt/Anno-sec", p.getRoot().getAttribute("5").getValue(1));
        assertEquals("44718 44718 44718", p.getRoot().getAttribute("5").getValue(2));

        assertEquals(EdmColor.STATIC, p.getRoot().getAttribute("6").getValue(0));
        assertEquals("GLOBAL title", p.getRoot().getAttribute("6").getValue(1));
        assertEquals("40606 40606 40606", p.getRoot().getAttribute("6").getValue(2));

        assertEquals(EdmColor.STATIC, p.getRoot().getAttribute("112").getValue(0));
        assertEquals("black", p.getRoot().getAttribute("112").getValue(1));
        assertEquals("0 0 0", p.getRoot().getAttribute("112").getValue(2));

        assertEquals(EdmColor.STATIC, p.getRoot().getAttribute("142").getValue(0));
        assertEquals("blinking purple", p.getRoot().getAttribute("142").getValue(1));
        assertEquals("49344 0 49344", p.getRoot().getAttribute("142").getValue(2));
        assertEquals("0 0 0", p.getRoot().getAttribute("142").getValue(3));

        assertEquals(EdmColor.STATIC, p.getRoot().getAttribute("155").getValue(0));
        assertEquals("bl in ki ng purple", p.getRoot().getAttribute("155").getValue(1));
        assertEquals("7852 4427 450", p.getRoot().getAttribute("155").getValue(2));
        assertEquals("1 786 5", p.getRoot().getAttribute("155").getValue(3));
    }

    @Test
    public void testNoStartQuoteError() {
        try {
            new EdmColorsListParser(colorsError1);
        } catch (EdmException e) {
            assertEquals(EdmException.STRING_FORMAT_ERROR, e.getType());
            assertTrue(e.getMessage().startsWith(
                    "STRING_FORMAT_ERROR exception: String value does not start with quote at line:"));
        }
    }

    @Test
    public void testNoFinalQuoteError() {
        try {
            new EdmColorsListParser(colorsError2);
        } catch (EdmException e) {
            assertEquals(EdmException.STRING_FORMAT_ERROR, e.getType());
            assertTrue(e.getMessage().startsWith(
                    "STRING_FORMAT_ERROR exception: String value does not end with quote at line:"));
        }
    }

    @Test
    public void testInvalidNumberFormat() {
        try {
            new EdmColorsListParser(colorsError3);
        } catch (EdmException e) {
            assertEquals(EdmException.COLOR_FORMAT_ERROR, e.getType());
            assertTrue(e.getMessage().startsWith(
                    "COLOR_FORMAT_ERROR exception: Wrong color input at line: "));
        }
    }

    @Test
    public void testInvalidColorFormat() {
        try {
            new EdmColorsListParser(colorsError4);
        } catch (EdmException e) {
            assertEquals(EdmException.COLOR_FORMAT_ERROR, e.getType());
            assertTrue(e.getMessage().startsWith(
                    "COLOR_FORMAT_ERROR exception: Wrong color input at line: "));
        }
    }

    // !!!!!!!!!!!! FOR THIS TEST TO PASS, SYSTEM PROPERTY "robustParsing" MUST BE SET TO "true"!
    @Test
    public void testRobustness() throws EdmException {

        System.setProperty("edm2xml.robustParsing", "true");

        EdmColorsListParser p = new EdmColorsListParser(colorsRobust);

        assertEquals("attribute_count", 3, p.getRoot().getAttributeCount());

        assertEquals(EdmColor.STATIC, p.getRoot().getAttribute("0").getValue(0));
        assertEquals("Disconn/Invalid", p.getRoot().getAttribute("0").getValue(1));
        assertEquals("65535 65535 65535", p.getRoot().getAttribute("0").getValue(2));

        assertEquals(EdmColor.STATIC, p.getRoot().getAttribute("142").getValue(0));
        assertEquals("blinking purple", p.getRoot().getAttribute("142").getValue(1));
        assertEquals("49344 0 49344", p.getRoot().getAttribute("142").getValue(2));
        assertEquals("0 0 0", p.getRoot().getAttribute("142").getValue(3));

        assertEquals(EdmColor.STATIC, p.getRoot().getAttribute("155").getValue(0));
        assertEquals("bl in ki ng purple", p.getRoot().getAttribute("155").getValue(1));
        assertEquals("7852 4427 450", p.getRoot().getAttribute("155").getValue(2));
        assertEquals("1 786 5", p.getRoot().getAttribute("155").getValue(3));
    }
}
