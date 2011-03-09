/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pace.model;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

/** JUnit plug-in test of Model
 *  Runs as headless application.
 *
 *  For PV connections to work, use junit_customization.ini
 *  (See comments in GUITest.java for more on that)
 *
 *  @author Kay Kasemir
 *
 *    reviewed by Delphy 01/29/09
 */
@SuppressWarnings("nls")
public class ModelTest
{
    /** Configuration file name used for testing.
     *  Tests are specific to this file,
     *  and some also expect to actually connect to
     *  the PVs in there.
     */
    private static final String TEST_CONFIG_FILE = "configFiles/rf_admin.pace";

    /** Counter for received updates from cells */
    private AtomicInteger updates = new AtomicInteger(0);

    /** ModelListener that counts received updates.
     *  @see #updates
     */
    final private ModelListener listener = new ModelListener()
    {
        @Override
        public void cellUpdate(final Cell cell)
        {
            // Atomically count up; thread-safe
            updates.incrementAndGet();
            System.out.println("CellUpdate: " + cell);
        }
    };


    /** Check if non-existing model file is detected */
    @Test
    public void testBadModel()
    {
        try
        {
                new Model(new FileInputStream("nonexisting_file.pace"));
        }
        catch (Exception ex)
        {
            // Detected the missing file?
            if (ex instanceof FileNotFoundException  &&
                ex.getMessage().equals("nonexisting_file.pace (No such file or directory)"))
                return;
            // Else: Didn't get the expected error
            ex.printStackTrace();
        }
        fail("Didn't catch missing file");
    }



    /** Check basic model readout:
     *  Correct title, # of columns, instances, cell's PV names?
     *
     *  Depends on configFiles/rf_admin.pace to be as expected.
     */
    @Test
    public void testModel() throws Exception
    {
        final Model model =
            new Model(new FileInputStream(TEST_CONFIG_FILE));

        assertEquals("HPRF Pwr and Duty Cycle Limits", model.getTitle());

        assertEquals(6, model.getColumnCount());
        assertEquals("DutyLmt", model.getColumn(3).getName());

        assertEquals(96, model.getInstanceCount());
        assertEquals("DTL 2", model.getInstance(6).getName());

        assertEquals("DTL_HPRF:Cav2:DutyLmt", model.getInstance(6).getCell(3).getName());
    }

    /** Check editing */
    @Test
    public void testModelEdits() throws Exception
    {
        // Create model that's not actually listening to PV updates
        final Model model =
            new Model(new FileInputStream(TEST_CONFIG_FILE));
        model.addListener(listener);

        // Model has not been edited, find a cell to test changes
        // Reset counter
        updates.set(0);
        // Confirm Model has not been edited
        assertFalse(model.isEdited());

        // Assert that we have a non-readonly cell to test
        final Cell cell = model.getInstance(1).getCell(1);
        assertFalse(cell.isReadOnly());

        // Edit the cell
        cell.setUserValue("10");
        // Expect an update, cell and model in "edited" state
        assertEquals(1, updates.get());
        assertTrue(cell.isEdited());
        assertTrue(model.isEdited());
        // Cell should reflect the value that we entered via setUserValue
        assertEquals("10", cell.getValue());
        assertEquals("10", cell.getUserValue());

        // Revert to original value
        cell.clearUserValue();
        // Should result in another update, since value changes back
        assertEquals(2, updates.get());
        // Confirm that the edited values were replaced with the original
        // and is no longer considered edited
        assertFalse(cell.isEdited());
        assertFalse(model.isEdited());
        assertEquals(null, cell.getUserValue());
    }

    /** Check PV connection.
     *  Will only work when we can actually connect to the PVs in the test file
     */
    @Test
    public void testModelPVs() throws Exception
    {
        final Model model =
            new Model(new FileInputStream(TEST_CONFIG_FILE));
        model.addListener(listener);
        // Reset counter
        updates.set(0);
        // Connect PVs, so now we expect to receive the current values
        model.start();
        // Give it some time
        Thread.sleep(5000);
        model.stop();
        // Even though nobody edited the model...
        assertFalse(model.isEdited());
        // ... should have received a few (initial) updates
        assertTrue(updates.get() > 0);
     }
}
