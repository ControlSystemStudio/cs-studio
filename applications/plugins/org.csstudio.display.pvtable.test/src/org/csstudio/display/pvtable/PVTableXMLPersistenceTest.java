/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

import org.csstudio.display.pvtable.model.PVTableModel;
import org.csstudio.display.pvtable.model.VTypeHelper;
import org.csstudio.display.pvtable.persistence.PVTablePersistence;
import org.csstudio.display.pvtable.persistence.PVTableXMLPersistence;
import org.junit.Before;
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
        assertThat(model.getItemCount(), equalTo(52));
        assertThat(model.getItem(0).getName(), equalTo(TestSettings.NAME));
        assertThat(VTypeHelper.toString(model.getItem(0).getSavedValue()), equalTo("3.14"));
        model.dispose();
    }


    @Test
    public void testWriteXML() throws Exception
    {
        final PVTablePersistence persistence = new PVTableXMLPersistence();
        final PVTableModel model = new PVTableModel();
        model.addItem(TestSettings.NAME);
        
        final ByteArrayOutputStream buf = new ByteArrayOutputStream();
        persistence.write(model, buf);
        final String xml = buf.toString();
        System.out.println(xml);
        model.dispose();
        
        assertThat(xml, containsString("<pvtable"));
        assertThat(xml, containsString("<pv>"));
        assertThat(xml, containsString("<name>"+TestSettings.NAME+"</name>"));
    }
}
