/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.annunciator.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.csstudio.utility.speech.Translation;
import org.junit.Test;

/** JUnit Test of the TranslationFileReader
 *  @author Katia Danilova
 *  @author Kay Kasemir
 *
 *       reviewed by Delphy 1/29/09
 */
@SuppressWarnings("nls")
public class TranslationFileReaderUnitTest
{
    /** Read translations from file, see if they match expected content */
    @Test
    public void testFileRead() throws Exception
    {
        final Translation translations[] =
            TranslationFileReader.getTranslations("../org.csstudio.alarm.beast.annunciator/translations.txt");
        assertEquals(10, translations.length);
        assertEquals("mebbit Vac error", translations[2].apply("MEBT Vac error"));
        assertEquals("MEBT vacuum error", translations[5].apply("MEBT Vac error"));
    }

    /** Check detection of missing file */
    @Test
    public void testFileError()
    {
        try
        {
            TranslationFileReader.getTranslations("not_there.txt");
        }
        catch (Exception ex)
        {
            System.out.println("OK, got exception ...." + ex.getMessage());
            return;
        }
        fail("Not exception?");
    }
}
