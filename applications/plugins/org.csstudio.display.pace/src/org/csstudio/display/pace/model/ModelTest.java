package org.csstudio.display.pace.model;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;

import org.junit.Ignore;
import org.junit.Test;

/** JUnit plug-in test of Model
 *  (runs as headless application)
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ModelTest
{
    private int updates = 0;
    
    final private ModelListener listener = new ModelListener()
    {
        public void cellUpdate(final Cell cell)
        {
            ++updates;
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

        assertEquals("DTL_HPRF:Cav2:DutyLmt", model.getInstance(6).getCell(3).getPV());
    }

    /** Check editing */
    @Test
    public void testModelEdits() throws Exception
    {
        // Create model that's not actually listening to PV updates
        final Model model =
            new Model(new FileInputStream("configFiles/rf_admin.pace"));
        model.addListener(listener);
        updates = 0;
        assertFalse(model.isEdited());
        final Cell cell = model.getInstance(1).getCell(1);
        assertFalse(cell.isReadOnly());
        
        // Edit a cell
        cell.setUserValue("10");
        assertEquals(1, updates);
        assertTrue(cell.isEdited());
        assertTrue(model.isEdited());
    
        // Revert to original value
        cell.clearUserValue();
        assertEquals(2, updates);
        assertFalse(cell.isEdited());
        assertFalse(model.isEdited());
    }

    /** Check PV connection */
    @Test
    public void testModelPVs() throws Exception
    {
        final Model model =
            new Model(new FileInputStream("configFiles/rf_admin.pace"));
        model.addListener(listener);
        updates = 0;
        model.start();
        Thread.sleep(5000);
        model.stop();
        assertFalse(model.isEdited());
        assertTrue(updates > 0);
     }    
}
