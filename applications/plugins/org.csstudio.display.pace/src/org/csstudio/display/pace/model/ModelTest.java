package org.csstudio.display.pace.model;

import static org.junit.Assert.*;

import java.io.FileInputStream;

import org.csstudio.platform.logging.CentralLogger;
import org.junit.Test;

/** JUnit plug-in test of Model
 *  (runs as headless application)
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ModelTest
{
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

    /** Check PV connection */
    @Test
    public void testModelPVs() throws Exception
    {
        final Model model =
            new Model(new FileInputStream("configFiles/rf_admin.pace"));
        model.addListener(new ModelListener()
        {
            public void cellUpdate(final Cell cell)
            {
                System.out.println("CellUpdate: " + cell);
            }
        });
        model.start();
        Thread.sleep(10000);
        model.stop();
     }

}
