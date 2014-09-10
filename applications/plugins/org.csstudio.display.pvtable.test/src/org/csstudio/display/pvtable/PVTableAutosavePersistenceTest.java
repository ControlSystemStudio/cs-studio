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
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.io.FileInputStream;

import org.csstudio.display.pvtable.model.PVTableItem;
import org.csstudio.display.pvtable.model.PVTableModel;
import org.csstudio.display.pvtable.model.VTypeHelper;
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
            System.out.println(item.getName() + " = " + VTypeHelper.getValue(item.getSavedValue()));
        }
        assertThat(model.getItemCount(), equalTo(255));
        assertThat(model.getItem(0).getName(), equalTo("DTL_LLRF:IOC1:vxiRead0.A"));
        assertThat(VTypeHelper.toDouble(model.getItem(0).getSavedValue()), equalTo(212.0));
        model.dispose();
    }

    @Test
    public void compareFiles() throws Exception
    {
        final PVTablePersistence persistence = new PVTableAutosavePersistence();
        final PVTableModel model = persistence.read(new FileInputStream("lib/auto.sav"));
        persistence.write(model, "/tmp/compare.sav");
        model.dispose();
        
        String[] original = linesInFile("lib/auto.sav");
        String[] copy = linesInFile("/tmp/compare.sav");
        // We know that the initial comment is different
        assertThat(original[0], not(equalTo(copy[0])));
        
        // Correct that, and rest should match
        copy[0] = original[0];
        assertThat(original, matchLinesIn(copy));
    }
}
