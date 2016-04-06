/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.imports;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.List;

import org.csstudio.archive.vtype.VTypeHelper;
import org.diirt.vtype.VType;
import org.junit.Test;

/** JUnit test of the {@link CSVSampleImporter}
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CSVSampleImporterUnitTest
{
    @Test
    public void testCSVImport() throws Exception
    {
        final InputStream input = getClass().getResourceAsStream("Lakeshore_A_9_2011.xml");
        final SampleImporter importer = new CSVSampleImporter();

        final List<VType> values = importer.importValues(input);
        assertTrue(values.size() > 0);
        for (VType value : values)
            System.out.println(VTypeHelper.toString(value));
        final String text = VTypeHelper.toString(values.get(values.size()-1));
        assertThat(text, containsString("2011-09-13"));
        assertThat(text, containsString("08:57:44.968"));
        assertThat(text, containsString("84.912"));
    }
}
