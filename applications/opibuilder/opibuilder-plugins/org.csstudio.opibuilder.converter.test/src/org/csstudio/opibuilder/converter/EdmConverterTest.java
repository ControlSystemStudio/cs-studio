/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter;

import java.io.File;

import org.csstudio.opibuilder.converter.model.EdmException;
import org.junit.Before;
import org.junit.Test;
/**
 * This is a convenience operation test that transforms the example files.
 * It does not perform any assertions so it will only fail if an exception is thrown
 * during conversion.
 */
public class EdmConverterTest {

    public static final String RESOURCES_LOCATION = "src/resources/";
    private static final String edl1 = RESOURCES_LOCATION + "ArcTest.edl";
    private static final String edl2 = RESOURCES_LOCATION + "LLRF_AUTO.edl";
    private static final String edl3 = RESOURCES_LOCATION + "navwogif.edl";
    private static final String edl4 = RESOURCES_LOCATION + "rccsWaterSkid.edl";

    public static final String COLOR_LIST_FILE = RESOURCES_LOCATION + "colors.list";

    @Before
    public void setEnvironment() {
        System.setProperty("edm2xml.colorsFile", COLOR_LIST_FILE);
        /**
         * Enable fail-fast mode for stricter tests.
         * Set this to true for the partial conversion in case of exceptions.
         */
        System.setProperty("edm2xml.robustParsing", "false");
    }

    @Test
    public void testExampleEDL1() throws EdmException {
        try {
            String[] args = {edl1};
            EdmConverter.main(args);
        } finally {
            deleteFile(edlToOpi(edl1));
        }
    }

    @Test
    public void testExampleEDL2() throws EdmException {
        try {
            String[] args = {edl2};
            EdmConverter.main(args);
        } finally {
            deleteFile(edlToOpi(edl2));
        }
    }

    @Test
    public void testExampleEDL3() throws EdmException {
        try {
            String[] args = {edl3};
            EdmConverter.main(args);
        } finally {
            deleteFile(edlToOpi(edl3));
        }
    }

    @Test
    public void testExampleEDL4() throws EdmException {
        try {
            String[] args = {edl4};
            EdmConverter.main(args);
        } finally {
            deleteFile(edlToOpi(edl4));
        }
    }

    public static String edlToOpi(String filename) {
        if (filename.endsWith("edl")) {
            return filename.substring(0, filename.length() -3 ) + "opi";
        } else {
            throw new IllegalArgumentException(filename + " is not an edl file.");
        }
    }

   public static void deleteFile(String filename) {
       File f = new File(filename);
       f.delete();
   }
}
