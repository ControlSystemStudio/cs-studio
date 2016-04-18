/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable;

import static org.csstudio.display.pvtable.FileTestUtil.linesInFile;
import static org.csstudio.display.pvtable.FileTestUtil.matchLinesIn;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.Arrays;

import org.csstudio.display.pvtable.model.PVTableModel;
import org.csstudio.display.pvtable.model.SavedArrayValue;
import org.csstudio.display.pvtable.model.SavedScalarValue;
import org.csstudio.display.pvtable.persistence.PVTablePersistence;
import org.csstudio.display.pvtable.persistence.PVTableXMLPersistence;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/** JUnit test of {@link PVTableXMLPersistence}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVTableXMLPersistenceTest
{
    @Before
    public void setup()
    {
        TestSettings.setup();
    }

    @Test
    public void testReadXML() throws Exception
    {
        final PVTablePersistence persistence = new PVTableXMLPersistence();
        final PVTableModel model = persistence.read(new FileInputStream("lib/example.pvs"));

        assertThat(model.getItemCount(), equalTo(53));

        assertThat(model.getItem(0).getName(), equalTo(TestSettings.NAME));
        assertThat(model.getItem(0).getSavedValue().get().toString(), equalTo("3.14"));

        assertThat(model.getItem(1).getName(), equalTo("loc://array(1.0, 2.0, 3.0)"));
        assertThat(model.getItem(1).getSavedValue().get().toString(), equalTo("1.0, 2.0, 3.0"));

        model.dispose();
    }

    @Test
    public void testWriteXML() throws Exception
    {
        final PVTablePersistence persistence = new PVTableXMLPersistence();
        final PVTableModel model = new PVTableModel();
        model.addItem(TestSettings.NAME, 0.1, new SavedScalarValue("3.14"), null, false, null);
        model.addItem("test_array", 0.1, new SavedArrayValue(Arrays.asList("3.14", "314")), null, false, null);

        final ByteArrayOutputStream buf = new ByteArrayOutputStream();
        persistence.write(model, buf);
        final String xml = buf.toString();
        System.out.println(xml);
        model.dispose();

        assertThat(xml, containsString("<pvtable"));
        assertThat(xml, containsString("<pv>"));
        assertThat(xml, containsString("<name>"+TestSettings.NAME+"</name>"));
        assertThat(xml, containsString("<saved_value>3.14</saved_value>"));
        assertThat(xml, containsString("<item>314</item>"));
    }

    @Test
    @Ignore // TODO update to handle the new file format which includes measure
    public void compareFiles() throws Exception
    {
        final PVTablePersistence persistence = new PVTableXMLPersistence();
        final PVTableModel model = persistence.read(new FileInputStream("lib/test.pvs"));
        persistence.write(model, "/tmp/compare.sav");
        model.dispose();

        String[] original = linesInFile("lib/test.pvs");
        String[] copy = linesInFile("/tmp/compare.sav");
        assertThat(original, matchLinesIn(copy));
    }
}
