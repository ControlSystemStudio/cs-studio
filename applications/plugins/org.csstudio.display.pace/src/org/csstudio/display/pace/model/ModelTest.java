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
 *  
 *    reviewed by Delphy 01/29/09
 */
//TODO Explain "use junit_customization.ini"
@SuppressWarnings("nls")
public class ModelTest
{
   //TODO Explain ... creating a counter for test ...
    private AtomicInteger updates = new AtomicInteger(0);
    // TODO Explain ... create ModelListener for test that counts ...
    // ModelListener that counts received updates
    final private ModelListener listener = new ModelListener()
    {
        public void cellUpdate(final Cell cell)
        {
           //TODO Explain ... incrementing and retrieving counter ...
            updates.incrementAndGet();
            System.out.println("CellUpdate: " + cell);
        }
    };

    /** Check basic model readout: title, columns, instances, cell's PV */
    //TODO Explain ... checking ... model title, number of columns, column names, 
    //number of instances, cell's PV name
    @Test
    public void testModel() throws Exception
    {
       // TODO Explain ... using the rf_admin config file for testing
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
        //TODO Explain ... change a value in the model
        updates.set(0);
        //TODO Explain ... confirm Model has not been edited
        assertFalse(model.isEdited());
        
        //TODO Explain ... retrieve the 2nd cell of the instance and confirm it is readonly
        final Cell cell = model.getInstance(1).getCell(1);
        assertFalse(cell.isReadOnly());
        
        // Edit a cell
        cell.setUserValue("10");
        //TODO Explain test
        assertEquals(1, updates.get());
        assertTrue(cell.isEdited());
        assertTrue(model.isEdited());
        //TODO Explain how cell value became 10
        assertEquals("10", cell.getValue());
        assertEquals("10", cell.getUserValue());
        
        // Revert to original value
        //TODO Explain ... Call clearUserValue to revert to the cells original value
        cell.clearUserValue();
        //TODO Explain test
        assertEquals(2, updates.get());
        //TODO Explain ... Confirm that the edited values were replaced with the original
        // and is no longer considered edited
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
        //TODO Explain 
        updates.set(0);
        //TODO Explain what is starting
        model.start();
        Thread.sleep(5000);
        model.stop();
        assertFalse(model.isEdited());
        // Should have received a few (initial) updates
        assertTrue(updates.get() > 0);
     }    
}
