/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pace;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.csstudio.display.pace.model.Cell;
import org.csstudio.display.pace.model.Model;
import org.csstudio.display.pace.model.ModelListener;
import org.csstudio.display.pace.model.VTypeHelper;
import org.csstudio.vtype.pv.PV;
import org.csstudio.vtype.pv.PVListenerAdapter;
import org.csstudio.vtype.pv.PVPool;
import org.diirt.vtype.VType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/** JUnit test of Model
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ModelUnitTest
{
    /** Counter for received updates from cells */
    private AtomicInteger updates = new AtomicInteger(0);
    private AtomicInteger values = new AtomicInteger(0);

    @Before
    public void setup()
    {
        TestSettings.setup();
    }

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
            String value = cell.getCurrentValue();
            if (value != null  &&  !value.isEmpty())
                values.incrementAndGet();
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
                ex.getMessage().contains("nonexisting_file.pace"))
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
            new Model(new FileInputStream(TestSettings.TEST_CONFIG_FILE));

        assertEquals("HPRF Pwr and Duty Cycle Limits", model.getTitle());

        assertEquals(10, model.getColumnCount());
        assertEquals("PwrLmtRad", model.getColumn(3).getName());

        assertEquals(96, model.getInstanceCount());
        assertEquals("DTL 2", model.getInstance(6).getName());

        assertEquals("DTL_HPRF:Cav2:PwrLmtRad", model.getInstance(6).getCell(3).getName());
    }

    /** Check editing */
    @Test
    public void testModelEdits() throws Exception
    {
        // Create model that's not actually listening to PV updates
        final Model model =
            new Model(new FileInputStream(TestSettings.TEST_CONFIG_FILE));
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
    @Ignore
    public void testModelPVs() throws Exception
    {
        final Model model =
            new Model(new FileInputStream(TestSettings.TEST_CONFIG_FILE));
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
        // ... should have received a few real values
        assertTrue(values.get() > 0);
    }

    /** Check PV changes, using local PV */
    @Test //(timeout=20000)
    public void testSaveRestore() throws Exception
    {
        final AtomicReference<String> pv_value = new AtomicReference<>("");
        final Semaphore have_update = new Semaphore(0);

        // Get PV that we'll change via the Cell
        final PV pv = PVPool.getPV("loc://limit1");
        pv.addListener(new PVListenerAdapter()
        {

            @Override
            public void valueChanged(final PV pv, final VType value)
            {
                final String text = VTypeHelper.getString(value);
                pv_value.set(text);
                System.out.println("PV update: " + text);
                have_update.release();
            }
        });

        pv.write(3.14);
        have_update.drainPermits();
        System.out.println("PV is " + pv_value.get());
        assertThat(pv_value.get(), equalTo("3.14"));

        // Start model for that PV
        final Model model =
            new Model(new FileInputStream("../org.csstudio.display.pace/configFiles/localtest.pace"));
        assertEquals("loc://limit1", model.getInstance(0).getCell(0).getName());

        model.addListener(listener);
        // Reset counter
        updates.set(0);
        model.start();

        // Give it some time to reflect current value
        while (updates.get() < 1)
            Thread.sleep(100);
        assertEquals("3.14", model.getInstance(0).getCell(0).getCurrentValue());
        assertFalse(model.isEdited());

        // Simulate user-entered value
        model.getInstance(0).getCell(0).setUserValue("6.28");
        assertTrue(model.isEdited());

        // Write model to PVs: PV is unchanged...
        assertThat(pv_value.get(), equalTo("3.14"));
        // .. until values get saved
        model.saveUserValues("test");
        have_update.acquire();
        System.out.println("PV is " + pv_value.get());
        assertThat(pv_value.get(), equalTo("6.28"));
        // Model is still 'edited' because we didn't revert nor clear
        assertTrue(model.isEdited());

        // Revert
        model.revertOriginalValues();
        have_update.acquire();
        assertThat(pv_value.get(), equalTo("3.14"));

        // We're back to having user-entered values, they're not written
        assertTrue(model.isEdited());
        model.clearUserValues();
        assertFalse(model.isEdited());


        // Simulate user-entered value
        model.getInstance(0).getCell(0).setUserValue("10.0");
        assertTrue(model.isEdited());

        // Write model to PVs, submit
        assertThat(pv_value.get(), equalTo("3.14"));
        model.saveUserValues("test");
        model.clearUserValues();
        assertFalse(model.isEdited());
        have_update.acquire();
        assertThat(pv_value.get(), equalTo("10.0"));

        model.stop();
        PVPool.releasePV(pv);
     }
}
