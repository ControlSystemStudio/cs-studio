/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.rdbtable.model;

import static org.junit.Assert.*;

import org.junit.Test;

/** JUnit test of the RDBTable
 *  <p>
 *  <b>NOTE:
 *     Some tests modify RDB data, so only run on a test RDB!
 *  </b>
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBTableModelTest implements RDBTableModelListener
{
    /** @see RDBTableModelListener */
    @Override
    public void rowChanged(final RDBTableRow row)
    {
        System.out.println("Row changed: " + row);
    }

    /** @see RDBTableModelListener */
    @Override
    public void newRow(final RDBTableRow row)
    {
        System.out.println("Added Row: " + row);
    }

    /** Test table readout, update, ... */
    @Test
    public void tableTest() throws Exception
    {
        final RDBTableModel table = new RDBTableModel("demo/example.rdb");
        assertEquals(false, table.needPassword());
        table.read();
        table.addListener(this);

        // Read configuration
        assertEquals("Example Configuration", table.getTitle());
        assertEquals(2, table.getColumnCount());
        assertEquals("Name", table.getHeader(1));

        // Read initial data
        assertTrue(table.getRowCount() > 0);
        System.out.println(table.toString());

        // Allow changes
        assertEquals(false, table.wasModified());
        table.addRow(new String [] { "9999", "Test" });
        assertEquals(true, table.wasModified());
        final int last_row = table.getRowCount()-1;
        assertEquals("Test", table.getRow(last_row).getColumn(1));

        table.getRow(last_row).setColumn(1, "Changed");
        assertEquals("Changed", table.getRow(last_row).getColumn(1));

        // Write changes out
        table.write();
        assertEquals(false, table.wasModified());
        System.out.println("\n -- With changes --");
        System.out.println(table.toString());

        // Delete a row (the one inserted/changed before)
        table.deleteRow(last_row);
        assertEquals(true, table.wasModified());
        table.write();
        assertEquals(false, table.wasModified());
    }

    /** Check handling of user/password info */
    @Test
    public void passwordTest() throws Exception
    {
        final RDBTableModel table = new RDBTableModel("demo/example2.rdb");
        assertEquals("alarm", table.getUser());
        assertEquals(true, table.needPassword());
        table.read(table.getUser(), "$" + table.getUser());
    }
}
