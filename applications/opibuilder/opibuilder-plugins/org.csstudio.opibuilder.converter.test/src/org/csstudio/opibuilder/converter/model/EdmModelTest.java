/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.model;

import org.csstudio.opibuilder.converter.EdmConverterTest;

import junit.framework.TestCase;

public class EdmModelTest extends TestCase {

    private String displayFile2 = EdmConverterTest.RESOURCES_LOCATION + "LLRF_AUTO.edl";

    public void testEdmModel() throws EdmException {

        System.setProperty("edm2xml.robustParsing", "false");
        System.setProperty("edm2xml.colorsFile", EdmConverterTest.COLOR_LIST_FILE);

        EdmModel.getInstance();
        EdmModel.getDisplay(displayFile2);

    }
}
