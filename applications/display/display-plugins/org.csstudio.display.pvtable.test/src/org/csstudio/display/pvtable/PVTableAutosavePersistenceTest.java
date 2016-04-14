/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
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
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.FileInputStream;

import org.csstudio.display.pvtable.model.PVTableItem;
import org.csstudio.display.pvtable.model.PVTableModel;
import org.csstudio.display.pvtable.model.SavedValue;
import org.csstudio.display.pvtable.persistence.PVTableAutosavePersistence;
import org.csstudio.display.pvtable.persistence.PVTablePersistence;
import org.junit.Before;
import org.junit.Test;

/** JUnit test of {@link PVTableAutosavePersistence}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVTableAutosavePersistenceTest
{
    @Before
    public void setup()
    {
        TestSettings.setup();
    }

    @Test
    public void testReadAutosave() throws Exception
    {
        final PVTablePersistence persistence = new PVTableAutosavePersistence();
        final PVTableModel model = persistence.read(new FileInputStream("lib/auto.sav"));
        for (int i=0; i<model.getItemCount(); ++i)
        {
            PVTableItem item = model.getItem(i);
            System.out.println(item.getName() + " = " + item.getSavedValue());
        }
        assertThat(model.getItemCount(), equalTo(83));

        assertThat(model.getItem(0).getName(), equalTo("BL7:test_ai.VAL"));
        assertThat(model.getItem(0).getSavedValue().get().toString(), equalTo("3.16"));

        assertThat(model.getItem(3).getName(), equalTo("BL7:test_ai.DESC"));
        assertThat(model.getItem(3).getSavedValue().get().toString(), equalTo("Howdy?"));

        assertThat(model.getItem(4).getName(), equalTo("BL7:test_ls.VAL"));
        assertThat(model.getItem(4).getSavedValue().get().toString(), equalTo("51, 32, 34, 71, 117, 121, 115, 63, 0"));

        assertThat(model.getItem(6).getName(), equalTo("DTL_LLRF:IOC1:vxiRead0.A"));
        assertThat(model.getItem(6).getSavedValue().get().toString(), equalTo("212"));
        model.dispose();
    }

    @Test
    public void testArrayValueParser() throws Exception
    {
        final PVTableAutosavePersistence persistence = new PVTableAutosavePersistence();
        SavedValue value = persistence.parseValue("3.14");
        assertThat(value.toString(), equalTo("3.14"));

        value = persistence.parseValue("@array@ { \"72\" \"101\" \"10\\8\" \"108\" \"111\" \"0\" }");
        assertThat(value.toString(), equalTo("72, 101, 108, 108, 111, 0"));

        value = persistence.parseValue("@array@ { \"Fred\" \"Jane\" \"2 \\\"Guys\\\"\" \"108\" \"111\" \"0\" }");
        assertThat(value.toString(), equalTo("Fred, Jane, 2 \"Guys\", 108, 111, 0"));

        value = persistence.parseValue("@array@ {  }");
        assertThat(value.toString(), equalTo(""));

        try
        {
            persistence.parseValue("@array@ { \" }");
            fail("Did not catch missing item end marker");
        }
        catch (Exception ex)
        {
            assertThat(ex.getMessage(), containsString("Missing end"));
        }
    }

    @Test
    public void compareFiles() throws Exception
    {
        final PVTablePersistence persistence = new PVTableAutosavePersistence();
        final PVTableModel model = persistence.read(new FileInputStream("lib/test.sav"));
        persistence.write(model, "/tmp/compare.sav");
        model.dispose();

        String[] original = linesInFile("lib/test.sav");
        String[] copy = linesInFile("/tmp/compare.sav");
        // We know that the initial comment is different
        assertThat(original[0], not(equalTo(copy[0])));

        // Correct that, and rest should match
        copy[0] = original[0];
        assertThat(original, matchLinesIn(copy));
    }
}
