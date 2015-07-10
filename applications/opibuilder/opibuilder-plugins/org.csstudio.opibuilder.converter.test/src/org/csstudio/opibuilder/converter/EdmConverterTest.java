/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter;

import junit.framework.TestCase;

import org.csstudio.opibuilder.converter.model.EdmException;
/**
 * This is a convenience operation test that transforms the example files.
 * It does not perform any assertions so it will only fail if an exception is thrown
 * during conversion.
 */
public class EdmConverterTest extends TestCase {

    public static final String RESOURCES_LOCATION = "src/resources/";
    private static final String edl1 = RESOURCES_LOCATION + "ArcTest.edl";
    private static final String edl2 = RESOURCES_LOCATION + "LLRF_AUTO.edl";
    private static final String edl3 = RESOURCES_LOCATION + "navwogif.edl";
    private static final String edl4 = RESOURCES_LOCATION + "rccsWaterSkid.edl";

    public static final String COLOR_LIST_FILE = RESOURCES_LOCATION + "colors.list";

    private void setEnvironment() {
        System.setProperty("edm2xml.colorsFile", COLOR_LIST_FILE);
        /**
         * Enable fail-fast mode for stricter tests.
         * Set this to true for the partial conversion in case of exceptions.
         */
        System.setProperty("edm2xml.robustParsing", "false");
    }

    public void testExampleEDL1() throws EdmException {
        setEnvironment();

        String[] args = {edl1};
        EdmConverter.main(args);
    }

    public void testExampleEDL2() throws EdmException {
        setEnvironment();

        String[] args = {edl2};
        EdmConverter.main(args);
    }

    public void testExampleEDL3() throws EdmException {
        setEnvironment();

        String[] args = {edl3};
        EdmConverter.main(args);
    }

    public void testExampleEDL4() throws EdmException {
        setEnvironment();

        String[] args = {edl4};
        EdmConverter.main(args);
    }
}
