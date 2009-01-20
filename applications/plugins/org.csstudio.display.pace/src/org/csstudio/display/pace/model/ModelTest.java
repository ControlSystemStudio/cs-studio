package org.csstudio.display.pace.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

/** JUnit plug-in test of Model
 *  Runs as headless application.
 *  For PV connections to work, use junit_customization.ini
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ModelTest
{
    private AtomicInteger updates = new AtomicInteger(0);
    
    // ModelListener that counts received updates
    final private ModelListener listener = new ModelListener()
    {
        public void cellUpdate(final Cell cell)
        {
            updates.incrementAndGet();
            System.out.println("CellUpdate: " + cell);
        }
    };

    /** Check basic model readout: title, columns, instances, cell's PV */
    @Test
    public void testModel() throws Exception
    {
        final Model model =
            new Model(new FileInputStream("configFiles/rf_admin.pace"));
        
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
            new Model(new FileInputStream("configFiles/rf_admin.pace"));
        model.addListener(listener);
        
        // Model has not been edited, find a cell to test changes
        updates.set(0);
        assertFalse(model.isEdited());
        final Cell cell = model.getInstance(1).getCell(1);
        assertFalse(cell.isReadOnly());
        
        // Edit a cell
        cell.setUserValue("10");
        assertEquals(1, updates.get());
        assertTrue(cell.isEdited());
        assertTrue(model.isEdited());
        assertEquals("10", cell.getValue());
        assertEquals("10", cell.getUserValue());
        
        // Revert to original value
        cell.clearUserValue();
        assertEquals(2, updates.get());
        assertFalse(cell.isEdited());
        assertFalse(model.isEdited());
        assertEquals(null, cell.getUserValue());
    }

    /** Check PV connection */
    @Test
    public void testModelPVs() throws Exception
    {
        final Model model =
            new Model(new FileInputStream("configFiles/rf_admin.pace"));
        model.addListener(listener);
        updates.set(0);
        model.start();
        Thread.sleep(5000);
        model.stop();
        assertFalse(model.isEdited());
        // Should have received a few (initial) updates
        assertTrue(updates.get() > 0);
     }    
}
