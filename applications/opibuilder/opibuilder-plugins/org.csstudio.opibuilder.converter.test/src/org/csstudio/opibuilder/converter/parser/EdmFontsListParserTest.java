/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.parser;

import static org.junit.Assert.assertEquals;

import org.csstudio.opibuilder.converter.EdmConverterTest;
import org.csstudio.opibuilder.converter.model.EdmException;
import org.junit.Test;

public class EdmFontsListParserTest {

    private String fontsFile = EdmConverterTest.RESOURCES_LOCATION + "fonts_example.list";

    @Test
    public void testEdmFontsListParser() throws EdmException {

        EdmFontsListParser p = new EdmFontsListParser(fontsFile);

        assertEquals("attribute_count", 2, p.getRoot().getAttributeCount());

        assertEquals("bold", p.getRoot().getAttribute("courier").getValue(0));
        assertEquals("r", p.getRoot().getAttribute("courier").getValue(1));
        assertEquals("12.0", p.getRoot().getAttribute("courier").getValue(2));

        assertEquals("bold", p.getRoot().getAttribute("helvetica").getValue(0));
        assertEquals("i", p.getRoot().getAttribute("helvetica").getValue(1));
        assertEquals("18.0", p.getRoot().getAttribute("helvetica").getValue(2));

    }
}
